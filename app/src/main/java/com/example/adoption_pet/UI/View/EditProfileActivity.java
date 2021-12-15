package com.example.adoption_pet.UI.View;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.adoption_pet.R;
import com.example.adoption_pet.UI.Menu.UserProfileActivity;
import com.example.adoption_pet.databinding.ActivityEditProfileBinding;
import com.example.adoption_pet.databinding.ToolbarBinding;
import com.example.adoption_pet.model.User;
import com.example.adoption_pet.utils.LogUtils;
import com.example.adoption_pet.utils.Utilities;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView userImg;

    private TextInputLayout til_fullName;
    private TextInputEditText fullName;

    private MaterialAutoCompleteTextView dob;

    private TextInputLayout til_phone;
    private TextInputEditText phone;

    private RadioGroup rg_gender;

    private TextInputEditText email;

    private MaterialAutoCompleteTextView mactv_district;

    private MaterialAutoCompleteTextView mactv_city;

    private TextInputEditText tiet_houseAddress;

    private Button saveBtn;

    private Uri imageUri;


    private Toast mToastText;

    private String encodedImage;

    private ProgressBar progressBar;

    private DatabaseReference reference;

    private ToolbarBinding toolbarBinding;

    private String userAvatar;

    private String u_gender;

    private ActivityEditProfileBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        reference = FirebaseDatabase.getInstance().getReference("Users");

        hook();

        validateFullName();

        validateDob();

        validateGender();

        validatePhone();

