package com.example.yunyao.lab9;
import java.util.ArrayList;
import java.util.List;
import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * Created by yunyao on 2016/11/30.
 */
public class RecycleAdapter  extends RecyclerView.Adapter<RecycleAdapter.ViewHolder>{
    private ArrayList<RecyclerWeather>  weather_list;
    private LayoutInflater  mInflater;
    public interface OnItemClickLitener {
        void onItemClick(View view,  int position,RecyclerWeather item);
    }
    private OnItemClickLitener  mOnItemClickLitener;
    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this. mOnItemClickLitener = mOnItemClickLitener;
    }
    public RecycleAdapter(Context context, ArrayList<RecyclerWeather> items) {
        super();
        weather_list = items;
        mInflater = LayoutInflater.from(context);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup,  int i) {
        View view =  mInflater.inflate(R.layout.recycler_item, viewGroup,  false);
        ViewHolder holder =  new ViewHolder(view);
        holder.Date_Text = (TextView)view.findViewById(R.id.date_Text);
        holder.Tem_text =(TextView)view.findViewById(R.id.Tem_Text);
        holder.Weather_Text = (TextView)view.findViewById(R.id.Weather_Text);
        return holder;
    }
    @Override
    public void onBindViewHolder( final ViewHolder viewHolder, final int i) {
        viewHolder. Date_Text.setText( weather_list.get(i).getDate());
        viewHolder. Tem_text.setText( weather_list.get(i).getTem());
        viewHolder. Weather_Text.setText(weather_list.get(i).getWeather());
        if ( mOnItemClickLitener !=  null)
        {
            viewHolder. itemView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickLitener.onItemClick(viewHolder.itemView, i, weather_list.get(i));
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return  weather_list.size();
    }
    public static class ViewHolder  extends RecyclerView.ViewHolder{
        public ViewHolder(View itemView) {
            super(itemView);
        }
        TextView  Date_Text;
        TextView  Tem_text;
        TextView  Weather_Text;
    }
}