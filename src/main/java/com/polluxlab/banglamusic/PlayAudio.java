package com.polluxlab.banglamusic;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.polluxlab.banglamusic.model.Song;
import com.polluxlab.banglamusic.util.Util;

import java.io.IOException;
import java.util.List;

/**
 * Created by ARGHA K ROY on 12/27/2014.
 */
public class PlayAudio extends Service {
    private static final String LOGCAT = "MUSIC";
    MediaPlayer objPlayer;
    static List<Song> songs;
    int pos;

    public void onCreate(){
        super.onCreate();
        Log.d(LOGCAT, "Service Started!");
        objPlayer = new MediaPlayer();
        objPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public int onStartCommand(Intent intent, int flags, int startId){

        pos=intent.getIntExtra("pos",0);
        if(songs!=null){
            new GetStreamLink(pos).execute();
        }
        return 1;
    }

    class GetStreamLink extends AsyncTask<String,String,String> {
        int pos;
        String link="";
        GetStreamLink(int i){
            pos=i;
        }

        @Override
        protected String doInBackground(String... params) {
            link=songs.get(pos).getStreamLink();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                objPlayer.setDataSource(link);
                objPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                });
            }catch (Exception e){
                Log.d(LOGCAT,e.getMessage());
            }
            Log.d(LOGCAT, "Media Player started!");
            objPlayer.prepareAsync();
        }
    }

    public void onStop(){
        objPlayer.stop();
        objPlayer.release();
    }

    public void onPause(){
        objPlayer.stop();
        objPlayer.release();
    }
    public void onDestroy(){
        objPlayer.stop();
        objPlayer.release();
    }
    @Override
    public IBinder onBind(Intent objIndent) {
        return null;
    }
}
