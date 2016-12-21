package com.example.yunyao.lab10;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

/**
 * Created by yunyao on 2016/12/21.
 */

public class ArrowView extends ImageView {
    private static final float ROTATION_THRESHOLD = 0.5f;
    public ArrowView(Context context){
        super(context);
        init();
    }
    public ArrowView(Context context, AttributeSet attr){
        super(context, attr);
        init();}
    public ArrowView(Context context, AttributeSet attr, int defStyleAttr){
        super(context, attr, defStyleAttr);
        init();}
    private float mCurRotation;
    private void init() {mCurRotation = 0.0f;}
    public float getCurRotation(){return mCurRotation;}
    public void onUpdateRotation(float newRotation) {
        RotateAnimation animation = new RotateAnimation(mCurRotation, newRotation,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(200);
        animation.setFillAfter(true);
        startAnimation(animation);
        newRotation = newRotation<-180 ? newRotation+360 : newRotation;
        mCurRotation = newRotation;
    }
}
