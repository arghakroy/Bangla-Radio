package com.polluxlab.banglamusic.model;

import android.util.*;
import com.google.gson.annotations.SerializedName;
import com.polluxlab.banglamusic.util.AppConstant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    private Links links;
    private String phone_number;

    public String getPackageName() {
        return packageName;
    }

    public Date getStartDate() {
        return parseDate(this.startDate);
    }

    public String getEndDate() {
        return this.endDate;
    }

    private Date parseDate(String date){
        Log.d(AppConstant.DEBUG,date);
        if( this.endDate.trim().isEmpty() ){
            return null;
        }
        //"end_date": "March 11, 2015 04:15"
        SimpleDateFormat from = new SimpleDateFormat("MMMM dd',' yyyy hh':'mm");
        try {
            return from.parse(date);
        } catch (ParseException e) {
            Log.d(AppConstant.DEBUG, "Failed to parse date");
            e.printStackTrace();
            return null;
        }
    }


    public String getPhone_number(){
        if(phone_number==null)return "";
        return phone_number;
    }

}
