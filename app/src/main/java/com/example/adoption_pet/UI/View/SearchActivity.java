package com.example.adoption_pet.UI.View;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.adoption_pet.Adapter.TabsAdapter;
import com.example.adoption_pet.R;
import com.example.adoption_pet.databinding.ActivitySearchBinding;
import com.example.adoption_pet.databinding.ToolbarBinding;
import com.google.android.material.tabs.TabLayout;

public class SearchActivity extends AppCompatActivity {

    private BasicFragment basicFragment;
    private AdvancedFragment advancedFragment;

    private ActivitySearchBinding binding;

    private ToolbarBinding toolbarBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.viewPager.beginFakeDrag();

        TabsAdapter tabsAdapter = new TabsAdapter(getSupportFragmentManager(),binding.tabLayoutSearch.getTabCount());

        binding.viewPager.setAdapter(tabsAdapter);

        binding.tabLayoutSearch.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        getToolBar();



    }

    private void getToolBar() {
        toolbarBinding = binding.toolbarSearch;
        setSupportActionBar(toolbarBinding.getRoot());
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //Change Text
        toolbarBinding.toolbarTitle.setText(getString(R.string.btn_search));

        setContentView(binding.getRoot());


    }

    //To back previous page
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        Animatoo.animateSlideRight(SearchActivity.this);
        return super.onSupportNavigateUp();
    }
}