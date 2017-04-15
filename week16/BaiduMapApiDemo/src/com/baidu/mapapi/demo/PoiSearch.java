package com.baidu.mapapi.demo;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKPoiInfo;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKSuggestionResult;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.PoiOverlay;


public class PoiSearch extends MapActivity {
	Button mBtnSearch = null;	// 搜索按钮
	Button mBtnDetailSearch = null;  // 详细搜搜按钮
	Button mSuggestionSearch = null;  //suggestion搜索
	ListView mSuggestionList = null;
	public static String mStrSuggestions[] = {};
	
	MapView mMapView = null;	// 地图View
	MKSearch mSearch = null;	// 搜索模块，也可去掉地图模块独立使用
	
	MKPoiResult mRes = null;    // poi检索结果
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        setContentView(R.layout.poisearch);
        
		BMapApiDemoApp app = (BMapApiDemoApp)this.getApplication();
		if (app.mBMapMan == null) {
			app.mBMapMan = new BMapManager(getApplication());
			app.mBMapMan.init(app.mStrKey, new BMapApiDemoApp.MyGeneralListener());
		}
		app.mBMapMan.start();
        // 如果使用地图SDK，请初始化地图Activity
        super.initMapActivity(app.mBMapMan);
        
        mMapView = (MapView)findViewById(R.id.bmapView);
        mMapView.setTraffic(true);
        //设置在缩放动画过程中也显示overlay,默认为不绘制
        mMapView.setDrawOverlayWhenZooming(true);
        
        // 初始化搜索模块，注册事件监听
        mSearch = new MKSearch();
        mSearch.init(app.mBMapMan, new MKSearchListener(){

            @Override
            public void onGetPoiDetailSearchResult(int type, int error) {
                if (error != 0) {
                    Toast.makeText(PoiSearch.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(PoiSearch.this, "成功，查看详情页面", Toast.LENGTH_SHORT).show();
                }
            }
            
			public void onGetPoiResult(MKPoiResult res, int type, int error) {
				// 错误号可参考MKEvent中的定义
				if (error != 0 || res == null) {
					Toast.makeText(PoiSearch.this, "抱歉，未找到结果", Toast.LENGTH_LONG).show();
					return;
				}

				mRes = res;
			    // 将地图移动到第一个POI中心点
			    if (res.getCurrentNumPois() > 0) {
				    // 将poi结果显示到地图上
					PoiOverlay poiOverlay = new PoiOverlay(PoiSearch.this, mMapView, mSearch);
					poiOverlay.setData(res.getAllPoi());
				    mMapView.getOverlays().clear();
				    mMapView.getOverlays().add(poiOverlay);
				    mMapView.invalidate();
			    	mMapView.getController().animateTo(res.getPoi(0).pt);
			    } else if (res.getCityListNum() > 0) {
			    	String strInfo = "在";
			    	for (int i = 0; i < res.getCityListNum(); i++) {
			    		strInfo += res.getCityListInfo(i).city;
			    		strInfo += ",";
			    	}
			    	strInfo += "找到结果";
					Toast.makeText(PoiSearch.this, strInfo, Toast.LENGTH_LONG).show();
			    }
			}
			public void onGetDrivingRouteResult(MKDrivingRouteResult res,
					int error) {
			}
			public void onGetTransitRouteResult(MKTransitRouteResult res,
					int error) {
			}
			public void onGetWalkingRouteResult(MKWalkingRouteResult res,
					int error) {
			}
			public void onGetAddrResult(MKAddrInfo res, int error) {
			}
			public void onGetBusDetailResult(MKBusLineResult result, int iError) {
			}
			@Override
			public void onGetSuggestionResult(MKSuggestionResult res, int arg1) {
				if (arg1 != 0 || res == null) {
					Toast.makeText(PoiSearch.this, "抱歉，未找到结果", Toast.LENGTH_LONG).show();
					return;
				}
				int nSize = res.getSuggestionNum();
				mStrSuggestions = new String[nSize];

				for (int i = 0; i < nSize; i++) {
					mStrSuggestions[i] = res.getSuggestion(i).city + res.getSuggestion(i).key;
				}
				ArrayAdapter<String> suggestionString = new ArrayAdapter<String>(PoiSearch.this, android.R.layout.simple_list_item_1,mStrSuggestions);
				mSuggestionList.setAdapter(suggestionString);
				Toast.makeText(PoiSearch.this, "suggestion callback", Toast.LENGTH_LONG).show();

			}
            @Override
            public void onGetRGCShareUrlResult(String arg0, int arg1) {
            }
			
        });
        mSuggestionList = (ListView) findViewById(R.id.listView1);
        // 设定搜索按钮的响应
        mBtnSearch = (Button)findViewById(R.id.search);
        
        OnClickListener clickListener = new OnClickListener(){
			public void onClick(View v) {
				SearchButtonProcess(v);
			}
        };
        mBtnSearch.setOnClickListener(clickListener); 
        
        mBtnDetailSearch = (Button)findViewById(R.id.detail_search_a);
        mBtnDetailSearch.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                SearchButtonProcess(v);
            }
        });
        
        // 设定suggestion响应
        mSuggestionSearch = (Button)findViewById(R.id.suggestionsearch);

		OnClickListener clickListener1 = new OnClickListener(){
			public void onClick(View v) {
				SuggestionSearchButtonProcess(v);
			}
		};
		mSuggestionSearch.setOnClickListener(clickListener1); 
	}
	void SearchButtonProcess(View v) {
		if (mBtnSearch.equals(v)) {
//			Intent intent = null;
//			intent = new Intent(PoiSearch.this, MapViewDemo.class);
//			this.startActivity(intent);
//		    JSONObject log = new JSONObject();
//		    log.put(name, value)
		    
			EditText editCity = (EditText)findViewById(R.id.city);
			EditText editSearchKey = (EditText)findViewById(R.id.searchkey);
			mSearch.poiSearchInCity(editCity.getText().toString(), 
					editSearchKey.getText().toString());
		}
		else if (mBtnDetailSearch.equals(v)) {
		    if (mRes != null) {
		        ArrayList<MKPoiInfo> allPoi = mRes.getAllPoi();
	            if (allPoi == null || allPoi.size() <= 0) {
	                Toast.makeText(PoiSearch.this, "请先搜索美食类poi", Toast.LENGTH_LONG).show();
	            }
	            else {
	                if (allPoi.get(0).hasCaterDetails) {
	                    mSearch.poiDetailSearch(allPoi.get(0).uid);
	                }
	            }
		    }
		    else {
		        Toast.makeText(PoiSearch.this, "请先搜索美食类poi", Toast.LENGTH_LONG).show();
		    }
		}
	}

	void SuggestionSearchButtonProcess(View v) {
		EditText editSearchKey = (EditText)findViewById(R.id.suggestionkey);
		mSearch.suggestionSearch(editSearchKey.getText().toString());
	}
	
	@Override
	protected void onPause() {
//		BMapApiDemoApp app = (BMapApiDemoApp)this.getApplication();
//		app.mBMapMan.stop();
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		BMapApiDemoApp app = (BMapApiDemoApp)this.getApplication();
		app.mBMapMan.start();
		super.onResume();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
