package com.coldwindx.strategy.httpclient;

import com.coldwindx.strategy.HttpClient;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import okhttp3.OkHttpClient;

@Component
public class DefaultSslClient implements HttpClient {

    @Value("${ssl.cert.path}")
    private String certPath;

    private OkHttpClient client;

    @PostConstruct
    public void init() throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        // 1. SSL配置
        TrustManager[] kms = load(certPath);
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, kms, new SecureRandom());
        SSLSocketFactory ssf = context.getSocketFactory();

        // 2. 创建HttpClient
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.sslSocketFactory(ssf, (X509TrustManager) kms[0]);

        client = builder.build();
    }

    protected TrustManager[] load(String cer) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
        // 1. 加载证书文件
        InputStream cerStream = new FileInputStream(cer);

        // 2. 构建证书库
        KeyStore ks = KeyStore.getInstance("PKCS12");

        // 3. 加载证书
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate ca = cf.generateCertificate(cerStream);
        ks.load(null, null);

        // 4. 设置公钥
        ks.setCertificateEntry("server", ca);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);
        return tmf.getTrustManagers();
    }
}
