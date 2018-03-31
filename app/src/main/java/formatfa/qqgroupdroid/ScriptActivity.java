package formatfa.qqgroupdroid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ClientCertRequest;
import android.webkit.ConsoleMessage;
import android.webkit.HttpAuthHandler;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import FQQSdk.FGroup;
import formatfa.qqgroupdroid.Tool.PathTool;

public class ScriptActivity extends AppCompatActivity {


    public class getGroupInterface
    {
        private FGroup group;
        private String aimNumber;

        public getGroupInterface(FGroup group, String aimNumber) {
            this.group = group;
            this.aimNumber = aimNumber;
        }
        @JavascriptInterface
        public String getAll()
        {
            try {
                return  all;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @JavascriptInterface
        public String  getGroup()
        {
            try {
              return  agroup;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        public getGroupInterface(FGroup group) {
            this.group = group;
        }
    }
    private WebView webView;
    private File dir;
    private PathTool pathTool;

    private String type,path,number;
    private MyApplication application;

    private File indexPath;

    private String all ;
    private String agroup;
    String aimNum;
    OutputStream logOutpusStream;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_script);
        Intent intent = getIntent();

       path = intent.getStringExtra("path");

        type = intent.getStringExtra("path");
        number = intent.getStringExtra("number");
        dir = new File(path);



        initView();
        new loadTask().execute();
    }

    class loadTask extends AsyncTask
    {


        ProgressDialog pd ;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(ScriptActivity.this,"haha","test");

        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                all =  pathTool.parseGroupList(application.getGroup().getGroupList()).toString();
                agroup = pathTool.parseGroupItem( application.getGroup().getGroupByNumber(number)).toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            pd.dismiss();
        }
    }
    private void initView() {

        application =(MyApplication)getApplication();
        webView = findViewById(R.id.webview);
        pathTool = new PathTool();

        indexPath =new File(dir,"index.html");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new getGroupInterface(application.getGroup(),number) ,"gege");

        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                try {
                    logOutpusStream.write(consoleMessage.message().getBytes());
                    logOutpusStream.write("\n".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return super.onConsoleMessage(consoleMessage);

            }


            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                return super.onJsConfirm(view, url, message, result);
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }
        });
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }









        });

        try {
            logOutpusStream = new FileOutputStream(new File(pathTool.getWorkPath(), "console.log"));
        }
        catch (FileNotFoundException e){
           e.printStackTrace();
        }
        webView.loadUrl("file://"+indexPath.getAbsolutePath());
    }
}
