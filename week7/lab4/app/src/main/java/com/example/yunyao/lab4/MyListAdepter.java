package com.example.yunyao.lab4;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
/**
 * Created by yunyao on 2016/10/18.
 */


public class MyListAdepter extends BaseAdapter {
    private Context context;
    private int itemindex;
    private List<FruitSource> list;
    public MyListAdepter(Context context, List<FruitSource> list){
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
            view = LayoutInflater.from(context).inflate(R.layout.activity_item, null);
            viewHolder.FruitPic = (ImageView) view.findViewById(R.id.FruitPic);
            viewHolder.FruitName = (TextView) view.findViewById(R.id.FruitName);

            // 将holder绑定到view
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        // 向ViewHolder中填入的数据
        viewHolder.FruitPic.setImageResource( list.get(i).getFruitSrc());
        viewHolder.FruitName.setText(list.get(i).getFruitName());

        return view;
    }

    /**
     * ViewHolder类用以储存item中控件的引用
     */
    final class ViewHolder {
        public ImageView FruitPic;
        public TextView FruitName;
    }
}
