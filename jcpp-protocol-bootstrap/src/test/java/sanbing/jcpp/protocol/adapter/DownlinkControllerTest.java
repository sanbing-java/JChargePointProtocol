/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.protocol.adapter;

import cn.hutool.core.util.HexUtil;
import com.fasterxml.jackson.databind.JsonNode;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import jakarta.annotation.Resource;
import org.junit.After;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import sanbing.jcpp.infrastructure.util.jackson.JacksonUtil;
import sanbing.jcpp.infrastructure.util.property.PropertyUtils;
import sanbing.jcpp.proto.gen.ProtocolProto;
import sanbing.jcpp.proto.gen.ProtocolProto.DownlinkRestMessage;
import sanbing.jcpp.protocol.AbstractProtocolTestBase;
import sanbing.jcpp.protocol.domain.DownlinkCmdEnum;
import sanbing.jcpp.protocol.domain.ProtocolSession;
import sanbing.jcpp.protocol.listener.tcp.configs.BinaryHandlerConfiguration;
import sanbing.jcpp.protocol.listener.tcp.decoder.JCPPLengthFieldBasedFrameDecoder;
import sanbing.jcpp.protocol.listener.tcp.decoder.TcpMsgDecoder;
import sanbing.jcpp.protocol.provider.impl.DefaultProtocolSessionRegistryProvider;

import java.nio.ByteOrder;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sanbing.jcpp.protocol.listener.tcp.configs.BinaryHandlerConfiguration.LITTLE_ENDIAN_BYTE_ORDER;

class DownlinkControllerTest extends AbstractProtocolTestBase {
    final String PROTOCOL_NAME = "yunkuaichongV150";

    @Value("${service.protocols.yunkuaichongV150.listener.tcp.handler.configuration}")
    private String yunkuaichongV150TcpHandler;

    @Value("${service.protocols.yunkuaichongV150.listener.tcp.bind-port}")
    private int yunkuaichongV150TcpPort;

    @Resource
    DefaultProtocolSessionRegistryProvider sessionRegistryProvider;

    private EventLoopGroup group;
    private Channel channel;

    @BeforeEach
    void setUp() throws InterruptedException {
        final JsonNode cfgJson = JacksonUtil.valueToTree(PropertyUtils.getProps(yunkuaichongV150TcpHandler));

        BinaryHandlerConfiguration binaryHandlerConfig = JacksonUtil.treeToValue(cfgJson, BinaryHandlerConfiguration.class);

        ByteOrder byteOrder = LITTLE_ENDIAN_BYTE_ORDER.equalsIgnoreCase(binaryHandlerConfig.getByteOrder())
                ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;

        JCPPLengthFieldBasedFrameDecoder framer = new JCPPLengthFieldBasedFrameDecoder(binaryHandlerConfig.getHead(), byteOrder,
                binaryHandlerConfig.getLengthFieldOffset(), binaryHandlerConfig.getLengthFieldLength(),
                binaryHandlerConfig.getLengthAdjustment(), binaryHandlerConfig.getInitialBytesToStrip());


        group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(framer)
                                .addLast("tcpByteDecoderOverride", new TcpMsgDecoder<>(PROTOCOL_NAME, TcpMsgDecoder::toByteArray))
                                .addLast(new SimpleChannelInboundHandler<>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

                                        log.info("接收到字节码:{}", msg);
                                    }
                                });
                    }
                });

        // 连接到服务器
        ChannelFuture f = b.connect("127.0.0.1", yunkuaichongV150TcpPort).sync();
        channel = f.channel();
    }

    @After
    public void tearDown() {
        if (channel != null) {
            channel.close();
        }
        group.shutdownGracefully();
    }

    @Test
    void remoteStartCharging() throws Exception {
        // 先发送一段登录
        channel.writeAndFlush(Unpooled.wrappedBuffer(HexUtil.decodeHex("6822001900012023121200001001011047562e393572313300898604d11722d0348606024E87"))).sync();

        // 停一会等注册完成 todo 也可以读下行消息判断是否登录成功
        Thread.sleep(1000);

        UUID messageId = UUID.randomUUID();
        ProtocolSession protocolSession = sessionRegistryProvider.getSESSION_CACHE().asMap().values().stream().findFirst().get().get();
        UUID sessionId = protocolSession.getId();
        UUID requestId = UUID.randomUUID();

        // 创建 DownlinkRestMessage 实例
        String pileCode = "20231212000010";
        DownlinkRestMessage downlinkMsg = DownlinkRestMessage.newBuilder()
                .setMessageIdMSB(messageId.getMostSignificantBits())
                .setMessageIdLSB(messageId.getLeastSignificantBits())
                .setSessionIdMSB(sessionId.getMostSignificantBits())
                .setSessionIdLSB(sessionId.getLeastSignificantBits())
                .setProtocolName(PROTOCOL_NAME)
                .setPileCode(pileCode)
                .setRequestIdMSB(requestId.getMostSignificantBits())
                .setRequestIdLSB(requestId.getLeastSignificantBits())
                .setDownlinkCmd(DownlinkCmdEnum.REMOTE_START_CHARGING.name())
                .setRemoteStartChargingRequest(ProtocolProto.RemoteStartChargingRequest.newBuilder()
                        .setPileCode(pileCode)
                        .setGunCode("01")
                        .setLimitCent(10000)
                        .setTradeNo("12345678901234567890")
                        .build())
                .build();

        // 序列化为 Protobuf 字节流
        byte[] protobufContent = downlinkMsg.toByteArray();

        // 调用 POST 接口
        mockMvc.perform(post("/api/onDownlink")
                        .contentType("application/x-protobuf")
                        .content(protobufContent))
                .andDo(print())  // 打印请求和响应信息
                .andExpect(status().is(HttpStatus.OK.value()));
    }

}