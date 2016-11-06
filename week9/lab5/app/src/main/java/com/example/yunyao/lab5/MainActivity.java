package com.example.yunyao.lab5;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.icu.text.SimpleDateFormat;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    PlayerService ms;
    //bindService 成功回调onServiceConnected函数，通过IBinder获取Service对象，实现绑定
    private  ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ms = ((PlayerService.MyBinder)service).getService();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            ms=null;
        }
    };
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //绑定service和activity
        Intent intent = new Intent(this, PlayerService.class);
        bindService(intent,sc, BIND_AUTO_CREATE);
        final boolean[] pauseflag = {true};
        final String path = "/sdcard/Music/lab6.mp3";
        final String playjudge = "Lab6.Music.Play";
        final String stopjudge = "Lab6.Music.Stop";
        final String pausejudge = "Lab6.Music.Pause";
        final Button playbutton = (Button)findViewById(R.id.PLAY);
        final Button stopbutton = (Button)findViewById(R.id.STOP);
        final Button quitbutton = (Button)findViewById(R.id.QUIT);
        final TextView textView = (TextView)findViewById(R.id.StateText);
        final SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar);
        final TextView playtime = (TextView)findViewById(R.id.plytime);
        final TextView totaltime = (TextView)findViewById(R.id.totaltime);
        playbutton.setOnClickListener(new View.OnClickListener() {
                int i=0;
                int c=0;
                @Override
                public  void onClick(View v)
                {
                    if(i==0){
                        i=1;
                        playbutton.setText("PAUSE");
                        textView.setText("Playing");
                        Intent intent = new Intent(MainActivity.this,PlayerService.class);
                        bindService(intent, sc,BIND_AUTO_CREATE);
                        if(c==0){
                            c=1;
                        }
                        else{
                            if(!ms.mp.isPlaying())
                            {
                                ms.mp.start();
                            }
                        }
                    }
                    else{
                        i=0;
                        playbutton.setText("PLAY");
                        textView.setText("Paused");
                        if(ms.mp.isPlaying())
                        {
                            ms.mp.pause();
                        }
                    }
                }
            });
        stopbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                textView.setText("Stopped");
                ms.mp.stop();
                try {
                    ms.mp.prepare();
                    ms.mp.seekTo(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        quitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ms.mp.stop();
                ms.mp.release();
                try {
                    unbindService(sc);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    MainActivity.this.finish();
                    System.exit(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    int process = seekBar.getProgress();
                    if (ms.mp != null) {
                        ms.mp.seekTo(process);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
