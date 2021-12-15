package com.example.adoption_pet.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Toast;

import com.example.adoption_pet.R;
import com.google.android.material.snackbar.Snackbar;

public class Utilities {


    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showSnackBar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }


    public static void setSoundError(Context context) {
        MediaPlayer music = MediaPlayer.create(context, R.raw.error_sound_effect);
        music.start();
    }

    public static void setSoundSuccess(Context context) {
        MediaPlayer music = MediaPlayer.create(context, R.raw.success);
        music.start();
    }

    public static void setVibrate(Context mContext) {
        Vibrator vib = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        vib.vibrate(VibrationEffect.createOneShot(Constants.VIBRATE_DURATION,50));
    }

    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


//    public static void ShowToastIcon(Context context  ,final String message, int resource,ViewGroup viewGroup){
//
//
//        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//        Toast mToastText = new Toast(context);
//
//        View view = inflater.inflate(R.layout.toast_layout, viewGroup);
//        TextView tvMessage = view.findViewById(R.id.tv_message);
//        tvMessage.setText(message);
//        ImageView toastImage = view.findViewById(R.id.image_toast);
//        toastImage.setImageResource(resource);
//
//        mToastText.setDuration(Toast.LENGTH_SHORT);
//        mToastText.setView(view);
//        mToastText.show();
//
//    }


}
