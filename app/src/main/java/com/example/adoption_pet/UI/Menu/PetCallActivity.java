package com.example.adoption_pet.UI.Menu;

import static com.example.adoption_pet.utils.Constants.CODEQR;
import static com.example.adoption_pet.utils.Constants.INTENT_CONTENT_KEY;
import static com.example.adoption_pet.utils.Constants.INTENT_MAIL_KEY;
import static com.example.adoption_pet.utils.Constants.INTENT_SUB_KEY;
import static com.example.adoption_pet.utils.Constants.PET;
import static com.example.adoption_pet.utils.Constants.PETID;
import static com.example.adoption_pet.utils.Constants.USER_ID;
import static com.example.adoption_pet.utils.Constants.USER_PATH;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import com.example.adoption_pet.UI.Widget.qrCode.QrCodeActivity;
import com.example.adoption_pet.databinding.ActivityPetCallBinding;
import com.example.adoption_pet.model.Pet;
import com.example.adoption_pet.model.User;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.URL;
import java.util.Objects;

public class PetCallActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    URL serverURL;

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

    private ActivityPetCallBinding binding;

    private String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityPetCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        hook();
        checkURL();

        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        mToastText = new Toast(getApplicationContext());

        binding.navigationView.setNavigationItemSelectedListener(this);
        binding.navigationView.getMenu().findItem(R.id.itemMessages).setChecked(true);

        setSupportActionBar(binding.topAppBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerToggle = setupDrawerToggle();

        binding.drawerLayout.addDrawerListener(drawerToggle);

        drawerToggle.syncState();

        //DatabaseReference
        reference = FirebaseDatabase.getInstance().getReference(USER_PATH);

        showUserInformation();

        binding.qrScanImgCheck.setOnClickListener(v -> {
            Intent intent = new Intent(PetCallActivity.this, QrCodeActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            Animatoo.animateSlideLeft(PetCallActivity.this);
        });

        getCode();

    }

    private void getCode() {
        Intent intent = getIntent();
        if (intent != null) {
            String code = intent.getStringExtra(CODEQR);
            binding.edtCode.setText(code);
            DatabaseReference petRef = FirebaseDatabase.getInstance().getReference(PET);
            petRef.orderByChild(PETID).equalTo(code).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String petUserId;
                    if (snapshot.exists()) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Pet mPet = ds.getValue(Pet.class);

                            petUserId = mPet.getUserId();

                            joinCall(petUserId, code);

                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }


    private void joinCall(String id, String code) {
        binding.btnJoin.setOnClickListener(v -> {

            DatabaseReference petRef = FirebaseDatabase.getInstance().getReference(PET);
            petRef.orderByChild(PETID).equalTo(binding.edtCode.getText().toString()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        for (DataSnapshot ds : snapshot.getChildren()){
                            Pet cPet = ds.getValue(Pet.class);

                            String oId = cPet.getUserId();

                            String currentId = FirebaseAuth.getInstance().getUid();

                            if(!oId.equals(currentId)){
                                JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                                        .setRoom(binding.edtCode.getText().toString())
                                        .setWelcomePageEnabled(false)
                                        .build();
                                JitsiMeetActivity.launch(PetCallActivity.this, options);

                                reference.orderByChild(USER_ID).equalTo(id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            for (DataSnapshot ds : snapshot.getChildren()) {
                                                User mUser = ds.getValue(User.class);
                                                String userEmail = mUser.getEmail();
                                                Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse(INTENT_MAIL_KEY + userEmail + INTENT_SUB_KEY + getString(R.string.txt_call_subject)
                                                        + INTENT_CONTENT_KEY + getString(R.string.txt_call_content) + code));
                                                try {
                                                    startActivity(Intent.createChooser(i, getString(R.string.txt_title_email_intent)));
                                                } catch (android.content.ActivityNotFoundException ex) {
                                                    Toast.makeText(PetCallActivity.this, getString(R.string.txt_error_intent_email), Toast.LENGTH_SHORT).show();
                                                }
                                            }


                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }else {
                                showToast(getString(R.string.txt_error_call),R.drawable.ic_baseline_error);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        });
    }

    private void showUserInformation() {

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
        return new ActionBarDrawerToggle(PetCallActivity.this, binding.drawerLayout, R.string.drawer_open, R.string.drawer_close);
    }

    private void checkURL() {
        try {
            serverURL = new URL("https://meet.jit.si");
            JitsiMeetConferenceOptions defaultOptions = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(serverURL)
                    .setWelcomePageEnabled(false)
                    .build();

            JitsiMeet.setDefaultConferenceOptions(defaultOptions);

        } catch (Exception e) {
            e.printStackTrace();
        }
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
            case R.id.itemMessages:
                showToast(getString(R.string.txt_itemmenu_Messages), R.drawable.ic_baseline_account_circle_24);
                break;

            case R.id.itemPets:
                showToast(getString(R.string.txt_itemmenu_Pets), R.drawable.itemmenu_pets);
                Intent intentPets = new Intent(PetCallActivity.this, PetsActivity.class);
                startActivity(intentPets);
                Animatoo.animateSlideLeft(PetCallActivity.this);
                break;

            case R.id.itemMyProfile:
                showToast(getString(R.string.txt_itemmenu_Favorites), R.drawable.ic_baseline_notifications_24);
                Intent intentFavorites = new Intent(PetCallActivity.this, UserProfileActivity.class);
                startActivity(intentFavorites);
                Animatoo.animateSlideLeft(PetCallActivity.this);
                break;
            case R.id.itemMyPets:
                //  Toast.makeText(PetsActivity.this, getString(R.string.txt_itemmenu_MyPets), Toast.LENGTH_SHORT).show();
                Intent intentMyPets = new Intent(PetCallActivity.this, MyPetsActivity.class);
                startActivity(intentMyPets);
                Animatoo.animateSlideLeft(PetCallActivity.this);
                break;

            case R.id.itemFavorites:
                // Toast.makeText(PetsActivity.this, getString(R.string.txt_itemmenu_Messages), Toast.LENGTH_SHORT).show();
                showToast(getString(R.string.txt_itemmenu_Messages), R.drawable.ic_baseline_message_24);
                Intent intentPetCall = new Intent(PetCallActivity.this, FavoritesActivity.class);
                startActivity(intentPetCall);
                Animatoo.animateSlideLeft(PetCallActivity.this);
                break;

            case R.id.itemAboutApp:
                showToast(getString(R.string.txt_itemmenu_AboutApp), R.drawable.ic_baseline_info_24);
                Intent intentAboutApp = new Intent(PetCallActivity.this, AboutPageActivity.class);
                startActivity(intentAboutApp);
                Animatoo.animateSlideLeft(PetCallActivity.this);
                break;

            case R.id.itemSetting:
                Toast.makeText(PetCallActivity.this, getString(R.string.txt_item_dark_mode), Toast.LENGTH_SHORT).show();
                showDialog();
                break;


            case R.id.itemSignOut:
                showToast(getString(R.string.txt_itemmenu_SignOut), R.drawable.ic_baseline_login_24);
                //Toast.makeText(PetsActivity.this, getString(R.string.txt_itemmenu_SignOut), Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(PetCallActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                Animatoo.animateSlideRight(PetCallActivity.this);
                PetCallActivity.this.finish();
                break;

            case R.id.itemChangePassword:
                showToast(getString(R.string.txt_itemmenu_changePassword), R.drawable.ic_baseline_lock);
                Intent intentChangePassword = new Intent(PetCallActivity.this, ChangePasswordActivity.class);
                startActivity(intentChangePassword);
                Animatoo.animateSlideLeft(PetCallActivity.this);
                break;

        }
        item.setChecked(true);

        binding.drawerLayout.closeDrawers();
        return true;
    }

    private void showDialog() {
        final String[] themes = getResources().getStringArray(R.array.theme);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(PetCallActivity.this);
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
