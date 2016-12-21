package net.micode.compass;

import android.app.Application;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class LocationApplication extends Application {
	private static LocationApplication instance;
	public LocationClient mLocationClient = null;
	public String mData;
	public String address;
	public MyLocationListenner myListener = new MyLocationListenner();
	public TextView mTv;
	public TextView mAddress;
	public static LocationApplication getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		instance = this;
		mLocationClient = new LocationClient(getApplicationContext());
		mLocationClient.registerLocationListener(myListener);
		setLocationOption();
		super.onCreate();
	}

	// 设置相关参数
	public void setLocationOption() {
		LocationClientOption option = new LocationClientOption();
		option.setProdName("Compass");
		option.setOpenGps(true); // 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setAddrType("all"); // 设置地址信息，仅设置为“all”时有地址信息，默认无地址信息
		option.setScanSpan(5 * 60 * 1000); // 设置定位模式，小于1秒则一次定位;大于等于1秒则定时定位
		option.setPriority(LocationClientOption.GpsFirst);
		mLocationClient.setLocOption(option);
	}


	/**
	 *监听函数，又新位置的时候，格式化成字符串，输出到屏幕中
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			if (location == null)
				return;
			StringBuffer sb = new StringBuffer(256);
			// sb.append("时间: ");
			// sb.append(location.getTime());
			sb.append("纬度 : ");
			sb.append(location.getLatitude() + "°");
			sb.append(", 经度 : ");
			sb.append(location.getLongitude() + "°");
//			sb.append(", 精度 : ");
//			sb.append(location.getRadius() + " 米");
			mData = sb.toString();
			if(mTv != null) mTv.setText(sb);
			
			if (location.getLocType() == BDLocation.TypeGpsLocation) {
				address = "速度 : " + location.getSpeed();
				// sb.append("\n�ٶ� : ");
				// sb.append(location.getSpeed());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				// sb.append("\n��ַ : ");
				// sb.append(location.getAddrStr());
				address = "地址 : " + location.getAddrStr();
			}
			if(mAddress !=null) mAddress.setText(address);
//			logMsg(sb.toString());
		}

		@Override
		public void onReceivePoi(BDLocation poiLocation) {
			// TODO Auto-generated method stub

		}

	}
}
