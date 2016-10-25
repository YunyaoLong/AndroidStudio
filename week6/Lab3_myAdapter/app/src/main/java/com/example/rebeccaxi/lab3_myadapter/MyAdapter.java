package com.example.rebeccaxi.lab3_myadapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RebeccaXi on 2016/10/12.
 */
public class MyAdapter extends ArrayAdapter<DetailActivity.Contacts> {
    private int resourceId;
    public MyAdapter(Context context, int resource, List<DetailActivity.Contacts> objects) {
        super(context, resource, objects);
        resourceId=resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DetailActivity.Contacts user=getItem(position);
        View view;
        ViewHolder viewHolder;
        //没有加载过布局的情况下，重新加载一遍布局
        if (convertView==null){
            //加载传递过来的布局
            view= LayoutInflater.from(getContext()).inflate(resourceId,null);
            viewHolder=new ViewHolder();
            //将布局中的两个控件存入到定义好的ViewHolder类中
            viewHolder.text_Image=(TextView)view.findViewById(R.id.textview_circle_initial);
            viewHolder.text_name=(TextView)view.findViewById(R.id.textview_contact_name);
            //将viewHolder存入到view中
            view.setTag(viewHolder);
        }
        //加载过布局后，直接调用保存好了的布局
        else {
            //获取之前加载过的布局文件
            view=convertView;
            viewHolder=(ViewHolder)view.getTag();
        }
        viewHolder.text_name.setText(user.getName());
        viewHolder.text_Image.setText(user.getFirstLetter());
        return view;
    }
    //定义一个新的类存取两个控件内容
    class ViewHolder{
        TextView text_Image;
        TextView text_name;
    }

    /**
     * Created by RebeccaXi on 2016/10/9.
     */
    public static class MainActivity extends AppCompatActivity {

        private String[] FirstLetter = getResources().getStringArray(R.array.first);
        private String[] contactListStringArray = getResources().getStringArray(R.array.contact_name_List);
        private String [] colorIds = getResources().getStringArray(R.array.Back_Color);
        private String [] phoneNumber = getResources().getStringArray(R.array.phoneNumber);
        private String [] address = getResources().getStringArray(R.array.address);

        private List<DetailActivity.Contacts> contactsList = new ArrayList<>();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            //初始化User数据
            initUsers();
            //定义自定义适配器的内容
            final MyAdapter adapter=new MyAdapter(MainActivity.this,R.layout.contact_item,contactsList);
            final ListView contactListView=(ListView)findViewById(R.id.contacts_list);
            //将自定义适配器与listView绑定
            contactListView.setAdapter(adapter);


            //单击事件
            contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?>parrent, View view,int position,long id){


                    DetailActivity.Contacts user=contactsList.get(position);
                    //实现跳转
                    Intent intent=new Intent(MainActivity.this,DetailActivity.class);
                    intent.putExtra("name",user.getName());
                    intent.putExtra("number",user.getPhoneNumber());
                    intent.putExtra("address",user.getAddress());
                    intent.putExtra("color",user.getBackground());
                    startActivity(intent);

                }
            });
            //长按事件
            contactListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    DetailActivity.Contacts user=contactsList.get(position);
                    String del_name=user.getName();
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("删除联系人");
                    builder.setMessage("你确定要删除联系人" + del_name + "吗？");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //删除数据，并更新
                            contactsList.remove(position);

                            adapter.notifyDataSetChanged();//更新列表
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();

                    return  true;
                }
            });
        }
        public void initUsers(){
            for (int i=0;i<contactListStringArray.length;i++){
                DetailActivity.Contacts user=new DetailActivity.Contacts(FirstLetter[i],contactListStringArray[i],address[i],phoneNumber[i],colorIds[i]);
                contactsList.add(user);
            }
        }
    }
}
