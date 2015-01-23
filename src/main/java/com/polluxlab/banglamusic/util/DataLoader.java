package com.polluxlab.banglamusic.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by samiron on 1/23/2015.
 */
public class DataLoader<T> extends AsyncTask<Void, Void, T> {

    Worker<T> worker;
    Context context;
    ProgressDialog pDialog;

    public DataLoader(Context con, Worker worker){
        this.worker = worker;
        this.context = con;
        pDialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog.setMessage("Loading. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    @Override
    protected T doInBackground(Void... strings) {
        return worker.work();
    }

    @Override
    protected void onPostExecute(T s) {
        super.onPostExecute(s);
        pDialog.dismiss();
        worker.done(s);
    }

    public interface Worker<TYPE> {
        public TYPE work();
        public void done(TYPE s);
    }
}
