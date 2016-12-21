package com.example.exchange;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.List;
import java.util.Map;

public class CourseDetailActivity extends AppCompatActivity {
    private String cookie,sid,username,jxbh,mode;
    private Map<String,Object> course_info;
    private ExchangeDatabase db;
    TextView course_class_id,course_year,course_semester
            ,course_id,course_name,course_type,course_department,course_zone,course_teacher
            ,course_credit,course_remain,course_to_filter,course_time_and_place,course_require
            ,course_class_name,course_english_name,course_capacity,course_hour,course_chosen
            ,course_real_chosen,course_exam_type,course_filter_type,course_intr,course_comments
            ,course_select;
    ToggleButton course_public;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        Bundle bundle=this.getIntent().getExtras();
        mode=bundle.getString("mode",mode);
        if(!mode.equals("quick"))cookie=bundle.getString("cookie");
        if(!mode.equals("quick"))sid=bundle.getString("sid");
        username=bundle.getString("username");
        jxbh=bundle.getString("course_class_id");
        db=new ExchangeDatabase();
        db.connect();

        course_class_id=(TextView)findViewById(R.id.course_class_id);
        course_year=(TextView)findViewById(R.id.course_year);
        course_semester=(TextView)findViewById(R.id.course_semester);
        course_id=(TextView)findViewById(R.id.course_id);
        course_name=(TextView)findViewById(R.id.course_name);
        course_type=(TextView)findViewById(R.id.course_type);
        course_department=(TextView)findViewById(R.id.course_department);
        course_zone=(TextView)findViewById(R.id.course_zone);
        course_teacher=(TextView)findViewById(R.id.course_teacher);
        course_credit=(TextView)findViewById(R.id.course_credit);
        course_remain=(TextView)findViewById(R.id.course_remain);
        course_to_filter=(TextView)findViewById(R.id.course_to_filter);
        course_time_and_place=(TextView)findViewById(R.id.course_time_and_place);
        course_require=(TextView)findViewById(R.id.course_require);
        course_class_name=(TextView)findViewById(R.id.course_class_name);
        course_english_name=(TextView)findViewById(R.id.course_english_name);
        course_capacity=(TextView)findViewById(R.id.course_capacity);
        course_hour=(TextView)findViewById(R.id.course_hour);
        course_chosen=(TextView)findViewById(R.id.course_chosen);
        course_real_chosen=(TextView)findViewById(R.id.course_real_chosen);
        course_exam_type=(TextView)findViewById(R.id.course_exam_type);
        course_filter_type=(TextView)findViewById(R.id.course_filter_type);
        course_intr=(TextView)findViewById(R.id.course_intr);
        course_comments=(TextView)findViewById(R.id.course_comments);
        course_select=(TextView)findViewById(R.id.course_select);
        course_public=(ToggleButton)findViewById(R.id.course_public);

        renewInfo();
        course_public.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String str;
                if(b){
                    str="public";
                }else{
                    str="private";
                }
                db.setPublic(str,jxbh,username);
            }
        });
    }
    public void renewInfo(){
        course_info=db.selectCourse(jxbh,username);
        course_class_id.setText((String)course_info.get("course_class_id"));
        course_year.setText((String)course_info.get("course_year"));
        course_semester.setText((String)course_info.get("course_semester"));
        course_id.setText((String)course_info.get("course_id"));
        course_name.setText((String)course_info.get("course_name"));
        course_type.setText((String)course_info.get("course_type"));
        course_department.setText((String)course_info.get("course_department"));
        course_zone.setText((String)course_info.get("course_zone"));
        course_teacher.setText((String)course_info.get("course_teacher"));
        course_credit.setText((String)course_info.get("course_credit"));
        course_remain.setText((String)course_info.get("course_remain"));
        course_to_filter.setText((String)course_info.get("course_to_filter"));
        course_time_and_place.setText((String)course_info.get("course_time_and_place"));
        course_require.setText((String)course_info.get("course_require"));
        course_class_name.setText((String)course_info.get("course_class_name"));
        course_english_name.setText((String)course_info.get("course_english_name"));
        course_capacity.setText((String)course_info.get("course_capacity"));
        course_hour.setText((String)course_info.get("course_hour"));
        course_chosen.setText((String)course_info.get("course_chosen"));
        course_real_chosen.setText((String)course_info.get("course_real_chosen"));
        course_exam_type.setText((String)course_info.get("course_exam_type"));
        course_filter_type.setText((String)course_info.get("course_filter_type"));
        //Log.d("intr","+"+(String)course_info.get("course_intr"));
        course_intr.setText((String)course_info.get("course_intr"));
        course_comments.setText((String)course_info.get("course_comments"));
        course_select.setText((String)course_info.get("course_select"));
        if(course_info.get("course_public").equals("public")){
            course_public.setChecked(true);
        }
        else {
            course_public.setChecked(false);
        }
    }
}
