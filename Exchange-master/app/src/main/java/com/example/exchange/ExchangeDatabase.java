package com.example.exchange;

import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExchangeDatabase {
    private Connection conn;
    private boolean lock=false;
    private boolean is_success=false;
    private List<Map<String,Object>> data;
    Map<String,Object>tmp;


    public ExchangeDatabase(){

    }

    public boolean connect() {
        lock=true;
        is_success=false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String connectString = "jdbc:mysql://172.18.59.33:3306/webdb"
                        + "?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&&useSSL=false";
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    conn = DriverManager.getConnection(connectString, "wt", "1234");
                    is_success=true;
                    Log.d("connect","successfully");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                lock=false;
            }
        }).start();
        while (lock);
        return is_success;
    }

    public boolean validatePassword(final String username, final String password){
        lock=true;
        is_success=false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String selectPwd="select user_pwd from user_stu where user_name='"+username+"';";
                Log.d("sql",selectPwd);
                try {
                    Statement stat=conn.createStatement();
                    ResultSet rs1=stat.executeQuery(selectPwd);
                    String rpassword="";
                    while (rs1.next()){
                        rpassword=rs1.getString("user_pwd");
                    }
                    if(password.equals(rpassword))
                        is_success=true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                lock=false;
            }
        }).start();
        while (lock);
        return is_success;
    }

    public boolean renewUsers(final String stu_name, final String stu_id, //若不存在则插入，若存在则更新
                              final String user_name, final String user_pwd){
        lock=true;
        is_success=false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String selectSQL="select * from user_stu where user_name='"+user_name+"';";
                String insertSQL="insert into user_stu values('"+stu_name+"','"+stu_id+"','"
                        +user_name+"','"+user_pwd+"'); ";
                Log.i("sql",insertSQL);
                try {
                    Statement stat=conn.createStatement();
                    ResultSet rs=stat.executeQuery(selectSQL);
                    if(!rs.next()){
                        stat.executeUpdate(insertSQL);
                    }
                    is_success=true;
                    Log.d("in","successfully");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                lock=false;
            }
        }).start();
        while (lock);
        return is_success;
    }

    public boolean renewCourse(final String user_name,          //若不存在则插入，若存在则更新
                                final String course_class_id, final String course_year,
                                final String course_semester, final String course_id,
                                final String course_name,final String course_type,
                                final String course_department,final String course_zone,
                                final String course_teacher,final String course_credit,
                                final String course_remain,final String course_to_filter,
                                final String course_time_and_place,final String course_require,

                                final String course_class_name,final String course_english_name,
                                final String course_capacity,final String course_hour,
                                final String course_chosen,final String course_real_chosen,
                                final String course_exam_type,final String course_filter_type,
                                final String course_intr,final String course_comments,
                                final String course_select){
        lock=true;
        is_success=false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String selectSC="select * from stu_course where user_name='"+user_name
                        +"' and course_class_id='"+course_class_id+"';";
                String selectCourse="select * from course_basic where course_class_id='"+course_class_id+"';";
                String updateCourseB="update course_basic set course_remain='"+course_remain
                        +"',course_to_filter='"+course_to_filter+"' where course_class_id='"
                        +course_class_id+"';";
                String insertCourseB="insert into course_basic values('"+
                        course_class_id+"','"+course_year+"','"+
                        course_semester+"','"+course_id+"','"+
                        course_name+"','"+course_type+"','"+
                        course_department+"','"+course_zone+"','"+
                        course_teacher+"','"+course_credit+"','"+
                        course_remain+"','"+course_to_filter+"','"+
                        course_time_and_place+"','"+course_require+"'); ";
                String insertCourseI="insert into course_info values('"+
                        course_class_id+"','"+course_class_name+"','"+
                        course_english_name+"','"+course_capacity+"','"+
                        course_hour+"','"+course_chosen+"','"+
                        course_real_chosen+"','"+course_exam_type+"','"+
                        course_filter_type+"','"+course_intr+"','"+
                        course_comments+"'); ";
                String insertSC="insert into stu_course values('"+
                        user_name+"','"+course_class_id+"','"+
                        "public"+"','"+course_select+"'); ";


                try {
                    Statement stat=conn.createStatement();
                    Log.i("sql",selectSC);
                    ResultSet rs=stat.executeQuery(selectSC);
                    if(!rs.next()){
                        Log.i("sql",selectCourse);
                        rs=stat.executeQuery(selectCourse);
                        if(rs.next()){
                            Log.i("sql",updateCourseB);
                            stat.executeUpdate(updateCourseB);
                        }
                        else{
                            Log.i("sql",insertCourseB);
                            stat.executeUpdate(insertCourseB);
                            Log.i("sql",insertCourseI);
                            stat.executeUpdate(insertCourseI);
                        }
                        Log.i("sql",insertSC);
                        stat.executeUpdate(insertSC);
                    }
                    is_success=true;
                    Log.d("in","successfully");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                lock=false;
            }
        }).start();
        while (lock);
        return is_success;
    }

    //这个函数太蠢了。。用下面那个吧
    public List<Map<String,Object>> selectCourses(final String user_name,final boolean isPrivate){
        data=new ArrayList<>();
        lock=true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String selectCourseB="select * from course_basic where course_class_id in(select course_class_id from stu_course where user_name='"
                        +user_name+"');";
                String selectCourseI="select * from course_info where course_class_id in(select course_class_id from stu_course where user_name='"
                        +user_name+"');";
                String selectSC="select * from stu_course where user_name='"+user_name+"';";
                Log.d("sql",selectSC);
                Log.d("sql",selectCourseB);
                Log.d("sql",selectCourseI);
                try {
                    Statement stat=conn.createStatement();
                    ResultSet rs1=stat.executeQuery(selectCourseB);
                    Statement stat2=conn.createStatement();
                    ResultSet rs2=stat2.executeQuery(selectCourseI);
                    Statement stat3=conn.createStatement();
                    ResultSet rs3=stat3.executeQuery(selectSC);
                    while (rs1.next()){
                        rs2.next();
                        rs3.next();
                        if(isPrivate&&rs3.getString("course_public").equals("private"))
                            continue;
                        Map<String,Object>tmp=new LinkedHashMap<>();
                        tmp.put("course_class_id",rs1.getString("course_class_id"));
                        tmp.put("course_year",rs1.getString("course_year"));
                        tmp.put("course_semester",rs1.getString("course_semester"));
                        tmp.put("course_id",rs1.getString("course_id"));
                        tmp.put("course_name",rs1.getString("course_name"));
                        tmp.put("course_type",rs1.getString("course_type"));
                        tmp.put("course_department",rs1.getString("course_department"));
                        tmp.put("course_zone",rs1.getString("course_zone"));
                        tmp.put("course_teacher",rs1.getString("course_teacher"));
                        tmp.put("course_credit",rs1.getString("course_credit"));
                        tmp.put("course_remain",rs1.getString("course_remain"));
                        tmp.put("course_to_filter",rs1.getString("course_to_filter"));
                        tmp.put("course_time_and_place",rs1.getString("course_time_and_place"));
                        tmp.put("course_require",rs1.getString("course_require"));

                        tmp.put("course_class_name",rs2.getString("course_class_name"));
                        tmp.put("course_english_name",rs2.getString("course_english_name"));
                        tmp.put("course_capacity",rs2.getString("course_capacity"));
                        tmp.put("course_hour",rs2.getString("course_hour"));
                        tmp.put("course_chosen",rs2.getString("course_chosen"));
                        tmp.put("course_real_chosen",rs2.getString("course_real_chosen"));
                        tmp.put("course_exam_type",rs2.getString("course_exam_type"));
                        tmp.put("course_filter_type",rs2.getString("course_filter_type"));
                        tmp.put("course_intr",rs2.getString("course_intr"));
                        tmp.put("course_comments",rs2.getString("course_comments"));

                        tmp.put("course_public",rs3.getString("course_public"));
                        tmp.put("course_select",rs3.getString("course_select"));

                        data.add(tmp);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                lock=false;
            }
        }).start();
        while (lock);
        return data;
    }

    public boolean findCourse(final String course_class_id,final String user_name){
        lock=true;
        is_success=false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String selectSC="select * from stu_course where user_name='"+user_name
                        +"' and course_class_id='"+course_class_id+"';";
                try {
                    Statement stat=conn.createStatement();
                    Log.i("sql",selectSC);
                    ResultSet rs=stat.executeQuery(selectSC);
                    if(rs.next()){
                        is_success=true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                lock=false;
            }
        }).start();
        while (lock);
        return is_success;
    }



    public Map<String,Object> selectCourse(final String course_class_id,final String user_name){
        tmp=new LinkedHashMap<>();
        lock=true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String selectCourseB="select * from course_basic where course_class_id='"
                        +course_class_id+"';";
                String selectCourseI="select * from course_info where course_class_id='"
                        +course_class_id+"';";
                String selectSC="select * from stu_course where user_name='"+user_name+"'and course_class_id='"+course_class_id+"';";
                Log.d("sql",selectSC);
                Log.d("sql",selectCourseB);
                Log.d("sql",selectCourseI);
                try {
                    Statement stat=conn.createStatement();
                    ResultSet rs1=stat.executeQuery(selectCourseB);
                    Statement stat2=conn.createStatement();
                    ResultSet rs2=stat2.executeQuery(selectCourseI);
                    Statement stat3=conn.createStatement();
                    ResultSet rs3=stat3.executeQuery(selectSC);
                    while (rs1.next()){
                        rs2.next();
                        rs3.next();

                        tmp.put("course_class_id",rs1.getString("course_class_id"));
                        tmp.put("course_year",rs1.getString("course_year"));
                        tmp.put("course_semester",rs1.getString("course_semester"));
                        tmp.put("course_id",rs1.getString("course_id"));
                        tmp.put("course_name",rs1.getString("course_name"));
                        tmp.put("course_type",rs1.getString("course_type"));
                        tmp.put("course_department",rs1.getString("course_department"));
                        tmp.put("course_zone",rs1.getString("course_zone"));
                        tmp.put("course_teacher",rs1.getString("course_teacher"));
                        tmp.put("course_credit",rs1.getString("course_credit"));
                        tmp.put("course_remain",rs1.getString("course_remain"));
                        tmp.put("course_to_filter",rs1.getString("course_to_filter"));
                        tmp.put("course_time_and_place",rs1.getString("course_time_and_place"));
                        tmp.put("course_require",rs1.getString("course_require"));

                        tmp.put("course_class_name",rs2.getString("course_class_name"));
                        tmp.put("course_english_name",rs2.getString("course_english_name"));
                        tmp.put("course_capacity",rs2.getString("course_capacity"));
                        tmp.put("course_hour",rs2.getString("course_hour"));
                        tmp.put("course_chosen",rs2.getString("course_chosen"));
                        tmp.put("course_real_chosen",rs2.getString("course_real_chosen"));
                        tmp.put("course_exam_type",rs2.getString("course_exam_type"));
                        tmp.put("course_filter_type",rs2.getString("course_filter_type"));
                        tmp.put("course_intr",rs2.getString("course_intr"));
                        tmp.put("course_comments",rs2.getString("course_comments"));

                        tmp.put("course_public",rs3.getString("course_public"));
                        tmp.put("course_select",rs3.getString("course_select"));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                lock=false;
            }
        }).start();
        while (lock);
        return tmp;
    }

    public boolean setPublic(final String course_public,final String course_class_id,final String user_name){
        lock=true;
        is_success=false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String updateSQL="update stu_course set course_public='"+course_public
                        +"' where course_class_id='"
                        +course_class_id+"'and user_name='"+user_name+"';";
                try {
                    Statement stat=conn.createStatement();
                    stat.executeUpdate(updateSQL);
                    is_success=true;
                    Log.d("in","successfully");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                lock=false;
            }
        }).start();
        while (lock);
        return is_success;
    }

    public boolean deleteSC(final String course_class_id,final String user_name){
        lock=true;
        is_success=false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String updateSQL="delete from stu_course where user_name='"
                        +user_name+"' and course_class_id='"+course_class_id+"';";
                try {
                    Statement stat=conn.createStatement();
                    stat.executeUpdate(updateSQL);
                    is_success=true;
                    Log.d("in","successfully");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                lock=false;
            }
        }).start();
        while (lock);
        return is_success;
    }

    public List<Map<String,Object>> selectPost(final int pgno, final int pgcnt){
        data=new ArrayList<>();
        lock=true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String selectPost="select * from post limit "+pgno*pgcnt+","+pgcnt+";";
                Log.d("sql",selectPost);
                try {
                    Statement stat=conn.createStatement();
                    ResultSet rs1=stat.executeQuery(selectPost);
                    while (rs1.next()){
                        Map<String,Object>tmp2=new LinkedHashMap<>();
                        tmp2.put("post_id",rs1.getString("post_id"));
                        tmp2.put("post_title",rs1.getString("post_title"));
                        tmp2.put("post_info",rs1.getString("post_info"));
                        tmp2.put("post_sender",rs1.getString("post_sender"));
                        data.add(tmp2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                lock=false;
            }
        }).start();
        while (lock);
        return data;
    }


    public Map<String,Object> selectStu(final String user_name){
        tmp=new LinkedHashMap<>();
        lock=true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String selectStu="select * from user_stu where user_name='"+user_name+"';";
                Log.d("sql",selectStu);
                try {
                    Statement stat=conn.createStatement();
                    ResultSet rs1=stat.executeQuery(selectStu);
                    if (rs1.next()){
                        tmp.put("stu_name",rs1.getString("stu_name"));
                        data.add(tmp);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                lock=false;
            }
        }).start();
        while (lock);
        return tmp;
    }

    public boolean isFriend(final String user_name1,final String user_name2){
        is_success=false;
        lock=true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String selectContact="select * from contact where contact_a='"+user_name1+"'and contact_b='"+user_name2+"';";
                Log.d("sql",selectContact);
                try {
                    Statement stat=conn.createStatement();
                    ResultSet rs1=stat.executeQuery(selectContact);
                    if (rs1.next()){
                        is_success=true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                lock=false;
            }
        }).start();
        while (lock);
        return is_success;
    }

    public List<Map<String,Object>> selectSimpleCourses(final String user_name){
        data=new ArrayList<>();
        lock=true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String selectSQL="select * from stu_course natural join course_basic where user_name='"+user_name
                        +"';";
                Log.d("sql",selectSQL);
                try {
                    Statement stat=conn.createStatement();
                    ResultSet rs1=stat.executeQuery(selectSQL);
                    while (rs1.next()){
                        Map<String,Object>tmp2=new LinkedHashMap<>();
                        tmp2.put("course_class_id",rs1.getString("course_class_id"));
                        tmp2.put("course_public",rs1.getString("course_public"));
                        tmp2.put("course_select",rs1.getString("course_select"));
                        tmp2.put("course_name",rs1.getString("course_name"));
                        data.add(tmp2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                lock=false;
            }
        }).start();
        while (lock);
        return data;
    }

    public List<Map<String,Object>> selectSimplePublicCourses(final String user_name){
        data=new ArrayList<>();
        lock=true;
        Log.d("user_name",user_name);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String selectSQL="select * from stu_course natural join course_basic where user_name='"+user_name
                        +"';";
                Log.d("sql",selectSQL);
                try {
                    Statement stat=conn.createStatement();
                    ResultSet rs1=stat.executeQuery(selectSQL);
                    while (rs1.next()){
                        Log.d("course_name",rs1.getString("course_name"));
                        String course_public=rs1.getString("course_public");
                        Log.d("course_public",course_public);
                        String course_select=rs1.getString("course_select");
                        Log.d("course_select",course_select);
                        if(course_public.equals("private"))continue;
                        if(course_select.equals("选课成功"))continue;
                        Map<String,Object>tmp2=new LinkedHashMap<>();
                        tmp2.put("course_class_id",rs1.getString("course_class_id"));
                        tmp2.put("course_public",course_public);
                        tmp2.put("course_select",course_select);
                        tmp2.put("course_name",rs1.getString("course_name"));
                        Log.d("add","course");
                        data.add(tmp2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                lock=false;
            }
        }).start();
        while (lock);
        return data;
    }

    public boolean insertPost(final String title,final String info,final String sender){
        lock=true;
        is_success=false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String insertSQL="insert into post(post_title,post_info,post_sender) values('"+title+"','"+info+"','"
                        +sender+"'); ";
                Log.d("sql",insertSQL);
                try {
                    Statement stat=conn.createStatement();
                    stat.executeUpdate(insertSQL);
                    is_success=true;
                    Log.d("in","successfully");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                lock=false;
            }
        }).start();
        while (lock);
        return is_success;
    }

    public boolean sendFriendRequest(final String sender,final String receiver,final String content){
        lock=true;
        is_success=false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String insertSQL="insert into request(req_sender,req_receiver,req_type,req_info,req_date,req_time) values('"
                        +sender+"','"+receiver+"','friend','"+content+"','"+getDate()+"','"+getTime()+"');";
                Log.d("sql",insertSQL);
                try {
                    Statement stat=conn.createStatement();
                    int a=stat.executeUpdate(insertSQL);
                    if(a!=0)is_success=true;
                    Log.d("in","successfully");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                lock=false;
            }
        }).start();
        while (lock);
        return is_success;
    }


    public List<Map<String,Object>> selectRequest(final String username){
        data=new ArrayList<>();
        lock=true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String selectRequest="select * from request join user_stu on user_name=req_sender where req_receiver='"+username+"';";
                Log.d("sql",selectRequest);
                try {
                    Statement stat=conn.createStatement();
                    ResultSet rs1=stat.executeQuery(selectRequest);
                    while (rs1.next()){
                        Map<String,Object>tmp2=new LinkedHashMap<>();
                        tmp2.put("req_id",rs1.getString("req_id"));
                        tmp2.put("req_sender",rs1.getString("req_sender"));
                        tmp2.put("stu_name",rs1.getString("stu_name"));
                        if(rs1.getString("req_type").equals("friend")){
                            tmp2.put("req_type","好友请求");
                        }
                        else if(rs1.getString("req_type").equals("exchange")){
                            tmp2.put("req_type","换课请求");
                        }
                        else{
                            tmp2.put("req_type",rs1.getString("req_type"));
                        }
                        tmp2.put("req_info",rs1.getString("req_info"));
                        tmp2.put("req_date",rs1.getString("req_date"));
                        tmp2.put("req_time",rs1.getString("req_time"));
                        data.add(tmp2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                lock=false;
            }
        }).start();
        while (lock);
        return data;
    }

    public boolean becomeFriend(final String stu1,final String stu2){
        lock=true;
        is_success=false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String insertSQL="insert into contact values('"+stu1+"','"+stu2+"'),('"+stu2+"','"+stu1+"');";
                Log.d("sql",insertSQL);
                try {
                    Statement stat=conn.createStatement();
                    int a=stat.executeUpdate(insertSQL);
                    if(a==2)is_success=true;
                    Log.d("in","successfully");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                lock=false;
            }
        }).start();
        while (lock);
        return is_success;
    }

    public boolean deleteRequest(final String id){
        lock=true;
        is_success=false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String deleteSQL="delete from request where req_id='"+id+"';";
                Log.d("sql",deleteSQL);
                try {
                    Statement stat=conn.createStatement();
                    int a=stat.executeUpdate(deleteSQL);
                    if(a==1)is_success=true;
                    Log.d("in","successfully");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                lock=false;
            }
        }).start();
        while (lock);
        return is_success;
    }

    public String getDate(){
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
        return dateFormat.format( now );
    }

    public String getTime(){
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format( now );
    }

    public List<Map<String,Object>> selectMessageNum(final String username){
        data=new ArrayList<>();
        lock=true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String selectPost="select info_sender, stu_name,count(cache_info) as info_num from info_cache left join user_stu on info_sender=user_name where info_receiver='"+username+"' group by info_sender;";
                Log.d("sql",selectPost);
                try {
                    Statement stat=conn.createStatement();
                    ResultSet rs1=stat.executeQuery(selectPost);
                    while (rs1.next()){
                        Map<String,Object>tmp2=new LinkedHashMap<>();
                        tmp2.put("info_sender",rs1.getString("info_sender"));
                        Log.d("info_sender",rs1.getString("info_sender"));
                        tmp2.put("stu_name",rs1.getString("stu_name"));
                        Log.d("stu_name",rs1.getString("stu_name"));
                        tmp2.put("info_num","未读消息："+rs1.getString("info_num")+"条");
                        Log.d("info_num","未读消息："+rs1.getString("info_num")+"条");
                        data.add(tmp2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                lock=false;
            }
        }).start();
        while (lock);
        return data;
    }

    public List<Map<String,Object>> selectContacts(final String username){
        data=new ArrayList<>();
        lock=true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String selectPost="select user_name,stu_name from contact left join user_stu on contact_b=user_name where contact_a='"+username+"';";
                Log.d("sql",selectPost);
                try {
                    Statement stat=conn.createStatement();
                    ResultSet rs1=stat.executeQuery(selectPost);
                    while (rs1.next()){
                        Map<String,Object>tmp2=new LinkedHashMap<>();
                        tmp2.put("user_name",rs1.getString("user_name"));
                        Log.d("user_name",rs1.getString("user_name"));
                        tmp2.put("stu_name",rs1.getString("stu_name"));
                        Log.d("stu_name",rs1.getString("stu_name"));
                        data.add(tmp2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                lock=false;
            }
        }).start();
        while (lock);
        return data;
    }















}
