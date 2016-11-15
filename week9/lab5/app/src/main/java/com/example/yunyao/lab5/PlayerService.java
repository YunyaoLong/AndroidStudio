package com.example.yunyao.lab5;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.security.Provider;

/**
 * Created by yunyao on 2016/11/6.
 */
  public class PlayerService extends Service{
    //binder来绑定MainActivity和Service之间的联系
    public final IBinder binder = new MyBinder();
    public class MyBinder extends Binder{
        PlayerService getService(){
            return PlayerService.this;
        }
    }
    //mp用来检查音乐播放的当前状态
    public static MediaPlayer mp = new MediaPlayer();
    boolean isPause = false;
    String playjudge = "Lab6.Music.Play";
    String stopjudge = "Lab6.Music.Stop";
    String pausejudge = "Lab6.Music.Pause";
    String path = "/sdcard/Music/lab6.mp3";
    //"/storage/extSdCard/MIUI/music/lab6.mp3";
    public void onCreate(){
        try {
            mp.setDataSource(path);
            mp.prepare();
            mp.setLooping(true);
        } catch (Exception e){
            e.printStackTrace();
        }
        //mp.start();
    }
    @Override
    public IBinder onBind(Intent intent){return null;}
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(mp.isPlaying()) {
            stop();
        }
        String msg = intent.getStringExtra("MSG");
        if(msg.equals(playjudge)) {
            play(0);
        } else if(msg.equals(pausejudge)) {
            pause();
        } else if(msg.equals(stopjudge)) {
            stop();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void play(int position) {
        try {
            mp.reset();//把各项参数恢复到初始状态
            mp.setDataSource(path);
            mp.prepare();  //进行缓冲
            mp.setOnPreparedListener(new PreparedListener(position));//注册一个监听器
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        mp.start();
    }

    private void pause() {
        if (mp != null && mp.isPlaying()) {
            mp.pause();
            isPause = true;
        }
    }

    private void stop(){
        if(mp != null) {
            mp.stop();
            try {
                //在调用stop后如果需要再次通过start进行播放,需要之前调用prepare函数
                mp.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private final class PreparedListener implements MediaPlayer.OnPreparedListener {
        private int positon;

        public PreparedListener(int positon) {
            this.positon = positon;
        }
        //检查音频文件的加载状态
        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.start();    //开始播放
            if(positon > 0) {    //如果音乐不是从头播放
                mp.seekTo(positon);
            }
        }
    }
}