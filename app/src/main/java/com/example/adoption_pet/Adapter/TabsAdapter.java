package com.example.adoption_pet.Adapter;

import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.adoption_pet.UI.View.AdvancedFragment;
import com.example.adoption_pet.UI.View.BasicFragment;

public class TabsAdapter extends FragmentPagerAdapter {

    private int numOfTabs;
    public TabsAdapter(@NonNull FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new BasicFragment();

            case 1:
                return new AdvancedFragment();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }

}
