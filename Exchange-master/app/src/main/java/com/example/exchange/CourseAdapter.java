package com.example.exchange;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class CourseAdapter extends BaseAdapter {
    private Context context;
    private List<Map<String,Object>> list;
    private LayoutInflater listContainer;
    public CourseAdapter(Context context, List<Map<String,Object>> list){
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
            convertView= listContainer.inflate(R.layout.course_list_item,null);
            viewHolder=new ViewHolder();
            viewHolder.courseName=(TextView)convertView.findViewById(R.id.course_name);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)convertView.getTag();
        }
        Log.d("adapter course data","+"+list.size());
        Log.d("adapter course_name","+"+list.get(i).get("course_name"));
        Log.d("adapter course_select","+"+list.get(i).get("course_select"));
        Log.d("adapter course_public","+"+list.get(i).get("course_public"));
        Log.d("adapter course_class_id","+"+list.get(i).get("course_class_id"));
        if(list.get(i).get("course_class_id")==null){
            LinearLayout layout=(LinearLayout)convertView.findViewById(R.id.layout);
            layout.setVisibility(View.GONE);
            return convertView;
        }

        viewHolder.courseName.setText((String)list.get(i).get("course_name"));
        String ispublic=(String)list.get(i).get("course_public");
        String select=(String)list.get(i).get("course_select");
        Log.d("size"+list.size(),""+i);
        if(select!=null)
        if(select.equals("选课成功")){
            viewHolder.courseName.setBackground(convertView.getResources().getDrawable(R.drawable.course_list_item_background_blue));
        }
        else{
            if (ispublic.equals("private")){
                viewHolder.courseName.setBackground(convertView.getResources().getDrawable(R.drawable.course_list_item_background_red));
            }else{
                viewHolder.courseName.setBackground(convertView.getResources().getDrawable(R.drawable.course_list_item_background_green));
            }
        }
        return convertView;
    }
    private class ViewHolder{
        public TextView courseName;
    }

}
