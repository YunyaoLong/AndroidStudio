package sysu.mobile.mapsensor;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.tbruyelle.rxpermissions.RxPermissions;

import rx.functions.Action1;

public class CheckPermissionActivity extends AppCompatActivity {

    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler();

        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        if (granted) { // Always true pre-M
                            Toast.makeText(CheckPermissionActivity.this, "Granted", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CheckPermissionActivity.this, SensorActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(CheckPermissionActivity.this, "App will finish in 3 secs...", Toast.LENGTH_SHORT).show();

                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            }, 3000);
                        }
                    }
                });
    }
}
