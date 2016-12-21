package com.oleg.hubal.accelbase.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.oleg.hubal.accelbase.R;
import com.oleg.hubal.accelbase.utility.Utility;

/**
 * Created by User on 01.11.2016.
 */

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText inputEmail;
    private Button btnReset, btnBack;
    private FirebaseAuth auth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        inputEmail = (EditText) findViewById(R.id.email);
        btnReset = (Button) findViewById(R.id.btn_reset_password);
        btnBack = (Button) findViewById(R.id.btn_back);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();

        btnBack.setOnClickListener(this);
        btnReset.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_reset_password:
                if (Utility.isNetworkConnected(getApplicationContext())) {
                    resetPassword();
                }
                break;
            case R.id.btn_back:
                finish();
                break;
        }
    }

    private void resetPassword() {
        String email = inputEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Utility.showToast(getApplication(), getString(R.string.enter_email_id));
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Utility.showToast(getApplication(), getString(R.string.pass_sent_instruction));
                } else {
                    Utility.showToast(getApplication(), getString(R.string.failed_to_send));
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
