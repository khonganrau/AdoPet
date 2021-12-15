package com.example.adoption_pet.UI.View;

import static com.example.adoption_pet.utils.Constants.COMMA;
import static com.example.adoption_pet.utils.Constants.EMAIL;
import static com.example.adoption_pet.utils.Constants.EMPTY_VALUE;
import static com.example.adoption_pet.utils.Constants.FULLNAME;
import static com.example.adoption_pet.utils.Constants.INTENT_CONTENT_KEY;
import static com.example.adoption_pet.utils.Constants.INTENT_DIAL_KEY;
import static com.example.adoption_pet.utils.Constants.INTENT_MAIL_KEY;
import static com.example.adoption_pet.utils.Constants.INTENT_PET_ID;
import static com.example.adoption_pet.utils.Constants.INTENT_SUB_KEY;
import static com.example.adoption_pet.utils.Constants.KEY_INTENT;
import static com.example.adoption_pet.utils.Constants.LEVEL_2;
import static com.example.adoption_pet.utils.Constants.LEVEL_4;
import static com.example.adoption_pet.utils.Constants.LEVEL_6;
import static com.example.adoption_pet.utils.Constants.MAX_SLIDER;
import static com.example.adoption_pet.utils.Constants.PHONE_NUMBER;
import static com.example.adoption_pet.utils.Constants.PLUS;
import static com.example.adoption_pet.utils.Constants.STERI_NO;
import static com.example.adoption_pet.utils.Constants.STERI_YES;
import static com.example.adoption_pet.utils.Constants.USER_ID;
import static com.example.adoption_pet.utils.Constants.USER_PATH;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.adoption_pet.R;
import com.example.adoption_pet.UI.Menu.PetCallActivity;
import com.example.adoption_pet.databinding.ActivityPetProfileBinding;
import com.example.adoption_pet.databinding.ToolbarBinding;
import com.example.adoption_pet.model.Pet;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.irozon.alertview.AlertActionStyle;
import com.irozon.alertview.AlertStyle;
import com.irozon.alertview.AlertView;
import com.irozon.alertview.objects.AlertAction;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.picasso.Picasso;

