package com.example.adoption_pet.Adapter;

import static com.example.adoption_pet.utils.Constants.DATE_FORMAT;
import static com.example.adoption_pet.utils.Constants.MORE_DAYS;
import static com.example.adoption_pet.utils.Constants.ONE_DAY;
import static com.example.adoption_pet.utils.Constants.PET;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.adoption_pet.R;
import com.example.adoption_pet.databinding.ItemNewestPetBinding;
import com.example.adoption_pet.model.Pet;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class NewPetAdapter extends RecyclerView.Adapter<NewPetAdapter.NewPetViewHolder> {

    private final Context mContext;

    private ArrayList<Pet> nPetList;

    private final NewPetClickListener newPetClickListener;

    public NewPetAdapter(Context mContext, ArrayList<Pet> nPetList, NewPetClickListener newPetClickListener) {
        this.mContext = mContext;
        this.nPetList = nPetList;
        this.newPetClickListener = newPetClickListener;
    }

    @NonNull
    @Override
    public NewPetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNewestPetBinding itemNewestPetBinding = ItemNewestPetBinding.
                inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new NewPetViewHolder(newPetClickListener, itemNewestPetBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull NewPetAdapter.NewPetViewHolder holder, int position) {
        Pet pet = nPetList.get(position);
        holder.bind(pet);
    }


    @Override
    public int getItemCount() {
        return nPetList == null ? 0 : nPetList.size();
    }

    public void removeItem(int position) {
        Pet nPet = nPetList.get(position);
        String Id = nPet.getPetId();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(PET);
        ref.child(Id).removeValue();
    }

    public void addData(ArrayList<Pet> pets) {
        int prevSize = this.nPetList.size();
        this.nPetList.addAll(pets);
        notifyItemRangeInserted(prevSize, pets.size());
    }

    public static class NewPetViewHolder extends RecyclerView.ViewHolder {

        private final NewPetClickListener newPetClickListener;
        private final ItemNewestPetBinding itemBiding;


        public NewPetViewHolder(NewPetClickListener newPetClickListener, ItemNewestPetBinding itemBiding) {
            super(itemBiding.getRoot());
            this.newPetClickListener = newPetClickListener;
            this.itemBiding = itemBiding;
        }

        public void bind(Pet pet) {
            itemBiding.tvPetName.setText(pet.getPetName());
            itemBiding.tvPetBreed.setText(pet.getBreed());
            long duration = getDuration(convertTime(pet.getTimestamp()));
            itemBiding.tvDate.setText(getDaysString(duration));
            Picasso.get().load(pet.getPetImg()).into(itemBiding.imgPetImage);

            itemBiding.newPetItemCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    newPetClickListener.onNewPetClicked(pet);
                }
            });

            itemBiding.btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    newPetClickListener.onNewPetEditClicked(pet);
                }
            });

            itemBiding.btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    newPetClickListener.onNewPetDeleteClicked(pet);
                }
            });
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


        public String convertTime(long time) {
            Date date = new Date(time);
            SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
            return format.format(date);
        }
    }


    public interface NewPetClickListener {
        void onNewPetClicked(Pet nPet);

        void onNewPetEditClicked(Pet nPet);

        void onNewPetDeleteClicked(Pet nPet);
    }

}
