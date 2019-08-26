package org.ministryofhealth.healthtalkkit;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.ministryofhealth.healthtalkkit.database.Database;
import org.ministryofhealth.healthtalkkit.helper.RetrofitHelper;
import org.ministryofhealth.healthtalkkit.model.HealthTalkKit;
import org.ministryofhealth.healthtalkkit.server.service.HealthTalkKitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;

public class SetupActivity extends AppCompatActivity {
    Database db;
    TextView txtProgressText;
    LinearLayout downloadingLayout, completedLayout, errorLayout;
    Button proceedButton;
    Button btnRetry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        db = new Database(this);

        txtProgressText = findViewById(R.id.downloading);
        downloadingLayout = findViewById(R.id.downloadingLayout);
        completedLayout = findViewById(R.id.completedLayout);
        btnRetry = findViewById(R.id.retryBtn);
        errorLayout = findViewById(R.id.errorLayout);
        proceedButton = findViewById(R.id.proceed_button);

        new DownloadContent().execute();

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DownloadContent().execute();
            }
        });

        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoHomePage();
            }
        });
    }

    void gotoHomePage(){
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public class DownloadContent extends AsyncTask<String, String, Boolean>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            downloadingLayout.setVisibility(View.VISIBLE);
            completedLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.GONE);
            txtProgressText.setText("Preparing Download...");
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                Retrofit retrofit = RetrofitHelper.getInstance().createHelper();

                HealthTalkKitService healthTalkKitService = retrofit.create(HealthTalkKitService.class);
                Call<List<HealthTalkKit>> kitCall = healthTalkKitService.getKits();
                Call<List<HealthTalkKit>> subcontentCall = healthTalkKitService.getSubcontent();
                Call<List<HealthTalkKit>> grandchildrenCall = healthTalkKitService.getGrandchildren();

                publishProgress("Downloading Health Talk Kit Titles");
                List<HealthTalkKit> kits = kitCall.execute().body();

                if (kits.size() > 0){
                    db.addHealthTalkKits(kits);
                }

                publishProgress("Downloading Health Talk Kit Subcontent");
                List<HealthTalkKit> subcontent = subcontentCall.execute().body();

                if (subcontent.size() > 0){
                    db.addHealthTalkKitSubcontents(subcontent);
                }

                publishProgress("Downloading Health Talk Kit Grandchildren");
                List<HealthTalkKit> grandchildren = grandchildrenCall.execute().body();

                if (grandchildren.size() > 0){
                    db.addHealthTalkKitGrandchildren(grandchildren);
                }
                return true;
            }catch (Exception ex){
                Log.e("SetupActivity", ex.getMessage());
                ex.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            txtProgressText.setText(values[0]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean){
                downloadingLayout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.GONE);
                completedLayout.setVisibility(View.VISIBLE);
            }else{
                downloadingLayout.setVisibility(View.GONE);
                completedLayout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
            }
        }
    }
}
