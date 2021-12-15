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
import com.example.adoption_pet.UI.View.LoginActivity;
import com.example.adoption_pet.databinding.ActivityFavoritesBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.Objects;


public class FavoritesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityFavoritesBinding binding;
//    private ToolbarBinding toolbarBinding;

    private RoundedImageView imageAvatar;
    private TextView tvEmail;
    private TextView tvName;

    private Toast mToastText;

    private ActionBarDrawerToggle drawerToggle;

    int checkedItem;
    private String selected;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    static final String CHECKDITEM = "check_item";

    private DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        binding = ActivityFavoritesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        hook();

        mToastText = new Toast(getApplicationContext());

        binding.navigationView.setNavigationItemSelectedListener(this);
        binding.navigationView.getMenu().findItem(R.id.itemFavorites).setChecked(true);

        setSupportActionBar(binding.topAppBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerToggle = setupDrawerToggle();

        binding.drawerLayout.addDrawerListener(drawerToggle);

        drawerToggle.syncState();

        //DatabaseReference
        reference = FirebaseDatabase.getInstance().getReference("Users");


//        getToolBar();

        showUserInformation();
    }

    private void showUserInformation() {
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        reference.orderByChild("id").equalTo(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String username = Objects.requireNonNull(ds.child("fullName").getValue()).toString();
                        String userEmail = Objects.requireNonNull(ds.child("email").getValue()).toString();
                        String userAvatar = ds.child("userImgId").getValue(String.class);

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

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(FavoritesActivity.this, binding.drawerLayout, R.string.drawer_open, R.string.drawer_close);
    }

    private void hook() {
        imageAvatar = binding.navigationView.getHeaderView(0).findViewById(R.id.userImg);
        tvEmail = binding.navigationView.getHeaderView(0).findViewById(R.id.tv_userEmail);
        tvName = binding.navigationView.getHeaderView(0).findViewById(R.id.tv_userName);
    }

    private void clickOnNavigation() {
        binding.topAppBar.setNavigationOnClickListener(v -> binding.drawerLayout.open());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemFavorites:
                showToast(getString(R.string.txt_itemmenu_Favorites), R.drawable.itemmenu_favorites);
                break;

            case R.id.itemPets:
                showToast(getString(R.string.txt_itemmenu_Pets), R.drawable.itemmenu_pets);
                Intent intentPets = new Intent(FavoritesActivity.this, PetsActivity.class);
                startActivity(intentPets);
                Animatoo.animateSlideLeft(FavoritesActivity.this);
                break;

            case R.id.itemMyProfile:
                showToast(getString(R.string.txt_itemmenu_myProfile), R.drawable.ic_baseline_notifications_24);
                Intent intentMyProfile = new Intent(FavoritesActivity.this, UserProfileActivity.class);
                startActivity(intentMyProfile);
                Animatoo.animateSlideLeft(FavoritesActivity.this);
                break;
            case R.id.itemMyPets:
                showToast(getString(R.string.txt_itemmenu_MyPets), R.drawable.itemmenu_mypets);
                Intent intentMyPets = new Intent(FavoritesActivity.this, MyPetsActivity.class);
                startActivity(intentMyPets);
                Animatoo.animateSlideLeft(FavoritesActivity.this);
                break;

            case R.id.itemMessages:
                // Toast.makeText(PetsActivity.this, getString(R.string.txt_itemmenu_Messages), Toast.LENGTH_SHORT).show();
                showToast(getString(R.string.txt_itemmenu_Call), R.drawable.ic_baseline_call_24);
                Intent intentPetCall = new Intent(FavoritesActivity.this, PetCallActivity.class);
                startActivity(intentPetCall);
                Animatoo.animateSlideLeft(FavoritesActivity.this);
                break;

            case R.id.itemAboutApp:
                showToast(getString(R.string.txt_itemmenu_AboutApp), R.drawable.ic_baseline_info_24);
                Intent intentAboutApp = new Intent(FavoritesActivity.this, AboutPageActivity.class);
                startActivity(intentAboutApp);
                Animatoo.animateSlideLeft(FavoritesActivity.this);
                break;

            case R.id.itemSetting:
                Toast.makeText(FavoritesActivity.this, getString(R.string.txt_item_dark_mode), Toast.LENGTH_SHORT).show();
                showDialog();
                break;


            case R.id.itemSignOut:
                showToast(getString(R.string.txt_itemmenu_SignOut), R.drawable.ic_baseline_login_24);
                //Toast.makeText(PetsActivity.this, getString(R.string.txt_itemmenu_SignOut), Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(FavoritesActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                Animatoo.animateSlideRight(FavoritesActivity.this);
                FavoritesActivity.this.finish();
                break;

            case R.id.itemChangePassword:
                showToast(getString(R.string.txt_itemmenu_changePassword), R.drawable.ic_baseline_lock);
                Intent intentChangePassword = new Intent(FavoritesActivity.this, ChangePasswordActivity.class);
                startActivity(intentChangePassword);
                Animatoo.animateSlideLeft(FavoritesActivity.this);
                break;

        }
        item.setChecked(true);

        binding.drawerLayout.closeDrawers();
        return true;
    }

    private void showDialog() {
        final String[] themes = getResources().getStringArray(R.array.theme);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(FavoritesActivity.this);
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

}