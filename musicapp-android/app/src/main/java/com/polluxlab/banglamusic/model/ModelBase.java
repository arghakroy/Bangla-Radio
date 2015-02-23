package com.polluxlab.banglamusic.model;

import android.util.*;
import com.google.gson.Gson;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URI;
import java.util.List;

/**
 * Created by samiron on 1/17/2015.
 */
public abstract class ModelBase implements ModelVerifiable, Serializable {

    protected transient static final String ENDPOINT_URL = "http://128.199.142.142/musicapp/server/web/app_dev.php/webservice/";
    private static DefaultHttpClient httpClient = null;
    protected static Gson gson = new Gson();

    protected static String get(String url){
        HttpGet httpGet = new HttpGet(url);
        return executeRequest(httpGet);
    }

    private static String executeRequest(HttpUriRequest request){
        ensureHttpClient();
        HttpResponse httpResponse = null;
        Log.i("", "============================================");
        for( Header h : request.getAllHeaders()){
            Log.i("WEB-HEADER", String.format("%s -> %s", h.getName(), h.getValue()));
        }
        Log.i("WEB-REQUEST", request.getURI().toString());
        try {
            httpResponse = httpClient.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
            return new String();
        } finally {

        }
        String response = "";
        if( httpResponse.getStatusLine().getStatusCode() == 200 ){
            response = readResponse(httpResponse);
        }
        try {
          httpResponse.getEntity().consumeContent();
        } catch (IOException e) {
          Log.d(ModelBase.class.getName(), "Failed to consume entity", e);
        }
        Log.i("WEB-RESPONSE", response);
        Log.i("", "============================================");
        return response;
    }

    protected static String get(String url, List<Header> headers){
        HttpGet httpGet = new HttpGet(url);
        for(Header h : headers){
            httpGet.addHeader(h);
        }
        return executeRequest(httpGet);
    }

    protected static String post(String Url, String data){
        ensureHttpClient();
        throw new RuntimeException("Not yet implemented");
    }

    private static void ensureHttpClient(){
        if( httpClient == null ) {
            httpClient = new DefaultHttpClient();
        }
    }

    public static void setSharedSecret(String username, String password){
        if ( httpClient != null){
            URI u = URI.create(ENDPOINT_URL);
            AuthScope authscope = new AuthScope(u.getHost(), u.getPort());
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
            CredentialsProvider credentialsProvider = httpClient.getCredentialsProvider();
            credentialsProvider.setCredentials(authscope, credentials);
        }
    }

    private static String readResponse(HttpResponse response){
        try {
            HttpEntity he = response.getEntity();
            if ( he == null)
                return new String();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    he.getContent(), "utf-8"), 8);
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            he.consumeContent();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return new String();
        }
    }

    @Override
    public boolean valid(){ return true; }
}

