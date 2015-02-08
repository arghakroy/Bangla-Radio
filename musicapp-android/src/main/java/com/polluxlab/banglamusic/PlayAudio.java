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
import com.polluxlab.banglamusic.util.AppConstant;
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
    static int pos;

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
        int currentPos;
        String link="";
        GetStreamLink(int i){
            currentPos=i;
            Log.d(AppConstant.DEBUG,currentPos+"");
        }

        @Override
        protected String doInBackground(String... params) {
            link=songs.get(currentPos).getStreamLink();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                objPlayer = new MediaPlayer();
                objPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                objPlayer.setDataSource(link);
            }catch (Exception e){
                Log.d(LOGCAT,"Error in onpost playaudio");
            }
            objPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            objPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.stop();
                    if(currentPos==songs.size()-1)currentPos=0;
                    else currentPos++;
                    new GetStreamLink(++currentPos).execute();
                }
            });
            Log.d(LOGCAT, "Media Player started!");
            objPlayer.prepareAsync();
            objPlayer.setScreenOnWhilePlaying(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(objPlayer.isPlaying())
        objPlayer.stop();

        objPlayer.release();
    }

    @Override
    public IBinder onBind(Intent objIndent) {
        return null;
    }
}
