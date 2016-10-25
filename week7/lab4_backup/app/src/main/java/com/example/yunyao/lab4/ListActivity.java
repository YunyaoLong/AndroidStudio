package com.example.yunyao.lab4;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yunyao on 2016/10/18.
 */
public class ListActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        int FruitSrc [] =new int[]{R.mipmap.apple, R.mipmap.banana,
                R.mipmap.cherry, R.mipmap.coco, R.mipmap.kiwi,
                R.mipmap.orange, R.mipmap.pear, R.mipmap.strawberry, R.mipmap.watermelon, };
        String FruitName[] = new String[]{"Apple", "Banana", "Cherry", "Coco", "Kiwi", "Orange", "Pear", "Strawberry", "Watermelon"};

        final List<FruitSource> data = new ArrayList<>();
        for (int i = 0; i<9; ++i){
            FruitSource temp = new FruitSource(FruitSrc[i], FruitName[i]);
            data.add(temp);
        }
        ListView listview = (ListView)findViewById(R.id.FruitList);
        final MyListAdepter myMainAdepter = new MyListAdepter(this, data);
        listview.setAdapter(myMainAdepter);
        //ListView显示完毕

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
                /*注释部分为SimpleAdepter*/
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // When clicked, show a toast with the TextView text
                Intent intent = new Intent("FruitSource");
                Bundle bundle = new Bundle();
                bundle.putString("FruitName", data.get(position).getFruitName());
                bundle.putInt("FruitSrc", data.get(position).getFruitSrc());
                intent.putExtras(bundle);
                sendBroadcast(intent);
            }
        });
    }
}
