package com.mpwork.monitor.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.mframework.utils.MResult;

/**
 * URL访问测试工具（URL/SSL）
 * @author dbb
 *
 */
public class UrlValidator {
	
	// 检测URL是否可访问
	public static MResult checkUrlAccessible(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            int responseCode = connection.getResponseCode();
            
            // responseCode == HttpURLConnection.HTTP_OK
        	MResult mres = MResult.buildOK();
        	mres.putParam("httpcode", responseCode);
        	return mres;
        } catch (IOException e) {
        	e.printStackTrace();
        	return MResult.build_A00001(e.getMessage());
        }
    }

	// 测试SSL证书状态
    public static MResult checkSSLCertificate(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, getTrustManager(), null);
            connection.setSSLSocketFactory(sslContext.getSocketFactory());
            connection.connect();
            Certificate[] certificates = connection.getServerCertificates();
            if (certificates.length > 0 && certificates[0] instanceof X509Certificate) {
                X509Certificate x509Certificate = (X509Certificate) certificates[0];
                x509Certificate.checkValidity(); // Throws CertificateExpiredException or CertificateNotYetValidException if expired
                
                // 返回证书正常，附带证书时间范围
                MResult mres = MResult.buildOK();
                mres.putParam("notbefore", x509Certificate.getNotBefore());
                mres.putParam("notafter", x509Certificate.getNotAfter());
                return mres;
            }
            
            System.out.println("checkSSLCertificate FAIL!!! certificates.length=" + certificates.length + " certificates=" + certificates);
            if(certificates.length>0)
            	System.out.println("checkSSLCertificate FAIL!!! certificates[0]=" + certificates[0]);
        } catch (IOException e) {
        	e.printStackTrace();
            return MResult.build_A00001(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return MResult.build_A00001(e.getMessage());
        }
        
        return MResult.build_A00005("INVALID CERT!");
    }

    private static TrustManager[] getTrustManager() {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};
        return trustAllCerts;
    }
}
