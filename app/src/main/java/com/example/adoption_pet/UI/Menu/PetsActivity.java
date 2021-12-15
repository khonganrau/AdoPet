package com.example.adoption_pet.UI.Menu;

import static com.example.adoption_pet.utils.Constants.ADDRESS;
import static com.example.adoption_pet.utils.Constants.AVATAR;
import static com.example.adoption_pet.utils.Constants.CITY;
import static com.example.adoption_pet.utils.Constants.DISTRICT;
import static com.example.adoption_pet.utils.Constants.DM_DARK;
import static com.example.adoption_pet.utils.Constants.DM_LIGHT;
import static com.example.adoption_pet.utils.Constants.DM_SD;
import static com.example.adoption_pet.utils.Constants.EMAIL;
import static com.example.adoption_pet.utils.Constants.FULLNAME;
import static com.example.adoption_pet.utils.Constants.ID;
import static com.example.adoption_pet.utils.Constants.KEY_INTENT;
import static com.example.adoption_pet.utils.Constants.LATITUDE;
import static com.example.adoption_pet.utils.Constants.LONGITUDE;
import static com.example.adoption_pet.utils.Constants.TAG_LOCATION;
import static com.example.adoption_pet.utils.Constants.TAG_PETS;
import static com.example.adoption_pet.utils.Constants.ZIPCODE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
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
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.adoption_pet.Adapter.PetAdapter;
import com.example.adoption_pet.R;
import com.example.adoption_pet.UI.View.LoginActivity;
import com.example.adoption_pet.UI.View.PetProfileActivity;
import com.example.adoption_pet.UI.View.SearchActivity;
import com.example.adoption_pet.databinding.ActivityPetsBinding;
import com.example.adoption_pet.model.Pet;
import com.example.adoption_pet.utils.Constants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.makeramen.roundedimageview.RoundedImageView;
import com.zoho.commons.LauncherModes;
import com.zoho.commons.LauncherProperties;
import com.zoho.salesiqembed.ZohoSalesIQ;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PetsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, PetAdapter.PetClickListener {

    private ActivityPetsBinding binding;

    MaterialToolbar topAppBar;
    NavigationView navigationView;

    private ActionBarDrawerToggle drawerToggle;
    // private Activity mContext;
    int checkedItem;
    private String selected;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    static final String CHECKDITEM = "check_item";
    //Drawer Layout
    DrawerLayout drawerLayout;
    private RoundedImageView imageAvatar;
    private TextView tvEmail;
    private TextView tvName;
    //Toast
    private Toast mToastText;
    //Location
    double latitude;
    double longitude;
    String zipCode;
    String address;
    String city;
    String dist;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private DatabaseReference reference;

    private ArrayList<Pet> pets;

    private PetAdapter petAdapter;

    private KProgressHUD loadingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        binding = ActivityPetsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(PetsActivity.this);

        //DatabaseReference
        reference = FirebaseDatabase.getInstance().getReference("Users");

        //get user locations
        getCurrentAddress();

        hook();

        showUserInformation();

        clickOnNavigation();


        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().findItem(R.id.itemPets).setChecked(true);

        // Set a Toolbar to replace the ActionBar.
        setSupportActionBar(topAppBar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // This will display an Up icon (<-), we will replace it with hamburger later
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Find our drawer view
        drawerToggle = setupDrawerToggle();

        //Tie DrawerLayout events to the ActionBarToggle
        drawerLayout.addDrawerListener(drawerToggle);

        //Sync the toggle state after onRestoreInstanceState has occurred
        drawerToggle.syncState();

        showAllPet();

        ZohoSalesIQ.init(getApplication(), Constants.APP_KEY, Constants.ACCESS_KEY);

        LauncherProperties launcherProperties = new LauncherProperties(LauncherModes.FLOATING);
//        launcherProperties.setIcon(AppCompatResources.getDrawable(this, R.drawable.ic_baseline_help_outline_24));
        ZohoSalesIQ.setLauncherProperties(launcherProperties);

        ZohoSalesIQ.showLauncher(true);

        navigateSearch();

        showLoadingDialog();
    }

    private void navigateSearch() {
        binding.btnSearchPet.setOnClickListener(v -> {
            Intent intent = new Intent(PetsActivity.this, SearchActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            Animatoo.animateSlideLeft(PetsActivity.this);
            showToast("Navigation Search page", R.drawable.ic_baseline_done);


        });
    }


    private void showAllPet() {

        binding.allPet.setHasFixedSize(true);

        pets = new ArrayList<>();

        petAdapter = new PetAdapter(PetsActivity.this, pets, this);

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Pets");
        dbRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "LogNotTimber"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Pet mPet = dataSnapshot.getValue(Pet.class);
                        pets.add(mPet);

                    }
                    scheduleDismiss();
                    petAdapter.notifyDataSetChanged();
                    binding.allPet.setAdapter(petAdapter);
                } else {
                    Log.d(TAG_PETS, getString(R.string.txt_nullData));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //pass any configuration changes to the drawerToggle
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
    }

    private void clickOnNavigation() {
        topAppBar.setNavigationOnClickListener(v -> binding.drawerLayout.open());
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemPets:
                showToast(getString(R.string.txt_itemmenu_Pets), R.drawable.itemmenu_pets);
                break;

            case R.id.itemFavorites:
                showToast(getString(R.string.txt_itemmenu_Favorites), R.drawable.ic_baseline_notifications_24);
                Intent intentFavorites = new Intent(PetsActivity.this, FavoritesActivity.class);
                startActivity(intentFavorites);
                Animatoo.animateSlideLeft(PetsActivity.this);
                break;
            case R.id.itemMyPets:
                Intent intentMyPets = new Intent(PetsActivity.this, MyPetsActivity.class);
                startActivity(intentMyPets);
                Animatoo.animateSlideLeft(PetsActivity.this);
                break;

            case R.id.itemMessages:
                showToast(getString(R.string.txt_itemmenu_Call), R.drawable.ic_baseline_call_24);
                Intent intentPetCall = new Intent(PetsActivity.this, PetCallActivity.class);
                startActivity(intentPetCall);
                Animatoo.animateSlideLeft(PetsActivity.this);
                break;

            case R.id.itemAboutApp:
                Toast.makeText(PetsActivity.this, getString(R.string.txt_itemmenu_AboutApp), Toast.LENGTH_SHORT).show();
                Intent intentAboutApp = new Intent(PetsActivity.this, AboutPageActivity.class);
                startActivity(intentAboutApp);
                Animatoo.animateSlideLeft(PetsActivity.this);
                break;

            case R.id.itemSetting:
                Toast.makeText(PetsActivity.this, getString(R.string.txt_item_dark_mode), Toast.LENGTH_SHORT).show();
                showDialog();
                break;


            case R.id.itemSignOut:
                showToast(getString(R.string.txt_itemmenu_SignOut), R.drawable.ic_baseline_login_24);
                //Toast.makeText(PetsActivity.this, getString(R.string.txt_itemmenu_SignOut), Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(PetsActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                Animatoo.animateSlideRight(PetsActivity.this);
                PetsActivity.this.finish();
                break;

            case R.id.itemChangePassword:
                showToast(getString(R.string.txt_itemmenu_changePassword), R.drawable.ic_baseline_lock);
                Intent intentChangePassword = new Intent(PetsActivity.this, ChangePasswordActivity.class);
                startActivity(intentChangePassword);
                Animatoo.animateSlideLeft(PetsActivity.this);
                break;

            case R.id.itemMyProfile:
                showToast(getString(R.string.txt_itemmenu_myProfile), R.drawable.ic_baseline_account_circle_24);
                Intent intentMyProfile = new Intent(PetsActivity.this, UserProfileActivity.class);
                startActivity(intentMyProfile);
                Animatoo.animateSlideLeft(PetsActivity.this);
                break;
        }
        item.setChecked(true);
        drawerLayout.closeDrawers();
        return true;
    }

    // Light and Dark Theme.
    private void showDialog() {
        final String[] themes = getResources().getStringArray(R.array.theme);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(PetsActivity.this);
        builder.setTitle(getString(R.string.txt_select_theme));
        builder.setSingleChoiceItems(R.array.theme, getCheckedItem(), (dialogInterface, i) -> {
            selected = themes[i];
            checkedItem = i;
        });

        builder.setPositiveButton(getString(R.string.positive_option), (dialogInterface, i) -> {
            if (selected == null) {
                selected = themes[i];
                checkedItem = i;
            }

            switch (selected) {

                case DM_SD:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

                    break;

                case DM_DARK:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                    break;
                case DM_LIGHT:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    break;
            }

            setCheckedItem(i);
        });

        builder.setNegativeButton(getString(R.string.negative_option), (dialog, which) -> dialog.dismiss());

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

    private void showUserInformation() {
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

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

    private void showToast(final String message, int resource) {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.toast_layout, findViewById(R.id.toast_layout_root));
        TextView tvMessage = view.findViewById(R.id.tv_message);
        tvMessage.setText(message);
        ImageView toastImage = view.findViewById(R.id.image_toast);
        toastImage.setImageResource(resource);

        // mToastText.cancel();
        mToastText.setDuration(Toast.LENGTH_SHORT);
        mToastText.setView(view);
        mToastText.show();
    }

    private void getCurrentAddress() {
        if (ActivityCompat.checkSelfPermission(PetsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //When permission granted
            getLocation();

        } else {

            //When permission denied
            ActivityCompat.requestPermissions(PetsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

    }


    @SuppressLint({"MissingPermission", "LogNotTimber"})
    private void getLocation() {

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {

            //Init location
            Location location = task.getResult();


            try {
                //Init geoCoder
                Geocoder geoCoder = new Geocoder(PetsActivity.this, Locale.ENGLISH);

                //Init address list
                List<Address> addresses = geoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                //Set Latitude on TextView
                latitude = addresses.get(0).getLatitude();

                //Set Longitude on TextView
                longitude = addresses.get(0).getLongitude();

                //Set zipCode
                zipCode = addresses.get(0).getPostalCode();

                //Address line
                address = addresses.get(0).getAddressLine(0);

                //Province or city
                city = addresses.get(0).getAdminArea();

                //District
                String u_district;
                if (addresses.get(0).getLocality() == null) {
                    u_district = addresses.get(0).getSubAdminArea();
                } else {
                    u_district = addresses.get(0).getLocality();
                }

                dist = u_district;

                //Zip code
                zipCode = addresses.get(0).getPostalCode();

                saveUserLocation();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @SuppressLint("LogNotTimber")
    private void saveUserLocation() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();

            HashMap<String, Object> userLocation = new HashMap<>();
            userLocation.put(CITY, city);
            userLocation.put(DISTRICT, dist);
            userLocation.put(ADDRESS, address);
            userLocation.put(LATITUDE, latitude);
            userLocation.put(LONGITUDE, longitude);
            userLocation.put(ZIPCODE, zipCode);

            HashMap<String, Object> petLocation = new HashMap<>();
            userLocation.put("provinces", city);
            userLocation.put(DISTRICT, dist);
            userLocation.put("address", address);
            userLocation.put(LATITUDE, latitude);
            userLocation.put(LONGITUDE, longitude);
            userLocation.put(ZIPCODE, zipCode);

            reference.child(uid).updateChildren(userLocation).addOnSuccessListener(unused -> Log.d(TAG_LOCATION,getString(R.string.txt_save_location_successful))).addOnFailureListener(e -> {
                Log.e(TAG_LOCATION, getString(R.string.txt_save_location_failed) + e.getCause());
                showToast(e.getMessage(), R.drawable.ic_baseline_error);
            });

        }
    }

    private void hook() {
        topAppBar = findViewById(R.id.topAppBar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigationView);

        /// dark mode
        sharedPreferences = getSharedPreferences("themes", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //drawable
        imageAvatar = navigationView.getHeaderView(0).findViewById(R.id.userImg);
        tvEmail = navigationView.getHeaderView(0).findViewById(R.id.tv_userEmail);
        tvName = navigationView.getHeaderView(0).findViewById(R.id.tv_userName);

        //Toast
        mToastText = new Toast(getApplicationContext());
    }


    @Override
    public void onPetClicked(Pet mPet) {
        Intent i = new Intent(PetsActivity.this, PetProfileActivity.class);
        i.putExtra(KEY_INTENT, mPet);
        startActivity(i);
        Animatoo.animateSlideLeft(PetsActivity.this);
        showToast(mPet.getPetName(), R.drawable.ic_baseline_pets_24);

    }

    private void scheduleDismiss() {
        Handler handler = new Handler();
        handler.postDelayed(() -> loadingProgress.dismiss(), 3000);
    }

    private void showLoadingDialog() {
        loadingProgress = KProgressHUD.create(PetsActivity.this).setLabel(getString(R.string.txt_pleaseWait))
                .setDetailsLabel(getString(R.string.txt_loading))
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
    }
}