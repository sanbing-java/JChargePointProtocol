/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.app.service.grpc;

import com.google.common.net.HostAndPort;
import io.grpc.CompressorRegistry;
import io.grpc.ConnectivityState;
import io.grpc.DecompressorRegistry;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.channel.ChannelOption;
import io.grpc.netty.shaded.io.netty.channel.nio.NioEventLoopGroup;
import io.grpc.netty.shaded.io.netty.channel.socket.nio.NioSocketChannel;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sanbing.jcpp.infrastructure.util.async.JCPPThreadFactory;
import sanbing.jcpp.infrastructure.util.trace.TracerRunnable;
import sanbing.jcpp.proto.gen.ProtocolDownlinkInterfaceGrpc;
import sanbing.jcpp.proto.gen.ProtocolDownlinkInterfaceGrpc.ProtocolDownlinkInterfaceStub;
import sanbing.jcpp.proto.gen.ProtocolProto.DownlinkRequestMessage;
import sanbing.jcpp.proto.gen.ProtocolProto.DownlinkResponseMessage;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author baigod
 */
@Component
@Slf4j
public class DownlinkGrpcClient {

    @Value("${downlink.rpc.grpc.netty.event_loop:}")
    private Integer rpcNettyEventLoop;

    @Value("${downlink.rpc.grpc.netty.so_sndbuf:65535}")
    private Integer rpcNettySoSndbuf;

    @Value("${downlink.rpc.grpc.netty.so_rcvbuf:65535}")
    private Integer rpcNettySoRcvbuf;

    @Value("${downlink.rpc.grpc.netty.no_delay:true}")
    private boolean rpcNoDelay;

    @Value("${downlink.rpc.grpc.netty.max_inbound_message_size:33554432}")
    private Integer rpcMaxInboundMessageSize;

    @Value("${downlink.rpc.grpc.keep_alive_time_sec:300}")
    private int keepAliveTimeSec;

    @Value("${downlink.rpc.grpc.max_records_size:102400}")
    private int maxRecordsSize;

    @Value("${downlink.rpc.grpc.batch_records_count:1024}")
    private int batchRecordsCount;

    @Value("${downlink.rpc.grpc.no_read_records_sleep:25}")
    private long noRecordsSleepInterval;

    @Value("${downlink.rpc.grpc.records_ttl:600000}")
    private long recordsTtl;

    private volatile boolean initialized = true;

    private final Map<HostAndPort, ManagedChannel> channelMap = new ConcurrentHashMap<>();
    private final Map<HostAndPort, StreamObserver<DownlinkRequestMessage>> inputStreamMap = new ConcurrentHashMap<>();
    private final Map<HostAndPort, LinkedBlockingQueue<DownlinkRequestMessage>> queueMap = new ConcurrentHashMap<>();
    private final Map<HostAndPort, ReentrantLock> msgHandleLocks = new ConcurrentHashMap<>();
    private final Map<HostAndPort, ExecutorService> msgHandleExecutors = new ConcurrentHashMap<>();
    private ExecutorService grpcStarterExecutor;
    private ScheduledExecutorService grpcStateCheckExecutor;
    private ScheduledExecutorService downlinkMsgsExecutor;

