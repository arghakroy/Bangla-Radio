package com.polluxlab.banglamusic.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.polluxlab.banglamusic.R;
import com.polluxlab.banglamusic.model.ModelBase;

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
		return Boolean.valueOf(((LocationManager)context.getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled("gps")).booleanValue();
	}

    public static String getUserId(Context context){
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        final String number=tm.getDeviceId();
        return number;
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
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-ddTHH:mm:ss.SSSZ");

        try {
            if (dateString != null)
                return df1.parse(dateString);
        } catch (ParseException e) {
            Log.d(Util.class.getName(), "Failed to parse datetime");
            e.printStackTrace();
        }
        return null;
    }

    public static Date parseISO8601Date_2( String input ) {

        //NOTE: SimpleDateFormat uses GMT[-+]hh:mm for the TZ which breaks
        //things a bit.  Before we go on we have to repair this.
        SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssz" );

        //this is zero time so we need to add that TZ indicator for
        if ( input.endsWith( "Z" ) ) {
            input = input.substring( 0, input.length() - 1) + "GMT-00:00";
        } else {
            int inset = 6;

            String s0 = input.substring( 0, input.length() - inset );
            String s1 = input.substring( input.length() - inset, input.length() );

            input = s0 + "GMT" + s1;
        }

        try {
            return df.parse( input );
        } catch (ParseException e) {
            Log.d(Util.class.getName(), "Failed to parse datetime");
            e.printStackTrace();
            return null;
        }

    }

    /**
     * TODO: Get the secret key from shared preference and return
     * @return
     */
    public static String getSecretKey(Context context){
        SharedPreferences sh=context.getSharedPreferences("MUSIC_PREF",Context.MODE_PRIVATE);
        String s = sh.getString("sharedSecret","");
        Log.d(Util.class.getName(), "~~secret fetched: " + s);
        return s;
    }

    public static void storeSecret(Context context, String key){
        Log.d(Util.class.getName(), "~~secret storing: " + key);
        SharedPreferences sh=context.getSharedPreferences("MUSIC_PREF",Context.MODE_PRIVATE);
        SharedPreferences.Editor edit=sh.edit();
        edit.putString("sharedSecret",key);
        edit.commit();
        ModelBase.setSharedSecret(key, key);
    }

    public static String toBangla(String word){
        String out="";
        for (int i=0;i<word.length();i++){
            switch (word.charAt(i)){
                case '0':
                    out+="০";
                    break;
                case '1':
                    out+="১";
                    break;
                case '2':
                    out+="২";
                    break;
                case '3':
                    out+="৩";
                    break;
                case '4':
                    out+="৪";
                    break;
                case '5':
                    out+="৫";
                    break;
                case '6':
                    out+="৬";
                    break;
                case '7':
                    out+="৭";
                    break;
                case '8':
                    out+="৮";
                    break;
                case '9':
                    out+="৯";
                    break;
                default:
                    out+=word.charAt(i);
                    break;
            }
        }
        return out;
    }
}
