package com.example.adoption_pet.UI.View;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.adoption_pet.R;
import com.example.adoption_pet.utils.Utilities;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private Button signup;
    private TextInputEditText fullName;
    private TextInputEditText email;
    private TextInputEditText password;
    private TextInputLayout tilPassword;
    //
    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;


    //
    // private ProgressDialog pd;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        hook();
        _validateSignUp();
        textChangeWatcher();

    }

    private void textChangeWatcher() {
        fullName.addTextChangedListener(tcw_fullName);
        email.addTextChangedListener(tcw_email);
        password.addTextChangedListener(tcw_password);
    }

    private final TextWatcher tcw_password = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            TextInputLayout til = findViewById(R.id.layout_password);
            if (s.length() == 0) {
                til.setError(getString(R.string.txt_error_validate_password));
                til.requestFocus();
            } else {
                til.setErrorEnabled(false);
            }
        }
    };

    private final TextWatcher tcw_email = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            TextInputLayout til = findViewById(R.id.layout_email);
            if (s.length() == 0) {
                til.setErrorEnabled(true);
                til.setError(getString(R.string.txt_error_validate_email));
                til.requestFocus();
            } else {
                til.setErrorEnabled(false);
            }
        }
    };

    private final TextWatcher tcw_fullName = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            TextInputLayout til = findViewById(R.id.layout_fullname);
            if (s.length() == 0) {
                til.setErrorEnabled(true);
                til.setError(getString(R.string.txt_error_validate_fullname));
                til.requestFocus();
            } else {
                til.setError(null);
            }
        }
    };


    private void _validateSignUp() {
        signup.setOnClickListener(v -> {
            String txtFullName = Objects.requireNonNull(fullName.getText()).toString().trim();
            String txtEmail = Objects.requireNonNull(email.getText()).toString().trim();
            String txtPassword = Objects.requireNonNull(password.getText()).toString().trim();


            if (TextUtils.isEmpty(txtFullName)) {
                TextInputLayout til = findViewById(R.id.layout_fullname);
                til.setError(getString(R.string.txt_error_validate_fullname));
                til.requestFocus();
            } else if (TextUtils.isEmpty(txtEmail)) {
                TextInputLayout til = findViewById(R.id.layout_email);
                til.setError(getString(R.string.txt_error_validate_email));
                til.requestFocus();
            } else if (TextUtils.isEmpty(txtPassword)) {
                TextInputLayout til = (TextInputLayout) findViewById(R.id.layout_password);
                til.setError(getString(R.string.txt_error_validate_password));
                til.requestFocus();
            } else if (txtPassword.length() >= 1 && txtPassword.length() < 7) {
                Toast.makeText(SignUpActivity.this, getString(R.string.txt_toast_shortPass), Toast.LENGTH_SHORT).show();
                tilPassword.setError(getString(R.string.txt_toast_shortPass));
                tilPassword.requestFocus();
            } else if (TextUtils.isEmpty(txtFullName) && TextUtils.isEmpty(txtEmail) && TextUtils.isEmpty(txtPassword)) {
                Toast.makeText(SignUpActivity.this, getString(R.string.txt_toast_empty_registration), Toast.LENGTH_SHORT).show();

                Utilities.setVibrate(this);
                Utilities.setSoundError(this);


            } else {
                signUpUser(txtFullName, txtEmail, txtPassword);
            }


        });

    }

    private void signUpUser(final String fullName, final String email, String password) {
        setProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("fullName", fullName);
            hashMap.put("email", email);
            hashMap.put("city", "default");
            hashMap.put("district", "");
            hashMap.put("latitude", "");
            hashMap.put("longitude", "");
            hashMap.put("phoneNumber", "");
            hashMap.put("userAddress", "");
            hashMap.put("gender", "");
            hashMap.put("userImgId", "");
            hashMap.put("dob", "");

            hashMap.put("id", Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
            mRootRef.child("Users").child(mAuth.getCurrentUser().getUid()).setValue(hashMap).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(fullName).build();
                    Objects.requireNonNull(user).updateProfile(profileUpdates);

                    //Sending verification email to the email of clients
                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, getString(R.string.txt_toast_signUp_successful), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignUpActivity.this,
                                    Objects.requireNonNull(task1.getException()).getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    /// Change Page
                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    Animatoo.animateSlideRight(SignUpActivity.this);
                    Utilities.setSoundSuccess(this);
                    finish();
                }
            });
        }).addOnFailureListener(e -> Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

//    private void VibrateError() {
//        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(400, 50));
//    }

    @SuppressLint("SetTextI18n")
    private void setProgressDialog() {

        int llPadding = 30;
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);


        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(this);
        tvText.setText("Loading ...");
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setView(ll);

        AlertDialog dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(layoutParams);
        }
    }

    private void hook() {
        signup = findViewById(R.id.btn_sign_up);
        fullName = findViewById(R.id.fullName_edit_text);
        email = findViewById(R.id.email_edit_text);
        password = findViewById(R.id.password_edit_text);
        tilPassword = findViewById(R.id.layout_password);

        //App Bar
        progressBar = new ProgressBar(this);

        // Firebase
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

}