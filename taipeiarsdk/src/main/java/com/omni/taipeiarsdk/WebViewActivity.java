package com.omni.taipeiarsdk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.omni.taipeiarsdk.tool.TaipeiArSDKText;


public class WebViewActivity extends AppCompatActivity {

    public WebView webView;
    private String weblink;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        findViewById(R.id.activity_web_view_fl_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            weblink = intent.getStringExtra(TaipeiArSDKText.KEY_WEB_LINK);
            title = intent.getStringExtra(TaipeiArSDKText.KEY_WEB_TITLE);
        }

        ((TextView) findViewById(R.id.activity_web_view_fl_action_bar_title)).setText(title);

//        Intent i = new Intent(Intent.ACTION_VIEW);
//        i.setData(Uri.parse(weblink));
//        startActivity(i);
//        finish();

        //Log.e("LOG", "WebViewActivity: " + weblink);


        webView = (WebView) findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        /**
         *  Angular.js support by HTML5 DOM setting ,
         *  is not support by Android WebView in default ,
         *  have to setDomStorageEnable(true), to open it.
         *
         * */
        webView.getSettings().setDomStorageEnabled(true);

        //webView.setWebChromeClient(new WebChromeClient());

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                view.loadUrl(url);

                return true;
            }

            @Override
            public void onPageFinished(WebView view, final String url) {

            }
        });

        webView.loadUrl(weblink);
    }
}
