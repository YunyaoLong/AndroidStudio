package com.example.yunyao.lab8;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by yunyao on 2016/11/20.
 */
public class MyAdapter extends BaseAdapter{

    private List<Content> mData;//定义数据。
    private Context context;
    private LayoutInflater mInflater;//定义Inflater,加载我们自定义的布局。

    /**
    定义构造器，在Activity创建对象Adapter的时候将数据data和Inflater传入自定义的Adapter中进行处理。
    */
    private int resourceId;
    public MyAdapter(Context context, int resource, List<Content> mData) {
        this.context = context;
        this.mData = mData;
        resourceId = resource;
    }

    public int getCount(){
        return mData == null ? 0 : mData.size();
    }

    public Object getItem(int i){
        return mData == null ? null : mData.get(i);
    }

    public long getItemId(int i){
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Content content = mData.get(position);
        View view;
        ViewHolder viewHolder;
        //没有加载过布局的情况下，重新加载一遍布局
        if (convertView==null){
            //加载传递过来的布局
            view= LayoutInflater.from(context).inflate(resourceId,null);
            viewHolder=new ViewHolder();
            //将布局中的两个控件存入到定义好的ViewHolder类中
            viewHolder.InfoListViewNameText=(TextView)view.findViewById(R.id.InfoListViewNameText);
            viewHolder.InfoListViewBirthdayText=(TextView)view.findViewById(R.id.InfoListViewBirthdayText);
            viewHolder.InfoListViewGiftText=(TextView)view.findViewById(R.id.InfoListViewGiftText);
            //将viewHolder存入到view中
            view.setTag(viewHolder);
        }
        //加载过布局后，直接调用保存好了的布局
        else {
            //获取之前加载过的布局文件
            view=convertView;
            viewHolder=(ViewHolder)view.getTag();
        }
        viewHolder.InfoListViewNameText.setText(content.getInfoObjectNameText());
        viewHolder.InfoListViewBirthdayText.setText(content.getInfoObjectBirthdayText());
        viewHolder.InfoListViewGiftText.setText(content.getInfoObjectGiftText());
        return view;
    }

    //定义一个新的类存取两个控件内容
    class ViewHolder{
        TextView InfoListViewNameText;
        TextView InfoListViewBirthdayText;
        TextView InfoListViewGiftText;
    }
}
