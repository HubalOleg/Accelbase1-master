package com.oleg.hubal.accelbase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.oleg.hubal.accelbase.R;
import com.oleg.hubal.accelbase.utility.Utility;

import static com.oleg.hubal.accelbase.R.id.password;

/**
 * Created by User on 01.11.2016.
 */

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText inputEmail, inputPassword;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();

        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

        btnResetPassword.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_up_button:
                if (Utility.isNetworkConnected(getApplicationContext())) {
                    checkInputAndCreateUser();
                }
                break;
            case R.id.sign_in_button:
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                break;
            case R.id.btn_reset_password:
                startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
                break;
        }
    }

    private void checkInputAndCreateUser() {
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (!Utility.isUserInputValid(email, password, getApplicationContext()))
            return;

        progressBar.setVisibility(View.VISIBLE);

        createUser(email, password);
    }

    private void createUser(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password).
                addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Utility.showToast(SignupActivity.this,
                                "createUserWithEmail:onComplete:" + task.isSuccessful());
                        progressBar.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {
                            Utility.showToast(SignupActivity.this,
                                    "Authentication failed." + task.getException());
                        } else {
                            startActivity(new Intent(SignupActivity.this, MainActivity.class));
                        }
                    }
                });
    }
}
