package com.example.yunyao.lab4;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
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
 * Implementation of App Widget functionality.
 */
public class Lab5_Widget extends AppWidgetProvider {

    private static final String STATICACTION = "FruitSource";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // 构建一个RemoteViews对象
        //在RemoteViews的构造函数中，通过传入layout文件的id来获取 “layout文件对应的视图(RemoteViews)”
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.lab5_widget);
        //然后，调用RemoteViews中的方法能对layout中的组件进行设置
        views.setTextViewText(R.id.WidgetText, widgetText);
        //调用集合管理器对集合进行更新
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //super.onUpdate(context, appWidgetManager, appWidgetIds);
        //对所有的widget进行更新
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);

            // 点击跳转的Intent事件
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            //通过remoteview找到桌面的widget，然后对它的图片设置一个监听器
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.lab5_widget);
            views.setOnClickPendingIntent(R.id.WidgetImage, pendingIntent);

            //告诉AppWidgetManager对当前应用程序窗口小部件进行更新
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(STATICACTION)){
            //获取 lab5_widget对应的RemoteViews
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.lab5_widget);
            Bundle bundle = intent.getExtras();
            views.setTextViewText(R.id.WidgetText, bundle.getString("FruitName"));
            views.setImageViewResource(R.id.WidgetImage, bundle.getInt("FruitSrc"));
            Log.d("FruitName", bundle.getString("FruitName"));
            // 实用widget管理器来更新小窗口
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            appWidgetManager.updateAppWidget(new ComponentName(context, Lab5_Widget.class), views);

        }
        if (intent.getAction().equals("FruitSource")){
            //新建状态栏通知
            Bundle bundle = intent.getExtras();
            //设置通知在状态栏显示的图标
            String name = bundle.getString("FruitName");
            int src = bundle.getInt("FruitSrc");
            //Toast.makeText(context, name, Toast.LENGTH_SHORT).show();
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder mBuilder=  new NotificationCompat.Builder(context);
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), src);
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
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

