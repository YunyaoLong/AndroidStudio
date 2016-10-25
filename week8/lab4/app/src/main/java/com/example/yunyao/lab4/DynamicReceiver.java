package com.example.yunyao.lab4;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Created by yunyao on 2016/10/18.
 */
public class DynamicReceiver extends BroadcastReceiver {
    private static final String DYNAMICACTION = "dynamicreceiver";
    @Override
    public void onReceive(Context context, Intent intent){
        if(intent.getAction().equals(DYNAMICACTION)){
            //新建状态栏通知
            Bundle bundle = intent.getExtras();
            //设置通知在状态栏显示的图标
            String text = bundle.getString("text");
            int src = bundle.getInt("src");
            //Toast.makeText(context, name, Toast.LENGTH_SHORT).show();
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder mBuilder=  new NotificationCompat.Builder(context);
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), src);
            mBuilder.setContentTitle("动态广播")//设置通知栏标题
                    .setContentText(text) //设置通知栏显示内容
                    .setTicker("收到了一条动态广播")
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
        if(intent.getAction().equals(DYNAMICACTION)){
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.lab5_widget);
            Bundle bundle = intent.getExtras();
            views.setTextViewText(R.id.WidgetText, bundle.getString("text"));
            views.setImageViewResource(R.id.WidgetImage, bundle.getInt("src"));
            Log.d("text", bundle.getString("text"));
            // 实用widget管理器来更新小窗口
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            appWidgetManager.updateAppWidget(new ComponentName(context, Lab5_Widget.class), views);

        }
    }
}
