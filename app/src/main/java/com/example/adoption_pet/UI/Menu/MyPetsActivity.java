package com.example.adoption_pet.UI.Menu;

import static com.example.adoption_pet.utils.Constants.KEY_INTENT;
import static com.example.adoption_pet.utils.Constants.KEY_PROFILE;
import static com.example.adoption_pet.utils.Constants.PET;
import static com.example.adoption_pet.utils.Constants.PETID;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.adoption_pet.Adapter.NewPetAdapter;
import com.example.adoption_pet.R;
import com.example.adoption_pet.UI.View.CreatePetActivity;
import com.example.adoption_pet.UI.View.EditPetProfileActivity;
import com.example.adoption_pet.UI.View.LoginActivity;
import com.example.adoption_pet.UI.View.PetProfileActivity;
import com.example.adoption_pet.databinding.ActivityMypetsBinding;
import com.example.adoption_pet.model.Pet;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.irozon.alertview.AlertActionStyle;
import com.irozon.alertview.AlertStyle;
import com.irozon.alertview.AlertView;
import com.irozon.alertview.objects.AlertAction;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Objects;

public class MyPetsActivity extends AppCompatActivity implements NewPetAdapter.NewPetClickListener,
NavigationView.OnNavigationItemSelectedListener{

    private ActivityMypetsBinding binding;

    //Toast
    private Toast mToastText;

    private ArrayList<Pet> pets;

    private NewPetAdapter newPetAdapter;

    private RoundedImageView imageAvatar;
    private TextView tvEmail;
    private TextView tvName;

    private ActionBarDrawerToggle drawerToggle;

    int checkedItem;
    private String selected;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    static final String CHECKDITEM = "check_item";

    private DatabaseReference reference;

    private KProgressHUD loadingProgress;

    private AlertView alertView;

    private Pet mPet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        binding = ActivityMypetsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mToastText = new Toast(getApplicationContext());

        showPetList();

        createPet();

        hook();

        mToastText = new Toast(getApplicationContext());

        //DatabaseReference
        reference = FirebaseDatabase.getInstance().getReference("Users");

        showUserInformation();

        showLoadingDialog();

        binding.navigationView.setNavigationItemSelectedListener(this);
        binding.navigationView.getMenu().findItem(R.id.itemMyPets).setChecked(true);
        setSupportActionBar(binding.topAppBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerToggle = setupDrawerToggle();
        binding.drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();



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
        return new ActionBarDrawerToggle(MyPetsActivity.this,binding.drawerLayout,
                R.string.drawer_open,R.string.drawer_close);
    }

    private void clickOnNavigation(){
        binding.topAppBar.setNavigationOnClickListener(v -> binding.drawerLayout.open());
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

    private void hook() {
        imageAvatar = binding.navigationView.getHeaderView(0).findViewById(R.id.userImg);
        tvEmail = binding.navigationView.getHeaderView(0).findViewById(R.id.tv_userEmail);
        tvName = binding.navigationView.getHeaderView(0).findViewById(R.id.tv_userName);
    }


    private void showDialog() {
        final String[] themes = getResources().getStringArray(R.array.theme);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MyPetsActivity.this);
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

    private void showPetList() {

        binding.petList.setHasFixedSize(true);

        pets = new ArrayList<>();

        newPetAdapter = new NewPetAdapter(MyPetsActivity.this, pets,this);



        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Pets");

        //Get user ID
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        dbRef.orderByChild("userId").equalTo(uid).addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Pet mPet = dataSnapshot.getValue(Pet.class);
                        pets.add(mPet);

                    }
                    newPetAdapter.notifyDataSetChanged();
                    binding.petList.setAdapter(newPetAdapter);
                    binding.tvNoti.setVisibility(View.GONE);

                    scheduleDismiss();
                } else {
                    scheduleDismiss();
                    binding.tvNoti.setVisibility(View.VISIBLE);
                    binding.petList.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void createPet() {
        binding.btnCreatePet.setOnClickListener(v -> {
            startActivity(new Intent(MyPetsActivity.this, CreatePetActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            Animatoo.animateSlideLeft(MyPetsActivity.this);
        });
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

    private void scheduleDismiss() {
        Handler handler = new Handler();
        handler.postDelayed(() -> loadingProgress.dismiss(), 3000);
    }

    private void showLoadingDialog() {
        loadingProgress = KProgressHUD.create(MyPetsActivity.this).setLabel(getString(R.string.txt_pleaseWait))
                .setDetailsLabel(getString(R.string.txt_loading))
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
    }
    @Override
    public void onNewPetClicked(Pet nPet) {
        Intent i = new Intent(MyPetsActivity.this, PetProfileActivity.class);
        i.putExtra(KEY_INTENT, nPet);
        startActivity(i);
        Animatoo.animateSlideLeft(MyPetsActivity.this);
        showToast(nPet.getPetName(), R.drawable.ic_baseline_pets_24);

    }

    @Override
    public void onNewPetEditClicked(Pet nPet) {
        Intent intentEditPet = new Intent(MyPetsActivity.this, EditPetProfileActivity.class);
        intentEditPet.putExtra(KEY_PROFILE,nPet);
        startActivity(intentEditPet);
        Animatoo.animateSlideLeft(MyPetsActivity.this);
        showToast(nPet.getPetName(), R.drawable.ic_baseline_pets_24);
    }

    @Override
    public void onNewPetDeleteClicked(Pet nPet) {
        String petId = nPet.getPetId();
        showRemoveDialog(petId);
    }

    private void showRemoveDialog(String petId) {
        alertView = new AlertView(getString(R.string.txt_remove), getString(R.string.subTile_remove_dialog), AlertStyle.DIALOG);
        alertView.addAction(new AlertAction(getString(R.string.txt_ster_yes), AlertActionStyle.POSITIVE, alertAction -> {
            removePet(petId);
        }));
        alertView.addAction(new AlertAction(getString(R.string.txt_cancel), AlertActionStyle.NEGATIVE, alertAction -> {
        }));
        alertView.show(MyPetsActivity.this);
    }

    private void removePet(String petId) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(PET);

        ref.orderByChild(PETID).equalTo(petId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot ds: snapshot.getChildren()){
                        ds.getRef().removeValue();
                    }
                    showToast("Successful removed.",R.drawable.ic_baseline_done);
                    Intent i = new Intent(MyPetsActivity.this,MyPetsActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    Animatoo.animateSlideUp(MyPetsActivity.this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.itemMyPets:
                showToast(getString(R.string.txt_itemmenu_MyPets),R.drawable.itemmenu_mypets);
                break;

            case R.id.itemPets:
                showToast(getString(R.string.txt_itemmenu_Pets),R.drawable.itemmenu_pets);
                startActivity(new Intent(MyPetsActivity.this,PetsActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                Animatoo.animateSlideLeft(MyPetsActivity.this);
                break;

            case R.id.itemFavorites:
                showToast(getString(R.string.txt_itemmenu_Favorites),R.drawable.itemmenu_favorites);
                startActivity(new Intent(MyPetsActivity.this,FavoritesActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                Animatoo.animateSlideLeft(MyPetsActivity.this);
                break;

            case R.id.itemMessages:
                showToast(getString(R.string.txt_itemmenu_Call),R.drawable.ic_baseline_call_24);
                startActivity(new Intent(MyPetsActivity.this, PetCallActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                Animatoo.animateSlideLeft(MyPetsActivity.this);
                break;

            case R.id.itemAboutApp:
                showToast(getString(R.string.txt_itemmenu_AboutApp),R.drawable.itemmenu_aboutapp);
                startActivity(new Intent(MyPetsActivity.this,AboutPageActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                Animatoo.animateSlideLeft(MyPetsActivity.this);
                break;

            case R.id.itemSetting:
                showToast(getString(R.string.txt_item_dark_mode),R.drawable.ic_baseline_dark_mode_setting);
                showDialog();
                break;

            case R.id.itemSignOut:
                showToast(getString(R.string.txt_itemmenu_SignOut),R.drawable.ic_baseline_login_24);
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MyPetsActivity.this,LoginActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                Animatoo.animateSlideRight(MyPetsActivity.this);
                MyPetsActivity.this.finish();
                break;

            case R.id.itemChangePassword:
                showToast(getString(R.string.txt_itemmenu_changePassword),R.drawable.ic_baseline_lock);
                startActivity(new Intent(MyPetsActivity.this, ChangePasswordActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                Animatoo.animateSlideLeft(MyPetsActivity.this);
                break;

            case R.id.itemMyProfile:
                showToast(getString(R.string.txt_itemmenu_myProfile),R.drawable.ic_baseline_account_circle_24);
                startActivity(new Intent(MyPetsActivity.this,UserProfileActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                Animatoo.animateSlideLeft(MyPetsActivity.this);
                break;
        }
        item.setChecked(true);
        binding.drawerLayout.closeDrawers();
        return true;
    }
}
