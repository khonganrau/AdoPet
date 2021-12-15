package com.example.adoption_pet.UI.Menu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
import com.example.adoption_pet.UI.View.EditProfileActivity;
import com.example.adoption_pet.UI.View.LoginActivity;
import com.example.adoption_pet.databinding.ActivityUserProfileBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;


public class UserProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityUserProfileBinding binding;

    private ActionBarDrawerToggle drawerToggle;

    private Toast mToastText;

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
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        editProfile();

        loadUserInfor();

        hook();

        mToastText = new Toast(getApplicationContext());

        //Navigation Drawer
        binding.navigationView.setNavigationItemSelectedListener(this);
        binding.navigationView.getMenu().findItem(R.id.itemMyProfile).setChecked(true);
        setSupportActionBar(binding.topAppBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerToggle = setupDrawerToggle();
        binding.drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();


    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(UserProfileActivity.this,binding.drawerLayout,R.string.drawer_open,
                R.string.drawer_close);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void hook() {
        imageAvatar = binding.navigationView.getHeaderView(0).findViewById(R.id.userImg);
        tvEmail = binding.navigationView.getHeaderView(0).findViewById(R.id.tv_userEmail);
        tvName = binding.navigationView.getHeaderView(0).findViewById(R.id.tv_userName);
    }


    private void loadUserInfor() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.orderByChild("id").equalTo(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String username = ds.child("fullName").getValue().toString();
                        String userEmail = ds.child("email").getValue().toString();
                        String userAvatar = ds.child("userImgId").getValue(String.class);

                        //Display user profile
                        binding.tvFullname.setText(username);
                        binding.tvUserEmail.setText(userEmail);

                        tvName.setText(username);
                        tvEmail.setText(userEmail);


                        if (userAvatar != null) {
                            byte[] decodeString = Base64.decode(userAvatar, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
                            binding.userImg.setImageBitmap(bitmap);
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

    private void editProfile() {
        binding.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
            Animatoo.animateSlideLeft(UserProfileActivity.this);
        });
    }



    // Light and Dark Theme.
    private void showDialog() {
        final String[] themes = getResources().getStringArray(R.array.theme);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(UserProfileActivity.this);
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

    private int getCheckedItem() {
        return sharedPreferences.getInt(CHECKDITEM, 0);
    }

    private void setCheckedItem(int i) {
        editor.putInt(CHECKDITEM, i);
        editor.apply();
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

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.itemMyProfile:
                showToast(getString(R.string.txt_itemmenu_myProfile),R.drawable.ic_baseline_account_circle);
                break;

            case R.id.itemPets:
                showToast(getString(R.string.txt_itemmenu_Pets),R.drawable.itemmenu_pets);
                startActivity(new Intent(UserProfileActivity.this,PetsActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                Animatoo.animateSlideLeft(UserProfileActivity.this);
                break;

            case R.id.itemFavorites:
                showToast(getString(R.string.txt_itemmenu_Favorites),R.drawable.itemmenu_favorites);
                startActivity(new Intent(UserProfileActivity.this,FavoritesActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                Animatoo.animateSlideLeft(UserProfileActivity.this);
                break;

            case R.id.itemMessages:
                showToast(getString(R.string.txt_itemmenu_Call),R.drawable.ic_baseline_call_24);
                startActivity(new Intent(UserProfileActivity.this, PetCallActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                Animatoo.animateSlideLeft(UserProfileActivity.this);
                break;

            case R.id.itemAboutApp:
                showToast(getString(R.string.txt_itemmenu_AboutApp),R.drawable.itemmenu_aboutapp);
                startActivity(new Intent(UserProfileActivity.this,AboutPageActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                Animatoo.animateSlideLeft(UserProfileActivity.this);
                break;

            case R.id.itemSetting:
                showToast(getString(R.string.txt_item_dark_mode),R.drawable.ic_baseline_dark_mode_setting);
                showDialog();
                break;

            case R.id.itemSignOut:
                showToast(getString(R.string.txt_itemmenu_SignOut),R.drawable.ic_baseline_login_24);
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(UserProfileActivity.this,LoginActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                Animatoo.animateSlideRight(UserProfileActivity.this);
                UserProfileActivity.this.finish();
                break;

            case R.id.itemChangePassword:
                showToast(getString(R.string.txt_itemmenu_changePassword),R.drawable.ic_baseline_lock);
                startActivity(new Intent(UserProfileActivity.this, ChangePasswordActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                Animatoo.animateSlideLeft(UserProfileActivity.this);
                break;

            case R.id.itemMyPets:
                showToast(getString(R.string.txt_itemmenu_MyPets),R.drawable.itemmenu_mypets);
                startActivity(new Intent(UserProfileActivity.this,MyPetsActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                Animatoo.animateSlideLeft(UserProfileActivity.this);
                break;
        }
        item.setChecked(true);
        binding.drawerLayout.closeDrawers();
        return true;
    }
}