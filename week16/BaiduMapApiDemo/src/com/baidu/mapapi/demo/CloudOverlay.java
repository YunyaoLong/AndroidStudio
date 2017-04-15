package com.baidu.mapapi.demo;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.ItemizedOverlay;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.OverlayItem;
import com.baidu.mapapi.cloud.CustomPoiInfo;

public class CloudOverlay extends ItemizedOverlay<OverlayItem> {

    List<CustomPoiInfo> mLbsPoints;
    Activity mContext;
    private int mDpi = 0; //0:low; 1:mid; 2:high
    
    public CloudOverlay(Activity context) {
        super(null);
        mContext = context;
        
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm); 
        if (dm.densityDpi <= 120) {
            mDpi = 0;
        } else if (dm.densityDpi <= 180) {
            mDpi = 1;
        } else {
            mDpi = 2;
        }
    }

    public void setData(List<CustomPoiInfo> lbsPoints) {
        if (lbsPoints != null) {
            mLbsPoints = lbsPoints;
            super.populate();
        }
    }
    
    @Override
    protected OverlayItem createItem(int i) {
        char[] pos = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'};
        char[] dpi = {'l', 'm', 'h'};
        CustomPoiInfo rec = mLbsPoints.get(i);
        GeoPoint pt = new GeoPoint((int)(rec.latitude * 1e6), (int)(rec.longitude * 1e6));
        OverlayItem item = new OverlayItem(pt , rec.name, rec.address);
        Drawable marker = null;
        if (i < 10) {
            StringBuilder sb = new StringBuilder(32);
            sb.append("icon_mark").append(pos[i]).append('_').append(dpi[mDpi]).append(".png");
            marker = getDrawable(mContext, sb.toString());
        }
        item.setMarker(boundCenterBottom(marker));
        return item;
    }
    
    @Override
    public int size() {
        if (mLbsPoints != null)
            return mLbsPoints.size();
        else
            return 0;
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        // TODO Auto-generated method stub
        return super.clone();
    }
    
    @Override
    public void draw(Canvas arg0, MapView arg1, boolean arg2) {
        // TODO Auto-generated method stub
        super.draw(arg0, arg1, arg2);
    }
    
    @Override
    public boolean draw(Canvas arg0, MapView arg1, boolean arg2, long arg3) {
        // TODO Auto-generated method stub
        return super.draw(arg0, arg1, arg2, arg3);
    }
    
    @Override
    protected boolean onTap(int arg0) {
        CustomPoiInfo item = mLbsPoints.get(arg0);
        Toast.makeText(mContext, item.name,Toast.LENGTH_LONG).show();
        return super.onTap(arg0);
    }
    
    private static final int sdkVersion = Integer.parseInt(Build.VERSION.SDK);
    private static Constructor<?> mConstructorBitmapDrawable = null;
    static Drawable getDrawable(Context context, String name) {
        try {
            AssetManager am = context.getAssets();
            InputStream is = am.open(name);
            Bitmap bm = BitmapFactory.decodeStream(is);
            is.close();
            if (sdkVersion < 4) {
                Drawable drawable = new BitmapDrawable(bm);
                return drawable;
            } else {
                Resources rs = context.getResources();
                if (mConstructorBitmapDrawable == null) {
                    Class<?> drawableClass = Class.forName("android.graphics.drawable.BitmapDrawable");
                    mConstructorBitmapDrawable = drawableClass.getConstructor(new Class[]{Resources.class, Bitmap.class});
                }
                Object obj = mConstructorBitmapDrawable.newInstance(new Object[]{rs, bm});
                return (Drawable)obj;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
