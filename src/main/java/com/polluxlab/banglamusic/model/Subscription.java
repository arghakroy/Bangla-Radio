package com.polluxlab.banglamusic.model;

import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.polluxlab.banglamusic.util.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by samiron on 1/22/2015.
 * {
     "sku": "MY-RADIO-RADIOBANGLA-FULL-W",
     "status": "ACTIVE",
     "start_date": "2015-01-22T16:04:17.146Z",
     "end_date": "2015-01-29T16:04:17.146Z",
     "links" : {
     "self": "https://162.248.162.2/musicapp/server/web/app_dev.php/webservice/subscriptions/"
     }
 }
 */
public class Subscription extends ModelBase {

    @SerializedName("sku")
    private String packageName;

    @SerializedName("start_date")
    private String startDate;

    @SerializedName("end_date")
    private String endDate;

    private String status;
    private Links links;

    public String getPackageName() {
        return packageName;
    }

    public Date getStartDate() {
        return parseDate(this.startDate);
    }

    public Date getEndDate() {
        return parseDate(this.endDate);
    }

    private Date parseDate(String date){
        if( this.endDate.trim().isEmpty() ){
            return null;
        }
        //"end_date": "2015-02-01"
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            Log.d(getClass().getName(), "Failed to parse date");
            e.printStackTrace();
            return null;
        }
    }

    public String getStatus() {
        return status;
    }

    @Override
    public boolean valid(){
        if( this.endDate.trim().isEmpty())
            return false;

        Date d = this.getEndDate();
        if ( d == null )
            return false;
        if( d.before(new Date())){
            return false;
        }
        return true;
    }

}
