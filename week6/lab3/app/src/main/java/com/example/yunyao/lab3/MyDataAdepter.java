package com.example.yunyao.lab3;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MyDataAdepter extends BaseAdapter {

    private Context context;
    private int itemindex;
    private List<dataclass> list;
    public MyDataAdepter(Context context, List<dataclass> list){
        this.context = context;
        this.list = list;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return (list == null ? 0 : list.size());
    }

    @Override
    public Object getItem(int i) {
        // TODO Auto-generated method stub
        return (list == null ? null : list.get(i));
    }

    @Override
    public long getItemId(int i) {
        // TODO Auto-generated method stub
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // TODO Auto-generated method stub
        //viewGroup是列表项View的父视图，调整列表项的高度
        //view表示一个视图
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.sourceitem, null);
            viewHolder.Number = (TextView) view.findViewById(R.id.Number);
            viewHolder.Equipment = (TextView) view.findViewById(R.id.Equipment);
            viewHolder.Detail = (TextView) view.findViewById(R.id.Detail);

            // 将holder绑定到view
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        // 向ViewHolder中填入的数据
        viewHolder.Equipment.setText(list.get(i).getEquipment());
        viewHolder.Number.setText(list.get(i).getNumber());
        viewHolder.Detail.setText(list.get(i).getDetail());

        return view;
    }

    /**
     * ViewHolder类用以储存item中控件的引用
     */
    final class ViewHolder {
        public TextView Number;
        public TextView Equipment;
        public TextView Detail;
    }

    public static class MainActivity extends Activity {

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

            /*注释部分为SimpleAdepter*/
            /*final List<Map<String, String>> data = new ArrayList<>();
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
            final SimpleAdapter simpleadapter = new SimpleAdapter(this, data, R.layout.mainitem,
                    new String[]{"First_letter", "Name"},
                    new int[]{R.id.First_letter,R.id.Name});
            listview.setAdapter(simpleadapter);*/

            final List<dataclass> data = new ArrayList<>();
            for (int i = 0; i<10; ++i){
                dataclass temp = new dataclass(First_letter[i], Name[i], Number[i], Equipment[i], Detail[i], Color[i]);
                data.add(temp);
            }
            ListView listview = (ListView)findViewById(R.id.list);
            final MyMainAdepter myMainAdepter = new MyMainAdepter(this, data);
            listview.setAdapter(myMainAdepter);

            /*至此MainActivity的ListView显示完毕*/

            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                /*注释部分为SimpleAdepter*/
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // When clicked, show a toast with the TextView text
                    Intent intent = new Intent(MainActivity.this, dataActivity.class);
                    intent.putExtra("Name", data.get(position).getName());
                    intent.putExtra("Number", data.get(position).getNumber());
                    intent.putExtra("Equipment", data.get(position).getEquipment());
                    intent.putExtra("Detail", data.get(position).getDetail());
                    intent.putExtra("Color", data.get(position).getColor());
                    startActivity(intent);
                }
            });
            listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    String name = new String();
                    name = data.get(position).getName();
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("确定删除联系人"+name);
                    builder.setTitle("删除联系人");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            data.remove(position);
                            myMainAdepter.notifyDataSetChanged();
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
}
