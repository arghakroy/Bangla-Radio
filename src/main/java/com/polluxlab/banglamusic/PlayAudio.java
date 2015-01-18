package com.polluxlab.banglamusic;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.polluxlab.banglamusic.util.Util;

import java.io.IOException;

/**
 * Created by ARGHA K ROY on 12/27/2014.
 */
public class PlayAudio extends Service {
    private static final String LOGCAT = "MUSIC";
    MediaPlayer objPlayer;

    public void onCreate(){
        super.onCreate();
        Log.d(LOGCAT, "Service Started!");
        objPlayer = new MediaPlayer();
        objPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public int onStartCommand(Intent intent, int flags, int startId){

        try {
            objPlayer.setDataSource(intent.getStringExtra("song"));
            objPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
        } catch (IOException e) {
            Util.showToast(PlayAudio.this,"Error playing song . "+e.getMessage());
        }
        objPlayer.prepareAsync();

        Log.d(LOGCAT, "Media Player started!");
        return 1;
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
