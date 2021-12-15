package com.example.adoption_pet.UI.View;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.adoption_pet.R;
import com.example.adoption_pet.UI.Menu.PetsActivity;
import com.example.adoption_pet.UI.Widget.WifiBroadcastReceiver;
import com.example.adoption_pet.utils.Utilities;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;
import java.util.concurrent.Executor;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE = 1207;
    private Button login;
    private Button signUp;
    private Button btnManage;
    private ImageView fingerprintBio;
    private ImageView imageLogo;
    private TextInputEditText email;
    private TextInputEditText password;
    private TextInputLayout tilPassword;
    private TextView forgotPassword;
    private ProgressBar progressBar;
    private Toast mToastText;

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private SharedPreferences sharedPreferences;

    private WifiBroadcastReceiver wifiBroadcastReceiver;

    private View view;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        wifiBroadcastReceiver = new WifiBroadcastReceiver();

        hook();
        _validateLogin();
        _signUpPage();
        _buildForgotPassword();
        _fingerprint();
        _loginPrompt();


    }


    private void _loginPrompt() {
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(LoginActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                showToast("Authentication error: " + errString, R.drawable.ic_baseline_error);
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                String email = sharedPreferences.getString("email", "");
                String password = sharedPreferences.getString("password", "");
                loginUser(email, password);

                showToast("Authentication succeeded!", R.drawable.ic_baseline_done);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                showToast("Authentication failed", R.drawable.ic_baseline_error);
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for my app")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Cancel")
                .build();

        // Prompt appears when user clicks "Log in".
        // Consider integrating with the keystore to unlock cryptographic operations,
        // if needed by your app.
        fingerprintBio.setOnClickListener(view -> biometricPrompt.authenticate(promptInfo));
    }

    private void _fingerprint() {
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BIOMETRIC_STRONG | DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                showToast(getString(R.string.toast_finger_no_exist), R.drawable.ic_baseline_error);
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                showToast(getString(R.string.toast_biometic_unavailable), R.drawable.ic_baseline_error);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // Prompts the user to create credentials that your app accepts.
                final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
                startActivityForResult(enrollIntent, REQUEST_CODE);
                break;
            case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
                showToast(getString(R.string.txt_biometric_error),R.drawable.ic_baseline_error);
                break;
            case BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED:
                showToast(getString(R.string.txt_biometric_error),R.drawable.ic_baseline_error);
                break;
            case BiometricManager.BIOMETRIC_STATUS_UNKNOWN:
                showToast(getString(R.string.txt_biometric_error),R.drawable.ic_baseline_error);
                break;
        }


    }

    private void _buildForgotPassword() {
        forgotPassword.setOnClickListener(v -> {
            EditText resetMail = new EditText(v.getContext());
            MaterialAlertDialogBuilder passwordResetDialog = new MaterialAlertDialogBuilder(v.getContext());

            passwordResetDialog.setTitle(getString(R.string.txt_reset_password_dialogs));
            passwordResetDialog.setMessage(getString(R.string.txt_reset_enter_email_dialogs));
            passwordResetDialog.setView(resetMail);

            passwordResetDialog.setPositiveButton(getString(R.string.txt_accept), (dialog, which) -> {
                String mail = resetMail.getText().toString().trim();
                mAuth.sendPasswordResetEmail(mail)
                        .addOnCompleteListener(task -> Toast.makeText(LoginActivity.this,
                                getString(R.string.txt_toast_resetPassword), Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(LoginActivity.this,
                                getString(R.string.txt_toast_error_resetpassword) + " " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }).setNegativeButton(getString(R.string.txt_cancel), (dialog, which) -> {
            });
            passwordResetDialog.create().show();

        });
    }

    private void _signUpPage() {
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                Animatoo.animateSlideLeft(LoginActivity.this);
                Utilities.setVibrate(LoginActivity.this);
            }
        });
    }

    private void _validateLogin() {

        login.setOnClickListener(v -> {

            String adminLogin = "admin";
            String adminPassword = "admin";


            String emailCheck = Objects.requireNonNull(email.getText()).toString().trim();
            String passwordCheck = Objects.requireNonNull(password.getText()).toString().trim();

            if (TextUtils.isEmpty(emailCheck)) {
                TextInputLayout til = findViewById(R.id.layout_username);
                til.setError(getString(R.string.txt_error_validate_email));
                til.requestFocus();
            } else if (TextUtils.isEmpty(passwordCheck)) {
                tilPassword.setError(getString(R.string.txt_error_validate_password));
                tilPassword.requestFocus();
            } else if (TextUtils.isEmpty(emailCheck) || TextUtils.isEmpty(passwordCheck)) {
                showToast(getString(R.string.empty_credentials_tv), R.drawable.ic_baseline_error);
                Utilities.setSoundError(LoginActivity.this);
                Utilities.setVibrate(this);
            } else if (emailCheck.equals(adminLogin) && passwordCheck.equals(adminPassword)) {
                btnManage.setVisibility(View.VISIBLE);
                btnManage.setOnClickListener(this);
            } else
                loginUser(emailCheck, passwordCheck);

        });

    }

    private void loginUser(String emailCheck, String passwordCheck) {

        mAuth.signInWithEmailAndPassword(emailCheck, passwordCheck).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                editor.putString("email", emailCheck);
                editor.putString("password", passwordCheck);
                if (Objects.requireNonNull(mAuth.getCurrentUser()).isEmailVerified()) {
                    /// Change pages
                    Utilities.setSoundSuccess(LoginActivity.this);
                    Utilities.setVibrate(this);
                    Intent intent = new Intent(LoginActivity.this, PetsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    editor.apply();
                    finish();
                } else {
                    Utilities.setVibrate(this);
                    Utilities.setSoundError(LoginActivity.this);
                    showToast(getString(R.string.txt_toast_verifyEmail), R.drawable.ic_baseline_error);
                    Toast.makeText(LoginActivity.this, getString(R.string.txt_toast_verifyEmail), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    private void showToast(final String message, int resource) {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.toast_layout, findViewById(R.id.toast_layout_root));
        TextView tvMessage = view.findViewById(R.id.tv_message);
        tvMessage.setText(message);
        ImageView toastImage = view.findViewById(R.id.image_toast);
        toastImage.setImageResource(resource);

        // mToastText.cancel();
        // mToastText.setGravity(Gravity.BOTTOM, 32, 32);
        mToastText.setDuration(Toast.LENGTH_SHORT);
        mToastText.setView(view);
        mToastText.show();
    }


    private void hook() {
        email = findViewById(R.id.userName_edit_text);
        password = findViewById(R.id.password_edit_text);
        login = findViewById(R.id.btn_login);
        signUp = findViewById(R.id.btn_sign_up);
        tilPassword = findViewById(R.id.layout_password);
        forgotPassword = findViewById(R.id.tv_forgot_password);
        fingerprintBio = findViewById(R.id.iv_fingerprint);
        imageLogo = findViewById(R.id.img_logo);
        btnManage = findViewById(R.id.btnManage);
        view = findViewById(R.id.main_layout_id);


//        mToastText = Toast.makeText(LoginActivity.this, "", Toast.LENGTH_SHORT);

        mToastText = new Toast(getApplicationContext());

        mAuth = FirebaseAuth.getInstance();


        progressBar = new ProgressBar(this);

        sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);

    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(wifiBroadcastReceiver, intentFilter);

    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(wifiBroadcastReceiver);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnManage) {
            Utilities.showSnackBar(view, "manage");
            Intent intent = new Intent(LoginActivity.this, WebViewActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            Animatoo.animateSlideLeft(LoginActivity.this);
        }
    }
}