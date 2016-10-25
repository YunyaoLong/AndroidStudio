package com.example.yunyao.lab3;

/**
 * Created by yunyao on 2016/10/12.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class MyMainAdepter extends BaseAdapter {

    private Context context;
    private int itemindex;
    private List<dataclass> list;
    public MyMainAdepter(Context context, List<dataclass> list){
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
            view = LayoutInflater.from(context).inflate(R.layout.mainitem, null);
            viewHolder.First_Letter = (Button) view.findViewById(R.id.First_letter);
            viewHolder.Name = (TextView) view.findViewById(R.id.Name);

            // 将holder绑定到view
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        // 向ViewHolder中填入的数据
        viewHolder.First_Letter.setText(list.get(i).getFirst_letter());
        viewHolder.Name.setText(list.get(i).getName());

        return view;
    }

    /**
     * ViewHolder类用以储存item中控件的引用
     */
    final class ViewHolder {
        public Button First_Letter;
        public TextView Name;
    }
}
