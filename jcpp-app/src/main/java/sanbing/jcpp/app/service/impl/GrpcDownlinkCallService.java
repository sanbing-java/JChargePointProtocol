/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.app.service.impl;

import com.google.common.net.HostAndPort;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;
import sanbing.jcpp.app.data.PileSession;
import sanbing.jcpp.app.service.DownlinkCallService;
import sanbing.jcpp.app.service.grpc.DownlinkGrpcClient;
import sanbing.jcpp.proto.gen.ProtocolProto.DownlinkRequestMessage;

/**
 * @author baigod
 */
@Service
@Slf4j
@ConditionalOnExpression("'${downlink.rpc.type:null}'=='grpc'")
public class GrpcDownlinkCallService extends DownlinkCallService {

    @Resource
    DownlinkGrpcClient downlinkGrpcClient;

    @Override
    protected void _sendDownlinkMessage(DownlinkRequestMessage downlinkMessage, PileSession pileSession) {
        try {

            downlinkGrpcClient.sendDownlinkRequest(HostAndPort.fromParts(pileSession.getNodeIp(), pileSession.getNodeGrpcPort()),
                    downlinkMessage);

        } catch (Exception e) {
            log.error("下行消息发送异常", e);
        }
    }
}