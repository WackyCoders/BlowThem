package com.blowthem.app;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

/**
 * Created by walter on 12.09.14.
 */
public class MusicService extends Service implements MediaPlayer.OnErrorListener{

    private final IBinder mBinder = new ServiceBinder();
    MediaPlayer mediaPlayer;
    private int length = 0;

    public MusicService(){}

    public class ServiceBinder extends Binder {
        MusicService getService(){
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.crusaderkings2_maintitle);
        mediaPlayer.setOnErrorListener(this);

        if(mediaPlayer != null){
            mediaPlayer.setLooping(true);
            mediaPlayer.setVolume(100, 100);
        }

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                onError(mp, what, extra);
                return true;
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mediaPlayer.start();
        return START_NOT_STICKY;
    }

    public void pauseMusic(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            length = mediaPlayer.getCurrentPosition();
        }
    }

    public void resumeMusic(){
        if(!mediaPlayer.isPlaying()){
            mediaPlayer.seekTo(length);
            mediaPlayer.start();
        }
    }

    public void stopMusic(){
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer != null){
            try{
                mediaPlayer.stop();
                mediaPlayer.release();
            } finally {
                mediaPlayer = null;
            }
        }
    }

    public boolean onError(MediaPlayer mp, int what, int extra){
        Toast.makeText(this, "music player failed", Toast.LENGTH_SHORT).show();
        if(mediaPlayer != null){
            try{
                mediaPlayer.stop();
                mediaPlayer.release();
            } finally {
                mediaPlayer = null;
            }
        }
        return false;
    }
}
