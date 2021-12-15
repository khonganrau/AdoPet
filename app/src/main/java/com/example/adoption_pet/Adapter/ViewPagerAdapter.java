package com.example.adoption_pet.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.adoption_pet.UI.Onbroading.OnbroadingFragment1;
import com.example.adoption_pet.UI.Onbroading.OnbroadingFragment2;
import com.example.adoption_pet.UI.Onbroading.OnbroadingFragment3;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {


    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new OnbroadingFragment1();
            case 1:
                return new OnbroadingFragment2();
            case 2:
                return new OnbroadingFragment3();
            default:
                return new OnbroadingFragment1();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
