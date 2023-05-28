package org.example;


import org.example.common.constant.Protocol;
import org.example.server.annotation.RpcServerApplication;

/**
 * WaitMeeting服务端
 */
@RpcServerApplication(rpcApiPackages = "", protocols = {Protocol.TCP})
public class MeetingServer {
    public static void main(String[] args) throws Exception {
        RpcServer.run(MeetingServer.class);
    }
}
