package es.cice.practicapedrogonzalez;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebsiteActivity extends AppCompatActivity {
    private final static String TAG = "WebsiteActivity";
    private WebView browser;
    String website;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_website);

        browser=(WebView)findViewById(R.id.webViewSite);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.getSettings().setBuiltInZoomControls(true);
        browser.setWebViewClient(new WebViewClient());

        website = getIntent().getExtras().getString("WebSite");
        browser.loadUrl(website);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e(TAG, "onOptionsItemSelected()");

        if(item.getItemId() == android.R.id.home){
            this.finish();
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }

}
