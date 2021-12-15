package com.example.adoption_pet.UI.View;

import static com.example.adoption_pet.utils.Constants.DEFAULT_NUMBER;
import static com.example.adoption_pet.utils.Constants.KEY_INTENT;
import static com.example.adoption_pet.utils.Constants.TAG_RESULT;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.adoption_pet.Adapter.PetAdapter;
import com.example.adoption_pet.R;
import com.example.adoption_pet.databinding.ActivityResultSearchBinding;
import com.example.adoption_pet.databinding.ToolbarBinding;
import com.example.adoption_pet.model.Pet;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;

public class ResultSearchActivity extends AppCompatActivity implements PetAdapter.PetClickListener {

    private ActivityResultSearchBinding binding;

    private ToolbarBinding toolbarBinding;

    private ArrayList<Pet> resultsList;

    private KProgressHUD loadingProgress;

    private Toast mToastText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        binding = ActivityResultSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mToastText = new Toast(getApplicationContext());

        resultsList = new ArrayList<>();

        getToolBar();

        Bundle dataReceive = getIntent().getExtras();

        if(dataReceive != null){
            Log.d(TAG_RESULT,"Successful bundled");
            resultsList = dataReceive.getParcelableArrayList("ResultList");
            int numOfResults = resultsList.size();

            if(numOfResults != DEFAULT_NUMBER){

                binding.tvNotification.setVisibility(View.GONE);

                binding.results.setVisibility(View.VISIBLE);

                binding.results.setHasFixedSize(true);

                PetAdapter adapterResult = new PetAdapter(ResultSearchActivity.this,resultsList,this);

                showLoadingDialog();
                scheduleDismiss();
                binding.results.setAdapter(adapterResult);
            }
            else {
                binding.tvNotification.setText(getString(R.string.txt_not_have_fit_item));
                binding.tvNotification.setVisibility(View.VISIBLE);
                binding.results.setVisibility(View.INVISIBLE);
            }


        }
        else {
            Log.d(TAG_RESULT,getString(R.string.txt_error_resultList));
        }

    }

    private void showLoadingDialog() {
        loadingProgress = KProgressHUD.create(ResultSearchActivity.this).setLabel(getString(R.string.txt_pleaseWait))
                .setDetailsLabel(getString(R.string.txt_loading))
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
    }


    private void getToolBar() {
        toolbarBinding = binding.toolbarResult;
        setSupportActionBar(toolbarBinding.getRoot());
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //Change Text
        toolbarBinding.toolbarTitle.setText(getString(R.string.txt_title_result));

        setContentView(binding.getRoot());

//        toolbarBinding.toolbarSearch.setOnClickListener(this);
    }

    //To back previous page
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onPetClicked(Pet mPet) {
        Intent i = new Intent(ResultSearchActivity.this, PetProfileActivity.class);
        i.putExtra(KEY_INTENT, mPet);
        startActivity(i);
        showToast(mPet.getPetName(), R.drawable.ic_baseline_pets_24);

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

    private void scheduleDismiss() {
        Handler handler = new Handler();
        handler.postDelayed(() -> loadingProgress.dismiss(), 3000);
    }
}