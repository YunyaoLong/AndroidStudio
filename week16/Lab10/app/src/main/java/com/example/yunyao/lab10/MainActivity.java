package com.example.yunyao.lab10;

import android.app.Activity;
import android.app.Service;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity implements SensorEventListener {
    // 定义显示指南针的图片
    ArrowView znzImage;
    TextView angle_value, longitude, latitude, current_position, shake_num, sematic_description;

    // 定义Sensor管理器
    SensorManager mSensorManager;
    Sensor mMagneticSensor, mAccelerometerSensor;

    LocationManager mLocationManager;
    Location mCurrentLocation;
    //震动
    private Vibrator vibrator;
    static int num_shack_num = 0;

    long curTime = 0;
    long lastshakeTime = 0;
    long lastlocateTime = 0;
    String provider;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        // 获取界面中显示指南针的图片
        znzImage = (ArrowView) findViewById(R.id.angle_pointer);

        //其他绑定
        angle_value = (TextView) findViewById(R.id.angle_value);
        longitude = (TextView) findViewById(R.id.longitude);
        latitude = (TextView) findViewById(R.id.latitude);
        current_position = (TextView) findViewById(R.id.current_position);
        shake_num = (TextView) findViewById(R.id.shake_num);
        sematic_description = (TextView) findViewById(R.id.sematic_description);

        //震动监听
        vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);

        //设置初始摇动时间以及初始定位时间备用
        lastshakeTime = System.currentTimeMillis();
        lastlocateTime = lastshakeTime;

        // 获取传感器管理服务
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //震动获取服务
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        //磁力传感器简称为M-sensor，返回x、y、z三轴的环境磁场数据。
        mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        //加速度传感器又叫G-sensor，返回x、y、z三轴的加速度数值，包含了重力加速度。
        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        //动态选择gps来源
        Criteria criteria = new Criteria();
        //一定要加上下面这句话，否则系统会在室内连接wifi的情况下的定位，会造成最后的无法定位
        // （错误原因未知）
        //寻找收集中各种可用的满足一定调价你的传感器
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//设置为最大精度
        criteria.setAltitudeRequired(false);//不要求海拔信息
        criteria.setBearingRequired(false);//不要求方位信息
        criteria.setCostAllowed(true);//是否允许付费
        criteria.setPowerRequirement(Criteria.POWER_LOW);;//对电量的要求

        //获取最佳定位传感器
        provider = mLocationManager.getBestProvider(criteria, true);
        Log.i("provider", provider);
        //下面这一个判断不是很懂，在stackflow上寻找到的
        // 如果不加上这句话，getLastKnownLocation会有红线报错
        // （但是该报错不会影响程序编译和运行）
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "First enable LOCATION ACCESS in settings.", Toast.LENGTH_LONG).show();
            return;
        }
        //获取上一次读取到的Location信息
        mCurrentLocation = mLocationManager.getLastKnownLocation(provider);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TYPE_ORIENTATION，为系统的方向传感器注册监听器
        //方向传感器简称为O-sensor，返回三轴的角度数据，方向数据的单位是角度。
        //这理我直接获取方向传感器数据，能够更加方便快速的获取手机的偏转信息
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
        //下面这段话是为了防止removeUpdate红线报错
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "First enable LOCATION ACCESS in settings.", Toast.LENGTH_LONG).show();
            return;
        }
        //调用了removeUpdates方法停止更新，停止缓存，这个时候调用getLastKnownLocation方法
        // 获取的就是最后一个缓存的位置修正信息。
        mLocationManager.removeUpdates(mLocationListener);
        //动态注册加速度传感器
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                //还有SENSOR_DELAY_UI、SENSOR_DELAY_FASTEST、SENSOR_DELAY_GAME等，
                //根据不同应用，需要的反应速率不同，具体根据实际情况设定
                SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    protected void onPause() {
        // 注销传感器
        mSensorManager.unregisterListener(this);
        //还是那一堆未知代码，防止requestLocationUpdates红线报错
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "First enable LOCATION ACCESS in settings.", Toast.LENGTH_LONG).show();
            return;
        }
        //一秒钟定位一次Location传感器，不断向缓存中写入当前的位置信息
        mLocationManager.requestLocationUpdates(provider, 1000, 1, mLocationListener);
        super.onPause();
    }

    @Override
    protected void onStop() {
        // 取消注册
        mSensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //获取当前的系统时间
        curTime = System.currentTimeMillis();
        // 获取触发event的传感器类型
        int sensorType = event.sensor.getType();
        switch (sensorType) {
            //如果检测到是加速度传感器（摇一摇动作）
            case Sensor.TYPE_ACCELEROMETER :
                if ((curTime-lastshakeTime) > 600) {
                    Log.i("currentTimeMilli_shake", "" + (curTime - lastshakeTime));
                    lastshakeTime = curTime;
                    //values[0]:X轴，values[1]：Y轴，values[2]：Z轴
                    float[] values = event.values;

                    /**
                    *因为一般正常情况下，任意轴数值最大就在9.8~10之间，只有在你突然摇动手机
                    *的时候，瞬时加速度才会突然增大或减少。
                    *所以，经过实际测试，只需监听任一轴的加速度大于16的时候，改变你需要的设置
                    *就OK了~~~
                    */
                    if ((Math.abs(values[0]) > 16 || Math.abs(values[1]) > 16 || Math.abs(values[2]) > 16)) {

                        //摇动手机后，设置button上显示的字为空
                        shake_num.setText(String.format(getString(R.string.shake_text), ++num_shack_num));
                        //摇动手机后，再伴随震动提示~~
                        vibrator.vibrate(100);

                    }
                }
                break;
            //如果发生改变的是方向传感器
            case Sensor.TYPE_ORIENTATION:
                // 获取绕Z轴转过的角度。
                //角度检测应该是实时的，因为角度变换速度很快
                znzImage.onUpdateRotation(-event.values[0]);
                angle_value.setText(String.format(getString(R.string.angle_text),
                        znzImage.getCurRotation()));

                //但是地理位置的检测速度不用太频繁，因为人的地理位置变化速度并不会非常迅速
                if (curTime - lastlocateTime > 3000) {
                    Log.i("currentTimeMilli_change", "" + (curTime-lastlocateTime));
                    lastlocateTime = curTime;
                    if (mCurrentLocation == null) {
                        longitude.setText(String.format(getString(R.string.long_text), 0f));
                        latitude.setText(String.format(getString(R.string.lat_text), 0f));
                    } else {
                        longitude.setText(String.format(getString(R.string.long_text),
                                mCurrentLocation.getLongitude()));
                        latitude.setText(String.format(getString(R.string.lat_text),
                                mCurrentLocation.getLatitude()));

                        new GeoDecoder().execute(mCurrentLocation);
                    }
                }
                break;
        }
    }

    @Override
    //如果加速度发生改变的时候，我们在传感器中已经实现了加速度摇一摇的使用，这里我们就不用在重写函数了
    // 只需要将这个虚函数实例化
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    //给Location添加一个Listener
    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.i("location", "时间："+location.getTime());
            Log.i("location", "经度："+location.getLongitude());
            Log.i("location", "纬度："+location.getLatitude());
            Log.i("location", "海拔："+location.getAltitude());
            mCurrentLocation = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(MainActivity.this, "Enable network provider", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(MainActivity.this, "Disable network provider", Toast.LENGTH_SHORT).show();
        }
    };
    private class GeoDecoder extends AsyncTask<Location, Integer, String> {

        @Override
        protected String doInBackground(Location... params) {
            Location param = params[0];
            Log.i("param","经度"+(param==null?"null":param.getLongitude())+"纬度"+(param==null?"null":param.getLatitude()));
            try {
                if (param != null) {
                    //执行HTTP协议，从网上获取当前位置的详细信息
                    String decode = sendHttpRequest(String.format(getString(R.string.geocoderv2),
                            param.getLatitude(), param.getLongitude(), getString(R.string.server_ak)));
                    //检查信息内容
                    for(int i = 0; i<decode.split("\"").length; ++i){
                        if (decode.split("\"")[i] == null) break;
                        Log.i("decode_splite_"+i, decode.split("\"")[i]);
                    }
                    //获取url地址，在浏览器上访问，检查是否正常
                    Log.i("URL2", String.format(getString(R.string.geocoderv2),
                            param.getLongitude(), param.getLatitude(),
                            getString(R.string.server_ak)));
                    Log.i("decode", decode.split("\"")[13]);
                    return decode.split("\"")[13]+"+"+decode.split("\"")[63];
                }
            } catch (IOException e) {
                //e.printStackTrace();
                Log.i("error", e.getMessage());
            }

            return "Error";
        }

        @Override
        protected void onPostExecute(String s) {
            current_position.setText(s.split("\\+")[0]);
            sematic_description.setText(s.split("\\+")[1]);
        }

        //通过http协议从网上抓取信息，这部分上希饰演已经讲过，就不在分析
        private String sendHttpRequest(String request) throws IOException {
            URL url = new URL(request);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream is = connection.getInputStream();
            Log.i("is_String", is.toString());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String response = "";
            String readLine;
            while ((readLine = br.readLine()) != null) {
                response = response + readLine;
            }
            Log.i("response", response);
            is.close();
            br.close();
            connection.disconnect();
            return response;
        }
    }
}