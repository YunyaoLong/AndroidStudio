package com.lzy.gpstask.location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;


/**
 * 类描述：GPS定位的管理类
 * Created by lizhenya on 2016/9/12.
 */
public class GPSLocationManager {
    private static final String GPS_LOCATION_NAME = android.location.LocationManager.GPS_PROVIDER;
    private static GPSLocationManager gpsLocationManager;
    private static Object objLock = new Object();
    private boolean isGpsEnabled;
    private static String mLocateType;
    private WeakReference<Activity> mContext;
    private LocationManager locationManager;
    private GPSLocation mGPSLocation;
    private boolean isOPenGps;
    private long mMinTime;
    private float mMinDistance;
    private ExecutorService fixedThreadPool;
    private Handler mainThreadHandler;

    private GPSLocationManager(Activity context) {
        initData(context);
    }

    private void initData(Activity context) {
        this.mContext = new WeakReference<>(context);
        if (mContext.get() != null) {
            locationManager = (LocationManager) (mContext.get().getSystemService(Context.LOCATION_SERVICE));
        }
        //定位类型：GPS
        mLocateType = locationManager.GPS_PROVIDER;
        //默认不强制打开GPS设置面板
        isOPenGps = false;
        //默认定位时间间隔为1000ms
        mMinTime = 1000;
        //默认位置可更新的最短距离为0m
        mMinDistance = 0;
        mainThreadHandler = new Handler() {};
    }

    public static GPSLocationManager getInstances(Activity context) {
        if (gpsLocationManager == null) {
            synchronized (objLock) {
                if (gpsLocationManager == null) {
                    gpsLocationManager = new GPSLocationManager(context);
                }
            }
        }
        return gpsLocationManager;
    }

    /**
     * 方法描述：设置发起定位请求的间隔时长
     *
     * @param minTime 定位间隔时长（单位ms）
     */
    public void setScanSpan(long minTime) {
        this.mMinTime = minTime;
    }

    /**
     * 方法描述：设置位置更新的最短距离
     *
     * @param minDistance 最短距离（单位m）
     */
    public void setMinDistance(float minDistance) {
        this.mMinDistance = minDistance;
    }

    /**
     * 方法描述：开启定位（默认情况下不会强制要求用户打开GPS设置面板）
     *
     * @param gpsLocationListener
     */
    public void start(GPSLocationListener gpsLocationListener) {
        this.start(gpsLocationListener, isOPenGps);
    }

    /**
     * 方法描述：开启定位
     *
     * @param gpsLocationListener
     * @param isOpenGps           当用户GPS未开启时是否强制用户开启GPS
     */
    public void start(final GPSLocationListener gpsLocationListener, boolean isOpenGps) {
        this.isOPenGps = isOpenGps;
        if (mContext.get() == null) {
            return;
        }
        mGPSLocation = new GPSLocation(gpsLocationListener);
        isGpsEnabled = locationManager.isProviderEnabled(GPS_LOCATION_NAME);
        if (!isGpsEnabled && isOPenGps) {
            openGPS();
            return;
        }
        if (ActivityCompat.checkSelfPermission(mContext.get(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (mContext.get(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location lastKnownLocation = locationManager.getLastKnownLocation(mLocateType);
        mGPSLocation.onLocationChanged(lastKnownLocation);
        //备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新
        locationManager.requestLocationUpdates(mLocateType, mMinTime, mMinDistance, mGPSLocation);
    }

    /**
     * 方法描述：转到手机设置界面，用户设置GPS
     */
    public void openGPS() {
        Toast.makeText(mContext.get(), "请打开GPS设置", Toast.LENGTH_SHORT).show();
        if (Build.VERSION.SDK_INT > 15) {
            Intent intent = new Intent(
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            mContext.get().startActivityForResult(intent, 0);
        }
    }

    /**
     * 方法描述：终止GPS定位,该方法最好在onPause()中调用
     */
    public void stop() {
        if (mContext.get() != null) {
            if (ActivityCompat.checkSelfPermission(mContext.get(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext.get(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.removeUpdates(mGPSLocation);
        }
    }
}
