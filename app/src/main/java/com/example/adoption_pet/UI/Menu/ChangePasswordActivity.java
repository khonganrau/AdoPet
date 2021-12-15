package com.example.adoption_pet.UI.Menu;

import static com.example.adoption_pet.utils.Constants.AVATAR;
import static com.example.adoption_pet.utils.Constants.EMAIL;
import static com.example.adoption_pet.utils.Constants.FULLNAME;
import static com.example.adoption_pet.utils.Constants.ID;
import static com.example.adoption_pet.utils.Constants.USER_PATH;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.adoption_pet.R;
import com.example.adoption_pet.UI.View.LoginActivity;
import com.example.adoption_pet.databinding.ActivityChangePasswordBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.Objects;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    private Toast mToastText;
    private ActivityChangePasswordBinding binding;
    private ActionBarDrawerToggle drawerToggle;
    int checkedItem;
    private String selected;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    static final String CHECKDITEM = "check_item";
    private RoundedImageView imageAvatar;
    private TextView tvEmail;
    private TextView tvName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        mAuth = FirebaseAuth.getInstance();
        hook();

        validatePassword();

        binding.navigationView.setNavigationItemSelectedListener(this);
        binding.navigationView.getMenu().findItem(R.id.itemChangePassword).setChecked(true);
        setSupportActionBar(binding.topAppBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerToggle = setupDrawerToggle();
        binding.drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        mToastText = new Toast(getApplicationContext());


        //Current password
        binding.currentPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }


            // TODO why <1
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() <= 1) {
                    binding.layoutCurrentPass.setError(getString(R.string.txt_error_currentPassword));
                    binding.layoutCurrentPass.requestFocus();
                } else {
                    binding.layoutCurrentPass.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //New Password
        binding.newPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() <= 1) {
                    binding.layoutNewPass.setError(getString(R.string.txt_error_newPassword));
                    binding.layoutNewPass.requestFocus();
                } else if (s.length() <= 7) {
                    binding.layoutNewPass.setError(getString(R.string.txt_error_newPassword_short));
                    binding.layoutNewPass.requestFocus();
                } else {
                    binding.layoutNewPass.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Confirm passwords
        binding.confirmPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() <= 1) {
                    binding.layoutConfirmPass.setError(getString(R.string.txt_error_confirmPassword));
                    binding.layoutConfirmPass.requestFocus();
                } else {
                    binding.layoutConfirmPass.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        showUserInformation();


    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(ChangePasswordActivity.this,binding.drawerLayout,
                R.string.drawer_open,R.string.drawer_close);
    }


    private void validatePassword() {
        binding.btnUpdate.setOnClickListener(v -> {
            String checkCurrentPassword = Objects.requireNonNull(binding.currentPass.getText()).toString().trim();
            String checkNewPassword = Objects.requireNonNull(binding.newPass.getText()).toString().trim();
            String checkConfirmPassword = Objects.requireNonNull(binding.confirmPass.getText()).toString().trim();


            if (!checkNewPassword.equals(checkConfirmPassword)) {
                binding.layoutConfirmPass.setError(getString(R.string.txt_error_confirmPassword_notmatch));
                showToast(getString(R.string.txt_error_confirmPassword_notmatch),R.drawable.ic_baseline_error);
            }
            if (TextUtils.isEmpty(checkCurrentPassword)) {
                binding.currentPass.setError(getString(R.string.txt_error_currentPassword));
                binding.currentPass.requestFocus();
            }
            if (TextUtils.isEmpty(checkNewPassword)) {
                binding.layoutNewPass.setError(getString(R.string.txt_error_newPassword));
                binding.layoutNewPass.requestFocus();
            }

            if (TextUtils.isEmpty(checkConfirmPassword)) {
                binding.layoutConfirmPass.setError(getString(R.string.txt_error_confirmPassword));
                binding.layoutConfirmPass.requestFocus();
            }
            else if (checkNewPassword.length() <= 7 || checkConfirmPassword.length() <= 7) {
                binding.layoutNewPass.setError(getString(R.string.txt_error_newPassword_short));
                binding.layoutNewPass.requestFocus();
            }

            if (TextUtils.isEmpty(checkCurrentPassword) || TextUtils.isEmpty(checkNewPassword) || TextUtils.isEmpty(checkConfirmPassword)) {
                Toast.makeText(ChangePasswordActivity.this, R.string.txt_blank_required_field, Toast.LENGTH_SHORT).show();

            } else {
                changePassword(checkCurrentPassword, checkConfirmPassword);
            }
        });

    }

    private void changePassword(String currentPassword, String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String email = user.getEmail();
            if (email != null) {
                AuthCredential credential = EmailAuthProvider
                        .getCredential(email, currentPassword);

// Prompt the user to re-provide their sign-in credentials
                user.reauthenticate(credential)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                //update password
                                user.updatePassword(newPassword)
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                showToast(getString(R.string.txt_success_changePassword), R.drawable.ic_baseline_done);
                                                mAuth.signOut();
                                                Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                            }
                                        });
                            } else {
                                showToast(getString(R.string.txt_currentPasswords_notMatch), R.drawable.ic_baseline_error);

                            }

                        });
            }
        } else {
            showToast(getString(R.string.txt_error_login), R.drawable.ic_baseline_error);

        }
    }

    private void showToast(final String message, int resource) {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.toast_layout, findViewById(R.id.toast_layout_root));
        TextView tvMessage = view.findViewById(R.id.tv_message);
        tvMessage.setText(message);
        ImageView toastImage = view.findViewById(R.id.image_toast);
        toastImage.setImageResource(resource);

        mToastText.setDuration(Toast.LENGTH_SHORT);
        mToastText.setView(view);
        mToastText.show();
    }


    @Override
    public void onClick(View v) {
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.itemChangePassword:
                showToast(getString(R.string.txt_itemmenu_changePassword),R.drawable.ic_baseline_lock);
                break;

            case R.id.itemPets:
                showToast(getString(R.string.txt_itemmenu_Pets),R.drawable.itemmenu_pets);
                startActivity(new Intent(ChangePasswordActivity.this,PetsActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                Animatoo.animateSlideLeft(ChangePasswordActivity.this);
                break;

            case R.id.itemFavorites:
                showToast(getString(R.string.txt_itemmenu_Favorites),R.drawable.itemmenu_favorites);
                startActivity(new Intent(ChangePasswordActivity.this,FavoritesActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                break;

            case R.id.itemMessages:
                showToast(getString(R.string.txt_itemmenu_Call),R.drawable.ic_baseline_call_24);
                startActivity(new Intent(ChangePasswordActivity.this, PetCallActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                Animatoo.animateSlideLeft(ChangePasswordActivity.this);
                break;

            case R.id.itemAboutApp:
                showToast(getString(R.string.txt_itemmenu_AboutApp),R.drawable.itemmenu_aboutapp);
                startActivity(new Intent(ChangePasswordActivity.this,AboutPageActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                Animatoo.animateSlideLeft(ChangePasswordActivity.this);
                break;

            case R.id.itemSetting:
                showToast(getString(R.string.txt_item_dark_mode),R.drawable.ic_baseline_dark_mode_setting);
                showDarkModeDialog();
                break;

            case R.id.itemSignOut:
                showToast(getString(R.string.txt_itemmenu_SignOut),R.drawable.ic_baseline_login_24);
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ChangePasswordActivity.this,LoginActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                Animatoo.animateSlideRight(ChangePasswordActivity.this);
                ChangePasswordActivity.this.finish();
                break;

            case R.id.itemMyPets:
                showToast(getString(R.string.txt_itemmenu_MyPets),R.drawable.itemmenu_mypets);
                startActivity(new Intent(ChangePasswordActivity.this, MyPetsActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                Animatoo.animateSlideLeft(ChangePasswordActivity.this);
                break;

            case R.id.itemMyProfile:
                showToast(getString(R.string.txt_itemmenu_myProfile),R.drawable.ic_baseline_account_circle_24);
                startActivity(new Intent(ChangePasswordActivity.this,UserProfileActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                Animatoo.animateSlideLeft(ChangePasswordActivity.this);
                break;
        }
        item.setChecked(true);
        binding.drawerLayout.closeDrawers();
        return true;
    }

    private int getCheckedItem() {
        return sharedPreferences.getInt(CHECKDITEM, 0);
    }

    private void setCheckedItem(int i) {
        editor.putInt(CHECKDITEM, i);
        editor.apply();
    }

    private void showDarkModeDialog() {
        final String[] themes = getResources().getStringArray(R.array.theme);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(ChangePasswordActivity.this);
        builder.setTitle("Select Theme");
        builder.setSingleChoiceItems(R.array.theme, getCheckedItem(), (dialogInterface, i) -> {
            selected = themes[i];
            checkedItem = i;
        });

        builder.setPositiveButton("Oke", (dialogInterface, i) -> {
            if (selected == null) {
                selected = themes[i];
                checkedItem = i;
            }

            switch (selected) {

                case "System Default":
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

                    break;

                case "Dark":
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                    break;
                case "Light":
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    break;
            }

            setCheckedItem(i);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showUserInformation() {
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(USER_PATH);

        reference.orderByChild(ID).equalTo(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String username = Objects.requireNonNull(ds.child(FULLNAME).getValue()).toString();
                        String userEmail = Objects.requireNonNull(ds.child(EMAIL).getValue()).toString();
                        String userAvatar = ds.child(AVATAR).getValue(String.class);

                        //Display user profile
                        tvName.setText(username);
                        tvEmail.setText(userEmail);


                        if (userAvatar != null) {
                            byte[] decodeString = Base64.decode(userAvatar, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
                            imageAvatar.setImageBitmap(bitmap);
                        }


                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void hook(){
        imageAvatar = binding.navigationView.getHeaderView(0).findViewById(R.id.userImg);
        tvEmail = binding.navigationView.getHeaderView(0).findViewById(R.id.tv_userEmail);
        tvName = binding.navigationView.getHeaderView(0).findViewById(R.id.tv_userName);
    }
}