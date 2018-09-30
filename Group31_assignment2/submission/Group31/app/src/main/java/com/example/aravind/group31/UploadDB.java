package com.example.aravind.group31;

/*
This module is to upload DB from SD Card to the server
*/
import java.io.FileNotFoundException;
import java.io.File;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import android.os.AsyncTask;
import android.util.Log;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import com.example.aravind.group31.properties.Constants;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cz.msebera.android.httpclient.conn.ssl.SSLSocketFactory;


public class UploadDB extends AsyncTask<String, Void, String> {

    private String filePath;
    UploadDB(String path1) {
        filePath = path1;
    }

    @Override
    protected String doInBackground(String... params1) {
        String url = "http://impact.asu.edu/CSE535Spring18Folder/UploadToServer.php";
        RequestParams params = new RequestParams();

        try {
            params.put("uploaded_file", Constants.DB_NAME);
            params.put("name", "uploaded_file");
            params.put("filename", Constants.DB_NAME);
            params.put("uploaded_file", new File(filePath));
            Log.i("Params",params.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {

            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(30 * 1000);
            //client.setSSLSocketFactory(
             //      new SSLSocketFactory(getSslContext(), SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER));
            client.post(url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
                    Log.i("Upload", "Successful");
                }

                @Override
                public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                    Log.i("Upload", "failed");
                }

                @Override
                public void onProgress(long bytesWritten, long totalSize) {
                }

                public boolean getUseSynchronousMode() {
                    return false;
                }

            });


        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public SSLContext getSslContext() {

        TrustManager[] dummyTrustManagers = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }
        } };

        SSLContext sslContext=null;
        try {
            sslContext = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sslContext.init(null, dummyTrustManagers, new java.security.SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return sslContext;
    }
}