package com.example.yunyao.lab5;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.icu.text.SimpleDateFormat;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
    private static SeekBar seekBar;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //绑定service和activity
        Intent intent = new Intent(this, PlayerService.class);
        bindService(intent,sc, BIND_AUTO_CREATE);
        final String path = "/sdcard/Music/lab6.mp3";
        final String playjudge = "Lab6.Music.Play";
        final String stopjudge = "Lab6.Music.Stop";
        final String pausejudge = "Lab6.Music.Pause";
        final Button playbutton = (Button)findViewById(R.id.PLAY);
        final Button stopbutton = (Button)findViewById(R.id.STOP);
        final Button quitbutton = (Button)findViewById(R.id.QUIT);
        final TextView textView = (TextView)findViewById(R.id.StateText);
        final TextView playtime = (TextView)findViewById(R.id.plytime);
        final TextView totaltime = (TextView)findViewById(R.id.totaltime);
        final ImageView MusicianPic= (ImageView)findViewById(R.id.MusicianPic);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        //添加一个动画类
        final Animation op = AnimationUtils.loadAnimation(this, R.anim.rotate_0_to_360);
        LinearInterpolator lin = new LinearInterpolator();
        op.setInterpolator(lin);
        playbutton.setOnClickListener(new View.OnClickListener() {
                boolean pause_play=false;
                @Override
                public  void onClick(View v) {
                    if(pause_play==true){
                        pause_play = false;
                        playbutton.setText("PAUSE");
                        textView.setText("Playing");
                        MusicianPic.startAnimation(op);
                        if(!ms.mp.isPlaying())
                            ms.mp.start();
                    } else {
                        pause_play=true;
                        playbutton.setText("PLAY");
                        textView.setText("Paused");
                        MusicianPic.clearAnimation();
                        if(ms.mp.isPlaying()) {
                            ms.mp.pause();
                        }
                    }
                }
            });
        stopbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                textView.setText("Stopped");
                MusicianPic.clearAnimation();
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
        handler.postDelayed(updateThread, 100);
    }
    private TextView plytime;
    private TextView totaltime;
    private java.text.SimpleDateFormat time = new java.text.SimpleDateFormat("mm:ss");
    final Handler handler = new Handler();
    final Runnable updateThread = new Runnable() {
        @Override
        public void run() {
            seekBar.setProgress(ms.mp.getCurrentPosition());
            plytime=(TextView) findViewById(R.id.plytime);
            seekBar.setMax(ms.mp.getDuration());
            totaltime=(TextView) findViewById(R.id.totaltime);
            plytime.setText(time.format(ms.mp.getCurrentPosition()));
            totaltime.setText(time.format(ms.mp.getDuration()));
            handler.postDelayed(this, 100);
        }
    };
}
