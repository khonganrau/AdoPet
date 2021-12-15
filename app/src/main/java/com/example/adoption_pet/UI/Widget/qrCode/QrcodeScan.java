package com.example.adoption_pet.UI.Widget.qrCode;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.example.adoption_pet.utils.LogUtils;
import com.example.adoption_pet.utils.Utilities;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;


public class QrcodeScan implements ImageAnalysis.Analyzer {
//    private final BarcodeViewModel mBarcodeViewModel;


    private String mData;

    private final BarcodeScanListener mBarcodeScanListener;


    private int mActivityWidth = 0;
    private int mActivityHeight = 0;


    private float mScanBoundX = 0;
    private float mScanBoundY = 0;
    private float mScanWidth = 0;
    private float mScanHeight = 0;


    private YuvToRgbConverter mYuvToRgbConverter;


    QrcodeScan(Context context, BarcodeScanListener barcodeScanListener) {
        mBarcodeScanListener = barcodeScanListener;
        mYuvToRgbConverter = new YuvToRgbConverter(context);
    }


    public void setActivitySize(int width, int height) {
        mActivityWidth = width;
        mActivityHeight = height;
    }


    public void setScanPosition(float x, float y) {
        mScanBoundX = x;
        mScanBoundY = y;
    }


    public void setScanSize(int width, int height) {
        mScanWidth = width;
        mScanHeight = height;
    }


    @Override
    @androidx.camera.core.ExperimentalGetImage
    public void analyze(@NonNull ImageProxy imageProxy) {
        try {
            //バーコード読み取り速度調整のため
            int SLEEP = 500;
            Thread.sleep(SLEEP); //0.5秒Sleepさせる
        } catch (InterruptedException e) {
            LogUtils.e(e.getMessage());
        }
        int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
        //イメージの取得

        Image mediaImage = imageProxy.getImage();
        //画像の受信
        if (mediaImage != null) {
            //画面スケールを計算する
            InputImage image;

            try {
                Bitmap originBitmap = Bitmap.createBitmap(imageProxy.getWidth(), imageProxy.getHeight(), Bitmap.Config.ARGB_8888);
                mYuvToRgbConverter.yuvToRgb(mediaImage, originBitmap);

                Bitmap rotatedBitmap = Utilities.rotateBitmap(originBitmap, rotationDegrees);

                float scaleWidth = (float) rotatedBitmap.getWidth() / mActivityWidth;
                float scaleHeight = (float) rotatedBitmap.getHeight() / mActivityHeight;

                int x = (int) (mScanBoundX * scaleWidth);
                int y = (int) (mScanBoundY * scaleHeight);
                int width = (int) (mScanWidth * scaleWidth);
                int height = (int) (mScanHeight * scaleHeight);

                Bitmap croppedBitmap = Bitmap.createBitmap(rotatedBitmap, x, y, width, height);

                image = InputImage.fromBitmap(croppedBitmap, 0);
            } catch (Exception e) {
                image = InputImage.fromMediaImage(mediaImage, rotationDegrees);
            }

            //画像データを渡す処理
            scanBarcode(imageProxy, image);
        }
    }

    private void scanBarcode(ImageProxy imageProxy, InputImage image) {
        //バーコードformat設定
        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(
                                Barcode.FORMAT_QR_CODE,
                                Barcode.FORMAT_CODE_128,
                                Barcode.FORMAT_CODE_39,
                                Barcode.FORMAT_CODABAR,
                                Barcode.FORMAT_EAN_13,
                                Barcode.FORMAT_EAN_8,
                                Barcode.FORMAT_ITF,
                                Barcode.FORMAT_UPC_A,
                                Barcode.FORMAT_UPC_E)
                        .build();
        //インスタンス取得し認識する形式を指定
        BarcodeScanner scanner = BarcodeScanning.getClient(options);
        //画像の処理（画像をプロセスメソッドに渡す）
        scanner.process(image)
                //バーコードからの情報を取得
                .addOnSuccessListener(barcodes -> {
                    for (Barcode barcode : barcodes) {
                        int length = barcode.getDisplayValue().length();
                        if (barcode.getFormat() == Barcode.FORMAT_UPC_A && length == 12) {
                            String janCode = barcode.getDisplayValue();
                            mData = "0" + janCode;
                        } else if (barcode.getFormat() == Barcode.FORMAT_UPC_E && length == 7) {
                            String janCode = barcode.getDisplayValue();
                            mData = "0" + janCode;
                        } else {
                            mData = barcode.getDisplayValue();
                        }

                        LogUtils.i("barcode: " + mData);
//                        mBarcodeViewModel.getPaymentConfirmation(mData);
                        mBarcodeScanListener.onPauseScanner();
                        return;
                    }
                })
                .addOnFailureListener(e -> LogUtils.e(e.getMessage()))
                .addOnCompleteListener(task -> imageProxy.close());
    }

    public interface BarcodeScanListener {
        void onPauseScanner();
    }
}


