package jehc.xtmodules.xtcore.httpclient;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

// 自定义私有类
class TrustAnyTrustManager implements X509TrustManager {     
      
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {     
    }     
  
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {     
    }     
  
    public X509Certificate[] getAcceptedIssuers() {     
        return new X509Certificate[]{};     
    }     
} 
