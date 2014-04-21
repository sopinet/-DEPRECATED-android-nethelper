package com.sopinet.android.nethelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetHelper {
    public static boolean isOnlineWifi(Context act) {
        ConnectivityManager manager = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);

        //For WiFi Check
        boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnectedOrConnecting();

        return isWifi;
    }

    public static boolean isOnline(Context act, String mode) {
        ConnectivityManager cm = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni!=null && ni.isAvailable() && ni.isConnected()) {
            if (mode.equals("wifi")) {
                if (NetHelper.isOnlineWifi(act)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
    public static boolean isOnline(Activity act, String mode) {
        Context con = act.getApplicationContext();
        return isOnline(con, mode);
    }
    public static boolean isOnline(Activity act) {
        return NetHelper.isOnline(act, "any");
    }
    public static boolean isOnline(Context act) {
        return NetHelper.isOnline(act, "any");
    }

    public static boolean isOnline3G(Context act) {
        ConnectivityManager manager = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);

        //For 3G check
        boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .isConnectedOrConnecting();

        return is3g;
    }

    static public String getContents(File aFile) {
        //...checks on aFile are elided
        StringBuilder contents = new StringBuilder();

        try {
            //use buffering, reading one line at a time
            //FileReader always assumes default encoding is OK!
            BufferedReader input =  new BufferedReader(new FileReader(aFile));
            try {
                String line = null; //not declared within while loop
	            /*
	            * readLine is a bit quirky :
	            * it returns the content of a line MINUS the newline.
	            * it returns null only for the END of the stream.
	            * it returns an empty String if two newlines appear in a row.
	            */
                while (( line = input.readLine()) != null){
                    contents.append(line);
                    contents.append(System.getProperty("line.separator"));
                }
            }
            finally {
                input.close();
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }

        return contents.toString();
    }
}