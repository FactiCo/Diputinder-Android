package mx.com.factico.diputinder;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import mx.com.factico.diputinder.dialogues.Dialogues;

/**
 * Created by zace3d on 18/05/15.
 */
public class WebViewActivity extends AppCompatActivity {
    public static final String TAG_CLASS = WebViewActivity.class.getSimpleName();

    private WebView webView;
    private String url;
    private TextView actionbarTitle;
    private String title;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        setSupportActionBar();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            url = bundle.getString("url", null);
            title = bundle.getString("actionbarTitle", null);

            if (url != null && !url.equals("")) {
                loadWebView(url);
            } else {
                endActivity();
            }
        } else {
            endActivity();
        }
    }

    protected void endActivity() {
        Dialogues.Toast(getBaseContext(), "No se pudo mostrar la página web", Toast.LENGTH_SHORT);
        finish();
    }

    protected void setSupportActionBar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.actionbar);
        mToolbar.setTitle("");
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        mToolbar.getBackground().setAlpha(255);
        actionbarTitle = (TextView) mToolbar.findViewById(R.id.actionbar_title);
        actionbarTitle.setTextColor(getResources().getColor(R.color.colorTextContent));
        mToolbar.setNavigationIcon(R.drawable.ic_close_black);

        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    protected void loadWebView(String url) {
        progressBar = (ProgressBar)findViewById(R.id.webview_progressbar);
        progressBar.setProgress(0);
        progressBar.setVisibility(View.VISIBLE);

        if (title != null && !title.equals(""))
            actionbarTitle.setText(title);
        else
            actionbarTitle.setText("");

        webView = (WebView) findViewById(R.id.webview);

        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new MyWebChromeClient());
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setSupportZoom(true);

        webView.loadUrl(url.endsWith(".pdf") ? "http://docs.google.com/gview?embedded=true&url=" + url : url);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (webView != null) {
            webView.clearCache(true);
            webView.clearHistory();

            webView.stopLoading();
            webView.loadData("", "text/html", "utf-8");
            webView.destroy();
        }
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

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int progress) {
            progressBar.setProgress(progress);
            if(progress == 100) {
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