    @PostConstruct
    public void init() {
        grpcStarterExecutor = Executors.newSingleThreadExecutor(JCPPThreadFactory.forName("grpc-starter-executor"));
        grpcStateCheckExecutor = Executors.newSingleThreadScheduledExecutor(JCPPThreadFactory.forName("grpc-check-executor"));
        downlinkMsgsExecutor = Executors.newSingleThreadScheduledExecutor(JCPPThreadFactory.forName("downlink-msgs-executor"));

        // 每秒进行一次连接检查与线程初始化
        downlinkMsgsExecutor.scheduleWithFixedDelay(() -> {
            queueMap.forEach((key, queue) -> {

                if (queue.isEmpty()) {
                    return;
                }

                ManagedChannel managedChannel = channelMap.get(key);

                if (managedChannel == null || managedChannel.getState(false) != ConnectivityState.READY) {
                    grpcStarterExecutor.submit(new TracerRunnable(() -> connect(key)));
                    return;
                }

                msgHandleExecutors.computeIfAbsent(key, hostAndPort ->
                                Executors.newFixedThreadPool(1, JCPPThreadFactory.forName("downlink-handle-threads-" + hostAndPort)))
                        .execute(new TracerRunnable(() -> {
                            while (initialized) {
                                try {
                                    handleMsgs(key, queue);
                                } catch (Exception e) {
                                    log.error("Failed to process messages handling!", e);
                                }
                            }
                        }));
            });

        }, 0, 1, TimeUnit.SECONDS);

        grpcStateCheckExecutor.scheduleWithFixedDelay(() -> {
            channelMap.forEach((key, channel) -> {
                ConnectivityState state = channel.getState(true);

                if (state == ConnectivityState.SHUTDOWN) {
                    log.info("Grpc 客户端SHUTDOWN {} {}", key, state);

                    LinkedBlockingQueue<DownlinkRequestMessage> queue = queueMap.get(key);
                    if (queue != null) {
                        queue.clear();
                        queueMap.remove(key);
                    }

                    ExecutorService executorService = msgHandleExecutors.get(key);
                    if (executorService != null) {
                        executorService.shutdownNow();
                        msgHandleExecutors.remove(key);
                    }
                }
            });
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void handleMsgs(HostAndPort key, LinkedBlockingQueue<DownlinkRequestMessage> queue) {
        StreamObserver<DownlinkRequestMessage> inputStream = inputStreamMap.get(key);

        if (inputStream == null) {
            return;
        }

        long acceptTs = System.currentTimeMillis() - recordsTtl;

        List<DownlinkRequestMessage> downlinkMsgs = new ArrayList<>(batchRecordsCount);

        queue.drainTo(downlinkMsgs, batchRecordsCount);

        for (DownlinkRequestMessage msg : downlinkMsgs) {

            long ts = msg.getTracer().getTs();

            if (ts > 0 && ts < acceptTs) {

                log.warn("[{}] 消息过期，直接丢弃 {}", key, ts);

                continue;
            }


            ReentrantLock lock = msgHandleLocks.computeIfAbsent(key, hostAndPort -> new ReentrantLock());

            lock.lock();
            try {
                inputStream.onNext(msg);
            } finally {
                lock.unlock();
            }
        }


        if (downlinkMsgs.isEmpty()) {
            try {
                Thread.sleep(noRecordsSleepInterval);
            } catch (InterruptedException e) {
                log.warn("Sleep interrupted!", e);
            }
        } else {
            downlinkMsgs.clear();
        }
    }

    @PreDestroy
    public void destroy() {
        log.info("Starting Grpc destroying process");

        initialized = false;

        try {

            grpcStarterExecutor.shutdownNow();

            grpcStateCheckExecutor.shutdownNow();

            downlinkMsgsExecutor.shutdownNow();

            msgHandleExecutors.values().forEach(ExecutorService::shutdownNow);

            inputStreamMap.values().forEach(StreamObserver::onCompleted);

            channelMap.values().forEach(ManagedChannel::shutdownNow);

        } catch (Exception e) {
            log.error("Exception during disconnect", e);
        }
    }

    public void connect(HostAndPort hostAndPort) {

        if (channelMap.get(hostAndPort) != null && channelMap.get(hostAndPort).getState(true).ordinal() < 2) {
            return;
        }

        log.info("[{}] Create new Grpc Client Channel!", hostAndPort);

        ManagedChannel managedChannel = NettyChannelBuilder.forAddress(hostAndPort.getHost(), hostAndPort.getPort())
                .eventLoopGroup(new NioEventLoopGroup(Optional.ofNullable(rpcNettyEventLoop).orElse(Runtime.getRuntime().availableProcessors() * 2)))
                .compressorRegistry(CompressorRegistry.getDefaultInstance())
                .decompressorRegistry(DecompressorRegistry.getDefaultInstance())
                .withOption(ChannelOption.SO_SNDBUF, rpcNettySoSndbuf)
                .withOption(ChannelOption.SO_RCVBUF, rpcNettySoRcvbuf)
                .withOption(ChannelOption.TCP_NODELAY, rpcNoDelay)
                .maxInboundMessageSize(rpcMaxInboundMessageSize)
                .channelType(NioSocketChannel.class)
                .directExecutor()
                .keepAliveTime(keepAliveTimeSec, TimeUnit.SECONDS)
                .usePlaintext()
                .keepAliveTime(5, TimeUnit.MINUTES) // Change to a larger value, e.g. 5min.
                .keepAliveTimeout(10, TimeUnit.SECONDS) // Change to a larger value, e.g. 10s.
                .keepAliveWithoutCalls(true)// You should normally avoid enabling this.
                .defaultLoadBalancingPolicy("round_robin")
                .build();

        log.info("Grpc 客户端READY {} {}", hostAndPort, managedChannel.getState(true));

        ManagedChannel remove = channelMap.remove(hostAndPort);

        if (remove != null) {
            channelMap.get(hostAndPort).shutdownNow();
        }

        channelMap.put(hostAndPort, managedChannel);

        ProtocolDownlinkInterfaceStub stub = ProtocolDownlinkInterfaceGrpc.newStub(managedChannel);

        StreamObserver<DownlinkRequestMessage> streamObserver = stub.onDownlink(new StreamObserver<>() {
            @Override
            public void onNext(DownlinkResponseMessage value) {
                log.info("Grpc 接收到通信层反向回复 {}", value);
            }

            @Override
            public void onError(Throwable t) {
                log.warn("Grpc 客户端异常 {}", t.getMessage());
            }

            @Override
            public void onCompleted() {
                log.info("[{}] The Grpc connection was closed!", hostAndPort);
            }
        });

        inputStreamMap.put(hostAndPort, streamObserver);

    }

    /**
     * 发送下行请求
     */
    public void sendDownlinkRequest(HostAndPort hostAndPort, DownlinkRequestMessage downlinkRequestMessage) {
        queueMap.computeIfAbsent(hostAndPort, k -> new LinkedBlockingQueue<>(maxRecordsSize)).add(downlinkRequestMessage);
    }
}