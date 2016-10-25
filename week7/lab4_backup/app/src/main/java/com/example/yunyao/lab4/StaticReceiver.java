package com.example.yunyao.lab4;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by yunyao on 2016/10/18.
 */
public class StaticReceiver extends BroadcastReceiver {
    @Override		//静态广播接收器执行的方法
    public void onReceive(Context context, Intent intent) {
        Log.i("debug", "I should be here");

        if (intent.getAction().equals("FruitSource")){
            //新建状态栏通知
            Bundle bundle = intent.getExtras();
            //设置通知在状态栏显示的图标
            String name = bundle.getString("FruitName");
            int src = bundle.getInt("FruitSrc");
            //Toast.makeText(context, name, Toast.LENGTH_SHORT).show();
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder mBuilder=  new NotificationCompat.Builder(context);
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),src);
            mBuilder.setContentTitle("静态广播")//设置通知栏标题
                    .setContentText(name) //设置通知栏显示内容
                    .setTicker(name+"发送了一条静态广播")
                    .setAutoCancel(true)
                    .setLargeIcon(bitmap)
                    //.setOngoing(true)
                    .setAutoCancel(true)
                    .setSmallIcon(src);//设置通知小ICON
            Intent resultIntent = new Intent(context, MainActivity.class);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, 0);
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // mId allows you to update the notification later on.
            mNotificationManager.notify(1, mBuilder.build());
        }
    }}
