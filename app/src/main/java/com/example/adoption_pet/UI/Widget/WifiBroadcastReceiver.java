package com.example.adoption_pet.UI.Widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;


public class WifiBroadcastReceiver extends BroadcastReceiver {
    WifiManager wifiManager;

    @Override
    public void onReceive(Context context, Intent intent) {


        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            if (isNetWorkAvailable(context)) {
                Toast.makeText(context, "Internet connected", Toast.LENGTH_LONG).show();
                Log.d("Internet", "Internet connected");
            } else {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setTitle("Connection problems ");
                builder1.setMessage("You need to open wifi for using app");
                builder1.setCancelable(true);
                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        }
    }

    private boolean isNetWorkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) {
                return false;
            } else {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
                return capabilities != null
                        && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
            }
        } else {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
    }
}
