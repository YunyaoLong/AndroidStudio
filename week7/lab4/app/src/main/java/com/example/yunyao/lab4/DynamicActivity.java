package com.example.yunyao.lab4;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * Created by yunyao on 2016/10/18.
 */
public class DynamicActivity extends Activity {
    private static final String DYNAMICACTION = "dynamicreceiver";
    boolean reg_flag = false;
    private DynamicReceiver myReceiver = new DynamicReceiver();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic);
        final Button RegisterButton = (Button)findViewById(R.id.RegisterButton);
        final Button SendButton = (Button)findViewById(R.id.SendButton);
        final TextView textView = (TextView)findViewById(R.id.editText);
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reg_flag = !reg_flag;
                if(reg_flag){
                    RegisterButton.setText("Unregister Broadcast");
                    IntentFilter dynamic_filter = new IntentFilter();
                    dynamic_filter.addAction(DYNAMICACTION);			//添加动态广播的Action
                    registerReceiver(myReceiver, dynamic_filter);    // 注册自定义动态广播消息
                }else{
                    RegisterButton.setText("Register Broadcast");
                    unregisterReceiver(myReceiver);
                }
            }
        });
        SendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(DYNAMICACTION);
                Bundle bundle = new Bundle();
                bundle.putString("text", textView.getText().toString());
                bundle.putInt("src", R.mipmap.dynamic);
                intent.putExtras(bundle);
                sendBroadcast(intent);
            }
        });
    ;}
}
