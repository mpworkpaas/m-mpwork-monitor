package com.mpwork.monitor.utils;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.mframework.utils.MResult;

public class TcpPortChecker {
	
	public static void main(String[] args) {
		MResult tcpMResult = checkTcp("www.baidu.com", 6379);
		System.out.println(tcpMResult);
	}
	
	// 检测TCP端口是否可连通
	public static MResult checkTcp(String remoteHost, int tcpPort) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(remoteHost, tcpPort), 5000);  // 设置超时时间为5秒
            socket.close();
            
            return MResult.buildOK();
        } catch (IOException e) {
            // System.out.println("The port is closed or an error occurred: " + e.getMessage());
        	return MResult.build_A00005(e.getMessage());
        }
    }
	
}
