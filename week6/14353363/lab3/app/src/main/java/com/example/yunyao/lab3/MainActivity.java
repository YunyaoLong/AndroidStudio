package com.example.yunyao.lab3;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] First_letter = new String[]{"A", "E", "D", "E", "F", "J", "I", "M", "J", "P"};
        final String[] Name = new String[]{"Aaron", "Elvis", "David", "Edwin", "Frank", "Joshua", "Ivan", "Mark", "Joseph", "Phoebe" };
        final String[] Number = new String[]{"17715523654","18825653224","15052116654","18854211875","13955188541","13621574410","15684122771","17765213579","13315466578","17895466428"};
        final String[] Equipment = new String[]{"手机","手机","手机","手机","手机","手机","手机","手机","手机","手机"};
        final String[] Detail = new String[]{"江苏苏州电信","广东揭阳移动","江苏无锡移动","山东青岛移动","安徽合肥移动","江苏苏州移动","山东烟台联通","广东珠海电信","河北石家庄电信","山东东营移动"};
        final String[] Color = new String[]{"#BB4C3B", "#c48d30", "#4469b0", "#20A17B", "#BB4C3B", "#c48d30", "#4469b0", "#20A17B", "#BB4C3B", "#c48d30"};

        final List<Map<String, String>> data = new ArrayList<>();
        for (int i = 0; i<10; ++i){
            Map<String, String> temp = new LinkedHashMap<>();
            temp.put("First_letter", First_letter[i]);
            temp.put("Name", Name[i]);
            temp.put("Number", Number[i]);
            temp.put("Equipment", Equipment[i]);
            temp.put("Detail", Detail[i]);
            temp.put("Color", Color[i]);
            data.add(temp);
        }
        ListView listview = (ListView)findViewById(R.id.list);
        final SimpleAdapter simpleadapter = new SimpleAdapter(this, data, R.layout.item,
                new String[]{"First_letter", "Name"},
                new int[]{R.id.First_letter,R.id.Name});
        listview.setAdapter(simpleadapter);
        /*至此MainActivity的ListView显示完毕*/

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // When clicked, show a toast with the TextView text
                Intent intent = new Intent(MainActivity.this, dataActivity.class);
                intent.putExtra("Name", data.get(position).get("Name"));
                intent.putExtra("Number", data.get(position).get("Number"));
                intent.putExtra("Equipment", data.get(position).get("Equipment"));
                intent.putExtra("Detail", data.get(position).get("Detail"));
                intent.putExtra("Color", data.get(position).get("Color"));
                startActivity(intent);
            }
        });
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                String name = new String();
                name = data.get(position).get("Name");
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("确定删除联系人"+name);
                builder.setTitle("删除联系人");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        data.remove(position);
                        simpleadapter.notifyDataSetChanged();
                        dialogInterface.dismiss();
                        //Toast.makeText(getApplicationContext(), "对话框\"确定\"按钮被点击", Toast.LENGTH_SHORT).show();
                        //MainActivity.this.finish();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
                return true;
            }
        });
    }

}
