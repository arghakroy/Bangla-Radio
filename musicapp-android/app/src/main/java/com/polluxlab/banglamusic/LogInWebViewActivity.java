package com.polluxlab.banglamusic;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.polluxlab.banglamusic.util.AppConstant;
import com.polluxlab.banglamusic.util.Util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ARGHA K ROY on 1/23/2015.
 */
public class LogInWebViewActivity extends Activity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_layout);
        centerActionBarTitle();

        webView= (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setBuiltInZoomControls(false);
        String url=getIntent().getStringExtra("url");
        webView.setWebViewClient(new SSLTolerentWebViewClient());
        if(url!=null){
            Map<String, String> headers = new HashMap<String, String>();
            headers.put(Util.getSecretKey(this), Util.getSecretKey(this));
            webView.loadUrl(url,headers);
        }

    }

     class SSLTolerentWebViewClient extends WebViewClient {

         ProgressDialog pDialog=new ProgressDialog(LogInWebViewActivity.this);

         @Override
         public void onPageStarted(WebView view, String url, Bitmap favicon) {
             super.onPageStarted(view, url, favicon);
             if(!pDialog.isShowing()) {
                 LayoutInflater inflater = getLayoutInflater();
                 View layout = inflater.inflate(R.layout.progress_layout,null);
                 pDialog.setIndeterminate(true);
                 pDialog.setCancelable(true);
                 pDialog.show();
                 pDialog.setContentView(layout);
             }
         }

         @Override
         public void onPageFinished(WebView view, String url) {
             super.onPageFinished(view, url);
             pDialog.dismiss();
         }

         @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed(); // Ignore SSL certificate errors
        }

         @Override
         public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
             super.onReceivedHttpAuthRequest(view, handler, host, realm);
             String secret=Util.getSecretKey(LogInWebViewActivity.this);
             handler.proceed(secret, secret);
         }

         public boolean shouldOverrideUrlLoading(WebView view, String url) {
             //called for any redirect to stay inside the WebView

             if (url.contains("polluxmusic")) { //checking the URL for scheme required
                 //and sending it within an explicit Intent
                 Log.d(AppConstant.DEBUG,url);
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

    private void centerActionBarTitle()
    {

        int titleId = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            titleId = getResources().getIdentifier("action_bar_title", "id", "android");
        }
        else
        {
            // This is the id is from your app's generated R class when ActionBarActivity is used
            // for SupportActionBar
            titleId = R.id.action_bar_title;
        }

        // Final check for non-zero invalid id
        if (titleId > 0)
        {
            TextView titleTextView = (TextView) findViewById(titleId);
            titleTextView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/solaiman_bold.ttf"));

            DisplayMetrics metrics = getResources().getDisplayMetrics();

            // Fetch layout parameters of titleTextView (LinearLayout.LayoutParams : Info from HierarchyViewer)
            LinearLayout.LayoutParams txvPars = (LinearLayout.LayoutParams) titleTextView.getLayoutParams();
            txvPars.gravity = Gravity.LEFT;
            txvPars.width = metrics.widthPixels;
            txvPars.leftMargin=Util.getDipValue(this,5);
            titleTextView.setLayoutParams(txvPars);

            titleTextView.setGravity(Gravity.LEFT);
        }
    }
}
