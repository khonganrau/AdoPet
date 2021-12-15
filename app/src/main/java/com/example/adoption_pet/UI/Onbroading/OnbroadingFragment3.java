package com.example.adoption_pet.UI.Onbroading;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.adoption_pet.R;
import com.example.adoption_pet.UI.View.LoginActivity;


public class OnbroadingFragment3 extends Fragment {

    private View mView;
    private Button btnGetStart;

    public OnbroadingFragment3() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_onbroading3, container, false);


        btnGetStart = mView.findViewById(R.id.btn_get_start);
        btnGetStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), LoginActivity.class);
                getActivity().startActivity(i);
            }
        });
        return mView;
    }
}