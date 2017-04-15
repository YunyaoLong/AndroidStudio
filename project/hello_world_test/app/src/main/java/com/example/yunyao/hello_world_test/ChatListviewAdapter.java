package com.example.yunyao.hello_world_test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yunyao on 2017/4/15.
 */

public class ChatListviewAdapter extends BaseAdapter {
    private Context context;
    private List<MsgInfo> datas = new ArrayList<>();

    private ViewHolder viewHolder;

    public void addDataToAdapter(MsgInfo e) {
        datas.add(e);
    }

    public ChatListviewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.chat_listview_item, null);
            viewHolder = new ViewHolder(convertView);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String left = datas.get(position).getLeft_text();
        String right = datas.get(position).getRight_text();

        if (left == null) {
            viewHolder.text_right.setText(right);
            viewHolder.right.setVisibility(View.VISIBLE);
            viewHolder.left.setVisibility(View.INVISIBLE);
        }

        if (right == null) {
            viewHolder.text_left.setText(left);
            viewHolder.left.setVisibility(View.VISIBLE);
            viewHolder.right.setVisibility(View.INVISIBLE);
        }

        return convertView;

    }

    public static class ViewHolder {
        public View rootView;
        public TextView text_left;
        public LinearLayout left;
        public TextView text_right;
        public LinearLayout right;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.text_left = (TextView) rootView.findViewById(R.id.text_left);
            this.left = (LinearLayout) rootView.findViewById(R.id.left);
            this.text_right = (TextView) rootView.findViewById(R.id.text_right);
            this.right = (LinearLayout) rootView.findViewById(R.id.right);
        }

    }
}
