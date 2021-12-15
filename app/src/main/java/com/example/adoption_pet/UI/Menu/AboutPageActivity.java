package com.example.adoption_pet.UI.Menu;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.amplitude.BuildConfig;
import com.example.adoption_pet.R;
import com.example.adoption_pet.databinding.ActivityAboutPageBinding;
import com.example.adoption_pet.databinding.ToolbarBinding;

import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutPageActivity extends AppCompatActivity {

    private ActivityAboutPageBinding binding;
    private ToolbarBinding toolbarBinding;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutPageBinding.inflate(getLayoutInflater());

        toolbarBinding = binding.toolbarAboutPage;
        setSupportActionBar(toolbarBinding.getRoot());
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //Change Text
        toolbarBinding.toolbarTitle.setText(getString(R.string.txt_itemmenu_AboutApp));

        setContentView(binding.getRoot());

        InformationOwn();


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }


    private void InformationOwn() {
        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.mipmap.ic_icon_app)
                .setDescription(getString(R.string.app_des))
                .addItem(new Element(getString(R.string.version) + BuildConfig.VERSION_NAME, R.drawable.ic_baseline_info_24))
                .addGroup(getString(R.string.txt_connect_with_us))
                .addEmail(getString(R.string.email), getString(R.string.aboutEmail))
                .addFacebook(getString(R.string.link_fb), getString(R.string.facebook))
                .addGitHub(getString(R.string.link_github), getString(R.string.github))
                .addInstagram(getString(R.string.instagram))
                .addItem(createCopyright())
                .create();
        setContentView(aboutPage);
    }

    private Element createCopyright() {
        Element copyright = new Element();
        @SuppressLint("DefaultLocale") final String copyrightString = String.format("Copyright %d by Duong Manh Quynh", Calendar.getInstance().get(Calendar.YEAR));
        copyright.setTitle(copyrightString);
        copyright.setGravity(Gravity.CENTER);
        copyright.setOnClickListener(v -> Toast.makeText(AboutPageActivity.this, copyrightString, Toast.LENGTH_SHORT).show());
        return copyright;
    }
}