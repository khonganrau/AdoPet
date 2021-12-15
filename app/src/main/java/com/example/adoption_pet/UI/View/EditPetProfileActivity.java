package com.example.adoption_pet.UI.View;

import static com.example.adoption_pet.utils.Constants.CAT;
import static com.example.adoption_pet.utils.Constants.DOG;
import static com.example.adoption_pet.utils.Constants.DURL;
import static com.example.adoption_pet.utils.Constants.FEMALE;
import static com.example.adoption_pet.utils.Constants.KEY_PROFILE;
import static com.example.adoption_pet.utils.Constants.MALE;
import static com.example.adoption_pet.utils.Constants.STERI_NO;
import static com.example.adoption_pet.utils.Constants.STERI_YES;
import static com.example.adoption_pet.utils.Constants.UPE;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.adoption_pet.R;
import com.example.adoption_pet.UI.Menu.MyPetsActivity;
import com.example.adoption_pet.databinding.ActivityEditPetProfileBinding;
import com.example.adoption_pet.databinding.ToolbarBinding;
import com.example.adoption_pet.model.Pet;
import com.example.adoption_pet.utils.Utilities;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.irozon.alertview.AlertActionStyle;
import com.irozon.alertview.AlertStyle;
import com.irozon.alertview.AlertView;
import com.irozon.alertview.objects.AlertAction;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class EditPetProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE = 1;

    private ActivityEditPetProfileBinding binding;

    private ToolbarBinding toolbarBinding;

    private Pet mPet;

    private KProgressHUD loadingProgress;

    private String proCi;

    private Uri imgUri;

    private Toast mToastText;

    private String currentImage;

    private double latitude;

    private double longitude;

    private String zipCode;

    private StorageReference mStorageRef;

    private DatabaseReference dbRef;

    private String petId;

    private String downloadImgUrl;

    private AlertView alertView;

    private long times;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityEditPetProfileBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        mToastText = new Toast(getApplicationContext());

        //StorageReference
        mStorageRef = FirebaseStorage.getInstance().getReference("Pet Images");

        //DatabaseReference
        dbRef = FirebaseDatabase.getInstance().getReference().child("Pets");


        rating();

        Intent mIntent = getIntent();
        mPet = mIntent.getParcelableExtra(KEY_PROFILE);

        showLoadingDialog();

        petType(mPet.getPetType());
        binding.petName.setText(mPet.getPetName());
        binding.breed.setText(mPet.getBreed());
        Picasso.get().load(mPet.getPetImg()).fit().centerCrop()
                .into(binding.imgPet);
        binding.petDob.setText(mPet.getDob());
        petGender(mPet.getGender());
        sterilizationStatus(mPet.getSteriStatus());
        binding.petDes.setText(mPet.getDescription());
        binding.petCity.setText(mPet.getProvinces());
        binding.petDistrict.setText(mPet.getDistrict());
        binding.houseNumber.setText(mPet.getAddress());
        binding.size.setText(mPet.getPetSize());
        binding.ager.setText(mPet.getPetAger());
        binding.sliderEnergy.setValue(mPet.getEnergyLevel());
        binding.sliderActivity.setValue(mPet.getActivityLevel());
        binding.sliderAffection.setValue(mPet.getAffectionLevel());
        binding.sliderPlayfull.setValue(mPet.getPlayfulnessLevel());
        binding.sliderTraining.setValue(mPet.getTrainingLevel());
        scheduleDismiss();

        currentImage = mPet.getPetImg();
        proCi = mPet.getProvinces();
        latitude = mPet.getLatitude();
        longitude = mPet.getLongitude();
        zipCode = mPet.getZipCode();
        petId = mPet.getPetId();
        times = mPet.getTimestamp();


        binding.btnUpdate.setOnClickListener(this);

        getToolBar();

        pProperty();

        selectImage();


    }

    private void sterilizationStatus(String status) {
        switch (status) {
            case STERI_YES:
                binding.rgSterilized.check(R.id.rbtnYes);
                break;
            case STERI_NO:
                binding.rgSterilized.check(R.id.rbtnNo);
                break;
        }
    }

    private void petGender(String gender) {
        switch (gender) {
            case FEMALE:
                binding.rgGender.check(R.id.rbtnFemale);
                break;
            case MALE:
                binding.rgGender.check(R.id.rbtnMale);
                break;
        }
    }

    private void petType(String type) {
        switch (type) {
            case CAT:
                binding.rbtnCat.setChecked(true);
                break;
            case DOG:
                binding.rbtnDog.setChecked(true);
                break;
        }
    }

    private String handlePetType(){
        int selectedId = binding.rgType.getCheckedRadioButtonId();
        String nType;
        if(selectedId == R.id.rbtnCat){
            nType = CAT;
        }else {
            nType = DOG;
        }
        return nType;
    }

    private String handlePetGender(){
        int selectedId = binding.rgGender.getCheckedRadioButtonId();
        String nGender;
        if(selectedId == R.id.rbtnFemale){
            nGender = FEMALE;
        }else {
            nGender = MALE;
        }
        return nGender;
    }

    private String handleSteriStatus(){
        int selectedId = binding.rgSterilized.getCheckedRadioButtonId();
        String nSteri;
        if(selectedId == R.id.rbtnYes){
            nSteri = STERI_YES;
        }else {
            nSteri = STERI_NO;
        }
        return nSteri;
    }

    private void pProperty(){

        //description
        binding.petDes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    binding.tilDes.setErrorEnabled(true);
                    binding.tilDes.setError(getString(R.string.txt_error_blank_fields));
                    binding.tilDes.requestFocus();
                } else if (s.length() >= 1 && s.length() < 100) {
                    binding.tilDes.setErrorEnabled(true);
                    binding.tilDes.setError(getString(R.string.txt_error_des_short));
                    binding.tilDes.requestFocus();
                } else if (s.length() > 3000) {
                    binding.tilDes.setErrorEnabled(true);
                    binding.tilDes.setError(getString(R.string.txt_error_des_long));
                    binding.tilDes.requestFocus();
                } else {
                    binding.tilDes.setError(null);
                }

            }
        });

        //pet name
        binding.petName.addTextChangedListener(new TextWatcher() {

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
                    binding.tilName.setErrorEnabled(true);
                    binding.tilName.setError(getString(R.string.txt_valid_name_unique_char));
                    binding.tilName.requestFocus();
                } else {
                    binding.tilName.setError(null);
                }

            }
        });

        //dob
        binding.petDob.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            DatePickerDialog datePickerDialog = new DatePickerDialog(EditPetProfileActivity.this, (datePicker, i, i1, i2) -> {
                calendar.set(i, i1, i2);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                binding.petDob.setText(simpleDateFormat.format(calendar.getTime()));
            }, year, month, day);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

        //ager
        String[] agers = getResources().getStringArray(R.array.age_array);
        ArrayAdapter<String> agerAdapter = new ArrayAdapter<>(EditPetProfileActivity.this, R.layout.item_dropdown_list, agers);
        binding.ager.setAdapter(agerAdapter);
        binding.ager.setThreshold(1);

        binding.ager.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    binding.tilAger.setErrorEnabled(true);
                    binding.tilAger.setError(getString(R.string.rbtn_nonselect));
                    binding.tilAger.requestFocus();
                } else {
                    binding.tilAger.setError(null);
                }

            }
        });

        //size
        String[] sizes = getResources().getStringArray(R.array.size_array);
        ArrayAdapter<String> sizeAdapter = new ArrayAdapter<>(EditPetProfileActivity.this, R.layout.item_dropdown_list, sizes);
        binding.size.setAdapter(sizeAdapter);
        binding.size.setThreshold(1);

        binding.size.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    binding.tilSize.setErrorEnabled(true);
                    binding.tilSize.setError(getString(R.string.rbtn_nonselect));
                    binding.tilSize.requestFocus();
                } else {
                    binding.tilSize.setError(null);
                }

            }
        });

    }

    private void validate() {
        String pType = handlePetType();
        String petBreeds = Objects.requireNonNull(binding.breed.getText().toString().trim());
        Uri petImg = imgUri;
        String name = Objects.requireNonNull(Objects.requireNonNull(binding.petName.getText()).toString().trim());
        String petGender = handlePetGender();
        String petSte = handleSteriStatus();
        String des = Objects.requireNonNull(Objects.requireNonNull(binding.petDes.getText()).toString().trim());
        String district = Objects.requireNonNull(binding.petDistrict.getText().toString().trim());
        String houseNumber = Objects.requireNonNull(Objects.requireNonNull(binding.houseNumber.getText()).toString().trim());
        String size = Objects.requireNonNull(binding.size.getText().toString().trim());
        String ager = Objects.requireNonNull(binding.ager.getText().toString().trim());
        float energy = binding.sliderEnergy.getValue();
        float activity = binding.sliderActivity.getValue();
        float playfulness = binding.sliderPlayfull.getValue();
        float affection = binding.sliderAffection.getValue();
        float training = binding.sliderTraining.getValue();

        //Type validate
        if (binding.rgType.getCheckedRadioButtonId() == -1 && pType == null) {
            binding.errorType.setVisibility(View.VISIBLE);
        }

        //Name validate
        if (TextUtils.isEmpty(name)) {
            binding.tilName.setErrorEnabled(true);
            binding.tilName.setError(getString(R.string.txt_error_blank_fields));
            binding.tilName.requestFocus();

        } else if (!name.matches("^[a-zA-Z\\s]+$") || name.matches("[0-9]")) {
            binding.tilName.setErrorEnabled(true);
            binding.tilName.setError(getString(R.string.txt_valid_name_unique_char));
            binding.tilName.requestFocus();
        }

        //Breed validate
        if (TextUtils.isEmpty(petBreeds)) {
            binding.tilBreed.setError(getString(R.string.txt_error_blank_fields));
            binding.tilBreed.requestFocus();
        }

        //Gender validate
        if (binding.rgGender.getCheckedRadioButtonId() == -1 && petGender == null) {
            binding.errorGender.setVisibility(View.VISIBLE);
        }

        //Sterilize validate
        if (binding.rgSterilized.getCheckedRadioButtonId() == -1 && petSte == null) {
            binding.errorSterilization.setVisibility(View.VISIBLE);
        }

        //Description validate
        if (TextUtils.isEmpty(des)) {
            binding.tilDes.setErrorEnabled(true);
            binding.tilDes.setError(getString(R.string.txt_error_blank_fields));
            Utilities.setVibrate(this);
            binding.tilDes.requestFocus();
        } else if (des.length() >= 1 && des.length() < 400) {
            binding.tilDes.setErrorEnabled(true);
            binding.tilDes.setError(getString(R.string.txt_error_des_short));
            Utilities.setVibrate(this);
            binding.tilDes.requestFocus();
        } else if (des.length() > 3000) {
            binding.tilDes.setErrorEnabled(true);
            binding.tilDes.setError(getString(R.string.txt_error_des_long));
            binding.tilDes.requestFocus();
        }

        //Large Sizes
        if (TextUtils.isEmpty(size)) {
            binding.tilSize.setError(getString(R.string.rbtn_nonselect));
            Utilities.setVibrate(this);
            binding.tilSize.requestFocus();
        }

        //Ager
        if (TextUtils.isEmpty(ager)) {
            binding.tilAger.setError(getString(R.string.rbtn_nonselect));
            Utilities.setVibrate(this);
            binding.tilAger.requestFocus();
        }

        //Energy rate
        if (energy == 0f) {
            binding.sttEnergy.setText(getString(R.string.txt_error_rate));
            binding.sttEnergy.setTextColor(getColor(R.color.red_error));
            binding.sttEnergy.setVisibility(View.VISIBLE);
            binding.sttEnergy.requestFocus();
        }

        //Activity Level
        if (activity == 0f) {
            binding.sttActivity.setText(getString(R.string.txt_error_rate));
            binding.sttActivity.setTextColor(getColor(R.color.red_error));
            binding.sttActivity.setVisibility(View.VISIBLE);
            binding.sttActivity.requestFocus();
        }

        //Playfulness Level
        if (playfulness == 0f) {
            binding.sttPlayfull.setText(getString(R.string.txt_error_rate));
            binding.sttPlayfull.setTextColor(getColor(R.color.red_error));
            binding.sttPlayfull.setVisibility(View.VISIBLE);
            binding.sttPlayfull.requestFocus();
        }

        //Affection Level
        if (affection == 0f) {
            binding.sttAffection.setText(getString(R.string.txt_error_rate));
            binding.sttAffection.setTextColor(getColor(R.color.red_error));
            binding.sttAffection.setVisibility(View.VISIBLE);
            binding.sttAffection.requestFocus();
        }

//        Ease for training
        if (training == 0f) {
            binding.sttTraining.setText(getString(R.string.txt_error_rate));
            binding.sttTraining.setTextColor(getColor(R.color.red_error));
            binding.sttTraining.setVisibility(View.VISIBLE);
            binding.sttTraining.requestFocus();
        }
        else if ((binding.rgType.getCheckedRadioButtonId() == -1 && pType == null) || TextUtils.isEmpty(petBreeds) || (TextUtils.isEmpty(name) || !name.matches("^[a-zA-Z\\s]+$") || name.matches("[0-9]")) ||
                (binding.rgGender.getCheckedRadioButtonId() == -1 && petGender == null) || (binding.rgSterilized.getCheckedRadioButtonId() == -1 && petSte == null) || (TextUtils.isEmpty(des) || (des.length() >= 1 && des.length() < 400) ||
                TextUtils.isEmpty(houseNumber)) || TextUtils.isEmpty(size) || TextUtils.isEmpty(ager) || energy == 0f || activity == 0f || playfulness == 0f || affection == 0f || training == 0 || TextUtils.isEmpty(district)) {
            showToast(getString(R.string.txt_error_blank_form), R.drawable.ic_baseline_error);
        }
        else {
            updatePetProfile(pType, petBreeds, petImg, name, petGender, petSte, des, district, houseNumber, size, ager, energy, activity, playfulness, affection, training);
        }
    }

    @SuppressLint("LogNotTimber")
    private void updatePetProfile(String petType, String petBreeds, Uri petImg, String name,
                                  String petGender, String petSte, String des, String district, String houseNumber, String size,
                                  String ager, float energy, float activity, float playfulness, float affection, float training) {

        showLoadingDialog();

        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        //upload pet image
        if (petImg != null) {

            String imgId = System.currentTimeMillis() + "." + getFileExtension(petImg);

            StorageReference imageRef = mStorageRef.child(imgId);

            imageRef.putFile(petImg).addOnCompleteListener(task -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                downloadImgUrl = uri.toString();

                HashMap<String, Object> pet = new HashMap<>();
                pet.put("petType", petType);
                pet.put("dob", binding.petDob.getText().toString().trim());
                pet.put("breed", petBreeds);
                pet.put("petImg", downloadImgUrl);
                pet.put("userId", userId);
                pet.put("petId", petId);
                pet.put("steriStatus", petSte);
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
                pet.put("gender", petGender);
                pet.put("timestamp", times);

                //update data of pet
                dbRef.child(petId).updateChildren(pet).addOnCompleteListener(task1 -> {
                    showToast(getString(R.string.txt_successful_update), R.drawable.ic_baseline_done);
                    scheduleDismiss();
                    navMyPet();

                }).addOnFailureListener(e -> {
                    showToast(getString(R.string.txt_failure_update), R.drawable.ic_baseline_error);
                    Log.e(UPE, e.getMessage());
                    loadingProgress.dismiss();
                });

            }).addOnFailureListener(e -> {
                Log.d(DURL, getString(R.string.txt_failed_downloadUrl));
                loadingProgress.dismiss();
            })).addOnFailureListener(e -> Log.d(DURL, getString(R.string.txt_image_upload_failed)));
        } else {
            HashMap<String, Object> pet = new HashMap<>();
            pet.put("petType", petType);
            pet.put("dob", binding.petDob.getText().toString().trim());
            pet.put("breed", petBreeds);
            pet.put("petImg", currentImage);
            pet.put("userId", userId);
            pet.put("petId", petId);
            pet.put("steriStatus", petSte);
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
            pet.put("gender", petGender);
            pet.put("timestamp", times);

            dbRef.child(petId).updateChildren(pet).addOnCompleteListener(task -> {
                showToast(getString(R.string.txt_successful_update), R.drawable.ic_baseline_done);
                navMyPet();
                loadingProgress.dismiss();
            }).addOnFailureListener(e -> {
                showToast(getString(R.string.txt_failure_update), R.drawable.ic_baseline_error);
                Log.e(UPE, e.getMessage());
                loadingProgress.dismiss();
            });
        }


    }




    private void navMyPet() {
        Intent intent = new Intent(EditPetProfileActivity.this, MyPetsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        Animatoo.animateSlideRight(EditPetProfileActivity.this);
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

    private void showDialog() {
        alertView = new AlertView(getString(R.string.txt_update), getString(R.string.subTile_update_dialog), AlertStyle.DIALOG);
        alertView.addAction(new AlertAction(getString(R.string.txt_ster_yes), AlertActionStyle.POSITIVE, alertAction -> {
            validate();
        }));
        alertView.addAction(new AlertAction(getString(R.string.txt_cancel), AlertActionStyle.NEGATIVE, alertAction -> {
        }));
        alertView.show(EditPetProfileActivity.this);

    }

    private void selectImage() {
        binding.imgPet.setOnClickListener(v -> {
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
            binding.errorImg.setVisibility(View.GONE);
            imgUri = data.getData();
            binding.imgPet.setImageURI(imgUri);

        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    @SuppressLint("SetTextI18n")
    private void rating() {
        //training
        binding.sliderTraining.addOnChangeListener((slider, value, fromUser) -> {
            int r_value = (int) value;
            if (value == 0) {
                binding.sttTraining.setVisibility(View.VISIBLE);
                binding.sttTraining.setTextColor(getColor(R.color.red_error));
                binding.sttTraining.setText(getString(R.string.txt_error_rate));
            } else if (value > 0 && value <= 2) {
                binding.sttTraining.setTextColor(getColor(R.color.blue));
                binding.sttTraining.setText(getString(R.string.txt_low) + " " + r_value + "/6");
                binding.sttTraining.setVisibility(View.VISIBLE);
            } else if (value > 2 && value <= 4) {
                binding.sttTraining.setTextColor(getColor(R.color.blue));
                binding.sttTraining.setText(getString(R.string.txt_moderate) + " " + r_value + "/6");
                binding.sttTraining.setVisibility(View.VISIBLE);
            } else if (value > 4 && value <= 6) {
                binding.sttTraining.setTextColor(getColor(R.color.blue));
                binding.sttTraining.setText(getString(R.string.txt_high) + " " + r_value + "/6");
                binding.sttTraining.setVisibility(View.VISIBLE);
            }
        });

        //affections
        binding.sliderAffection.addOnChangeListener((slider, value, fromUser) -> {
            int r_value = (int) value;
            if (value == 0) {
                binding.sttAffection.setVisibility(View.VISIBLE);
                binding.sttAffection.setTextColor(getColor(R.color.red_error));
                binding.sttAffection.setText(getString(R.string.txt_error_rate));
            } else if (value > 0 && value <= 2) {
                binding.sttAffection.setTextColor(getColor(R.color.blue));
                binding.sttAffection.setText(getString(R.string.txt_low) + " " + r_value + "/6");
                binding.sttAffection.setVisibility(View.VISIBLE);
            } else if (value > 2 && value <= 4) {
                binding.sttAffection.setTextColor(getColor(R.color.blue));
                binding.sttAffection.setText(getString(R.string.txt_moderate) + " " + r_value + "/6");
                binding.sttAffection.setVisibility(View.VISIBLE);
            } else if (value > 4 && value <= 6) {
                binding.sttAffection.setTextColor(getColor(R.color.blue));
                binding.sttAffection.setText(getString(R.string.txt_high) + " " + r_value + "/6");
                binding.sttAffection.setVisibility(View.VISIBLE);
            }
        });

        //playfulness
        binding.sliderPlayfull.addOnChangeListener((slider, value, fromUser) -> {
            int r_value = (int) value;
            if (value == 0) {
                binding.sttPlayfull.setVisibility(View.VISIBLE);
                binding.sttPlayfull.setTextColor(getColor(R.color.red_error));
                binding.sttPlayfull.setText(getString(R.string.txt_error_rate));
            } else if (value > 0 && value <= 2) {
                binding.sttPlayfull.setTextColor(getColor(R.color.blue));
                binding.sttPlayfull.setText(getString(R.string.txt_low) + " " + r_value + "/6");
                binding.sttPlayfull.setVisibility(View.VISIBLE);
            } else if (value > 2 && value <= 4) {
                binding.sttPlayfull.setTextColor(getColor(R.color.blue));
                binding.sttPlayfull.setText(getString(R.string.txt_moderate) + " " + r_value + "/6");
                binding.sttPlayfull.setVisibility(View.VISIBLE);
            } else if (value > 4 && value <= 6) {
                binding.sttPlayfull.setTextColor(getColor(R.color.blue));
                binding.sttPlayfull.setText(getString(R.string.txt_high) + " " + r_value + "/6");
                binding.sttPlayfull.setVisibility(View.VISIBLE);
            }
        });

        //activity
        binding.sliderActivity.addOnChangeListener((slider, value, fromUser) -> {
            int r_value = (int) value;
            if (value == 0) {
                binding.sttActivity.setVisibility(View.VISIBLE);
                binding.sttActivity.setTextColor(getColor(R.color.red_error));
                binding.sttActivity.setText(getString(R.string.txt_error_rate));
            } else if (value > 0 && value <= 2) {
                binding.sttActivity.setTextColor(getColor(R.color.blue));
                binding.sttActivity.setText(getString(R.string.txt_low) + " " + r_value + "/6");
                binding.sttActivity.setVisibility(View.VISIBLE);
            } else if (value > 2 && value <= 4) {
                binding.sttActivity.setTextColor(getColor(R.color.blue));
                binding.sttActivity.setText(getString(R.string.txt_moderate) + " " + r_value + "/6");
                binding.sttActivity.setVisibility(View.VISIBLE);
            } else if (value > 4 && value <= 6) {
                binding.sttActivity.setTextColor(getColor(R.color.blue));
                binding.sttActivity.setText(getString(R.string.txt_high) + " " + r_value + "/6");
                binding.sttActivity.setVisibility(View.VISIBLE);
            }
        });

        //energy
        binding.sliderEnergy.addOnChangeListener((slider, value, fromUser) -> {
            int r_value = (int) value;
            if (value == 0) {
                binding.sttEnergy.setVisibility(View.VISIBLE);
                binding.sttEnergy.setTextColor(getColor(R.color.red_error));
                binding.sttEnergy.setText(getString(R.string.txt_error_rate));
            } else if (value > 0 && value <= 2) {
                binding.sttEnergy.setTextColor(getColor(R.color.blue));
                binding.sttEnergy.setText(getString(R.string.txt_low) + " " + r_value + "/6");
                binding.sttEnergy.setVisibility(View.VISIBLE);
            } else if (value > 2 && value <= 4) {
                binding.sttEnergy.setTextColor(getColor(R.color.blue));
                binding.sttEnergy.setText(getString(R.string.txt_moderate) + " " + r_value + "/6");
                binding.sttEnergy.setVisibility(View.VISIBLE);
            } else if (value > 4 && value <= 6) {
                binding.sttEnergy.setTextColor(getColor(R.color.blue));
                binding.sttEnergy.setText(getString(R.string.txt_high) + " " + r_value + "/6");
                binding.sttEnergy.setVisibility(View.VISIBLE);
            }
        });
    }

    private void getToolBar() {
        toolbarBinding = binding.toolbarEditPet;
        setSupportActionBar(toolbarBinding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbarBinding.toolbarTitle.setText(Objects.requireNonNull(binding.petName.getText()).toString()
        );

        setContentView(binding.getRoot());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        Animatoo.animateSlideRight(EditPetProfileActivity.this);
        return super.onSupportNavigateUp();

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnUpdate) {
            showDialog();
        }
    }

    private void scheduleDismiss() {
        Handler handler = new Handler();
        handler.postDelayed(() -> loadingProgress.dismiss(), 3000);
    }

    private void showLoadingDialog() {
        loadingProgress = KProgressHUD.create(EditPetProfileActivity.this).setLabel(getString(R.string.txt_pleaseWait))
                .setDetailsLabel(getString(R.string.txt_loading))
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
    }
}