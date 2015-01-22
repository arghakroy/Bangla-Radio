package com.polluxlab.banglamusic.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Patterns;
import android.widget.Toast;

public class Util {


	/**
	 * Checking for all possible internet providers
	 * **/
	public static boolean isConnectingToInternet(Context con){
		ConnectivityManager connectivity = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null)
		{
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED)
					{
						return true;
					}

		}
		return false;
	}



	public static void showNoInternetDialog(final Context con) {

		AlertDialog.Builder build=new AlertDialog.Builder(con);
		build.setTitle("No Internet");
		build.setMessage("Internet is not available. Please check your connection");
		build.setCancelable(true);
		build.setPositiveButton("Settings", new Dialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
				con.startActivity(intent);
			}
		});

		build.setNegativeButton("Cancel", new Dialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});

		AlertDialog alert=build.create();
		alert.show();
	}
	public static boolean validEmail(String email) {
	    Pattern pattern = Patterns.EMAIL_ADDRESS;
	    return pattern.matcher(email).matches();
	}

	public static void showToast(Context con,String message){
		Toast.makeText(con, message, Toast.LENGTH_SHORT).show();
	}

	public static boolean isGPSOn(Context context)
	{
		return Boolean.valueOf(((LocationManager)context.getSystemService("location")).isProviderEnabled("gps")).booleanValue();
	}



	public static String getAmPm(int hour) {
		if(hour<12){
			return "AM";
		}else{
			return "PM";
		}
	}

	public static String formatHour(int hour) {
		if(hour==0){
			hour=12;
		}else{
			hour-=12;
		}
		return hour+"";
	}

    public static Date parseISO8601Date(String dateString){
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        try {
            if (dateString != null)
                return df1.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * TODO: Get the secret key from shared preference and return
     * @return
     */
    public static String getSecretKey(){
        return "";
    }
}
