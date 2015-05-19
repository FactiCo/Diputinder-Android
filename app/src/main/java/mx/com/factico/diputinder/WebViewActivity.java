package mx.com.factico.diputinder;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import mx.com.factico.diputinder.dialogues.Dialogues;

/**
 * Created by zace3d on 18/05/15.
 */
public class WebViewActivity extends ActionBarActivity {
    public static final String TAG_CLASS = WebViewActivity.class.getSimpleName();

    private WebView webView;
    private String url;
    private TextView actionbarTitle;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        setSupportActionBar();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            url = bundle.getString("url", "");
            title = bundle.getString("actionbarTitle", "");

            if (!url.equals("")) {
                //Dialogues.Toast(getBaseContext(), "URL: " + url, Toast.LENGTH_LONG);
                loadWebView(url);
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    protected void setSupportActionBar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.actionbar);
        mToolbar.setTitle("");
        mToolbar.getBackground().setAlpha(255);
        actionbarTitle = (TextView) mToolbar.findViewById(R.id.actionbar_title);

        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    protected void loadWebView(String url) {
        if (title != null && !title.equals(""))
            actionbarTitle.setText(title);
        else
            actionbarTitle.setText(getResources().getString(R.string.app_name));

        webView = (WebView) findViewById(R.id.webview);

        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setSupportZoom(true);
        webView.loadUrl("http://docs.google.com/gview?embedded=true&url=" + url);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        webView.stopLoading();
        webView.loadData("", "text/html", "utf-8");
        webView.destroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    }
}
