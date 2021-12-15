package com.example.adoption_pet.Adapter;

import static com.example.adoption_pet.utils.Constants.DATE_FORMAT;
import static com.example.adoption_pet.utils.Constants.MORE_DAYS;
import static com.example.adoption_pet.utils.Constants.ONE_DAY;
import static com.example.adoption_pet.utils.Constants.TODAY_POST;
import static com.example.adoption_pet.utils.Constants.VALUE_DIFF_ONE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adoption_pet.databinding.ItemGridPetsLayoutBinding;
import com.example.adoption_pet.model.Pet;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {

    private final Context mContext;

    ArrayList<Pet> mPetList;

    private final PetClickListener petClickListener;

    public PetAdapter(Context mContext, ArrayList<Pet> mPetList, PetClickListener petClickListener) {
        this.mContext = mContext;
        this.mPetList = mPetList;
        this.petClickListener = petClickListener;
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemGridPetsLayoutBinding itemGridPetsLayoutBinding = ItemGridPetsLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PetViewHolder(petClickListener, itemGridPetsLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        Pet pet = mPetList.get(position);
        holder.bind(pet);


    }

    @Override
    public int getItemCount() {
        return mPetList == null ? 0 : mPetList.size();
    }

    public void removeItem(int position) {
        mPetList.remove(position);
        notifyItemRemoved(position);
    }

    public void addData(ArrayList<Pet> pets) {
        int prevSize = this.mPetList.size();
        this.mPetList.addAll(pets);
        notifyItemRangeInserted(prevSize, pets.size());
    }

    public static class PetViewHolder extends RecyclerView.ViewHolder {

        TextView petName;
        TextView petBreed;
        ImageView petImage;
        TextView postDay;

        private final PetClickListener petClickListener;
        private final ItemGridPetsLayoutBinding itemBinding;

        public PetViewHolder(PetClickListener petClickListener, ItemGridPetsLayoutBinding itemBinding) {
            super(itemBinding.getRoot());
            this.petClickListener = petClickListener;
            this.itemBinding = itemBinding;
        }

        public void bind(Pet pet) {
            itemBinding.tvPetName.setText(pet.getPetName());
            itemBinding.tvPetBreed.setText(pet.getBreed());
            postDay = itemBinding.tvDate;
            Picasso.get().load(pet.getPetImg()).into(itemBinding.imgPetImage);
            long pDate = getDuration(convertTime(pet.getTimestamp()));
            itemBinding.tvDate.setText(getDaysString(pDate));

            itemBinding.petItemCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    petClickListener.onPetClicked(pet);
                }
            });

        }

        public static String getDaysString(long days) {
            String result;
            if (days < VALUE_DIFF_ONE) {
                result = TODAY_POST;
            } else if (days == VALUE_DIFF_ONE) {
                result = days + ONE_DAY;
            } else {
                result = days + MORE_DAYS;
            }

            return result;
        }

        public static long getDuration(String pDate) {
            SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
            long duration = 0;
            Date currentDate = Calendar.getInstance().getTime();
            Date postDate;

            try {
                postDate = format.parse(pDate);
                assert postDate != null;
                duration = currentDate.getTime() - postDate.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return TimeUnit.DAYS.convert(duration, TimeUnit.MILLISECONDS);
        }


        public String convertTime(long time) {
            Date date = new Date(time);
            SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
            return format.format(date);
        }
    }

    public interface PetClickListener {
        void onPetClicked(Pet mPet);
    }

}