//        validateInfo();

        loadingUserInfo();

        setImage();

        initialBtn();

        getToolBar();


    }

    private void getToolBar() {
        toolbarBinding = binding.toolbarProfile;
        setSupportActionBar(toolbarBinding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbarBinding.toolbarTitle.setText(getString(R.string.txt_editProfile));

        setContentView(binding.getRoot());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        Animatoo.animateSlideRight(EditProfileActivity.this);
        return super.onSupportNavigateUp();

    }

    private void initialBtn() {
        saveBtn.setOnClickListener(this);

    }


    private void setImage() {

        userImg.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });

    }


    private void loadingUserInfo() {
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        reference.orderByChild("id").equalTo(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        User user = ds.getValue(User.class);
                        String userGender = ds.child("gender").getValue().toString();
                        userAvatar = ds.child("userImgId").getValue(String.class);

                        fullName.setText(user.getFullName());
                        phone.setText(user.getPhoneNumber());
                        email.setText(user.getEmail());
                        dob.setText(user.getDob());
                        mactv_city.setText(user.getCity());
                        mactv_district.setText(user.getDistrict());
                        tiet_houseAddress.setText(user.getUserAddress());


//                        handle user gender
                        switch (userGender) {
                            case "Female":
                                rg_gender.check(R.id.rbtn_u_female);
                                break;

                            case "Male":
                                rg_gender.check(R.id.rbtn_u_male);
                                break;

                            case "Other":
                                rg_gender.check(R.id.rbtn_u_other);
                                break;
                        }

                        if (userAvatar != null) {
                            byte[] decodeString = Base64.decode(userAvatar, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
                            userImg.setImageBitmap(bitmap);
                        }


                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void validateInfo() {

        String name = Objects.requireNonNull(fullName.getText().toString().trim());
        String userPhoneNumber = Objects.requireNonNull(phone.getText().toString().trim());

        //Fullname
        if (TextUtils.isEmpty((name))) {
            til_fullName.setErrorEnabled(true);
            til_fullName.setError(getString(R.string.txt_error_blank_fields));
            til_fullName.requestFocus();
        } else if (!name.matches("^[a-zA-Z\\s]+$") || name.matches("[0-9]")) {
            til_fullName.setErrorEnabled(true);
            til_fullName.setError(getString(R.string.txt_valid_name_unique_char));
            til_fullName.requestFocus();
        } else if (name.length() > 100) {
            til_fullName.setErrorEnabled(true);
            til_fullName.setError(getString(R.string.txt_valid_long_name));
            til_fullName.requestFocus();
        }

        //Gender
        if (rg_gender.getCheckedRadioButtonId() == -1 && TextUtils.isEmpty(getUserGender())) {
            TextView tv_error = findViewById(R.id.tv_error);
            tv_error.setVisibility(View.VISIBLE);
        }

        //Phone numbers
        if (TextUtils.isEmpty(userPhoneNumber)) {
            til_phone.setErrorEnabled(true);
            til_phone.setError(getString(R.string.txt_error_blank_fields));
            til_phone.requestFocus();
        } else if (userPhoneNumber.length() > 0 && userPhoneNumber.length() < 10 || userPhoneNumber.matches("[^0\\d{9}$]")) {
            til_phone.setErrorEnabled(true);
            til_phone.setError(getString(R.string.invalid_format_phoneNumbers));
            til_phone.requestFocus();
        } else {
            Toast.makeText(EditProfileActivity.this, "Validated successful", Toast.LENGTH_SHORT).show();
            editInfo();

        }


    }

    private void editInfo() {

        loading(true);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            if (userAvatar != encodedImage) {
                String userId = firebaseUser.getUid();

                HashMap<String, Object> u_user = new HashMap<>();
                u_user.put("fullName", fullName.getText().toString().trim());
                u_user.put("dob", dob.getText().toString().trim());
                u_user.put("phoneNumber", phone.getText().toString().trim());
                u_user.put("gender", getUserGender());
                u_user.put("userImgId", encodedImage);

                reference.child(userId).updateChildren(u_user).addOnSuccessListener(unused -> {
                    loading(false);
                    showToast(getString(R.string.txt_success_update_profile), R.drawable.ic_baseline_done);
                    Utilities.setSoundSuccess(EditProfileActivity.this);


                }).addOnFailureListener(e -> {
                    Log.e("UPU", "Update failed: " + e.getCause());
                    showToast(e.getMessage(), R.drawable.ic_baseline_error);
                });


            } else {
                String userId = firebaseUser.getUid();

                HashMap<String, Object> u_user = new HashMap<>();
                u_user.put("fullName", fullName.getText().toString().trim());
                u_user.put("dob", dob.getText().toString().trim());
                u_user.put("phoneNumber", phone.getText().toString().trim());
                u_user.put("gender", getUserGender());
                u_user.put("userImgId", userAvatar);

                reference.child(userId).updateChildren(u_user).addOnSuccessListener(unused -> {
                    loading(false);
                    showToast(getString(R.string.txt_success_update_profile), R.drawable.ic_baseline_done);
                    startActivity(new Intent(EditProfileActivity.this, UserProfileActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    Animatoo.animateSlideRight(EditProfileActivity.this);


                }).addOnFailureListener(e -> {
                    LogUtils.e("Update failed: " + e.getCause());
                    showToast(e.getMessage(), R.drawable.ic_baseline_error);
                });

            }
        }

    }


    private String getEncodedImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            userImg.setImageBitmap(bitmap);
                            encodedImage = getEncodedImage(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }

            });

    private void loading(Boolean isLoading) {
        if (isLoading) {
            saveBtn.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);

        } else {
            progressBar.setVisibility(View.INVISIBLE);
            saveBtn.setVisibility(View.VISIBLE);
        }
    }


    //validate phone numbers
    private void validatePhone() {
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String phoneNumbers = s.toString().trim();
                if (s.length() == 0) {
                    til_phone.setErrorEnabled(true);
                    til_phone.setError(getString(R.string.txt_error_blank_fields));
                    til_phone.requestFocus();
                } else if (s.length() > 0 && s.length() < 10 || phoneNumbers.matches("[^0\\d{9}$]")) {
                    til_phone.setErrorEnabled(true);
                    til_phone.setError(getString(R.string.invalid_format_phoneNumbers));
                    til_phone.requestFocus();
                } else {
                    til_phone.setErrorEnabled(false);
                }

            }
        });
    }

    //validate gender
    //TODO: gender check
    private String getUserGender() {
        int genderId = rg_gender.getCheckedRadioButtonId();

        RadioButton rb = findViewById(genderId);

        u_gender = rb.getText().toString();

        return u_gender;
    }

    @SuppressLint("NonConstantResourceId")
    private void validateGender() {
        rg_gender.setOnCheckedChangeListener((group, checkedId) -> {


            TextView tv_error = findViewById(R.id.tv_error);
            if (rg_gender.getCheckedRadioButtonId() == -1) {
                tv_error.setText(getString(R.string.rbtn_nonselect));
                tv_error.setVisibility(View.VISIBLE);
            } else {

                switch (checkedId) {
                    case R.id.rbtn_u_female:
                        tv_error.setVisibility(View.GONE);
                        break;

                    case R.id.rbtn_u_male:
                        tv_error.setVisibility(View.GONE);
                        break;

                    case R.id.rbtn_u_other:
                        tv_error.setVisibility(View.GONE);
                        break;

                }
            }
        });
    }

    //validate dob
    private void validateDob() {
        dob.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            DatePickerDialog datePickerDialog = new DatePickerDialog(EditProfileActivity.this, (datePicker, i, i1, i2) -> {
                calendar.set(i, i1, i2);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                dob.setText(simpleDateFormat.format(calendar.getTime()));
            }, year, month, day);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });


    }

    //validate fullname
    private void validateFullName() {
        fullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String name = s.toString().trim();
                if (s.length() == 0) {
                    til_fullName.setErrorEnabled(true);
                    til_fullName.setError(getString(R.string.txt_error_blank_fields));
                    til_fullName.requestFocus();
                } else if (!name.matches("^[a-zA-Z\\s]+$") || name.matches("[0-9]")) {
                    til_fullName.setErrorEnabled(true);
                    til_fullName.setError(getString(R.string.txt_valid_name_unique_char));
                    til_fullName.requestFocus();
                } else if (s.length() > 100) {
                    til_fullName.setErrorEnabled(true);
                    til_fullName.setError(getString(R.string.txt_valid_long_name));
                } else {
                    til_fullName.setErrorEnabled(false);
                }

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

        mToastText.setDuration(Toast.LENGTH_SHORT);
        mToastText.setView(view);
        mToastText.show();
    }


    private void hook() {
        //Fullname
        til_fullName = findViewById(R.id.til_fullName);
        fullName = findViewById(R.id.tiet_fullName);

        //Dob
        dob = findViewById(R.id.mactv_dob);

        //Gender
        rg_gender = findViewById(R.id.rg_u_gender);

        //Phone numbers
        til_phone = findViewById(R.id.til_phoneNumber);
        phone = findViewById(R.id.tiet_phoneNumber);

        //emails
        email = findViewById(R.id.tiet_email);

        //Districts
        mactv_district = findViewById(R.id.mactv_dist);

        //City
        mactv_city = findViewById(R.id.mactv_proCi);

        //House address
        tiet_houseAddress = findViewById(R.id.tiet_hNumber);

        //userImg
        userImg = findViewById(R.id.userImg);

        //Save button
        saveBtn = findViewById(R.id.btn_edit);

        //Toast
        mToastText = new Toast(getApplicationContext());

        //Progress bar
        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_edit) {
            validateInfo();
        }
    }
}