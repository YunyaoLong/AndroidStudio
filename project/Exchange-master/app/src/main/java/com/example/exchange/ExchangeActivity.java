package com.example.exchange;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExchangeActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mycourse_img,platform_img,chat_img,exchange_img,contacts_img,request_img;
    private LinearLayout course_layout,platform_layout,chat_layout,exchange_layout,contacts_layout,request_layout,current_layout;
    boolean layout_flag=true;
    private ListView course_listView,post_listView,request_listView,contact_listView,chat_listView;
    private String cookie,sid;
    private String mode="";
    private Button new_post,next_page,last_page;
    private static final int COURSE_RENEW_CODE=10001,POST_RENEW_CODE=10002,REQUEST_RENEW_CODE=10003,CONTACT_RENEW_CODE=10004,CHAT_RENEW_CODE=10005;

    private List<Map<String,Object>> courseList;
    private List<Map<String,Object>> postList;
    private List<Map<String,Object>> requestList;
    private List<Map<String,Object>> contactList;
    private List<Map<String,Object>> chatList;

    private CourseAdapter courseAdapter;
    private PostAdapter postAdapter;

    private String courseAll;
    private String coursePage;
    private boolean lock=false;

    private String current_xnd="2016-2017";
    private String current_xq="1";

    private ExchangeDatabase database;

    private String username="",password="";

    private int semaphore;

    private int current_page=0;
    private int num_in_each_page=4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);
        findView();
        init();
        bindButton();
        if(!mode.equals("quick"))getCourseAll();
        database.connect();
        if(!mode.equals("quick"))renewUser();
        if(!mode.equals("quick"))renewCourses();
        course_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle=new Bundle();
                bundle.putString("mode",mode);
                if(!mode.equals("quick"))bundle.putString("cookie",cookie);
                if(!mode.equals("quick"))bundle.putString("sid",sid);
                bundle.putString("username",username);
                bundle.putString("course_class_id",(String)courseList.get(i).get("course_class_id"));
                Intent intent=new Intent();
                intent.setClass(ExchangeActivity.this,CourseDetailActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        post_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle=new Bundle();
                bundle.putString("myusername",username);
                bundle.putString("type","post");
                bundle.putString("username",(String)postList.get(i).get("post_sender"));
                bundle.putString("post_id",(String)postList.get(i).get("post_id"));
                bundle.putString("post_info",(String)postList.get(i).get("post_info"));
                bundle.putString("post_title",(String)postList.get(i).get("post_title"));
                Intent intent=new Intent();
                intent.setClass(ExchangeActivity.this,StudentDetailActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        request_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int index=i;
                LayoutInflater factory=LayoutInflater.from(ExchangeActivity.this);
                AlertDialog.Builder builder=new AlertDialog.Builder(ExchangeActivity.this);
                builder.setTitle((String)requestList.get(i).get("req_type"));
                builder.setPositiveButton("同意", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(requestList.get(index).get("req_type").equals("好友请求"))
                            database.becomeFriend(username,(String)requestList.get(index).get("req_sender"));
                        database.deleteRequest((String)requestList.get(index).get("req_id"));
                        dialogInterface.dismiss();
                        mHandler.sendEmptyMessage(POST_RENEW_CODE);
                    }
                });
                builder.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        database.deleteRequest((String)requestList.get(index).get("req_id"));
                        dialogInterface.dismiss();
                        mHandler.sendEmptyMessage(POST_RENEW_CODE);
                    }
                });
                builder.show();
            }
        });
        contact_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle=new Bundle();
                bundle.putString("myusername",username);
                bundle.putString("type","student");
                bundle.putString("username",(String)contactList.get(i).get("user_name"));
                Intent intent=new Intent();
                intent.setClass(ExchangeActivity.this,StudentDetailActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        chat_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle=new Bundle();
                bundle.putString("username",(String)chatList.get(i).get("info_sender"));
                bundle.putString("myusername",username);
                Intent intent=new Intent();
                intent.setClass(ExchangeActivity.this,ChatActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(current_layout==course_layout){
            mHandler.sendEmptyMessage(COURSE_RENEW_CODE);
        }else if(current_layout==platform_layout){
            mHandler.sendEmptyMessage(POST_RENEW_CODE);
        }else if(current_layout==request_layout){
            mHandler.sendEmptyMessage(REQUEST_RENEW_CODE);
        }else if(current_layout==chat_layout){
            mHandler.sendEmptyMessage(CHAT_RENEW_CODE);
        }
    }

    private void init(){
        Bundle bundle=this.getIntent().getExtras();
        mode=bundle.getString("mode");
        if(!mode.equals("quick"))cookie=bundle.getString("cookie");
        if(!mode.equals("quick"))sid=bundle.getString("sid");
        username=bundle.getString("username");
        password=bundle.getString("password");
        database=new ExchangeDatabase();
        if(mode.equals("quick"))
            exchange_img.setVisibility(View.GONE);
        current_layout=course_layout;
    }

    private void findView(){
        mycourse_img=(ImageView)findViewById(R.id.mycourse_img);
        platform_img=(ImageView)findViewById(R.id.platform_img);
        chat_img=(ImageView)findViewById(R.id.chat_img);
        exchange_img=(ImageView)findViewById(R.id.exchange_img);
        contacts_img=(ImageView)findViewById(R.id.contacts_img);
        request_img=(ImageView)findViewById(R.id.request_img);

        course_listView=(ListView)findViewById(R.id.course_list);
        post_listView=(ListView)findViewById(R.id.post_list);
        request_listView=(ListView)findViewById(R.id.request_list);
        contact_listView=(ListView)findViewById(R.id.contact_list);
        chat_listView=(ListView)findViewById(R.id.chat_list);


        course_layout=(LinearLayout)findViewById(R.id.course_layout);
        platform_layout=(LinearLayout)findViewById(R.id.platform_layout);
        chat_layout=(LinearLayout)findViewById(R.id.chat_layout);
        exchange_layout=(LinearLayout)findViewById(R.id.exchange_layout);
        contacts_layout=(LinearLayout)findViewById(R.id.contacts_layout);
        request_layout=(LinearLayout)findViewById(R.id.request_layout);

        new_post=(Button)findViewById(R.id.new_post);
        next_page=(Button)findViewById(R.id.next_page);
        last_page=(Button)findViewById(R.id.last_page);
    }

    private void bindButton(){
        mycourse_img.setOnClickListener(this);
        platform_img.setOnClickListener(this);
        chat_img.setOnClickListener(this);
        exchange_img.setOnClickListener(this);
        contacts_img.setOnClickListener(this);
        request_img.setOnClickListener(this);

        new_post.setOnClickListener(this);
        next_page.setOnClickListener(this);
        last_page.setOnClickListener(this);
    }


    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.mycourse_img:
                current_layout=course_layout;
                layout_flag=false;
                mHandler.sendEmptyMessage(COURSE_RENEW_CODE);
            case R.id.platform_img:
                if(layout_flag){
                    current_layout=platform_layout;
                    layout_flag=false;
                    mHandler.sendEmptyMessage(POST_RENEW_CODE);
                }
            case R.id.chat_img:
                if(layout_flag){
                    current_layout=chat_layout;
                    layout_flag=false;
                    mHandler.sendEmptyMessage(CHAT_RENEW_CODE);
                }
            case R.id.exchange_img:
                if(layout_flag){
                    current_layout=exchange_layout;
                    layout_flag=false;
                }
            case R.id.contacts_img:
                if(layout_flag){
                    current_layout=contacts_layout;
                    layout_flag=false;
                    mHandler.sendEmptyMessage(CONTACT_RENEW_CODE);
                }
            case R.id.request_img:
                if(layout_flag){
                    current_layout=request_layout;
                    layout_flag=false;
                    mHandler.sendEmptyMessage(REQUEST_RENEW_CODE);
                }
                course_layout.setVisibility(View.GONE);
                platform_layout.setVisibility(View.GONE);
                chat_layout.setVisibility(View.GONE);
                exchange_layout.setVisibility(View.GONE);
                contacts_layout.setVisibility(View.GONE);
                request_layout.setVisibility(View.GONE);
                current_layout.setVisibility(View.VISIBLE);
                Drawable drawable=getResources().getDrawable(R.color.colorPrimaryGreen);
                mycourse_img.setBackground(drawable);
                platform_img.setBackground(drawable);
                chat_img.setBackground(drawable);
                exchange_img.setBackground(drawable);
                contacts_img.setBackground(drawable);
                request_img.setBackground(drawable);
                v.setBackground(getResources().getDrawable(R.color.darkGreen));
                layout_flag=true;
                break;
            case R.id.new_post:
                LayoutInflater factory=LayoutInflater.from(ExchangeActivity.this);
                View view2=factory.inflate(R.layout.new_post,null);
                AlertDialog.Builder builder=new AlertDialog.Builder(ExchangeActivity.this);
                final EditText editTitle=(EditText)view2.findViewById(R.id.edit_title);
                final EditText editContent=(EditText)view2.findViewById(R.id.edit_content);
                builder.setView(view2);
                builder.setPositiveButton("发布", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        database.insertPost(editTitle.getText().toString()
                                ,editContent.getText().toString(),username);
                        renewPostList(current_page,num_in_each_page);
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
                break;
            case R.id.next_page:
                current_page++;
                renewPostList(current_page,num_in_each_page);
                if(current_page!=0)last_page.setVisibility(View.VISIBLE);
                break;
            case R.id.last_page:
                current_page--;
                if(current_page<0)current_page=0;
                renewPostList(current_page,num_in_each_page);
                if(current_page==0)last_page.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }

    }

    public void renewPostList(int pgno,int pgcnt){
        postList=database.selectPost(pgno,pgcnt);
        postAdapter=new PostAdapter(this,postList);
        post_listView.setAdapter(postAdapter);
    }

    public void getCourseAll(){
        lock=true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url="http://uems.sysu.edu.cn/elect/s/courseAll?xnd="
                            +current_xnd+"&xq="+current_xq+"&sid="+sid;
                    HttpURLConnection connection;
                    connection=(HttpURLConnection)(new URL(url)).openConnection();
                    connection.addRequestProperty("Cookie",cookie);
                    StringBuilder sb = new StringBuilder("");
                    int c1;
                    InputStreamReader in =new InputStreamReader(connection.getInputStream());
                    while ((c1 = in.read()) != -1) {
                        if(c1!='\t'&&c1!='\n'&&c1!='\r')       //去除空格
                            sb.append((char) c1);
                    }
                    courseAll=sb.toString();
                }catch (Exception e){
                    e.printStackTrace();
                }
                lock=false;
            }
        }).start();
        while (lock);
    }

    public void renewUser(){
        String pattern3="(?<=<td class='val'>)[^<]*(?=</td>)";
        Pattern p3= Pattern.compile(pattern3);
        Matcher m3=p3.matcher(courseAll);
        int i=0;
        String name="",id="";
        while(m3.find()){
            if(i==0)
                name=m3.group();
            if(i==1)
                id=m3.group();
            //System.out.println(m3.group());
            i++;
        }
        database.renewUsers(name,id,username,password);
    }

    public void renewCourses(){
        String pattern1="(?<=<td class='c'>)[^<]*(?=</td>)";
        String pattern2="(?<=\" >).*?(?=</tr>)";
        Pattern p=Pattern.compile(pattern2);
        int i=0;
        Matcher m=p.matcher(courseAll);
        semaphore=0;
        while(m.find()){
            String str=m.group();
            if(i!=0){
                String course_class_id="";
                String course_select="";
                int j=0;
                Pattern p2=Pattern.compile(pattern1);
                Matcher m2=p2.matcher(str);
                while(m2.find()){
                    if(j==8)
                        course_select=m2.group();
                    if(j==11)
                        course_class_id=m2.group();
                    j++;
                }
                renewSC(course_class_id,course_select);
            }
            i++;
        }
        while (semaphore!=0);
    }

    public void renewSC(final String course_class_id,final String course_select){
        semaphore++;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(database.findCourse(course_class_id,username)){
                    semaphore--;
                    return;
                }
                try {
                    String url="http://uems.sysu.edu.cn/elect/s/courseDet?jxbh="+course_class_id
                            +"&xnd="+current_xnd+"&xq="+current_xq+"&sid="+sid;
                    HttpURLConnection connection;
                    connection=(HttpURLConnection)(new URL(url)).openConnection();
                    connection.addRequestProperty("Cookie",cookie);
                    StringBuilder sb = new StringBuilder("");
                    int c1;
                    InputStreamReader in =new InputStreamReader(connection.getInputStream());
                    while ((c1 = in.read()) != -1) {
                        if(c1!='\t'&&c1!='\n'&&c1!='\r')       //去除空格
                            sb.append((char) c1);
                    }
                    coursePage=sb.toString();
                }catch (Exception e){
                    e.printStackTrace();
                }
                String pattern4="(?<=<td class='val'( colspan='3')?>)[^<]*(?=</td>)";
                Pattern p4=Pattern.compile(pattern4);
                Matcher m4=p4.matcher(coursePage);
                String course_year="",course_semester="",course_id="";
                String course_name="",course_type="",course_department="",course_zone="";
                String course_teacher="",course_credit="",course_remain="",course_to_filter="";
                String course_time_and_place="",course_require="";

                String course_class_name="",course_english_name="",course_capacity="",course_hour="";
                String course_chosen="",course_real_chosen="",course_exam_type="",course_filter_type="";
                String course_intr="",course_comments="";
                int k=0;
                while(m4.find()){
                    if(k==0) course_year=m4.group();
                    else if(k==1) course_semester=m4.group();
                    else if(k==2) course_id=m4.group();
                    else if(k==3) course_name=m4.group();
                    else if(k==5) course_class_name=m4.group();
                    else if(k==6) course_type=m4.group();
                    else if(k==7) course_english_name=m4.group();
                    else if(k==8) course_department=m4.group();
                    else if(k==9) course_zone=m4.group();
                    else if(k==10) course_teacher=m4.group();
                    else if(k==11) course_credit=m4.group();
                    else if(k==12) course_capacity=m4.group();
                    else if(k==13) course_hour=m4.group();
                    else if(k==14) course_chosen=m4.group();
                    else if(k==15) course_real_chosen=m4.group();
                    else if(k==16) course_remain=m4.group();
                    else if(k==17) course_to_filter=m4.group();
                    else if(k==18) course_exam_type=m4.group();
                    else if(k==19) course_filter_type=m4.group();
                    else if(k==20) course_time_and_place=m4.group();
                    else if(k==21) course_require=m4.group();
                    else if(k==22) course_intr=m4.group();
                    else if(k==23) course_comments=m4.group();
                    k++;
                }
                database.renewCourse(username,course_class_id,course_year,course_semester
                        ,course_id,course_name,course_type,course_department,course_zone,course_teacher
                        ,course_credit,course_remain,course_to_filter,course_time_and_place,course_require
                        , course_class_name,course_english_name,course_capacity,course_hour,course_chosen
                        ,course_real_chosen,course_exam_type,course_filter_type,course_intr,course_comments,course_select);
                semaphore--;
            }
        }).start();
    }


    public void getCoursePage(final String course_class_id){
        lock=true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url="http://uems.sysu.edu.cn/elect/s/courseDet?jxbh="+course_class_id
                            +"&xnd="+current_xnd+"&xq="+current_xq+"&sid="+sid;
                    HttpURLConnection connection;
                    connection=(HttpURLConnection)(new URL(url)).openConnection();
                    connection.addRequestProperty("Cookie",cookie);
                    StringBuilder sb = new StringBuilder("");
                    int c1;
                    InputStreamReader in =new InputStreamReader(connection.getInputStream());
                    while ((c1 = in.read()) != -1) {
                        if(c1!='\t'&&c1!='\n'&&c1!='\r')       //去除空格
                            sb.append((char) c1);
                    }
                    coursePage=sb.toString();
                }catch (Exception e){
                    e.printStackTrace();
                }
                lock=false;
            }
        }).start();
        while (lock);
    }

    Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            Context context=ExchangeActivity.this.getApplicationContext();
            switch (msg.what){
                case COURSE_RENEW_CODE:
                    courseList=database.selectSimpleCourses(username);
                    courseAdapter=new CourseAdapter(context,courseList);
                    course_listView.setAdapter(courseAdapter);
                    break;
                case POST_RENEW_CODE:
                    renewPostList(current_page,num_in_each_page);
                    break;
                case REQUEST_RENEW_CODE:
                    requestList=database.selectRequest(username);
                    SimpleAdapter simpleAdapter=new SimpleAdapter(ExchangeActivity.this,requestList,R.layout.request_list_item,
                            new String[]{"stu_name","req_type","req_info","req_date","req_time"},new int[]{R.id.name,R.id.title,R.id.content,R.id.date,R.id.time});
                    request_listView.setAdapter(simpleAdapter);
                    break;
                case CONTACT_RENEW_CODE:
                    contactList=database.selectContacts(username);
                    Log.d("size",""+contactList.size());
                    SimpleAdapter simpleAdapter2=new SimpleAdapter(ExchangeActivity.this,contactList,R.layout.contact_list_item,
                            new String[]{"stu_name"},new int[]{R.id.name});
                    contact_listView.setAdapter(simpleAdapter2);
                    break;
                case CHAT_RENEW_CODE:
                    chatList=database.selectMessageNum(username);
                    SimpleAdapter simpleAdapter3=new SimpleAdapter(ExchangeActivity.this,chatList,R.layout.chat_list_item,
                            new String[]{"stu_name","info_num"},new int[]{R.id.name,R.id.num});
                    chat_listView.setAdapter(simpleAdapter3);
                    break;

                default:
                    break;
            }
        }
    };


}
