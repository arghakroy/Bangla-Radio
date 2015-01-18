package com.polluxlab.banglamusic.model;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by samiron on 1/17/2015.
 */
public abstract class ModelBase {

    private static HttpClient httpClient = null;
    protected static Gson gson = new Gson();

    protected static String get(String url){
        ensureHttpClient();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpGet);
        } catch (IOException e) {
            e.printStackTrace();
            return new String();
        }
        return readResponse(httpResponse);
    }

    protected static String post(String Url, String data){
        ensureHttpClient();
        throw new RuntimeException("Not yet implemented");
    }

    private static void ensureHttpClient(){
        if( httpClient == null )
            httpClient = new DefaultHttpClient();
    }

    private static String readResponse(HttpResponse response){
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent(), "iso-8859-1"), 8);
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return new String();
        }
    }
}
