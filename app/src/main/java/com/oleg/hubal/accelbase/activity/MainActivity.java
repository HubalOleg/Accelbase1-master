package com.oleg.hubal.accelbase.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.oleg.hubal.accelbase.R;
import com.oleg.hubal.accelbase.event.FirebaseMessageEvent;
import com.oleg.hubal.accelbase.fragment.AccelerometerFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String FIREBASE_TOPIC = "some_topic";

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
            FirebaseMessaging.getInstance().subscribeToTopic("some_topic");
            AccelerometerFragment accelerometerFragment = new AccelerometerFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.flContainer, accelerometerFragment)
                    .commit();
        }

        Log.d(TAG, "onCreate: " + FirebaseInstanceId.getInstance().getToken());

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
            case R.id.menu_screenshot:
                takeScreenshot();
                return true;
            default:
                return false;
        }
    }

    private void takeScreenshot() {
        try {
            String mPath = Environment.getExternalStorageDirectory().toString() + "/Screenshot_" +  System.currentTimeMillis() + ".jpg";

            View screenView = getWindow().getDecorView().getRootView();
            screenView.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
            screenView.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            openScreenshot(imageFile);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFirebaseMessageEvent(FirebaseMessageEvent event) {
        Log.d(TAG, "onFirebaseMessageEvent: " + event.messageBody);
        Toast.makeText(MainActivity.this, event.messageBody, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(MainActivity.this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(MainActivity.this);
    }
}
