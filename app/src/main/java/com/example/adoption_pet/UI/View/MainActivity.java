package com.example.adoption_pet.UI.View;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.adoption_pet.R;
import com.example.adoption_pet.UI.Widget.OnboardingActivity;
import com.example.adoption_pet.utils.Constants;

public class MainActivity extends AppCompatActivity {


    Animation topAnim;
    Animation bottomAnim;
    ImageView logo;
    TextView slogan;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hook();
        actionPage();
    }

    private void actionPage() {
        // set animation
        logo.setAnimation(topAnim);
        slogan.setAnimation(bottomAnim);

        handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, OnboardingActivity.class);
            startActivity(intent);
            Animatoo.animateSlideLeft(MainActivity.this);
            finish();
        }, Constants.TOAST_LENGTH_SHORT);

    }

    private void hook() {
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        logo = findViewById(R.id.img_logo);
        slogan = findViewById(R.id.txt_slogan);
    }
}