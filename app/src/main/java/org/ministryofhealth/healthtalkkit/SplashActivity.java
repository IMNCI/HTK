package org.ministryofhealth.healthtalkkit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.ministryofhealth.healthtalkkit.database.Database;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Database db = new Database(this);

        Intent intent;
        if(db.getHealthTalkKits().size() > 0){
            intent = new Intent(this, MainActivity.class);
        }else{
            intent = new Intent(this, SetupActivity.class);
        }
        startActivity(intent);
        finish();

    }
}
