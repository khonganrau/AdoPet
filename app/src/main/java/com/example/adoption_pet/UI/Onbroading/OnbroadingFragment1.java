package com.example.adoption_pet.UI.Onbroading;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.adoption_pet.R;


public class OnbroadingFragment1 extends Fragment {

    private View mView;

    public OnbroadingFragment1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_onbroading1, container, false);


        return mView;
    }
}