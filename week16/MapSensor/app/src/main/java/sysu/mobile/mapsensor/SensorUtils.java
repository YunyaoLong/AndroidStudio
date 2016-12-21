package sysu.mobile.mapsensor;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

/**
 * Created by limkuan on 16/8/23.
 */
public class SensorUtils {
    public static final int TYPE_ROTATION_CHANGE = 1;
    public static final int TYPE_SHAKE = 2;
    public static final int TYPE_LOCATION_CHANGE = 3;

    public static final int THRESHOLD_SHAKE_SPEED = 18;
    public static final int THRESHOLD_SHAKE_INTERVAL = 1000;

    public static final float THRESHOLD_ROTATION_UPDATE = 0.5f;

    private static SensorUtils mInstance;

    public static synchronized SensorUtils getInstance() {
        if (mInstance == null) {
            mInstance = new SensorUtils();
        }
        return mInstance;
    }

    private SensorManager mSensorManager;
    private Sensor mMagneticSensor;
    private Sensor mAccelerometerSensor;

    private LocationManager mLocationManager;
    private Location mCurrentLocation;
    private float mCurrentRotation = 0f;

    private OnSensorUpdateListener mOnSensorUpdateListener;

    private SensorEventListener mSensorEventListener = new SensorEventListener() {

        float[] accValues = null;
        float[] magValues = null;
        long lastShakeTime = 0;

        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    accValues = event.values.clone();

                    long curTime = System.currentTimeMillis();
                    if ((curTime - lastShakeTime) >= THRESHOLD_SHAKE_INTERVAL) {
                        lastShakeTime = curTime;
                        // System.out.printf("%f %f %f\n", event.values[0], event.values[1], event.values[2]);
                        if (mOnSensorUpdateListener != null
                                && (Math.abs(event.values[0]) > THRESHOLD_SHAKE_SPEED
                                || Math.abs(event.values[1]) > THRESHOLD_SHAKE_SPEED
                                || Math.abs(event.values[2]) > THRESHOLD_SHAKE_SPEED)) {
                            mOnSensorUpdateListener.onSensorUpdate(TYPE_SHAKE,
                                    event.values.clone());
                        }
                    }

                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    magValues = event.values.clone();
                    break;
                default:
                    break;
            }

            if (accValues != null && magValues != null) {
                float[] R = new float[9];
                float[] values = new float[3];

                SensorManager.getRotationMatrix(R, null, accValues, magValues);
                SensorManager.getOrientation(R, values);

                float newRotationDegree = -(float) Math.toDegrees(values[0]);

                if (Math.abs(newRotationDegree - mCurrentRotation) > THRESHOLD_ROTATION_UPDATE) {
                    if (mOnSensorUpdateListener != null) {
                        mOnSensorUpdateListener.onSensorUpdate(TYPE_ROTATION_CHANGE,
                                new float[]{newRotationDegree, 0, 0});
                    }

                    mCurrentRotation = newRotationDegree;
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private LocationListener mGPSListener = new LocationListener() {

        private boolean networkIsRemove = false;

        @Override
        public void onLocationChanged(Location location) {
            boolean flag = isBetterLocation(location,
                    mCurrentLocation);
            if (flag) {
                mCurrentLocation = location;
                if (mOnSensorUpdateListener != null) {
                    mOnSensorUpdateListener.onSensorUpdate(TYPE_LOCATION_CHANGE,
                            new float[]{(float) mCurrentLocation.getLongitude(),
                                    (float) mCurrentLocation.getLatitude(),
                                    (float) mCurrentLocation.getAltitude()});
                }
                makeToast("Location Update from GPS");
            }

            if (location != null && !networkIsRemove) {
                if (ActivityCompat.checkSelfPermission(BaseApplication.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(BaseApplication.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mLocationManager.removeUpdates(mNetworkListener);
                networkIsRemove = true;
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (LocationProvider.OUT_OF_SERVICE == status) {
                makeToast("Lost GPS, change to network");
                if (ActivityCompat.checkSelfPermission(BaseApplication.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(BaseApplication.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    mLocationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER, 0, 0, mNetworkListener);
                    networkIsRemove = false;
                }
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            makeToast("Enable GPS provider");
        }

        @Override
        public void onProviderDisabled(String provider) {
            makeToast("Disable GPS provider");
        }
    };

    private LocationListener mNetworkListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mCurrentLocation = location;
            if (mOnSensorUpdateListener != null) {
                mOnSensorUpdateListener.onSensorUpdate(TYPE_LOCATION_CHANGE,
                        new float[]{(float) mCurrentLocation.getLongitude(),
                                (float) mCurrentLocation.getLatitude(),
                                (float) mCurrentLocation.getAltitude()});
            }
            makeToast("Location Update from network");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            makeToast("Enable network provider");
        }

        @Override
        public void onProviderDisabled(String provider) {
            makeToast("Disable network provider");
        }
    };

    private SensorUtils() {
        mSensorManager = (SensorManager) BaseApplication.getContext()
                .getSystemService(Context.SENSOR_SERVICE);
        mLocationManager = (LocationManager) BaseApplication.getContext()
                .getSystemService(Context.LOCATION_SERVICE);

        mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void setOnSensorUpdateListener(OnSensorUpdateListener onSensorUpdateListener) {
        this.mOnSensorUpdateListener = onSensorUpdateListener;
    }

    public void removeOnSensorUpdateListener(OnSensorUpdateListener onSensorUpdateListener) {
        if (onSensorUpdateListener == this.mOnSensorUpdateListener) {
            this.mOnSensorUpdateListener = null;
        }
    }

    public void registerSensor() throws SecurityException {
        mSensorManager.registerListener(mSensorEventListener, mMagneticSensor,
                SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(mSensorEventListener, mAccelerometerSensor,
                SensorManager.SENSOR_DELAY_FASTEST);

        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mGPSListener);
        }
        if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mNetworkListener);
        }
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);

        String provider = mLocationManager.getBestProvider(criteria, true);
        mLocationManager.getLastKnownLocation(provider);

        mCurrentLocation = mLocationManager.getLastKnownLocation(provider);
    }

    public void unregisterSensor() throws SecurityException {
        mSensorManager.unregisterListener(mSensorEventListener);
    }

    private void makeToast(String msg) {
        Toast.makeText(BaseApplication.getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public Location getCurrentLocation() {
        return mCurrentLocation;
    }

    public float getCurrentRotation() {
        return mCurrentRotation;
    }

    public interface OnSensorUpdateListener {
        void onSensorUpdate(int type, float[] values);
    }

    private static final int TWO_MINUTES = 1000 * 60 * 2;

    /**
     * Determines whether one Location reading is better than the current Location fix
     *
     * @param location            The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

}
