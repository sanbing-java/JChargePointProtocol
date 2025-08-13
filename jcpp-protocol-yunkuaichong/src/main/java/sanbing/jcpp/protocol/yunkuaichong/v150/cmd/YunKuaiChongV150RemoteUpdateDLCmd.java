package sanbing.jcpp.protocol.yunkuaichong.v150.cmd;

import static sanbing.jcpp.protocol.yunkuaichong.enums.YunKuaiChongDownlinkCmdEnum.REMOTE_UPDATE;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import sanbing.jcpp.proto.gen.ProtocolProto;
import sanbing.jcpp.protocol.ProtocolContext;
import sanbing.jcpp.protocol.listener.tcp.TcpSession;
import sanbing.jcpp.protocol.yunkuaichong.YunKuaiChongDownlinkCmdExe;
import sanbing.jcpp.protocol.yunkuaichong.YunKuaiChongDwonlinkMessage;
import sanbing.jcpp.protocol.yunkuaichong.annotation.YunKuaiChongCmd;


/**
 * 云快充1.5.0  远程更新
 * @author bawan
 */
@Slf4j
@YunKuaiChongCmd(0x94)
public class YunKuaiChongV150RemoteUpdateDLCmd extends YunKuaiChongDownlinkCmdExe {

    @Override
    public void execute(TcpSession tcpSession, YunKuaiChongDwonlinkMessage message, ProtocolContext ctx) {
        log.info("{} 云快充1.5.0 远程更新", tcpSession);
        // check
        if (message.getMsg().hasOtaRequest()) {
            log.error("云快充1.5.0 远程更新消息体为空");
            return;
        }

        // 初始化 buf
        ByteBuf msgBody = Unpooled.buffer(94);
        // buf 转换
        ProtocolProto.OtaRequest request = message.getMsg().getOtaRequest();
        msgBody.writeBytes(encodePileCode(request.getPileCode()));
        msgBody.writeByte(request.getPileModel());
        msgBody.writeBytes(writeParamFillZero(request.getPilePower(),2));
        msgBody.writeBytes(writeParamFillZero(request.getAddress(),16));
        msgBody.writeBytes(writeParamFillZero(request.getPort(),2));
        msgBody.writeBytes(writeParamFillZero(request.getUsername(),16));
        msgBody.writeBytes(writeParamFillZero(request.getPassword(),16));
        msgBody.writeBytes(writeParamFillZero(request.getFilePath(),32));
        msgBody.writeByte(request.getExecutionControl());
        msgBody.writeByte(request.getDownloadTimeout());

        super.encodeAndWriteFlush(REMOTE_UPDATE,msgBody,tcpSession);
    }


}
