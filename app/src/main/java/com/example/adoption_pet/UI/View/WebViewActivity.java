package com.example.adoption_pet.UI.View;

import android.os.Bundle;
import android.view.WindowManager;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.example.adoption_pet.databinding.ActivityWebViewBinding;
import com.example.adoption_pet.utils.Constants;

public class WebViewActivity extends AppCompatActivity {


    private ActivityWebViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        binding = ActivityWebViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.webView.setWebViewClient(new WebViewClient());
        binding.webView.loadUrl(Constants.LINK_MANAGE);
    }


    @Override
    public void onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}