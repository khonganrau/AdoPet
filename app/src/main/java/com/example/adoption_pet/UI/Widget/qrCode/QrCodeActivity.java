package com.example.adoption_pet.UI.Widget.qrCode;

import static com.example.adoption_pet.utils.Constants.CODEQR;
import static com.example.adoption_pet.utils.Constants.REQUEST_CAMERA;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.adoption_pet.R;
import com.example.adoption_pet.UI.Menu.PetCallActivity;
import com.google.zxing.Result;
import com.irozon.alertview.AlertActionStyle;
import com.irozon.alertview.AlertStyle;
import com.irozon.alertview.AlertView;
import com.irozon.alertview.objects.AlertAction;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QrCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private static int cam = Camera.CameraInfo.CAMERA_FACING_BACK;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mScannerView = new ZXingScannerView(QrCodeActivity.this);
        setContentView(mScannerView);

        int currentapiVersion = Build.VERSION.SDK_INT;
        if(currentapiVersion >= Build.VERSION_CODES.M){
            if (checkPermission()){
                Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
            }else{
                requestPermission();
            }

        }
    }

    private boolean checkPermission(){
        return (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED);


    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(QrCodeActivity.this, new String[] {Manifest.permission.CAMERA},REQUEST_CAMERA);

    }

    @SuppressLint("ObsoleteSdkInt")
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length > 0) {
                boolean cameraAcept = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (cameraAcept) {
                    Toast.makeText(getApplicationContext(), "Permission Granted By User", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Not Granted By User", Toast.LENGTH_SHORT).show();
                    if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                        showMessageOKCancle("You need to grant the permission",
                                (dialogInterface, i) -> {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                                    }
                                });
                        return;

                    }
                }
            }
        }

    }



    private void showMessageOKCancle(String message, DialogInterface.OnClickListener okListener){
        new AlertDialog.Builder(QrCodeActivity.this)
                .setMessage(message)
                .setPositiveButton("OK",okListener)
                .setNegativeButton("Cancle",null)
                .create()
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.M){
            if (checkPermission()){
                if (mScannerView == null){
                    mScannerView = new ZXingScannerView(QrCodeActivity.this);
                    setContentView(mScannerView);
                }
                mScannerView.setResultHandler(QrCodeActivity.this);
                mScannerView.startCamera();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mScannerView.stopCamera();
        mScannerView = null;
    }

    @Override
    public void handleResult(Result rawResult) {

        AlertView alertView = new AlertView(getString(R.string.txt_scan_result), rawResult.getText(),
                AlertStyle.DIALOG);
        alertView.addAction(new AlertAction(getString(R.string.txt_ok), AlertActionStyle.POSITIVE, alertAction -> {
            Intent i = new Intent(QrCodeActivity.this, PetCallActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra(CODEQR,rawResult.getText());
            Animatoo.animateSlideLeft(QrCodeActivity.this);
            startActivity(i);
        }));
        alertView.addAction(new AlertAction(getString(R.string.txt_cancel),AlertActionStyle.DEFAULT, alertAction -> {
            mScannerView.resumeCameraPreview(QrCodeActivity.this);
        }));
        alertView.show(QrCodeActivity.this);
    }

}