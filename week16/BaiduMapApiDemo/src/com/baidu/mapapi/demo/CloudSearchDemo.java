package com.baidu.mapapi.demo;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.Bounds;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.cloud.BoundsSearchInfo;
import com.baidu.mapapi.cloud.DetailResult;
import com.baidu.mapapi.cloud.DetailSearchInfo;
import com.baidu.mapapi.cloud.GeoSearchListener;
import com.baidu.mapapi.cloud.GeoSearchManager;
import com.baidu.mapapi.cloud.GeoSearchResult;
import com.baidu.mapapi.cloud.NearbySearchInfo;
import com.baidu.mapapi.cloud.RegionSearchInfo;

public class CloudSearchDemo extends MapActivity implements GeoSearchListener{
    
    MapView mMapView;
    
    @Override
    protected void onCreate(Bundle icicle) {
        // TODO Auto-generated method stub
        super.onCreate(icicle);
        setContentView(R.layout.lbssearch);
        
        
        BMapApiDemoApp app = (BMapApiDemoApp)this.getApplication();
        if (app.mBMapMan == null) {
            app.mBMapMan = new BMapManager(getApplication());
            app.mBMapMan.init(app.mStrKey, new BMapApiDemoApp.MyGeneralListener());
        }
        app.mBMapMan.start();
        // 如果使用地图SDK，请初始化地图Activity
        super.initMapActivity(app.mBMapMan);
        
        GeoSearchManager.getInstance().init(app.mBMapMan, CloudSearchDemo.this);
        
        mMapView = (MapView)findViewById(R.id.bmapView);
        
        findViewById(R.id.regionSearch).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                RegionSearchInfo r = new RegionSearchInfo();
                r.queryWords = "北京市五中";
                r.ak = "输入ak";
                r.cityName = "北京";
                r.filter.put("databox", 848);
                r.scope = 2;
                GeoSearchManager.getInstance().searchRegion(r);
            }
        });
        findViewById(R.id.nearbySearch).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                NearbySearchInfo r = new NearbySearchInfo();
                r.queryWords = "北京";
                r.ak = "输入ak";
                r.location = new GeoPoint(39956948, 116412214);
                r.radius = 10000000;
                r.filter.put("databox", 848);
                r.scope = 2;
                GeoSearchManager.getInstance().searchNearby(r);
            }
        });
        
        
        findViewById(R.id.boundsSearch).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BoundsSearchInfo r = new BoundsSearchInfo();
                r.queryWords = "五中";
                r.ak = "输入ak";
                r.bounds = new Bounds(39843895,116402214,40956948,116431457);
                r.filter.put("databox", 848);
                r.scope = 2;
                GeoSearchManager.getInstance().searchBounds(r);
            }
        });
        findViewById(R.id.detailsSearch).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailSearchInfo r = new DetailSearchInfo();
                r.id = 81217;
                r.ak = "输入ak";
                r.scope = 2;
                GeoSearchManager.getInstance().searchDetail(r);
            }
        });
    }
    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    @Override
    public void onGetGeoDetailsResult(DetailResult result, int type, int iError) {
        if (result != null) {
            if (result.content != null) {
                Toast.makeText(CloudSearchDemo.this, result.content.name, Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(CloudSearchDemo.this, "status:" + result.status, Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    @Override
    public void onGetGeoResult(GeoSearchResult result, int type, int iError) {
        if (result != null && result.poiList!= null && result.poiList.size() > 0) {
            CloudOverlay poiOverlay = new CloudOverlay(this);
            poiOverlay.setData(result.poiList);
            mMapView.getOverlays().clear();
            mMapView.getOverlays().add(poiOverlay);
            mMapView.invalidate();
            mMapView.getController().animateTo(new GeoPoint((int)(result.poiList.get(0).latitude * 1e6), (int)(result.poiList.get(0).longitude * 1e6)));
        }
    }
}
