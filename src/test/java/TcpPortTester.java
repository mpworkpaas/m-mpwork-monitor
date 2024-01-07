import org.mframework.utils.MResult;

import com.mpwork.monitor.utils.TcpPortChecker;

public class TcpPortTester {
	
	public static void main(String[] args) {
		MResult tcpMResult = TcpPortChecker.checkTcp("www.baidu.com", 6379);
		System.out.println(tcpMResult);
	}
	
}
