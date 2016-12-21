/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.micode.compass;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CompassActivity extends Activity {
	private static final int EXIT_TIME = 2000;// 两次按返回键的间隔判断
	private final float MAX_ROATE_DEGREE = 1.0f;// 最多旋转一周，即360°
	private SensorManager mSensorManager;// 传感器管理对象
	private Sensor mOrientationSensor;// 传感器对象
	// private LocationManager mLocationManager;// 位置管理对象
	// private String mLocationProvider;// 位置提供者名称，GPS设备还是网络
	private float mDirection;// 当前浮点方向
	private float mTargetDirection;// 目标浮点方向
	private AccelerateInterpolator mInterpolator;// 动画从开始到结束，变化率是一个加速的过程,就是一个动画速率
	protected final Handler mHandler = new Handler();
	private boolean mStopDrawing;// 是否停止指南针旋转的标志位
	private boolean mChinease;// 系统当前是否使用中文
	private long firstExitTime = 0L;// 用来保存第一次按返回键的时间

	LocationApplication application;
	View mCompassView;
	CompassView mPointer;// 指南针view
	// TextView mLocationTextView;// 显示位置的view
	TextView mLatitudeTV;// 纬度
	TextView mLongitudeTV;// 经度
	LinearLayout mDirectionLayout;// 显示方向（东南西北）的view
	LinearLayout mAngleLayout;// 显示方向度数的view
	View mViewGuide;
	ImageView mGuideAnimation;
	protected Handler invisiableHandler = new Handler() {
		public void handleMessage(Message msg) {
			mViewGuide.setVisibility(View.GONE);
		}
	};

	public void onWindowFocusChanged(boolean hasFocus) {
		AnimationDrawable anim = (AnimationDrawable) mGuideAnimation
				.getDrawable();
		anim.start();
	}

	// 这个是更新指南针旋转的线程，handler的灵活使用，每20毫秒检测方向变化值，对应更新指南针旋转
	protected Runnable mCompassViewUpdater = new Runnable() {
		@Override
		public void run() {
			if (mPointer != null && !mStopDrawing) {
				if (mDirection != mTargetDirection) {

					// calculate the short routine
					float to = mTargetDirection;
					if (to - mDirection > 180) {
						to -= 360;
					} else if (to - mDirection < -180) {
						to += 360;
					}

					// limit the max speed to MAX_ROTATE_DEGREE
					float distance = to - mDirection;
					if (Math.abs(distance) > MAX_ROATE_DEGREE) {
						distance = distance > 0 ? MAX_ROATE_DEGREE
								: (-1.0f * MAX_ROATE_DEGREE);
					}

					// need to slow down if the distance is short
					mDirection = normalizeDegree(mDirection
							+ ((to - mDirection) * mInterpolator
									.getInterpolation(Math.abs(distance) > MAX_ROATE_DEGREE ? 0.4f
											: 0.3f)));// 用了一个加速动画去旋转图片，很细致
					mPointer.updateDirection(mDirection);// 更新指南针旋转
				}

				updateDirection();// 更新方向值

				mHandler.postDelayed(mCompassViewUpdater, 20);// 20毫米后重新执行自己，比定时器好
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		application = LocationApplication.getInstance();
		setContentView(R.layout.main);
		initResources();// 初始化view
		initServices();// 初始化传感器和位置服务
		application.mTv = mLatitudeTV;
		application.mAddress = mLongitudeTV;

		if (application.mData != null)
			mLatitudeTV.setText(application.mData);
		if (application.address != null)
			mLatitudeTV.setText(application.address);
		application.mLocationClient.start();
	}

	@Override
	public void onBackPressed() {// 覆盖返回键
		long curTime = System.currentTimeMillis();
		if (curTime - firstExitTime < EXIT_TIME) {// 两次按返回键的时间小于2秒就退出应用
			finish();
		} else {
			Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
			firstExitTime = curTime;
		}
	}

	@Override
	protected void onResume() {// 在恢复的生命周期里判断、启动位置更新服务和传感器服务
		super.onResume();
		// if (mLocationProvider != null) {
		// updateLocation(mLocationManager
		// .getLastKnownLocation(mLocationProvider));
		// mLocationManager.requestLocationUpdates(mLocationProvider, 2000,
		// 10, mLocationListener);// 2秒或者距离变化10米时更新一次地理位置
		// }
		// else {
		// mLocationTextView.setText(R.string.cannot_get_location);
		// }
		if (mOrientationSensor != null) {
			mSensorManager.registerListener(mOrientationSensorEventListener,
					mOrientationSensor, SensorManager.SENSOR_DELAY_GAME);
		} else {
			// Toast.makeText(this, R.string.cannot_get_sensor,
			// Toast.LENGTH_SHORT)
			// .show();
		}
		mStopDrawing = false;
		mHandler.postDelayed(mCompassViewUpdater, 20);// 20毫秒执行一次更新指南针图片旋转
	}

	@Override
	protected void onPause() {// 在暂停的生命周期里注销传感器服务和位置更新服务
		super.onPause();
		mStopDrawing = true;
		if (mOrientationSensor != null) {
			mSensorManager.unregisterListener(mOrientationSensorEventListener);
		}
		// if (mLocationProvider != null) {
		// mLocationManager.removeUpdates(mLocationListener);
		// }
	}

	// 初始化view
	private void initResources() {
		mViewGuide = findViewById(R.id.view_guide);
		mViewGuide.setVisibility(View.VISIBLE);
		invisiableHandler.sendMessageDelayed(new Message(), 3000);
		mGuideAnimation = (ImageView) findViewById(R.id.guide_animation);
		mDirection = 0.0f;// 初始化起始方向
		mTargetDirection = 0.0f;// 初始化目标方向
		mInterpolator = new AccelerateInterpolator();// 实例化加速动画对象
		mStopDrawing = true;
		mChinease = TextUtils.equals(Locale.getDefault().getLanguage(), "zh");// 判断系统当前使用的语言是否为中文

		mCompassView = findViewById(R.id.view_compass);// 实际上是一个LinearLayout，装指南针ImageView和位置TextView
		mPointer = (CompassView) findViewById(R.id.compass_pointer);// 自定义的指南针view
		// mLocationTextView = (TextView)
		// findViewById(R.id.textview_location);// 显示位置信息的TextView
		mLongitudeTV = (TextView) findViewById(R.id.textview_location_longitude_degree);
		mLatitudeTV = (TextView) findViewById(R.id.textview_location_latitude_degree);
		mDirectionLayout = (LinearLayout) findViewById(R.id.layout_direction);// 顶部显示方向名称（东南西北）的LinearLayout
		mAngleLayout = (LinearLayout) findViewById(R.id.layout_angle);// 顶部显示方向具体度数的LinearLayout

		// mPointer.setImageResource(mChinease ? R.drawable.compass_cn
		// : R.drawable.compass);// 如果系统使用中文，就用中文的指南针图片
	}

	// 初始化传感器和位置服务
	private void initServices() {
		// sensor manager
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mOrientationSensor = mSensorManager.getSensorList(
				Sensor.TYPE_ORIENTATION).get(0);
		// Log.i("way", mOrientationSensor.getName());

		// location manager
		// mLocationManager = (LocationManager)
		// getSystemService(Context.LOCATION_SERVICE);
		// Criteria criteria = new Criteria();// 条件对象，即指定条件过滤获得LocationProvider
		// criteria.setAccuracy(Criteria.ACCURACY_FINE);// 较高精度
		// criteria.setAltitudeRequired(false);// 是否需要高度信息
		// criteria.setBearingRequired(false);// 是否需要方向信息
		// criteria.setCostAllowed(true);// 是否产生费用
		// criteria.setPowerRequirement(Criteria.POWER_LOW);// 设置低电耗
		// mLocationProvider = mLocationManager.getBestProvider(criteria,
		// true);// 获取条件最好的Provider

	}

	// 更新顶部方向显示的方法
	private void updateDirection() {
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		// 先移除layout中所有的view
		mDirectionLayout.removeAllViews();
		mAngleLayout.removeAllViews();

		// 下面是根据mTargetDirection，作方向名称图片的处理
		ImageView east = null;
		ImageView west = null;
		ImageView south = null;
		ImageView north = null;
		float direction = normalizeDegree(mTargetDirection * -1.0f);
		if (direction > 22.5f && direction < 157.5f) {
			// east
			east = new ImageView(this);
			east.setImageResource(mChinease ? R.drawable.e_cn : R.drawable.e);
			east.setLayoutParams(lp);
		} else if (direction > 202.5f && direction < 337.5f) {
			// west
			west = new ImageView(this);
			west.setImageResource(mChinease ? R.drawable.w_cn : R.drawable.w);
			west.setLayoutParams(lp);
		}

		if (direction > 112.5f && direction < 247.5f) {
			// south
			south = new ImageView(this);
			south.setImageResource(mChinease ? R.drawable.s_cn : R.drawable.s);
			south.setLayoutParams(lp);
		} else if (direction < 67.5 || direction > 292.5f) {
			// north
			north = new ImageView(this);
			north.setImageResource(mChinease ? R.drawable.n_cn : R.drawable.n);
			north.setLayoutParams(lp);
		}
		// 下面是根据系统使用语言，更换对应的语言图片资源
		if (mChinease) {
			// east/west should be before north/south
			if (east != null) {
				mDirectionLayout.addView(east);
			}
			if (west != null) {
				mDirectionLayout.addView(west);
			}
			if (south != null) {
				mDirectionLayout.addView(south);
			}
			if (north != null) {
				mDirectionLayout.addView(north);
			}
		} else {
			// north/south should be before east/west
			if (south != null) {
				mDirectionLayout.addView(south);
			}
			if (north != null) {
				mDirectionLayout.addView(north);
			}
			if (east != null) {
				mDirectionLayout.addView(east);
			}
			if (west != null) {
				mDirectionLayout.addView(west);
			}
		}
		// 下面是根据方向度数显示度数图片数字
		int direction2 = (int) direction;
		boolean show = false;
		if (direction2 >= 100) {
			mAngleLayout.addView(getNumberImage(direction2 / 100));
			direction2 %= 100;
			show = true;
		}
		if (direction2 >= 10 || show) {
			mAngleLayout.addView(getNumberImage(direction2 / 10));
			direction2 %= 10;
		}
		mAngleLayout.addView(getNumberImage(direction2));
		// 下面是增加一个°的图片
		ImageView degreeImageView = new ImageView(this);
		degreeImageView.setImageResource(R.drawable.degree);
		degreeImageView.setLayoutParams(lp);
		mAngleLayout.addView(degreeImageView);
	}

	// 获取方向度数对应的图片，返回ImageView
	private ImageView getNumberImage(int number) {
		ImageView image = new ImageView(this);
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		switch (number) {
		case 0:
			image.setImageResource(R.drawable.number_0);
			break;
		case 1:
			image.setImageResource(R.drawable.number_1);
			break;
		case 2:
			image.setImageResource(R.drawable.number_2);
			break;
		case 3:
			image.setImageResource(R.drawable.number_3);
			break;
		case 4:
			image.setImageResource(R.drawable.number_4);
			break;
		case 5:
			image.setImageResource(R.drawable.number_5);
			break;
		case 6:
			image.setImageResource(R.drawable.number_6);
			break;
		case 7:
			image.setImageResource(R.drawable.number_7);
			break;
		case 8:
			image.setImageResource(R.drawable.number_8);
			break;
		case 9:
			image.setImageResource(R.drawable.number_9);
			break;
		}
		image.setLayoutParams(lp);
		return image;
	}

	// 更新位置显示
	private void updateLocation(Location location) {
		if (location == null) {
			// mLocationTextView.setText(R.string.getting_location);
			return;
		} else {
			// StringBuilder sb = new StringBuilder();
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			String latitudeStr;
			String longitudeStr;
			if (latitude >= 0.0f) {
				latitudeStr = getString(R.string.location_north,
						getLocationString(latitude));
			} else {
				latitudeStr = getString(R.string.location_south,
						getLocationString(-1.0 * latitude));
			}

			// sb.append("    ");

			if (longitude >= 0.0f) {
				longitudeStr = getString(R.string.location_east,
						getLocationString(longitude));
			} else {
				longitudeStr = getString(R.string.location_west,
						getLocationString(-1.0 * longitude));
			}
			mLatitudeTV.setText(latitudeStr);
			mLongitudeTV.setText(longitudeStr);
			// mLocationTextView.setText(sb.toString());//
			// 显示经纬度，其实还可以作反向编译，显示具体地址
		}
	}

	// 把经纬度转换成度分秒显示
	private String getLocationString(double input) {
		int du = (int) input;
		int fen = (((int) ((input - du) * 3600))) / 60;
		int miao = (((int) ((input - du) * 3600))) % 60;
		return String.valueOf(du) + "°" + String.valueOf(fen) + "′"
				+ String.valueOf(miao) + "″";
	}

	// 方向传感器变化监听
	private SensorEventListener mOrientationSensorEventListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {
			float direction = event.values[mSensorManager.DATA_X] * -1.0f;
			mTargetDirection = normalizeDegree(direction);// 赋值给全局变量，让指南针旋转
			// Log.i("way", event.values[mSensorManager.DATA_Y] + "");
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};

	// 调整方向传感器获取的值
	private float normalizeDegree(float degree) {
		return (degree + 720) % 360;
	}

	// 位置信息更新监听
	// LocationListener mLocationListener = new LocationListener() {
	//
	// @Override
	// public void onStatusChanged(String provider, int status, Bundle extras) {
	// if (status != LocationProvider.OUT_OF_SERVICE) {
	// updateLocation(mLocationManager
	// .getLastKnownLocation(mLocationProvider));
	// }
	// // else {
	// // mLocationTextView.setText(R.string.cannot_get_location);
	// // }
	// }
	//
	// @Override
	// public void onProviderEnabled(String provider) {
	// }
	//
	// @Override
	// public void onProviderDisabled(String provider) {
	// }
	//
	// @Override
	// public void onLocationChanged(Location location) {
	// updateLocation(location);// 更新位置
	// }
	// };
}