public class PetProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityPetProfileBinding binding;
    private ToolbarBinding toolbarBinding;
    private Pet mPet;

    private AlertView alertView;
    private String email;

    private KProgressHUD loadingProgress;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityPetProfileBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());


        Intent mIntent = getIntent();
        mPet = (Pet) mIntent.getParcelableExtra(KEY_INTENT);

        showLoadingDialog();

        //pet Image
        Picasso.get().load(mPet.getPetImg()).fit().into(binding.petDetailImg);

        //pet name
        binding.tvPetName.setText(mPet.getPetName()+COMMA+mPet.getPetAger());

        //pet breed
        binding.tvPetBreed.setText(mPet.getBreed());

        //pet address
        binding.tvPetAddress.setText(mPet.getAddress());

        //pet description
        binding.petDescription.setText(mPet.getDescription());

        //pet gender
        binding.gender.setText(mPet.getGender());

        //pet sterilization status

        String status = mPet.getSteriStatus();

        switch (status){
            case STERI_YES:
                binding.steri.setText(getString(R.string.txt_sterilized));
                break;
            case STERI_NO:
                binding.steri.setText(R.string.txt_unsterilized);
                break;
        }

        //energy level
        float energyLevel = mPet.getEnergyLevel();
        binding.sliderEnergy.setValue(energyLevel);
        binding.sttEnergy.setText(returnStatus(energyLevel));

        //activity level
        float activityLevel = mPet.getActivityLevel();
        binding.sliderActivity.setValue(activityLevel);
        binding.sttActivity.setText(returnStatus(activityLevel));

        //playfulness level
        float playfulnessLevel = mPet.getPlayfulnessLevel();
        binding.sliderPlayfull.setValue(playfulnessLevel);
        binding.sttPlayfull.setText(returnStatus(playfulnessLevel));

        //affection level
        float affectionLevel = mPet.getAffectionLevel();
        binding.sliderAffection.setValue(affectionLevel);
        binding.sttAffection.setText(returnStatus(affectionLevel));

        //training level
        float trainingLevel = mPet.getTrainingLevel();
        binding.sliderTraining.setValue(trainingLevel);
        binding.sttTraining.setText(returnStatus(trainingLevel));

        scheduleDismiss();

        binding.btnContact.setOnClickListener(this);
        binding.btnShare.setOnClickListener(this);

        getUserName();

        checkUser();
        communicateMethod();

        getToolBar();
        QRCode();
    }

    private void checkUser(){
        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(mPet.getUserId().equals(uId)){
            binding.btnContact.setVisibility(View.GONE);
            binding.tvUserName.setVisibility(View.GONE);
            binding.email.setVisibility(View.GONE);
            binding.numberPhone.setVisibility(View.GONE);

        }else{
            binding.btnContact.setVisibility(View.VISIBLE);
            binding.tvUserName.setVisibility(View.VISIBLE);
            binding.email.setVisibility(View.VISIBLE);
            binding.numberPhone.setVisibility(View.VISIBLE);
        }
    }
    private void communicateMethod(){
        binding.btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    showDialog();

            }
        });
    }

    private void getUserName() {
        String id = mPet.getUserId();
        DatabaseReference df = FirebaseDatabase.getInstance().getReference(USER_PATH);
        df.orderByChild(USER_ID).equalTo(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        String userName = ds.child(FULLNAME).getValue(String.class);
                        String phoneNumber = ds.child(PHONE_NUMBER).getValue(String.class);
                        email = ds.child(EMAIL).getValue(String.class);

                        binding.tvUserName.setText(userName);
                        binding.numberPhone.setText(phoneNumber);
                        binding.email.setText(email);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void contact(){
        if(!binding.numberPhone.getText().toString().isEmpty()){
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(INTENT_DIAL_KEY+binding.numberPhone.getText().toString()));
            startActivity(intent);
            Animatoo.animateSlideLeft(PetProfileActivity.this);

        }else{
            Toast.makeText(PetProfileActivity.this,getString(R.string.txt_not_have_phoneNumber),Toast.LENGTH_SHORT).show();
        }

    }

    private void showDialog() {
        alertView = new AlertView(getString(R.string.txt_contact), getString(R.string.subTitle_contact_dialog), AlertStyle.IOS);
        alertView.addAction(new AlertAction(getString(R.string.txt_phone_method),AlertActionStyle.DEFAULT, alertAction -> {
            contact();
        }));
        alertView.addAction(new AlertAction(getString(R.string.call_vid),AlertActionStyle.DEFAULT, alertAction -> {
            startActivity(new Intent(PetProfileActivity.this,PetCallActivity.class).addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP
            ));
            Animatoo.animateSlideLeft(PetProfileActivity.this);
        }));

        alertView.addAction(new AlertAction(getString(R.string.txt_email_method),AlertActionStyle.DEFAULT, alertAction -> {
            sendEmail();
        }));
        alertView.show(PetProfileActivity.this);

    }

    private void sendEmail() {



        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(PetProfileActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_email_dialog, null);

        TextInputEditText receiverEmail = view.findViewById(R.id.receiverEmail);
        TextInputEditText subject = view.findViewById(R.id.subject);
        TextInputEditText content = view.findViewById(R.id.content);
        TextInputLayout contentLayout = view.findViewById(R.id.contentLayout);
        Button btnCancle = view.findViewById(R.id.btnCancle);
        Button btnSend = view.findViewById(R.id.btnSend);

        receiverEmail.setText(binding.email.getText().toString());
        subject.setText(getString(R.string.subject)+mPet.getPetName()+PLUS+mPet.getBreed());

        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0){
                    contentLayout.setErrorEnabled(false);
                }
                else {
                    contentLayout.setErrorEnabled(true);
                    contentLayout.setError(getString(R.string.txt_blank_required_field));
                }

            }
        });

        builder.setView(view);
        builder.setCancelable(false);

        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        alertDialog.show();
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (content.getText().toString().isEmpty()) {
                    contentLayout.setErrorEnabled(true);
                    contentLayout.setError(getString(R.string.txt_blank_required_field));
                }
                else {
                    contentLayout.setErrorEnabled(false);
                    Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse(INTENT_MAIL_KEY +receiverEmail.getText().toString()+INTENT_SUB_KEY+subject.getText().toString()
                            +INTENT_CONTENT_KEY + content.getText().toString()));
                    try {
                        startActivity(Intent.createChooser(i,getString(R.string.txt_title_email_intent)));
                        alertDialog.dismiss();
                    }catch (android.content.ActivityNotFoundException ex){
                        Toast.makeText(PetProfileActivity.this,getString(R.string.txt_error_intent_email),Toast.LENGTH_SHORT).show();
                    }


                }
            }
        });

        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }
    private String returnStatus(float number){
        String levelStatus = EMPTY_VALUE;
        if (number <= LEVEL_2){
            levelStatus = getString(R.string.txt_low)+getString(R.string.space)+ number+MAX_SLIDER;
        }
        if(number > LEVEL_2 && number <= LEVEL_4 ){
            levelStatus = getString(R.string.txt_moderate)+getString(R.string.space)+ number+MAX_SLIDER;
        }
        if(number > LEVEL_4 && number <= LEVEL_6){
            levelStatus = getString(R.string.txt_high)+getString(R.string.space)+ number+MAX_SLIDER;
        }

        return levelStatus;
    }

    private void QRCode() {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(mPet.getPetId(), BarcodeFormat.QR_CODE, 100, 100);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            binding.imgQRCode.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }


    private void getToolBar() {
        //      TODO  To set app bar
        toolbarBinding = binding.toolbarDetail;
        setSupportActionBar(toolbarBinding.getRoot());
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //Change Text
        toolbarBinding.toolbarTitle.setText(mPet.getPetName());
        toolbarBinding.toolbarSearch.setVisibility(View.GONE);

        setContentView(binding.getRoot());


    }

    //To back previous page
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        Animatoo.animateSlideRight(PetProfileActivity.this);
        return super.onSupportNavigateUp();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnShare:
                sendTextPet();
                break;
            default:
                break;
        }

    }


    private void sendTextPet() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, mPet.getPetId());
        sendIntent.setType(INTENT_PET_ID);

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    private void scheduleDismiss() {
        Handler handler = new Handler();
        handler.postDelayed(() -> loadingProgress.dismiss(), 3000);
    }

    private void showLoadingDialog() {
        loadingProgress = KProgressHUD.create(PetProfileActivity.this).setLabel(getString(R.string.txt_pleaseWait))
                .setDetailsLabel(getString(R.string.txt_loading))
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
    }

}