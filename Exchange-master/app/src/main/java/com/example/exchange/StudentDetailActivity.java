package com.example.exchange;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

public class StudentDetailActivity extends AppCompatActivity {
    List<Map<String,Object>> data;
    Map<String,Object> stu_info;
    ExchangeDatabase db;
    ListView courseList;
    private CourseAdapter courseAdapter;
    String myusername,username,type=null;
    boolean isFriend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_detail);
        Bundle bundle=this.getIntent().getExtras();
        myusername=bundle.getString("myusername");
        username=bundle.getString("username");
        type=bundle.getString("type");
        TextView title=(TextView)findViewById(R.id.title);
        TextView content=(TextView)findViewById(R.id.content);
        if(type!=null&&type.equals("post")){
            title.setText(bundle.getString("post_title"));
            String contentText="    "+bundle.getString("post_info");
            content.setText(contentText);
            title.setVisibility(View.VISIBLE);
            content.setVisibility(View.VISIBLE);
        }
        else{
            title.setVisibility(View.GONE);
            content.setVisibility(View.GONE);
        }
        db=new ExchangeDatabase();
        db.connect();
        data=db.selectSimplePublicCourses(username);
        //Log.d("course data",""+data.size());
        //Log.d("course_name",""+data.get(0).get("course_name"));
        //Log.d("course_select",""+data.get(0).get("course_select"));
        //Log.d("course_public",""+data.get(0).get("course_public"));
        //Log.d("course_class_id",""+data.get(0).get("course_class_id"));

        courseList=(ListView)findViewById(R.id.course_list);
        courseAdapter=new CourseAdapter(this,data);
        courseList.setAdapter(courseAdapter);
        stu_info=db.selectStu(username);
        TextView stu_id=(TextView)findViewById(R.id.id);
        TextView stu_name=(TextView)findViewById(R.id.name);
        stu_id.setText(username);
        stu_name.setText((String)stu_info.get("stu_name"));
        isFriend=db.isFriend(myusername,username);
        Button button=(Button)findViewById(R.id.button);
        if(username.equals(myusername))
            button.setVisibility(View.GONE);
        if(isFriend){
            button.setText("发送消息");
        }else {
            button.setText("加为好友");
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFriend){
                    Bundle bundle=new Bundle();
                    bundle.putString("username",username);
                    bundle.putString("myusername",myusername);
                    Intent intent=new Intent();
                    intent.setClass(StudentDetailActivity.this,ChatActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else{
                    LayoutInflater factory=LayoutInflater.from(StudentDetailActivity.this);
                    View view2=factory.inflate(R.layout.new_friend_request,null);
                    AlertDialog.Builder builder=new AlertDialog.Builder(StudentDetailActivity.this);
                    final EditText editContent=(EditText)view2.findViewById(R.id.edit_content);
                    builder.setView(view2);
                    builder.setPositiveButton("发送", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(db.sendFriendRequest(myusername,username,editContent.getText().toString()))
                                Toast.makeText(StudentDetailActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                            dialogInterface.dismiss();
                        }
                    });
                    builder.setNegativeButton("放弃", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();
                }
            }
        });
        courseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String mode="quick";
                Bundle bundle=new Bundle();
                bundle.putString("mode",mode);
                bundle.putString("username",myusername);
                bundle.putString("course_class_id",(String)data.get(i).get("course_class_id"));
                Intent intent=new Intent();
                intent.setClass(StudentDetailActivity.this,CourseDetailActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

}
