package com.example.adoption_pet.UI.View;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.adoption_pet.R;
import com.example.adoption_pet.UI.Menu.MyPetsActivity;
import com.example.adoption_pet.databinding.ActivityCreatePetBinding;
import com.example.adoption_pet.databinding.ToolbarBinding;
import com.example.adoption_pet.utils.Utilities;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class CreatePetActivity extends AppCompatActivity {

    private ActivityCreatePetBinding petBinding;

    private DatabaseReference db;

    private String userId;

    private double latitude;

    private double longitude;

    private final int REQUEST_CODE = 1;

    private Uri imgUri;

    private Uri url;

    private String type;

    //variable contain gender in String
    String gender;

    //variable contain sterilization status in String
    String ste;


    //variable contain zip Code
    private String zipCode;

    Toast mToastText;

    private StorageReference mStorageRef;

    private DatabaseReference dbRef;

    private ToolbarBinding toolbarBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        petBinding = ActivityCreatePetBinding.inflate(getLayoutInflater());
        View view = petBinding.getRoot();
        setContentView(view);

        mToastText = new Toast(getApplicationContext());

        //DatabaseReference of Users collection
        db = FirebaseDatabase.getInstance().getReference();

        //StorageReference
        mStorageRef = FirebaseStorage.getInstance().getReference("Pet Images");

        //DatabaseReference
        dbRef = FirebaseDatabase.getInstance().getReference().child("Pets");

        getPetLocation();

        getUserId();

        rating();

        petProperties();

        createPet();

        selectImage();

        getToolBar();
    }

    private void getToolBar() {
        toolbarBinding = petBinding.toobarCreate;
        setSupportActionBar(toolbarBinding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbarBinding.toolbarTitle.setText(getString(R.string.txt_title_create));

        setContentView(petBinding.getRoot());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        Animatoo.animateSlideRight(CreatePetActivity.this);
        return super.onSupportNavigateUp();

    }

    private void createPet() {
        petBinding.btnCreate.setOnClickListener(v -> validate());

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

    private void validate() {

        String petType = type;
        String petBreeds = Objects.requireNonNull(petBinding.mactvBreed.getText().toString().trim());
        Uri petImg = imgUri;
        String name = Objects.requireNonNull(Objects.requireNonNull(petBinding.petName.getText()).toString().trim());
        String petGender = gender;
        String petSte = ste;
        String des = Objects.requireNonNull(Objects.requireNonNull(petBinding.petDes.getText()).toString().trim());
        String district = Objects.requireNonNull(petBinding.petDistrict.getText().toString().trim());
        String houseNumber = Objects.requireNonNull(Objects.requireNonNull(petBinding.houseNumber.getText()).toString().trim());
        String size = Objects.requireNonNull(petBinding.size.getText().toString().trim());
        String ager = Objects.requireNonNull(petBinding.ager.getText().toString().trim());
        float energy = petBinding.sliderEnergy.getValue();
        float activity = petBinding.sliderActivity.getValue();
        float playfulness = petBinding.sliderPlayfull.getValue();
        float affection = petBinding.sliderAffection.getValue();
        float training = petBinding.sliderTraining.getValue();

        //Type validate
        if (petBinding.rgType.getCheckedRadioButtonId() == -1 && petType == null) {
            petBinding.errorType.setVisibility(View.VISIBLE);
        }

        //Name validate
        if (TextUtils.isEmpty(name)) {
            petBinding.tilName.setErrorEnabled(true);
            petBinding.tilName.setError(getString(R.string.txt_error_blank_fields));
            petBinding.tilName.requestFocus();

        } else if (!name.matches("^[a-zA-Z\\s]+$") || name.matches("[0-9]")) {
            petBinding.tilName.setErrorEnabled(true);
            petBinding.tilName.setError(getString(R.string.txt_valid_name_unique_char));
            petBinding.tilName.requestFocus();
        }

        //Breed validate
        if (TextUtils.isEmpty(petBreeds)) {
            petBinding.tilBreed.setError(getString(R.string.txt_error_blank_fields));
            petBinding.tilBreed.requestFocus();
        }

        if (petImg == null) {
            petBinding.errorImg.setVisibility(View.VISIBLE);
        }

        //Gender validate
        if (petBinding.rgGender.getCheckedRadioButtonId() == -1 && petGender == null) {
            petBinding.errorGender.setVisibility(View.VISIBLE);
        }

        //Sterilize validate
        if (petBinding.rgSterilized.getCheckedRadioButtonId() == -1 && petSte == null) {
            petBinding.errorSterilization.setVisibility(View.VISIBLE);
        }

        //Description validate
        if (TextUtils.isEmpty(des)) {
            petBinding.tilDes.setErrorEnabled(true);
            petBinding.tilDes.setError(getString(R.string.txt_error_blank_fields));
            Utilities.setVibrate(this);
            petBinding.tilDes.requestFocus();
        } else if (des.length() >= 1 && des.length() < 400) {
            petBinding.tilDes.setErrorEnabled(true);
            petBinding.tilDes.setError(getString(R.string.txt_error_des_short));
            Utilities.setVibrate(this);
            petBinding.tilDes.requestFocus();
        } else if (des.length() > 3000) {
            petBinding.tilDes.setErrorEnabled(true);
            petBinding.tilDes.setError(getString(R.string.txt_error_des_long));
            petBinding.tilDes.requestFocus();
        }

        //Large Sizes
        if (TextUtils.isEmpty(size)) {
            petBinding.tilSize.setError(getString(R.string.rbtn_nonselect));
            Utilities.setVibrate(this);
            petBinding.tilSize.requestFocus();
        }

        //Ager
        if (TextUtils.isEmpty(ager)) {
            petBinding.tilAger.setError(getString(R.string.rbtn_nonselect));
            Utilities.setVibrate(this);
            petBinding.tilAger.requestFocus();
        }

        //Energy rate
        if (energy == 0f) {
            petBinding.sttEnergy.setText(getString(R.string.txt_error_rate));
            petBinding.sttEnergy.setTextColor(getColor(R.color.red_error));
            petBinding.sttEnergy.setVisibility(View.VISIBLE);
            petBinding.sttEnergy.requestFocus();
        }

        //Activity Level
        if (activity == 0f) {
            petBinding.sttActivity.setText(getString(R.string.txt_error_rate));
            petBinding.sttActivity.setTextColor(getColor(R.color.red_error));
            petBinding.sttActivity.setVisibility(View.VISIBLE);
            petBinding.sttActivity.requestFocus();
        }

        //Playfulness Level
        if (playfulness == 0f) {
            petBinding.sttPlayfull.setText(getString(R.string.txt_error_rate));
            petBinding.sttPlayfull.setTextColor(getColor(R.color.red_error));
            petBinding.sttPlayfull.setVisibility(View.VISIBLE);
            petBinding.sttPlayfull.requestFocus();
        }

        //Affection Level
        if (affection == 0f) {
            petBinding.sttAffection.setText(getString(R.string.txt_error_rate));
            petBinding.sttAffection.setTextColor(getColor(R.color.red_error));
            petBinding.sttAffection.setVisibility(View.VISIBLE);
            petBinding.sttAffection.requestFocus();
        }

        //Ease for training
        if (training == 0f) {
            petBinding.sttTraining.setText(getString(R.string.txt_error_rate));
            petBinding.sttTraining.setTextColor(getColor(R.color.red_error));
            petBinding.sttTraining.setVisibility(View.VISIBLE);
            petBinding.sttTraining.requestFocus();
        } else if ((petBinding.rgType.getCheckedRadioButtonId() == -1 && petType == null) || TextUtils.isEmpty(petBreeds) || petImg == null || (TextUtils.isEmpty(name) || !name.matches("^[a-zA-Z\\s]+$") || name.matches("[0-9]")) ||
                (petBinding.rgGender.getCheckedRadioButtonId() == -1 && petGender == null) || (petBinding.rgSterilized.getCheckedRadioButtonId() == -1 && petSte == null) || (TextUtils.isEmpty(des) || (des.length() >= 1 && des.length() < 400) ||
                TextUtils.isEmpty(houseNumber)) || TextUtils.isEmpty(size) || TextUtils.isEmpty(ager) || energy == 0f || activity == 0f || playfulness == 0f || affection == 0f || training == 0 || TextUtils.isEmpty(district)) {
            showToast(getString(R.string.txt_error_blank_form), R.drawable.ic_baseline_error);
        } else {
            uploadPet();
        }
    }

    private void uploadPet() {

        String petType = type;
        String petBreeds = Objects.requireNonNull(petBinding.mactvBreed.getText().toString().trim());
        String name = Objects.requireNonNull(Objects.requireNonNull(petBinding.petName.getText()).toString().trim());
        String des = Objects.requireNonNull(Objects.requireNonNull(petBinding.petDes.getText()).toString().trim());
        String proCi = Objects.requireNonNull(Objects.requireNonNull(petBinding.petCity.getText()).toString().trim());
        String district = Objects.requireNonNull(petBinding.petDistrict.getText().toString().trim());
        String houseNumber = Objects.requireNonNull(Objects.requireNonNull(petBinding.houseNumber.getText()).toString().trim());
        String size = Objects.requireNonNull(petBinding.size.getText().toString().trim());
        String ager = Objects.requireNonNull(petBinding.ager.getText().toString().trim());
        float energy = petBinding.sliderEnergy.getValue();
        float activity = petBinding.sliderActivity.getValue();
        float playfulness = petBinding.sliderPlayfull.getValue();
        float affection = petBinding.sliderAffection.getValue();
        float training = petBinding.sliderTraining.getValue();

        //get imgId
        String imgId = System.currentTimeMillis() + "." + getFileExtension(imgUri);

        StorageReference petImgRef = mStorageRef.child(imgId);

        //get petId
        String petId = dbRef.push().getKey();

        //upload to Firebase Storage

        petImgRef.putFile(imgUri).addOnCompleteListener(task -> {
            petImgRef.getDownloadUrl().addOnSuccessListener(uri -> {
                url = uri;
                //set information for pet
                HashMap<String, Object> pet = new HashMap<>();
                pet.put("petType", petType);
                pet.put("dob", petBinding.petDob.getText().toString().trim());
                pet.put("breed", petBreeds);
                pet.put("petImg", url.toString());
                pet.put("userId", userId);
                pet.put("petId", petId);
                pet.put("steriStatus", ste);
                pet.put("description", des);
                pet.put("provinces", proCi);
                pet.put("district", district);
                pet.put("address", houseNumber);
                pet.put("zipCode", zipCode);
                pet.put("latitude", latitude);
                pet.put("petSize", size);
                pet.put("petAger", ager);
                pet.put("energyLevel", energy);
                pet.put("activityLevel", activity);
                pet.put("playfulnessLevel", playfulness);
                pet.put("affectionLevel", affection);
                pet.put("trainingLevel", training);
                pet.put("petName", name);
                pet.put("longitude", longitude);
                pet.put("gender", gender);
                pet.put("timestamp", ServerValue.TIMESTAMP);
                //push to Database
                dbRef.child(petId).setValue(pet);
            });
            showToast(getString(R.string.txt_success_create_pet), R.drawable.ic_baseline_done);
            Utilities.setSoundSuccess(CreatePetActivity.this);
            Intent intent = new Intent(CreatePetActivity.this, MyPetsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Animatoo.animateSlideRight(CreatePetActivity.this);
            startActivity(intent);
        }).addOnProgressListener(snapshot -> showToast(getString((R.string.txt_wait)), R.drawable.ic_baseline_loop_24)).addOnFailureListener(e -> {
            showToast(getString(R.string.txt_failed_create), R.drawable.ic_baseline_error);
            Log.e("PIMG", e.getMessage());
        });

    }

    private void selectImage() {
        petBinding.imgPet.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            petBinding.errorImg.setVisibility(View.GONE);
            imgUri = data.getData();
            petBinding.imgPet.setImageURI(imgUri);

        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    @SuppressLint("NonConstantResourceId")
    private void petProperties() {

        //breed and type
        petBinding.rgType.setOnCheckedChangeListener((group, checkedId) -> {
            if (petBinding.rgType.getCheckedRadioButtonId() == -1) {
                petBinding.errorType.setVisibility(View.VISIBLE);
            } else {
                petBinding.errorType.setVisibility(View.GONE);

                switch (checkedId) {
                    case R.id.rbtnCat:
                        petBinding.mactvBreed.setText("");
                        type = "Cat";
                        petBinding.mactvBreed.setFocusable(true);
                        petBinding.mactvBreed.setEnabled(true);
                        petBinding.mactvBreed.setFocusableInTouchMode(true);
                        String[] cat_breeds = getResources().getStringArray(R.array.beeds_cat);
                        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(CreatePetActivity.this, R.layout.item_dropdown_list, cat_breeds);
                        petBinding.mactvBreed.setAdapter(catAdapter);
                        petBinding.mactvBreed.setThreshold(1);
                        break;


                    case R.id.rbtnDog:
                        petBinding.mactvBreed.setText("");
                        type = "Dog";
                        petBinding.mactvBreed.setFocusable(true);
                        petBinding.mactvBreed.setEnabled(true);
                        petBinding.mactvBreed.setFocusableInTouchMode(true);
                        String[] dog_breeds = getResources().getStringArray(R.array.beeds_dog);
                        ArrayAdapter<String> dogAdapter = new ArrayAdapter<>(CreatePetActivity.this, R.layout.item_dropdown_list, dog_breeds);
                        petBinding.mactvBreed.setAdapter(dogAdapter);
                        petBinding.mactvBreed.setThreshold(1);
                        break;
                }
            }

        });


        //Breed errors
        petBinding.mactvBreed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() <= 0) {
                    petBinding.tilBreed.setHelperTextEnabled(false);
                    petBinding.tilBreed.setError(getString(R.string.txt_error_breed));
                    petBinding.tilBreed.requestFocus();
                } else {
                    petBinding.tilBreed.setHelperTextEnabled(false);
                    petBinding.tilBreed.setError(null);
                }
            }
        });

        //description
        petBinding.petDes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    petBinding.tilDes.setErrorEnabled(true);
                    petBinding.tilDes.setError(getString(R.string.txt_error_blank_fields));
                    petBinding.tilDes.requestFocus();
                } else if (s.length() >= 1 && s.length() < 100) {
                    petBinding.tilDes.setErrorEnabled(true);
                    petBinding.tilDes.setError(getString(R.string.txt_error_des_short));
                    petBinding.tilDes.requestFocus();
                } else if (s.length() > 3000) {
                    petBinding.tilDes.setErrorEnabled(true);
                    petBinding.tilDes.setError(getString(R.string.txt_error_des_long));
                    petBinding.tilDes.requestFocus();
                } else {
                    petBinding.tilDes.setError(null);
                }

            }
        });

        //pet name
        petBinding.petName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                String name = s.toString();
                if (s.length() == 0 || !name.matches("^[a-zA-Z\\s]+$") || name.matches("[0-9]")) {
                    petBinding.tilName.setErrorEnabled(true);
                    petBinding.tilName.setError(getString(R.string.txt_valid_name_unique_char));
                    petBinding.tilName.requestFocus();
                } else {
                    petBinding.tilName.setError(null);
                }

            }
        });

        //dob
        petBinding.petDob.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            DatePickerDialog datePickerDialog = new DatePickerDialog(CreatePetActivity.this, (datePicker, i, i1, i2) -> {
                calendar.set(i, i1, i2);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                petBinding.petDob.setText(simpleDateFormat.format(calendar.getTime()));
            }, year, month, day);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

        //ager
        String[] agers = getResources().getStringArray(R.array.age_array);
        ArrayAdapter<String> agerAdapter = new ArrayAdapter<>(CreatePetActivity.this, R.layout.item_dropdown_list, agers);
        petBinding.ager.setAdapter(agerAdapter);
        petBinding.ager.setThreshold(1);

        petBinding.ager.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    petBinding.tilAger.setErrorEnabled(true);
                    petBinding.tilAger.setError(getString(R.string.rbtn_nonselect));
                    petBinding.tilAger.requestFocus();
                } else {
                    petBinding.tilAger.setError(null);
                }

            }
        });

        //size
        String[] sizes = getResources().getStringArray(R.array.size_array);
        ArrayAdapter<String> sizeAdapter = new ArrayAdapter<>(CreatePetActivity.this, R.layout.item_dropdown_list, sizes);
        petBinding.size.setAdapter(sizeAdapter);
        petBinding.size.setThreshold(1);

        petBinding.size.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    petBinding.tilSize.setErrorEnabled(true);
                    petBinding.tilSize.setError(getString(R.string.rbtn_nonselect));
                    petBinding.tilSize.requestFocus();
                } else {
                    petBinding.tilSize.setError(null);
                }

            }
        });

        //sterilized
        petBinding.rgSterilized.setOnCheckedChangeListener((group, checkedId) -> {

            if (group.getCheckedRadioButtonId() == -1) {
                petBinding.errorSterilization.setVisibility(View.VISIBLE);
            } else {
                petBinding.errorSterilization.setVisibility(View.GONE);
                switch (checkedId) {
                    case R.id.rbtnYes:
                        ste = getString(R.string.txt_ster_yes);
                        break;

                    case R.id.rbtnNo:
                        ste = getString(R.string.txt_ster_no);
                        break;

                }
            }
        });

        //gender
        petBinding.rgGender.setOnCheckedChangeListener((group, checkedId) -> {
            if (group.getCheckedRadioButtonId() == -1) {
                petBinding.errorGender.setVisibility(View.VISIBLE);
            } else {
                petBinding.errorGender.setVisibility(View.GONE);
                switch (checkedId) {
                    case R.id.rbtnFemale:
                        gender = getString(R.string.txt_gender_female);
                        break;

                    case R.id.rbtnMale:
                        gender = getString(R.string.txt_gender_male);
                        break;

                }
            }

        });
    }

    @SuppressLint("SetTextI18n")
    private void rating() {

        //training
        petBinding.sliderTraining.addOnChangeListener((slider, value, fromUser) -> {
            int r_value = (int) value;
            if (value == 0) {
                petBinding.sttTraining.setVisibility(View.VISIBLE);
                petBinding.sttTraining.setTextColor(getColor(R.color.red_error));
                petBinding.sttTraining.setText(getString(R.string.txt_error_rate));
            } else if (value > 0 && value <= 2) {
                petBinding.sttTraining.setTextColor(getColor(R.color.blue));
                petBinding.sttTraining.setText(getString(R.string.txt_low) + " " + r_value + "/6");
                petBinding.sttTraining.setVisibility(View.VISIBLE);
            } else if (value > 2 && value <= 4) {
                petBinding.sttTraining.setTextColor(getColor(R.color.blue));
                petBinding.sttTraining.setText(getString(R.string.txt_moderate) + " " + r_value + "/6");
                petBinding.sttTraining.setVisibility(View.VISIBLE);
            } else if (value > 4 && value <= 6) {
                petBinding.sttTraining.setTextColor(getColor(R.color.blue));
                petBinding.sttTraining.setText(getString(R.string.txt_high) + " " + r_value + "/6");
                petBinding.sttTraining.setVisibility(View.VISIBLE);
            }
        });

        //affections
        petBinding.sliderAffection.addOnChangeListener((slider, value, fromUser) -> {
            int r_value = (int) value;
            if (value == 0) {
                petBinding.sttAffection.setVisibility(View.VISIBLE);
                petBinding.sttAffection.setTextColor(getColor(R.color.red_error));
                petBinding.sttAffection.setText(getString(R.string.txt_error_rate));
            } else if (value > 0 && value <= 2) {
                petBinding.sttAffection.setTextColor(getColor(R.color.blue));
                petBinding.sttAffection.setText(getString(R.string.txt_low) + " " + r_value + "/6");
                petBinding.sttAffection.setVisibility(View.VISIBLE);
            } else if (value > 2 && value <= 4) {
                petBinding.sttAffection.setTextColor(getColor(R.color.blue));
                petBinding.sttAffection.setText(getString(R.string.txt_moderate) + " " + r_value + "/6");
                petBinding.sttAffection.setVisibility(View.VISIBLE);
            } else if (value > 4 && value <= 6) {
                petBinding.sttAffection.setTextColor(getColor(R.color.blue));
                petBinding.sttAffection.setText(getString(R.string.txt_high) + " " + r_value + "/6");
                petBinding.sttAffection.setVisibility(View.VISIBLE);
            }
        });

        //playfulness
        petBinding.sliderPlayfull.addOnChangeListener((slider, value, fromUser) -> {
            int r_value = (int) value;
            if (value == 0) {
                petBinding.sttPlayfull.setVisibility(View.VISIBLE);
                petBinding.sttPlayfull.setTextColor(getColor(R.color.red_error));
                petBinding.sttPlayfull.setText(getString(R.string.txt_error_rate));
            } else if (value > 0 && value <= 2) {
                petBinding.sttPlayfull.setTextColor(getColor(R.color.blue));
                petBinding.sttPlayfull.setText(getString(R.string.txt_low) + " " + r_value + "/6");
                petBinding.sttPlayfull.setVisibility(View.VISIBLE);
            } else if (value > 2 && value <= 4) {
                petBinding.sttPlayfull.setTextColor(getColor(R.color.blue));
                petBinding.sttPlayfull.setText(getString(R.string.txt_moderate) + " " + r_value + "/6");
                petBinding.sttPlayfull.setVisibility(View.VISIBLE);
            } else if (value > 4 && value <= 6) {
                petBinding.sttPlayfull.setTextColor(getColor(R.color.blue));
                petBinding.sttPlayfull.setText(getString(R.string.txt_high) + " " + r_value + "/6");
                petBinding.sttPlayfull.setVisibility(View.VISIBLE);
            }
        });

        //activity
        petBinding.sliderActivity.addOnChangeListener((slider, value, fromUser) -> {
            int r_value = (int) value;
            if (value == 0) {
                petBinding.sttActivity.setVisibility(View.VISIBLE);
                petBinding.sttActivity.setTextColor(getColor(R.color.red_error));
                petBinding.sttActivity.setText(getString(R.string.txt_error_rate));
            } else if (value > 0 && value <= 2) {
                petBinding.sttActivity.setTextColor(getColor(R.color.blue));
                petBinding.sttActivity.setText(getString(R.string.txt_low) + " " + r_value + "/6");
                petBinding.sttActivity.setVisibility(View.VISIBLE);
            } else if (value > 2 && value <= 4) {
                petBinding.sttActivity.setTextColor(getColor(R.color.blue));
                petBinding.sttActivity.setText(getString(R.string.txt_moderate) + " " + r_value + "/6");
                petBinding.sttActivity.setVisibility(View.VISIBLE);
            } else if (value > 4 && value <= 6) {
                petBinding.sttActivity.setTextColor(getColor(R.color.blue));
                petBinding.sttActivity.setText(getString(R.string.txt_high) + " " + r_value + "/6");
                petBinding.sttActivity.setVisibility(View.VISIBLE);
            }
        });

        //energy
        petBinding.sliderEnergy.addOnChangeListener((slider, value, fromUser) -> {
            int r_value = (int) value;
            if (value == 0) {
                petBinding.sttEnergy.setVisibility(View.VISIBLE);
                petBinding.sttEnergy.setTextColor(getColor(R.color.red_error));
                petBinding.sttEnergy.setText(getString(R.string.txt_error_rate));
            } else if (value > 0 && value <= 2) {
                petBinding.sttEnergy.setTextColor(getColor(R.color.blue));
                petBinding.sttEnergy.setText(getString(R.string.txt_low) + " " + r_value + "/6");
                petBinding.sttEnergy.setVisibility(View.VISIBLE);
            } else if (value > 2 && value <= 4) {
                petBinding.sttEnergy.setTextColor(getColor(R.color.blue));
                petBinding.sttEnergy.setText(getString(R.string.txt_moderate) + " " + r_value + "/6");
                petBinding.sttEnergy.setVisibility(View.VISIBLE);
            } else if (value > 4 && value <= 6) {
                petBinding.sttEnergy.setTextColor(getColor(R.color.blue));
                petBinding.sttEnergy.setText(getString(R.string.txt_high) + " " + r_value + "/6");
                petBinding.sttEnergy.setVisibility(View.VISIBLE);
            }
        });
    }

    //get user Id function
    private String getUserId() {

        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mUser != null) {
            userId = mUser.getUid();
        }

        return userId;
    }

    //get pet location function
    private void getPetLocation() {

        String uId = getUserId();

        DatabaseReference ref = db.child("Users");

        Query userQuery = ref.orderByChild("id").equalTo(uId);

        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String cityName = Objects.requireNonNull(ds.child("city").getValue()).toString();
                        String districtName = Objects.requireNonNull(ds.child("district").getValue()).toString();
                        String petAddress = Objects.requireNonNull(ds.child("userAddress").getValue()).toString();

                        //load data to form
                        petBinding.petCity.setText(cityName);
                        petBinding.petDistrict.setText(districtName);
                        petBinding.houseNumber.setText(petAddress);

                        //get latitude and longitude
                        latitude = ds.child("latitude").getValue(Double.class);
                        longitude = ds.child("longitude").getValue(Double.class);

                        //get zipCodes
                        zipCode = ds.child("zipCode").getValue(String.class);


                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}