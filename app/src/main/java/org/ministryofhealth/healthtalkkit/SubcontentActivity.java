package org.ministryofhealth.healthtalkkit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import org.ministryofhealth.healthtalkkit.adapter.KitAdapter;
import org.ministryofhealth.healthtalkkit.database.Database;
import org.ministryofhealth.healthtalkkit.model.HealthTalkKit;

import java.util.List;

public class SubcontentActivity extends AppCompatActivity implements KitAdapter.ItemClick{
    Database db;

    WebView webView;
    RecyclerView recyclerView;

    String content;
    HealthTalkKit kit;

    KitAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subcontent);

        db = new Database(this);
        recyclerView = findViewById(R.id.subcontent_titles);
        webView = findViewById(R.id.kitContent);
        int id = getIntent().getIntExtra("kit_id", 0);
        kit = db.getKit(id, "kit");

        getSupportActionBar().setTitle(kit.getTitle());

        if (kit.getIs_parent() == 1){
            webView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);
            adapter = new KitAdapter(this);
            recyclerView.setAdapter(adapter);
            List<HealthTalkKit> kits = db.getHealthTalkKitSubcontent(kit.getId());
            adapter.setKits(kits);
        }else{
            webView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            new LoadSubcontentWebView().execute();
        }
    }

    @Override
    public void onClick(HealthTalkKit kit) {
        Intent intent = new Intent(this, GrandchildrenActivity.class);
        intent.putExtra("subcontent_id", kit.getId());
        startActivity(intent);
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
