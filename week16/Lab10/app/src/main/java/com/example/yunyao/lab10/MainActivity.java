package com.example.yunyao.lab10;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by yunyao on 2016/12/21.
 */

public class MainActivity extends Activity implements SensorEventListener {
    // 定义显示指南针的图片
    ArrowView znzImage;

    // 定义Sensor管理器
    SensorManager mSensorManager;
    Sensor mMagneticSensor, mAccelerometerSensor;

    TextView angle_value;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 获取界面中显示指南针的图片
        znzImage = (ArrowView) findViewById(R.id.angle_pointer);
        angle_value = (TextView) findViewById(R.id.angle_value);

        // 获取传感器管理服务
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        // 取消注册
        mSensorManager.unregisterListener(this);
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
        // 获取触发event的传感器类型
        int sensorType = event.sensor.getType();
        switch (sensorType) {
            case Sensor.TYPE_ORIENTATION:
                // 获取绕Z轴转过的角度。
                znzImage.onUpdateRotation(-event.values[0]);
                angle_value.setText(String.format("旋转角度为：%.2f", znzImage.getCurRotation()));
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}