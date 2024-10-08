/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.protocol.yunkuaichong;

import sanbing.jcpp.protocol.ProtocolContext;
import sanbing.jcpp.protocol.listener.tcp.TcpSession;

/**
 * @author baigod
 */
public abstract class YunKuaiChongDownlinkCmdExe extends AbstractYunKuaiChongCmdExe{

    public abstract void execute(TcpSession tcpSession, YunKuaiChongDwonlinkMessage yunKuaiChongDwonlinkMessage, ProtocolContext ctx);

}