package com.oleg.hubal.accelbase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.FirebaseAuth;
import com.oleg.hubal.accelbase.R;
import com.oleg.hubal.accelbase.fragment.AccelerometerFragment;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(getApplication(), SignupActivity.class));
            finish();
        }

        if (savedInstanceState == null) {
            AccelerometerFragment accelerometerFragment = new AccelerometerFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.flContainer, accelerometerFragment)
                    .commit();
        }

//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.flContainer, new DiagramFragment())
//                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sign_out:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, SignupActivity.class));
                return true;
            case R.id.menu_history:
                startActivity(new Intent(this, HistoryActivity.class));
                return true;
            default:
                return false;
        }
    }
}
