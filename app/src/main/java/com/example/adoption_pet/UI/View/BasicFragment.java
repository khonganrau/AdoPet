package com.example.adoption_pet.UI.View;

import static com.example.adoption_pet.utils.Constants.DEFAULT_OPTION;
import static com.example.adoption_pet.utils.Constants.KEY_ARRAY;
import static com.example.adoption_pet.utils.Constants.PET;
import static com.example.adoption_pet.utils.Constants.PROVINCE;
import static com.example.adoption_pet.utils.Constants.SIZE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.adoption_pet.R;
import com.example.adoption_pet.databinding.FragmentBasicBinding;
import com.example.adoption_pet.model.Pet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class BasicFragment extends Fragment {

    private FragmentBasicBinding binding;
    private String gender = DEFAULT_OPTION;
    private String size = DEFAULT_OPTION;
    private String steri = DEFAULT_OPTION;
    private String age = DEFAULT_OPTION;

    private DatabaseReference df;

    private ArrayList<Pet> basicResults;


    public BasicFragment() {
        super(R.layout.fragment_basic);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBasicBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        df = FirebaseDatabase.getInstance().getReference().child(PET);
        //provinces
        String[] provinces = getResources().getStringArray(R.array.city_array);
        ArrayAdapter<String> provinceAdapter = new ArrayAdapter<>(view.getContext(), R.layout.item_dropdown_list, provinces);
        binding.province.setAdapter(provinceAdapter);

        //type
        String[] types = getResources().getStringArray(R.array.type);
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(view.getContext(), R.layout.item_dropdown_list, types);
        binding.type.setAdapter(typeAdapter);

        //breed
        binding.type.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String typeValue = binding.type.getText().toString();
                if (typeValue.equals(getString(R.string.cat))) {
                    binding.layoutBreed.setEnabled(true);
                    String[] catBreed = getResources().getStringArray(R.array.beeds_cat_search);
                    ArrayAdapter<String> catAdapter = new ArrayAdapter<>(view.getContext(), R.layout.item_dropdown_list, catBreed);
                    binding.breed.setAdapter(catAdapter);
                } else if (typeValue.equals(getString(R.string.dog))) {
                    binding.layoutBreed.setEnabled(true);
                    String[] dogBreed = getResources().getStringArray(R.array.beeds_dog_search);
                    ArrayAdapter<String> dogAdapter = new ArrayAdapter<>(view.getContext(), R.layout.item_dropdown_list, dogBreed);
                    binding.breed.setAdapter(dogAdapter);
                } else if (typeValue.equals(getString(R.string.txt_option_any))) {
                    binding.breed.setEnabled(false);
                    binding.layoutBreed.setEnabled(false);
                }

            }
        });


        binding.btnSearch.setOnClickListener(v -> {
            basicResults = new ArrayList<>();
            String zipCode = binding.zipCode.getText().toString().trim();
            String province = binding.province.getText().toString().trim();
            String type = binding.type.getText().toString().trim();
            String breed = binding.breed.getText().toString().trim();
            if ((province.isEmpty() || type.isEmpty()) && binding.gender.getCheckedRadioButtonId() == R.id.rbtn_gender_any
                    && binding.sterilization.getCheckedRadioButtonId() == R.id.steri_any && binding.size.getCheckedRadioButtonId() == R.id.rbtn_size_any &&
                    binding.age.getCheckedRadioButtonId() == R.id.rbtn_ager_any) {

                binding.layoutProvince.setErrorEnabled(true);
                binding.layoutProvince.setError(getString(R.string.txt_error_province));

                binding.typeLayout.setErrorEnabled(true);
                binding.typeLayout.setError(getString(R.string.txt_error_province));


                Toast.makeText(view.getContext(), getString(R.string.txt_search_empty), Toast.LENGTH_SHORT).show();

            } else {
                if ((!breed.isEmpty() || !breed.equals(DEFAULT_OPTION))
                        && !gender.equals(DEFAULT_OPTION)
                        && !steri.equals(DEFAULT_OPTION)
                        && !age.equals(DEFAULT_OPTION)
                        && !size.equals(DEFAULT_OPTION)) {
                    searchAll(province,type,breed,gender,steri,age,size);
                }

                //province, type,breed
                if ((!breed.isEmpty() || !breed.equals(DEFAULT_OPTION))
                        && gender.equals(DEFAULT_OPTION)
                        && steri.equals(DEFAULT_OPTION)
                        && age.equals(DEFAULT_OPTION)
                        && size.equals(DEFAULT_OPTION)){
                    searchPtb(province,type,breed);
                }

                //gender, steri
                if ((breed.isEmpty() || breed.equals(DEFAULT_OPTION))
                        && !gender.equals(DEFAULT_OPTION)
                        && !steri.equals(DEFAULT_OPTION)
                        && age.equals(DEFAULT_OPTION)
                        && size.equals(DEFAULT_OPTION)){
                    searchGenderSteri(province,type,gender,steri);
                }


                //breed,gender
                if ((!breed.isEmpty() || !breed.equals(DEFAULT_OPTION))
                        && !gender.equals(DEFAULT_OPTION)
                        && steri.equals(DEFAULT_OPTION)
                        && age.equals(DEFAULT_OPTION)
                        && size.equals(DEFAULT_OPTION)){
                    searchPtbg(province,type,breed,gender);
                }


                //breed,gender,steri
                if ((!breed.isEmpty() || !breed.equals(DEFAULT_OPTION))
                        && !gender.equals(DEFAULT_OPTION)
                        && !steri.equals(DEFAULT_OPTION)
                        && age.equals(DEFAULT_OPTION)
                        && size.equals(DEFAULT_OPTION)){
                    searchPtbgs(province,type,breed,gender,steri);
                }

                //breed, gender, age
                if ((!breed.isEmpty() || !breed.equals(DEFAULT_OPTION))
                        && !gender.equals(DEFAULT_OPTION)
                        && steri.equals(DEFAULT_OPTION)
                        && !age.equals(DEFAULT_OPTION)
                        && size.equals(DEFAULT_OPTION)){
                    searchPtbga(province,type,breed,gender,age);
                }

                //breed, gender, size
                if ((!breed.isEmpty() || !breed.equals(DEFAULT_OPTION))
                        && !gender.equals(DEFAULT_OPTION)
                        && steri.equals(DEFAULT_OPTION)
                        && age.equals(DEFAULT_OPTION)
                        && !size.equals(DEFAULT_OPTION)){
                    searchPtbgz(province,type,breed,gender,size);
                }

                //breed,gender,steri,age
                if ((!breed.isEmpty() || !breed.equals(DEFAULT_OPTION))
                        && !gender.equals(DEFAULT_OPTION)
                        && !steri.equals(DEFAULT_OPTION)
                        && !age.equals(DEFAULT_OPTION)
                        && size.equals(DEFAULT_OPTION)){
                    searchPtbgsa(province,type,breed,gender,steri,age);
                }

                //breed, gender, steri, size
                if ((!breed.isEmpty() || !breed.equals(DEFAULT_OPTION))
                        && !gender.equals(DEFAULT_OPTION)
                        && !steri.equals(DEFAULT_OPTION)
                        && age.equals(DEFAULT_OPTION)
                        && !size.equals(DEFAULT_OPTION)){
                    searchPtbgsz(province,type,breed,gender,steri,size);
                }

                //breed, size
                if ((!breed.isEmpty() || !breed.equals(DEFAULT_OPTION))
                        && gender.equals(DEFAULT_OPTION)
                        && steri.equals(DEFAULT_OPTION)
                        && age.equals(DEFAULT_OPTION)
                        && !size.equals(DEFAULT_OPTION)){
                    searchBreedSize(province,type,breed,size);
                }


                //breed, steri
                if ((!breed.isEmpty() || !breed.equals(DEFAULT_OPTION))
                        && gender.equals(DEFAULT_OPTION)
                        && !steri.equals(DEFAULT_OPTION)
                        && age.equals(DEFAULT_OPTION)
                        && size.equals(DEFAULT_OPTION)){
                    searchBreedSteri(province,type,breed,steri);
                }

                //breed, steri, size
                if ((!breed.isEmpty() || !breed.equals(DEFAULT_OPTION))
                        && gender.equals(DEFAULT_OPTION)
                        && !steri.equals(DEFAULT_OPTION)
                        && age.equals(DEFAULT_OPTION)
                        && !size.equals(DEFAULT_OPTION)){
                    searchBreedSteriSize(province,type,breed,steri,size);
                }

                // breed, steri, age
                if ((!breed.isEmpty() || !breed.equals(DEFAULT_OPTION))
                        && gender.equals(DEFAULT_OPTION)
                        && !steri.equals(DEFAULT_OPTION)
                        && !age.equals(DEFAULT_OPTION)
                        && size.equals(DEFAULT_OPTION)){
                    searchBreedSteriAge(province,type,breed,steri,age);
                }
                //breed, age
                if ((!breed.isEmpty() || !breed.equals(DEFAULT_OPTION))
                        && gender.equals(DEFAULT_OPTION)
                        && steri.equals(DEFAULT_OPTION)
                        && !age.equals(DEFAULT_OPTION)
                        && size.equals(DEFAULT_OPTION)){
                    searchBreedAge(province,type,breed,age);
                }

                //breed, age, size
                if ((!breed.isEmpty() || !breed.equals(DEFAULT_OPTION))
                        && gender.equals(DEFAULT_OPTION)
                        && steri.equals(DEFAULT_OPTION)
                        && !age.equals(DEFAULT_OPTION)
                        && !size.equals(DEFAULT_OPTION)){
                    searchBreedAgeSize(province,type,breed,age,size);
                }





                //gender, steri, age
                if ((breed.isEmpty() || breed.equals(DEFAULT_OPTION))
                        && !gender.equals(DEFAULT_OPTION)
                        && !steri.equals(DEFAULT_OPTION)
                        && !age.equals(DEFAULT_OPTION)
                        && size.equals(DEFAULT_OPTION)){
                    searchGenderSteriAge(province,type,gender,steri,age);
                }

                //gender, steri, size
                if ((breed.isEmpty() || breed.equals(DEFAULT_OPTION))
                        && !gender.equals(DEFAULT_OPTION)
                        && !steri.equals(DEFAULT_OPTION)
                        &&  age.equals(DEFAULT_OPTION)
                        && !size.equals(DEFAULT_OPTION)){
                    searchGenderSteriSize(province,type,gender,steri,size);
                }


                //gender, age
                if ((breed.isEmpty() || breed.equals(DEFAULT_OPTION))
                        && !gender.equals(DEFAULT_OPTION)
                        && steri.equals(DEFAULT_OPTION)
                        && !age.equals(DEFAULT_OPTION)
                        && size.equals(DEFAULT_OPTION)){
                    searchGenderAge(province,type,gender,age);
                }

                //gender, age, size
                if ((breed.isEmpty() || breed.equals(DEFAULT_OPTION))
                        && !gender.equals(DEFAULT_OPTION)
                        && steri.equals(DEFAULT_OPTION)
                        && !age.equals(DEFAULT_OPTION)
                        && !size.equals(DEFAULT_OPTION)){
                    searchGenderAgeSize(province,type,gender,age,size);
                }

                //gender, size
                if ((breed.isEmpty() || breed.equals(DEFAULT_OPTION))
                        && !gender.equals(DEFAULT_OPTION)
                        && steri.equals(DEFAULT_OPTION)
                        && age.equals(DEFAULT_OPTION)
                        && !size.equals(DEFAULT_OPTION)){
                    searchGenderSize(province,type,gender,size);
                }

                //gender, steri, age, size
                if ((breed.isEmpty() || breed.equals(DEFAULT_OPTION))
                        && !gender.equals(DEFAULT_OPTION)
                        && !steri.equals(DEFAULT_OPTION)
                        && !age.equals(DEFAULT_OPTION)
                        && !size.equals(DEFAULT_OPTION)){
                    searchGenderSteriAgeSize(province,type,gender,size);
                }

                //steri, age, size
                if ((breed.isEmpty() || breed.equals(DEFAULT_OPTION))
                        && gender.equals(DEFAULT_OPTION)
                        && !steri.equals(DEFAULT_OPTION)
                        && !age.equals(DEFAULT_OPTION)
                        && !size.equals(DEFAULT_OPTION)){
                    searchSteriAgeSize(province,type,steri,age,size);
                }


                //steri, age
                if ((breed.isEmpty() || breed.equals(DEFAULT_OPTION))
                        && gender.equals(DEFAULT_OPTION)
                        && !steri.equals(DEFAULT_OPTION)
                        && !age.equals(DEFAULT_OPTION)
                        && size.equals(DEFAULT_OPTION)){
                    searchSteriAge(province,type,steri,age);
                }

                //steri,size
                if ((breed.isEmpty() || breed.equals(DEFAULT_OPTION))
                        && gender.equals(DEFAULT_OPTION)
                        && !steri.equals(DEFAULT_OPTION)
                        && age.equals(DEFAULT_OPTION)
                        && !size.equals(DEFAULT_OPTION)){
                    searchSteriSize(province,type,steri,size);
                }

                //age,size
                if ((breed.isEmpty() || breed.equals(DEFAULT_OPTION))
                        && gender.equals(DEFAULT_OPTION)
                        && steri.equals(DEFAULT_OPTION)
                        && !age.equals(DEFAULT_OPTION)
                        && !size.equals(DEFAULT_OPTION)){
                    searchAgeSize(province,type,age,size);
                }

                //genders
                if ((breed.isEmpty() || breed.equals(DEFAULT_OPTION))
                        && !gender.equals(DEFAULT_OPTION)
                        && steri.equals(DEFAULT_OPTION)
                        && age.equals(DEFAULT_OPTION)
                        && size.equals(DEFAULT_OPTION)){
                    searchGender(province,type,gender);
                }

                //steri
                if ((breed.isEmpty() || breed.equals(DEFAULT_OPTION))
                        && gender.equals(DEFAULT_OPTION)
                        && !steri.equals(DEFAULT_OPTION)
                        && age.equals(DEFAULT_OPTION)
                        && size.equals(DEFAULT_OPTION)){
                    searchSteri(province,type,steri);
                }

                //age
                if ((breed.isEmpty() || breed.equals(DEFAULT_OPTION))
                        && gender.equals(DEFAULT_OPTION)
                        && steri.equals(DEFAULT_OPTION)
                        && !age.equals(DEFAULT_OPTION)
                        && size.equals(DEFAULT_OPTION)){
                    searchAge(province,type,age);
                }

                //size
                if ((breed.isEmpty() || breed.equals(DEFAULT_OPTION))
                        && gender.equals(DEFAULT_OPTION)
                        && steri.equals(DEFAULT_OPTION)
                        && age.equals(DEFAULT_OPTION)
                        && !size.equals(DEFAULT_OPTION)){
                    searchSize(province,type,size);
                }

                //province, type
                if ((breed.isEmpty() || breed.equals(DEFAULT_OPTION))
                        && gender.equals(DEFAULT_OPTION)
                        && steri.equals(DEFAULT_OPTION)
                        && age.equals(DEFAULT_OPTION)
                        && size.equals(DEFAULT_OPTION)){
                    searchProvinceType(province,type);
                }

            }
        });


        setGender();

        setSterilization();

        setSize();

        setAge();

        province();

        type();

    }

    private void searchBasic(String province, String type, String breed, String gender, String steri
            , String age, String size) {





    }

    private void searchProvinceType(String province, String type) {
        df.orderByChild(PROVINCE).equalTo(province).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Pet rPet = ds.getValue(Pet.class);
                        if (rPet.getPetType().equals(type)) {
                            basicResults.add(rPet);
                        }
                    }
                    Log.d(SIZE, getString(R.string.txt_data_existed));

                    navigateToResult(basicResults);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void searchSize(String province, String type, String size) {
        df.orderByChild(PROVINCE).equalTo(province).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Pet rPet = ds.getValue(Pet.class);
                        if (rPet.getPetType().equals(type) && rPet.getPetSize().equals(size)) {
                            basicResults.add(rPet);
                        }
                    }
                    Log.d(SIZE, getString(R.string.txt_data_existed));

                    navigateToResult(basicResults);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchAge(String province, String type, String age) {
        df.orderByChild(PROVINCE).equalTo(province).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Pet rPet = ds.getValue(Pet.class);
                        if (rPet.getPetType().equals(type) && rPet.getPetAger().equals(age)) {
                            basicResults.add(rPet);
                        }
                    }
                    Log.d(SIZE, getString(R.string.txt_data_existed));

                    navigateToResult(basicResults);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchSteri(String province, String type, String steri) {
        df.orderByChild(PROVINCE).equalTo(province).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Pet rPet = ds.getValue(Pet.class);
                        if (rPet.getPetType().equals(type) && rPet.getSteriStatus().equals(steri)) {
                            basicResults.add(rPet);
                        }
                    }
                    Log.d(SIZE, getString(R.string.txt_data_existed));

                    navigateToResult(basicResults);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchGender(String province, String type, String gender) {
        df.orderByChild(PROVINCE).equalTo(province).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Pet rPet = ds.getValue(Pet.class);
                        if (rPet.getPetType().equals(type) && rPet.getGender().equals(gender)) {
                            basicResults.add(rPet);
                        }
                    }
                    Log.d(SIZE, getString(R.string.txt_data_existed));

                    navigateToResult(basicResults);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchAgeSize(String province, String type, String age, String size) {
        df.orderByChild(PROVINCE).equalTo(province).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Pet rPet = ds.getValue(Pet.class);
                        if (rPet.getPetType().equals(type) && rPet.getPetAger().equals(age)
                                && rPet.getPetSize().equals(size)) {
                            basicResults.add(rPet);
                        }
                    }
                    Log.d(SIZE, getString(R.string.txt_data_existed));

                    navigateToResult(basicResults);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchSteriSize(String province, String type, String steri, String size) {
        df.orderByChild(PROVINCE).equalTo(province).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Pet rPet = ds.getValue(Pet.class);
                        if (rPet.getPetType().equals(type) && rPet.getSteriStatus().equals(steri)
                                && rPet.getPetSize().equals(size)) {
                            basicResults.add(rPet);
                        }
                    }
                    Log.d(SIZE, getString(R.string.txt_data_existed));

                    navigateToResult(basicResults);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchSteriAge(String province, String type, String steri, String age) {
        df.orderByChild(PROVINCE).equalTo(province).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Pet rPet = ds.getValue(Pet.class);
                        if (rPet.getPetType().equals(type) && rPet.getSteriStatus().equals(steri)
                                && rPet.getPetAger().equals(age)) {
                            basicResults.add(rPet);
                        }
                    }
                    Log.d(SIZE, getString(R.string.txt_data_existed));

                    navigateToResult(basicResults);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchSteriAgeSize(String province, String type, String steri, String age, String size) {
        df.orderByChild(PROVINCE).equalTo(province).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Pet rPet = ds.getValue(Pet.class);
                        if (rPet.getPetType().equals(type) && rPet.getSteriStatus().equals(steri)
                                && rPet.getPetAger().equals(age)
                                && rPet.getPetSize().equals(size)) {
                            basicResults.add(rPet);
                        }
                    }
                    Log.d(SIZE, getString(R.string.txt_data_existed));

                    navigateToResult(basicResults);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchGenderSteriAgeSize(String province, String type, String gender, String size) {
        df.orderByChild(PROVINCE).equalTo(province).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Pet rPet = ds.getValue(Pet.class);
                        if (rPet.getPetType().equals(type) && rPet.getGender().equals(gender) &&
                                rPet.getSteriStatus().equals(steri) && rPet.getPetAger().equals(age)
                                && rPet.getPetSize().equals(size)) {
                            basicResults.add(rPet);
                        }
                    }
                    Log.d(SIZE, getString(R.string.txt_data_existed));

                    navigateToResult(basicResults);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchGenderAgeSize(String province, String type, String gender, String age, String size) {
        df.orderByChild(PROVINCE).equalTo(province).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Pet rPet = ds.getValue(Pet.class);
                        if (rPet.getPetType().equals(type) && rPet.getGender().equals(gender)
                                && rPet.getPetAger().equals(age)&& rPet.getPetSize().equals(size)) {
                            basicResults.add(rPet);
                        }
                    }
                    Log.d(SIZE, getString(R.string.txt_data_existed));

                    navigateToResult(basicResults);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchGenderSteriSize(String province, String type, String gender, String steri, String size) {
        df.orderByChild(PROVINCE).equalTo(province).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Pet rPet = ds.getValue(Pet.class);
                        if (rPet.getPetType().equals(type) && rPet.getGender().equals(gender)
                                && rPet.getSteriStatus().equals(steri)&& rPet.getPetSize().equals(size)) {
                            basicResults.add(rPet);
                        }
                    }
                    Log.d(SIZE, getString(R.string.txt_data_existed));

                    navigateToResult(basicResults);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchGenderSteriAge(String province, String type, String gender, String steri, String age) {
        df.orderByChild(PROVINCE).equalTo(province).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Pet rPet = ds.getValue(Pet.class);
                        if (rPet.getPetType().equals(type) && rPet.getGender().equals(gender)
                                && rPet.getSteriStatus().equals(steri)&& rPet.getPetAger().equals(age)) {
                            basicResults.add(rPet);
                        }
                    }
                    Log.d(SIZE, getString(R.string.txt_data_existed));

                    navigateToResult(basicResults);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchBreedAgeSize(String province, String type, String breed, String age, String size) {
        df.orderByChild(PROVINCE).equalTo(province).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Pet rPet = ds.getValue(Pet.class);
                        if (rPet.getPetType().equals(type) && rPet.getBreed().equals(breed) &&
                                rPet.getPetSize().equals(size) && rPet.getPetAger().equals(age)) {
                            basicResults.add(rPet);
                        }
                    }
                    Log.d(SIZE, getString(R.string.txt_data_existed));

                    navigateToResult(basicResults);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchBreedSteriAge(String province, String type, String breed, String steri, String age) {
        df.orderByChild(PROVINCE).equalTo(province).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Pet rPet = ds.getValue(Pet.class);
                        if (rPet.getPetType().equals(type) && rPet.getBreed().equals(breed) &&
                                rPet.getSteriStatus().equals(steri) && rPet.getPetAger().equals(age)) {
                            basicResults.add(rPet);
                        }
                    }
                    Log.d(SIZE, getString(R.string.txt_data_existed));

                    navigateToResult(basicResults);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchBreedSteriSize(String province, String type, String breed, String steri, String size) {
        df.orderByChild(PROVINCE).equalTo(province).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Pet rPet = ds.getValue(Pet.class);
                        if (rPet.getPetType().equals(type) && rPet.getBreed().equals(breed) &&
                                rPet.getSteriStatus().equals(steri) && rPet.getPetSize().equals(size)) {
                            basicResults.add(rPet);
                        }
                    }
                    Log.d(SIZE, getString(R.string.txt_data_existed));

                    navigateToResult(basicResults);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchPtbgsz(String province, String type, String breed, String gender, String steri, String size) {
        df.orderByChild(PROVINCE).equalTo(province).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Pet rPet = ds.getValue(Pet.class);
                        if (rPet.getPetType().equals(type) && rPet.getBreed().equals(breed) &&
                                rPet.getGender().equals(gender) && rPet.getSteriStatus().equals(steri) && rPet.getPetSize().equals(size)) {
                            basicResults.add(rPet);
                        }
                    }
                    Log.d(SIZE, getString(R.string.txt_data_existed));

                    navigateToResult(basicResults);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchPtbgz(String province, String type, String breed, String gender, String size) {

        df.orderByChild(PROVINCE).equalTo(province).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Pet rPet = ds.getValue(Pet.class);
                        if (rPet.getPetType().equals(type) && rPet.getBreed().equals(breed) &&
                                rPet.getGender().equals(gender) && rPet.getPetSize().equals(size)) {
                            basicResults.add(rPet);
                        }
                    }
                    Log.d(SIZE, getString(R.string.txt_data_existed));

                    navigateToResult(basicResults);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchPtbga(String province, String type, String breed, String gender, String age) {
        df.orderByChild(PROVINCE).equalTo(province).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Pet rPet = ds.getValue(Pet.class);
                        if (rPet.getPetType().equals(type) && rPet.getBreed().equals(breed) &&
                        rPet.getGender().equals(gender) && rPet.getPetAger().equals(age)) {
                            basicResults.add(rPet);
                        }
                    }
                    Log.d(SIZE, getString(R.string.txt_data_existed));

                    navigateToResult(basicResults);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchGenderSize(String province, String type, String gender, String size) {
        df.orderByChild(PROVINCE).equalTo(province).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Pet rPet = ds.getValue(Pet.class);
                        if (rPet.getPetType().equals(type) && rPet.getGender().equals(gender) && rPet.getPetId().equals(size)) {
                            basicResults.add(rPet);
                        }
                    }
                    Log.d(SIZE, getString(R.string.txt_data_existed));

                    navigateToResult(basicResults);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchGenderAge(String province, String type, String gender, String age) {
        df.orderByChild(PROVINCE).equalTo(province).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Pet rPet = ds.getValue(Pet.class);
                        if (rPet.getPetType().equals(type) && rPet.getGender().equals(gender) && rPet.getPetAger().equals(age)) {
                            basicResults.add(rPet);
                        }
                    }
                    Log.d(SIZE, getString(R.string.txt_data_existed));

                    navigateToResult(basicResults);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchGenderSteri(String province,String type,String gender, String steri) {
        df.orderByChild(PROVINCE).equalTo(province).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Pet rPet = ds.getValue(Pet.class);
                        if (rPet.getPetType().equals(type) && rPet.getGender().equals(gender) && rPet.getSteriStatus().equals(steri)) {
                            basicResults.add(rPet);
                        }
                    }
                    Log.d(SIZE, getString(R.string.txt_data_existed));

                    navigateToResult(basicResults);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchBreedAge(String province, String type, String breed, String age) {
        df.orderByChild(PROVINCE).equalTo(province).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Pet rPet = ds.getValue(Pet.class);
                        if (rPet.getPetType().equals(type) && rPet.getBreed().equals(breed) &&rPet.getPetAger().equals(age)) {
                            basicResults.add(rPet);
                        }
                    }
                    Log.d(SIZE, getString(R.string.txt_data_existed));

                    navigateToResult(basicResults);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void searchBreedSteri(String province, String type, String breed, String steri) {
        df.orderByChild(PROVINCE).equalTo(province).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Pet rPet = ds.getValue(Pet.class);
                        if (rPet.getPetType().equals(type) && rPet.getBreed().equals(breed) &&rPet.getSteriStatus().equals(steri)) {
                            basicResults.add(rPet);
                        }
                    }
                    Log.d(SIZE, getString(R.string.txt_data_existed));

                    navigateToResult(basicResults);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchBreedSize(String province, String type, String breed, String size) {
        df.orderByChild(PROVINCE).equalTo(province).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Pet rPet = ds.getValue(Pet.class);
                        if (rPet.getPetType().equals(type) && rPet.getBreed().equals(breed) &&rPet.getPetSize().equals(size)) {
                            basicResults.add(rPet);
                        }
                    }
                    Log.d(SIZE, getString(R.string.txt_data_existed));

                    navigateToResult(basicResults);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Search by group - province, type, breed, gender,steri,age
    private void searchPtbgsa(String province, String type, String breed, String gender, String steri, String age) {
        df.orderByChild(PROVINCE).equalTo(province).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Pet rPet = ds.getValue(Pet.class);
                        if (rPet.getPetType().equals(type) && rPet.getBreed().equals(breed) && rPet.getGender().equals(gender)
                                && rPet.getSteriStatus().equals(steri) && rPet.getPetAger().equals(age)) {
                            basicResults.add(rPet);
                        }
                    }
                    Log.d(SIZE, getString(R.string.txt_data_existed));

                    navigateToResult(basicResults);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Search by group - province, type, breed, gender,steri
    private void searchPtbgs(String province, String type, String breed, String gender, String steri) {
        df.orderByChild(PROVINCE).equalTo(province).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Pet rPet = ds.getValue(Pet.class);
                        if (rPet.getPetType().equals(type) && rPet.getBreed().equals(breed) && rPet.getGender().equals(gender)
                        && rPet.getSteriStatus().equals(steri)) {
                            basicResults.add(rPet);
                        }
                    }
                    Log.d(SIZE, getString(R.string.txt_data_existed));

                    navigateToResult(basicResults);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Search by group - province, type, breed, gender
    private void searchPtbg(String province, String type, String breed, String gender) {
        df.orderByChild(PROVINCE).equalTo(province).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Pet rPet = ds.getValue(Pet.class);
                        if (rPet.getPetType().equals(type) && rPet.getBreed().equals(breed) && rPet.getGender().equals(gender)) {
                            basicResults.add(rPet);
                        }
                    }
                    Log.d(SIZE, getString(R.string.txt_data_existed));

                    navigateToResult(basicResults);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    //Search multiple attributes
    private void searchAll(String province, String type, String breed, String gender, String steri
            , String age, String size) {
        df.orderByChild(PROVINCE).equalTo(province).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Pet rPet = ds.getValue(Pet.class);
                        if (rPet.getPetType().equals(type) && rPet.getBreed().equals(breed) &&
                                rPet.getGender().equals(gender) && rPet.getSteriStatus().equals(steri)
                                && rPet.getPetAger().equals(age) && rPet.getPetSize().equals(size)) {
                            basicResults.add(rPet);
                        }
                    }
                    Log.d(SIZE, getString(R.string.txt_data_existed));

                    navigateToResult(basicResults);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    //Search by group - province, type, and breed
    private void searchPtb(String province, String type, String breed) {
        df.orderByChild(PROVINCE).equalTo(province).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Pet rPet = ds.getValue(Pet.class);
                        if (rPet.getPetType().equals(type) && rPet.getBreed().equals(breed)) {
                            basicResults.add(rPet);
                        }
                    }
                    Log.d(SIZE, getString(R.string.txt_data_existed));

                    navigateToResult(basicResults);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Search by group - province and type
    private void searchProType(String province, String type) {
        df.orderByChild(PROVINCE).equalTo(province).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Pet rPet = ds.getValue(Pet.class);
                        if (rPet.getPetType().equals(type)) {
                            basicResults.add(rPet);
                        }
                    }
                    Log.d(SIZE, getString(R.string.txt_data_existed));

                    navigateToResult(basicResults);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void navigateToResult(ArrayList<Pet> petArrayList) {
        Intent intent = new Intent(getActivity(), ResultSearchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle dataSend = new Bundle();
        dataSend.putParcelableArrayList(KEY_ARRAY, petArrayList);
        intent.putExtras(dataSend);
        startActivity(intent);
        Animatoo.animateSlideLeft(getView().getContext());
    }

    private void type(){
        binding.type.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String value = s.toString();
                if (s.length() == 0){
                    binding.typeLayout.setErrorEnabled(true);
                    binding.typeLayout.setError(getString(R.string.txt_error_province));
                }else {
                    binding.typeLayout.setErrorEnabled(false);
                }
            }
        });
    }

    private void province() {
        binding.province.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                String value = s.toString();
                if(s.length() == 0){
                    binding.layoutProvince.setErrorEnabled(true);
                    binding.layoutProvince.setError(getString(R.string.txt_error_province));
                }
                else {
                    binding.layoutProvince.setErrorEnabled(false);
                    switch (value) {
                        case "Đà Nẵng":
                            binding.zipCode.setText("550000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "An Giang":
                            binding.zipCode.setText("880000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Bà Rịa - Vũng Tàu":
                            binding.zipCode.setText("790000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Bạc Liêu":
                            binding.zipCode.setText("260000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Bắc Kạn":
                            binding.zipCode.setText("960000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Bắc Giang":
                            binding.zipCode.setText("220000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Bắc Ninh":
                            binding.zipCode.setText("160001");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Bến Tre":
                            binding.zipCode.setText("930000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Bình Dương":
                            binding.zipCode.setText("590000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Bình Định":
                            binding.zipCode.setText("820000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Bình Phước":
                            binding.zipCode.setText("830000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Bình Thuận":
                            binding.zipCode.setText("880000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Cà Mau":
                            binding.zipCode.setText("970000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Cao Bằng":
                            binding.zipCode.setText("270000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Cần Thơ":
                            binding.zipCode.setText("900000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Điện Biên":
                            binding.zipCode.setText("380000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Đắk Lắk":
                            binding.zipCode.setText("630000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Đắk Nông":
                            binding.zipCode.setText("640000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Đồng Nai":
                            binding.zipCode.setText("810000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Đồng Tháp":
                            binding.zipCode.setText("870000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Gia Lai":
                            binding.zipCode.setText("600000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Hà Giang":
                            binding.zipCode.setText("310000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Hà Nam":
                            binding.zipCode.setText("400000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Hà Nội":
                            binding.zipCode.setText("100000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Hà Tĩnh":
                            binding.zipCode.setText("480000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Hải Dương":
                            binding.zipCode.setText("170000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Hải Phòng":
                            binding.zipCode.setText("180000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Hòa Bình":
                            binding.zipCode.setText("350000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Hồ Chí Minh":
                            binding.zipCode.setText("700000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Hưng Yên":
                            binding.zipCode.setText("160000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Hậu Giang":
                            binding.zipCode.setText("910000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Khánh Hòa":
                            binding.zipCode.setText("650000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Kiên Giang":
                            binding.zipCode.setText("920000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Kon Tum":
                            binding.zipCode.setText("580000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Lai Châu":
                            binding.zipCode.setText("390000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Lào Cai":
                            binding.zipCode.setText("330000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Lạng Sơn":
                            binding.zipCode.setText("240000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Lâm Đồng":
                            binding.zipCode.setText("670000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Long An":
                            binding.zipCode.setText("850000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Nghệ An":
                            binding.zipCode.setText("420000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Ninh Bình":
                            binding.zipCode.setText("430000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Ninh Thuân":
                            binding.zipCode.setText("660000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Phú Thọ":
                            binding.zipCode.setText("290000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Phú Yên":
                            binding.zipCode.setText("620000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Quảng Bình":
                            binding.zipCode.setText("510000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Quảng Nam":
                            binding.zipCode.setText("560000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Quảng Ngãi":
                            binding.zipCode.setText("570000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Quảng Ninh":
                            binding.zipCode.setText("200000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Quảng Trị":
                            binding.zipCode.setText("520000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Sóc Trăng":
                            binding.zipCode.setText("950000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Sơn La":
                            binding.zipCode.setText("360000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Tây Ninh":
                            binding.zipCode.setText("840000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Thái Bình":
                            binding.zipCode.setText("410000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Thái Nguyên":
                            binding.zipCode.setText("250000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Thanh Hóa":
                            binding.zipCode.setText("440000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Thừa Thiên - Huế":
                            binding.zipCode.setText("530000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Tiền Giang":
                            binding.zipCode.setText("860000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Trà Vinh":
                            binding.zipCode.setText("940000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Tuyên Quang":
                            binding.zipCode.setText("300000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Vĩnh Long":
                            binding.zipCode.setText("890000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Vĩnh Phúc":
                            binding.zipCode.setText("280000");
                            binding.codeLocation.setEnabled(false);
                            break;

                        case "Yên Bái":
                            binding.zipCode.setText("320000");
                            binding.codeLocation.setEnabled(false);
                            break;

                    }
                }


            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    private void setAge() {
        binding.age.setOnCheckedChangeListener((group, checkedId) -> {
            if (group.getCheckedRadioButtonId() == -1) {
                age = getString(R.string.txt_option_any);
            } else {
                switch (checkedId) {
                    case R.id.rbtn_ager_any:
                        age = getString(R.string.txt_option_any);
                        break;

                    case R.id.radio_ager_baby:
                        age = getString(R.string.txt_ager_baby);
                        break;

                    case R.id.radio_ager_young:
                        age = getString(R.string.txt_ager_young);
                        break;

                    case R.id.radio_ager_adult:
                        age = getString(R.string.txt_ager_adult);
                        break;

                    case R.id.radio_ager_senior:
                        age = getString(R.string.txt_ager_senior);
                        break;

                    default:
                        age = getString(R.string.txt_option_any);

                }
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    private void setSterilization() {
        binding.sterilization.setOnCheckedChangeListener((group, checkedId) -> {
            if (group.getCheckedRadioButtonId() == -1) {
                steri = getString(R.string.txt_option_any);
            } else {
                switch (checkedId) {
                    case R.id.sterilized:
                        steri = getString(R.string.txt_ster_yes);
                        break;

                    case R.id.unsterized:
                        steri = getString(R.string.txt_ster_no);
                        break;

                    case R.id.steri_any:
                        steri = getString(R.string.txt_option_any);
                        break;

                    default:
                        steri = getString(R.string.txt_option_any);

                }
            }
        });
    }


    @SuppressLint("NonConstantResourceId")
    private void setSize() {
        binding.size.setOnCheckedChangeListener((group, checkedId) -> {
            if (group.getCheckedRadioButtonId() == -1) {
                size = getString(R.string.txt_option_any);
            } else {
                switch (checkedId) {
                    case R.id.rbtn_size_any:
                        size = getString(R.string.txt_option_any);
                        break;

                    case R.id.radio_size_s:
                        size = getString(R.string.txt_size_s);
                        break;

                    case R.id.radio_size_m:
                        size = getString(R.string.txt_size_m);
                        break;

                    case R.id.radio_size_l:
                        size = getString(R.string.txt_size_l);
                        break;

                    case R.id.radio_size_xl:
                        size = getString(R.string.txt_size_xl);
                        break;

                    default:
                        size = getString(R.string.txt_option_any);

                }
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    public void setGender() {
        binding.gender.setOnCheckedChangeListener((group, checkedId) -> {
            if (group.getCheckedRadioButtonId() == -1) {
                gender = getString(R.string.txt_option_any);
            } else {
                switch (checkedId) {
                    case R.id.radio_gender_female:
                        gender = getString(R.string.txt_gender_female);
                        break;

                    case R.id.radio_gender_male:
                        gender = getString(R.string.txt_gender_male);
                        break;

                    case R.id.rbtn_gender_any:
                        gender = getString(R.string.txt_option_any);
                        break;

                    default:
                        gender = getString(R.string.txt_option_any);

                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}