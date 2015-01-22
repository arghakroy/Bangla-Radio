package com.polluxlab.banglamusic;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by ARGHA K ROY on 1/23/2015.
 */
public class LogInWebViewActivity extends Activity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_layout);
        webView= (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setBuiltInZoomControls(true);
        String url=getIntent().getStringExtra("url");
        webView.setWebViewClient(new SSLTolerentWebViewClient());
        if(url!=null)
            webView.loadUrl(url);

    }

     class SSLTolerentWebViewClient extends WebViewClient {

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed(); // Ignore SSL certificate errors
        }

         public boolean shouldOverrideUrlLoading(WebView view, String url) {
             //called for any redirect to stay inside the WebView

             if (url.contains("polluxmusic")) { //checking the URL for scheme required
                 //and sending it within an explicit Intent
                 Intent myapp_intent = new Intent(LogInWebViewActivity.this, UrlHandler.class);
                 myapp_intent.setData(Uri.parse(url));
                 startActivity(myapp_intent);
                 finish();
                 return true; //this might be unnecessary because another Activity
                 //start had already been called
             }
             view.loadUrl(url); //handling non-customschemed redirects inside the WebView
             return false; // then it is not handled by default action
         }
    }
}
