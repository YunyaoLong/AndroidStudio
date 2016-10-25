package com.example.yunyao.lab3;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yunyao on 2016/10/11.
 */
public class dataActivity extends Activity {

    static boolean starflag = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        Intent intent = getIntent();

        /*
        final List<Map<String, String>> sourece = new ArrayList<>();
        Map<String, String> source_temp = new LinkedHashMap<>();
        source_temp.put("Number", intent.getStringExtra("Number"));
        source_temp.put("Equipment", intent.getStringExtra("Equipment"));
        source_temp.put("Detail", intent.getStringExtra("Detail"));
        source_temp.put("Name", intent.getStringExtra("Name"));
        source_temp.put("Color", intent.getStringExtra("Color"));
        sourece.add(source_temp);

        ListView source_listview = (ListView)findViewById(R.id.source);
        final SimpleAdapter source_simpleadapter = new SimpleAdapter(this, sourece, R.layout.sourceitem,
                new String[]{"Number", "Equipment", "Detail"},
                new int[]{R.id.Number, R.id.Equipment, R.id.Detail});
        source_listview.setAdapter(source_simpleadapter);
        */
        String Number = new String(intent.getStringExtra("Number"));
        String Equipment = new String(intent.getStringExtra("Equipment"));
        String Detail = new String(intent.getStringExtra("Detail"));
        String Name = new String(intent.getStringExtra("Name"));
        String sourceColor = new String(intent.getStringExtra("Color"));
        final List<dataclass> list= new ArrayList<>();
        dataclass sourcetemp = new dataclass("", Name, Number, Equipment, Detail, sourceColor);
        list.add(sourcetemp);
        ListView source_listview = (ListView)findViewById(R.id.source);
        final MyDataAdepter myAdepter = new MyDataAdepter(this, list);
        source_listview.setAdapter(myAdepter);

        TextView text = (TextView)findViewById(R.id.Name);
        text.setText(Name);

        RelativeLayout layout = (RelativeLayout)findViewById(R.id.Content_Background);
        layout.setBackgroundColor(Color.parseColor(sourceColor));


        final List<Map<String, String>> sourcedata = new ArrayList<>();
        String[] operation = new String[]{"编辑联系人","分享联系人","加入黑名单","删除联系人"};
        for (int i = 0; i<4; ++i){
            Map<String, String> temp = new LinkedHashMap<>();
            temp.put("operation", operation[i]);
            sourcedata.add(temp);
        }
        ListView listview = (ListView)findViewById(R.id.operation);
        final SimpleAdapter simpleadapter = new SimpleAdapter(this, sourcedata, R.layout.dataitem,
                new String[]{"operation"},
                new int[]{R.id.op});
        listview.setAdapter(simpleadapter);

        ImageButton backbutton = (ImageButton) findViewById(R.id.Back);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ImageButton starbutton = (ImageButton)findViewById(R.id.empty_star);
        starbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                starflag = !starflag;
                if (starflag == true) {
                    //重新设置按下时的背景图片
                    view.setBackground(getResources().getDrawable(R.mipmap.empty_star));
                } else if (starflag == false) {
                    //再修改为抬起时的正常图片
                    view.setBackground(getResources().getDrawable(R.mipmap.full_star));
                }
            }
        });
    }
}
