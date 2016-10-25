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
    private static final String DYNAMICACTION = "dynamicreceiver";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.lab5_widget);
        views.setTextViewText(R.id.WidgetText, widgetText);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //super.onUpdate(context, appWidgetManager, appWidgetIds);
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);

            // Create an Intent to launch ExampleActivity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.lab5_widget);
            views.setOnClickPendingIntent(R.id.WidgetImage, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(STATICACTION)){
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.lab5_widget);
            Bundle bundle = intent.getExtras();
            views.setTextViewText(R.id.WidgetText, bundle.getString("FruitName"));
            views.setImageViewResource(R.id.WidgetImage, bundle.getInt("FruitSrc"));
            Log.d("FruitName", bundle.getString("FruitName"));
            // Instruct the widget manager to update the widget
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

