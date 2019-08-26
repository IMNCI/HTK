package org.ministryofhealth.healthtalkkit;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;

import org.ministryofhealth.healthtalkkit.adapter.KitAdapter;
import org.ministryofhealth.healthtalkkit.database.Database;
import org.ministryofhealth.healthtalkkit.model.HealthTalkKit;

import java.util.ArrayList;
import java.util.List;

public class GrandchildrenActivity extends AppCompatActivity implements KitAdapter.ItemClick{
    Database db;

    WebView webView;
    RecyclerView recyclerView;

    String content;
    HealthTalkKit kit;
    List<HealthTalkKit> children = new ArrayList<HealthTalkKit>();

    KitAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grandchildren);

        db = new Database(this);
        recyclerView = findViewById(R.id.grandchildren_titles);
        webView = findViewById(R.id.kitContent);

        int id = getIntent().getIntExtra("subcontent_id", 0);
        kit = db.getKit(id, "subcontent");

        getSupportActionBar().setTitle(kit.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (kit.getIs_parent() == 1){
            webView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);
            adapter = new KitAdapter(this);
            recyclerView.setAdapter(adapter);
            children = db.getHealthTalkKitGrandchildren(kit.getId());
            adapter.setKits(children);
        }else{
            webView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            new LoadSubcontentWebView().execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!kit.getContent().equals("<p><br></p>")) {
            getMenuInflater().inflate(R.menu.subcontent_menu, menu);
            if (children.size() == 0){
                invalidateOptionsMenu();
                MenuItem item = menu.findItem(R.id.menu_info);
                item.setVisible(false);
            }
        }
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
                showContentDialog(kit.getContent());
                break;
        }
        return true;
    }

    protected void showContentDialog(String content){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.layout_content);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        WebView webview = dialog.findViewById(R.id.contentWebview);

        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadDataWithBaseURL("", content, "text/html", "UTF-8", "");
        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
    @Override
    public void onClick(HealthTalkKit kit) {
        Intent intent = new Intent(this, GrandchildrenContentActivity.class);
        intent.putExtra("kit_id", kit.getId());
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
