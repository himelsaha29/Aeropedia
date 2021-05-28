package com.willblaschko.android.alexa.connection;

import android.os.Build;
import android.util.Log;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.CipherSuite;
import okhttp3.ConnectionPool;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;

/**
 * Create a singleton OkHttp client that, hopefully, will someday be able to make sure all connections are valid according to AVS's strict
 * security policy--this will hopefully fix the Connection Reset By Peer issue.
 *
 * Created by willb_000 on 6/26/2016.
 */
public class ClientUtil {

    private static OkHttpClient mClient;
    private static final long CONNECTION_POOL_TIMEOUT_MILLISECONDS = 60 * 60 * 1000;

    public static OkHttpClient getTLS12OkHttpClient(){
        if(mClient == null) {

            ConnectionPool connectionPool = new ConnectionPool(5,
                    CONNECTION_POOL_TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS);
            OkHttpClient.Builder client = new OkHttpClient.Builder().connectTimeout(0, TimeUnit.MILLISECONDS)  // 0 => no timeout.
                    .readTimeout(0, TimeUnit.MILLISECONDS)
                    .connectionPool(connectionPool);

            if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 22) {
                try {

                    TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                    trustManagerFactory.init((KeyStore) null);
                    TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
                    if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                        throw new IllegalStateException("Unexpected default trust managers:"
                                + Arrays.toString(trustManagers));
                    }

                    X509TrustManager trustManager = (X509TrustManager) trustManagers[0];

                    SSLContext sc = SSLContext.getInstance("TLSv1.2");
                    sc.init(null, null, null);

                    String[] enabled = sc.getSocketFactory().getDefaultCipherSuites();
                    String[] supported = sc.getSocketFactory().getSupportedCipherSuites();

                    client.sslSocketFactory(new Tls12SocketFactory(sc.getSocketFactory()), trustManager);

                    ConnectionSpec cs = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                            .tlsVersions(TlsVersion.TLS_1_2)
                            .cipherSuites(CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                                    CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384)
                            .build();

                    List<ConnectionSpec> specs = new ArrayList<>();
                    specs.add(cs);

                    client.connectionSpecs(specs);
                } catch (Exception exc) {
                    Log.e("OkHttpTLSCompat", "Error while setting TLS 1.2", exc);
                }
            }

            mClient = client.build();
        }
        return mClient;
    }

}
