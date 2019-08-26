package org.ministryofhealth.healthtalkkit;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

import org.ministryofhealth.healthtalkkit.database.Database;
import org.ministryofhealth.healthtalkkit.model.HealthTalkKit;

public class GrandchildrenContentActivity extends AppCompatActivity {
    WebView webView;
    Database db;
    HealthTalkKit kit;
    String content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grandchildren_content);

        db = new Database(this);

        int kit_id = getIntent().getIntExtra("kit_id", 0);
        kit = db.getKit(kit_id, "grandchild");
        webView = findViewById(R.id.webview);

        getSupportActionBar().setTitle(kit.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        new LoadSubcontentWebView().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.subcontent_menu, menu);
        invalidateOptionsMenu();
        MenuItem item = menu.findItem(R.id.menu_info);
        item.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                super.onBackPressed();
                break;
            case R.id.menu_home:
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
            case R.id.menu_info:
//                showContentDialog(kit.getContent());
                break;
        }
        return true;
    }

    class LoadSubcontentWebView extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            content = kit.getContent();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    WebSettings s = webView.getSettings();
                    s.setUseWideViewPort(false);
                    s.setSupportZoom(true);
                    s.setBuiltInZoomControls(true);
                    s.setDisplayZoomControls(true);
                    s.setJavaScriptEnabled(true);
                    webView.loadData(content, "text/html", "utf-8");
                }
            });
        }
    }
}
