import java.util.Date;

import org.mframework.utils.CommonUtils;
import org.mframework.utils.MResult;

import com.mpwork.monitor.utils.UrlValidator;

public class SSLTester {
	
    public static void main(String[] args) {
        // String url = "https://local.test.mpwork.com/ajax";
        // String url = "https://dbb.test.mpwork.com/ajax";
    	String url = "https://m.capitalmuseum.org.cn/ajax";
    	MResult accessMRes = UrlValidator.checkUrlAccessible(url);
        MResult mres = UrlValidator.checkSSLCertificate(url);
        System.out.println("URL accessible: " + accessMRes);
        System.out.println("SSL certificate validator: " + mres.putToNewJSONObject());
        
        if(mres.isOk()) {
        	Date notafter = mres.getParamT("notafter");
            System.out.println("notafter=" + CommonUtils.getDate(notafter));
        } else {
        	
        }
    }
    
}
