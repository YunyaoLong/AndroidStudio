package com.example.exchange;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class PostAdapter extends BaseAdapter {
    private Context context;
    private List<Map<String,Object>> list;
    private LayoutInflater listContainer;
    public PostAdapter(Context context, List<Map<String,Object>> list){
        this.context=context;
        listContainer= LayoutInflater.from(context);
        this.list=list;
    }
    @Override
    public int getCount(){
        if(list==null) return 0;
        return list.size();
    }
    @Override
    public Object getItem(int i){
        if(list==null) return 0;
        return list.get(i);
    }
    @Override
    public long getItemId(int i){
        return i;
    }
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup){
        ViewHolder viewHolder;
        if(convertView==null){
            convertView= listContainer.inflate(R.layout.post_list_item,null);
            viewHolder=new ViewHolder();
            viewHolder.title=(TextView)convertView.findViewById(R.id.title);
            viewHolder.content=(TextView)convertView.findViewById(R.id.content);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)convertView.getTag();
        }
        viewHolder.title.setText((String)list.get(i).get("post_title"));
        viewHolder.content.setText((String)list.get(i).get("post_info"));
        return convertView;
    }
    private class ViewHolder{
        public TextView title;
        public TextView content;
    }
}
