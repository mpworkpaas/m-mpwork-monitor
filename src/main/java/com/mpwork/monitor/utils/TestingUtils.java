package com.mpwork.monitor.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.regex.Pattern;

import org.mframework.utils.CommonUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * 播测工具
 * @author dbb
 *
 */
public class TestingUtils {
	
    private static String regexIPv6 = "^(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]).){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]).){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))$";
    
//    // 播测接口
//	public static String testingUrl(String url) {
//		return MApiCaller.doHttpGet(url);	// "https://jnt.mfu.com.cn/ajax";
//	}
	
	@Getter
	@Setter
	public static class PingResult {
	    private String ip;
	    private String output;
	    private boolean isSuccess;
	    private float roundTripTime;
	}
	
	public static void main(String[] args) {
        String ipAddress = "127.0.0.1";
        PingResult result = ping(ipAddress);
        System.out.println("IP: " + result.getIp());
        System.out.println("Ping Result:" + result.isSuccess());
        System.out.println("Ping Time: " + result.getRoundTripTime() + "ms");
        System.out.println("--------------------");
        System.out.println(result.getOutput());
    }
	
	private static String buildPingCommand(String ipAddress) {
        int count = 1;
        int packetSize = 32;
        String cmd = "";
		if(CommonUtils.isWindows()) {
			cmd = "cmd /c chcp 437 & ";	// 解决Windows返回中文结果的问题
            Pattern pattern = Pattern.compile(regexIPv6);
            if (pattern.matcher(ipAddress).matches() == true) {
            	cmd += "ping -6 -n " + count + " -l " + packetSize + " " + ipAddress;
            } else {
            	cmd += "ping -n " + count + " -l " + packetSize + " " + ipAddress;
            }
		} else {
            Pattern pattern = Pattern.compile(regexIPv6);
            if (pattern.matcher(ipAddress).matches() == true) {
            	cmd = "ping6 -c " + count + " -s " + packetSize + " " + ipAddress;
            } else {
            	cmd = "ping -c " + count + " -s " + packetSize + " " + ipAddress;
            }
		}
		
		return cmd;
	}
	
	// PING播测
    public static PingResult ping(String ipAddress) {
        PingResult result = new PingResult();
        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            
            String pingCommand = buildPingCommand(ipAddress);
            Process process = Runtime.getRuntime().exec(pingCommand);
            int exitCode = process.waitFor();
            
            result.setIp(inetAddress.getHostAddress());
            
        	StringBuffer outputBuffer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            boolean loadedTime = false;
            while ((line = reader.readLine()) != null) {
            	outputBuffer.append(line);
            	outputBuffer.append("\n");
                if (!loadedTime && (line.contains("time=") || line.contains("time<"))) {
                    int startIndex = line.indexOf(" time");
                    int endIndex = line.indexOf("ms", startIndex);
                    String roundTripTime = line.substring(startIndex + 6, endIndex).trim();
                    result.setRoundTripTime(Float.parseFloat(roundTripTime));
                    loadedTime = true;	// 不再重复提升
                }
            }
            reader.close();

            result.setSuccess(exitCode == 0 && result.getRoundTripTime()>0 && result.getRoundTripTime()<1000);
            result.setOutput(outputBuffer.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
