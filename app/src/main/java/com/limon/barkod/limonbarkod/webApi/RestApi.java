package com.limon.barkod.limonbarkod.webApi;

import android.os.Build;

import com.limon.barkod.limonbarkod.customJson.jsonHelper;
import com.limon.barkod.limonbarkod.logger.CustomLogger;

import org.springframework.util.MultiValueMap;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;


/**
 * http post yapmak için kullanılır
 * Created by Cantekin on 28.7.2016.
 */

public class RestApi<T> {
    private String TAG = "RestApi";
    private final String webApiAddress;

    public void setTAG(String TAG) {
        this.TAG = TAG;
    }

    public RestApi(String webApiAddress) {
        this.webApiAddress = webApiAddress;
    }

    private HttpURLConnection getConnection() {
        try {
            URL url = new URL(webApiAddress);
            if (webApiAddress.contains("https")) {
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection = (HttpsURLConnection) OauthHeaders.getHeaders(urlConnection);
                if (Build.VERSION.SDK_INT <= 19) {
                    SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
                    TrustManager[] trustManagers = new TrustManager[]{new TrustManagerManipulator()};
                    sslContext.init(null, trustManagers, new SecureRandom());
                    SSLSocketFactory noSSLv3Factory = new TLSSocketFactory(sslContext.getSocketFactory());
                    urlConnection.setSSLSocketFactory(noSSLv3Factory);
                }
                return urlConnection;
            } else {
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection = (HttpURLConnection) OauthHeaders.getHeaders(urlConnection);
                return urlConnection;

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String Post(T requests) {
        String requestJson = jsonHelper.objectToJson(requests);
        String response = "";
        try {

            HttpURLConnection urlConnection = getConnection();
            urlConnection.setRequestMethod("POST");
            OutputStream os = urlConnection.getOutputStream();
            os.write(requestJson.getBytes("UTF-8"));
            os.close();
            urlConnection.connect();
            if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + urlConnection.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (urlConnection.getInputStream())));

            String row;
            String output = "";
            while ((row = br.readLine()) != null) {
                output += row;
            }
            urlConnection.disconnect();
            response = output;
        } catch (Exception e) {
            CustomLogger.error(TAG, e.getMessage());
            CustomLogger.error(TAG, webApiAddress + "\n" + requestJson);
            e.printStackTrace();
        }
        CustomLogger.info(TAG, response);
        return response;
    }

    public String PostToken(MultiValueMap<String, String> map) {
        String requestJson = jsonHelper.objectToJson(map);
        String response = "";
        try {
            HttpURLConnection urlConnection = getConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("accept", "application/json");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            OutputStream os = urlConnection.getOutputStream();
            String body = "";
            body += "grant_type=" + map.getFirst("grant_type") + "&";
            body += "username=" + map.getFirst("username") + "&";
            body += "password=" + map.getFirst("password");

            os.write(body.getBytes());
            os.close();
            urlConnection.connect();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (urlConnection.getInputStream())));

            String row;
            String output = "";
            while ((row = br.readLine()) != null) {
                output += row;
            }
            urlConnection.disconnect();
            if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + urlConnection.getResponseCode());
            }
            response = output;
        } catch (Exception e) {
            CustomLogger.error(TAG, e.getMessage());
            CustomLogger.error(TAG, webApiAddress + "\n" + requestJson);
            e.printStackTrace();
            response = "{error:400,error_description:\"kullanıcı adı veya şifre hatalı\"}";
        }
        CustomLogger.info(TAG, response);
        return response;
    }


    public String PostFile(File file) {
        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";
        String response = "";
        try {
            HttpURLConnection urlConnection = getConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"Files\"; FileName=\"" + file.getName() + "\"" + lineEnd);

            FileInputStream fStream = new FileInputStream(file);
            // CustomLogger.error(TAG + "fStream.read()", String.valueOf(fStream.read()));
            CustomLogger.error(TAG + "getAbsolutePath", String.valueOf(file.getAbsolutePath()));
            CustomLogger.error(TAG + " file.getName()", String.valueOf(file.getName()));
            CustomLogger.error(TAG + "fStream.available()", String.valueOf(fStream.available()));

            if (fStream == null)
                throw new Exception("Failed : file is null");
            if (file == null)
                throw new Exception("file : file is null");
            if (file.length() == 0)
                throw new Exception("file size: file is null");
            if (fStream.available() < 1)
                throw new Exception("fStream fStream: available");
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int length = -1;
            int k = 0;
            while ((length = fStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
                k++;
            }
            outputStream.flush();
            if (k == 0)
                throw new Exception("dosya okunmadı");
            CustomLogger.error(TAG + "kkk", String.valueOf(k));

            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);


         //   urlConnection.connect();
            if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + urlConnection.getResponseCode());
            }


            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (urlConnection.getInputStream())));

            String row;
            String output = "";
            while ((row = br.readLine()) != null) {
                output += row;
            }
            urlConnection.disconnect();
            CustomLogger.info(TAG, output + "--");
            CustomLogger.info(TAG, outputStream.toString());

            outputStream.close();
            fStream.close();
            response = String.valueOf(urlConnection.getResponseCode());
        } catch (Exception e) {
            CustomLogger.error(TAG, e.getMessage());
            e.printStackTrace();
        }
        CustomLogger.info(TAG, response);
        return response;

    }


}

