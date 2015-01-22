package com.polluxlab.banglamusic.model;

import com.google.gson.annotations.SerializedName;
import com.polluxlab.banglamusic.util.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
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
public class Subscription {

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
        return Util.parseISO8601Date(this.startDate);
    }

    public Date getEndDate() {
        return Util.parseISO8601Date(this.endDate);
    }

    public String getStatus() {
        return status;
    }
}
