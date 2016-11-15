package com.example.yunyao.lab6;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    //因为SavedPasswordFlag和Password均为隐私属性，因此最好存在内部储存中
    //传入一个文件名和写入的字符串，返回boolean类型告知是否成功
    boolean WriteToStorage(String filename, String input_str) {
        try {
            //文件设置为只读，因为一旦存进去不需要再做修改
            FileOutputStream outStream=this.openFileOutput(filename, Context.MODE_PRIVATE);
            outStream.write(input_str.getBytes());
            outStream.close();
            return true;
            //Toast.makeText(MainActivity.this,"Saved",Toast.LENGTH_LONG).show();
        }
        catch (IOException e){
            return false;
        }
    }
    //传入文件名，取出文件中的数据
    String ReadFromStorage(String filename) {
        try {
            FileInputStream inStream=this.openFileInput(filename);
            byte[] buffer=new byte[1024];
            inStream.read(buffer);
            inStream.close();
            return (new String(buffer));
            //Toast.makeText(MainActivity.this,"Loaded",Toast.LENGTH_LONG).show();
        } catch (IOException e){
            return "";
        }
    }


    static String SavedPasswordFlag = "F";
    static final String SavedPasswordFlag_txt = "SavedPasswordFlag.txt";
    static final String Context_txt = "Context.txt";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText password1st = (EditText)findViewById(R.id.password1st);
        final EditText password2ed = (EditText)findViewById(R.id.password2ed);
        Button ok_button = (Button)findViewById(R.id.ok_button);
        Button clear_button = (Button)findViewById(R.id.clear_button);

        //读取标识符
        SavedPasswordFlag = ReadFromStorage(SavedPasswordFlag_txt);
        //如果标志服没有初始化过，就将它初始化为F
        if (SavedPasswordFlag.equals("")) SavedPasswordFlag = "F";
        Log.i("SavedPasswordFlag", SavedPasswordFlag);

        if (SavedPasswordFlag.equals("F") || SavedPasswordFlag.equals("")) {
            password1st.setVisibility(View.VISIBLE); //设置TextView2可见
        }else {
            password2ed.setHint("Password");
            password1st.setVisibility(View.GONE); //设置TextView2隐藏
        }

        ok_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //如果清除过一次SavedPasswordFlag_txt中的内容，小心这个时候SavedPasswordFlag没有初始化
                //如果没有保存过
                if (SavedPasswordFlag.equals("F")){
                    String string1st = password1st.getText().toString();
                    String string2ed = password2ed.getText().toString();
                    if (string1st.equals("")){
                        Toast.makeText(MainActivity.this, "Password cannot be empty.", Toast.LENGTH_SHORT).show();
                    }else if (!string1st.equals(string2ed)){
                        Toast.makeText(MainActivity.this, "Password Mismatch.", Toast.LENGTH_SHORT).show();
                    }else{
                        SavedPasswordFlag = "T";
                        //存入flag
                        if (WriteToStorage(SavedPasswordFlag_txt, SavedPasswordFlag)) {
                            SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("password", string1st);
                            editor.apply();
                            Toast.makeText(MainActivity.this, "Password is correctly saved!", Toast.LENGTH_SHORT).show();
                            //跳转到第二个activity
                            Intent intent = new Intent();
                            intent.setClass(MainActivity.this, TextActivity.class);
                            startActivity(intent);
                        }
                    }
                }else{
                    //如果保存了密码
                    SharedPreferences preferences=getSharedPreferences("user", Context.MODE_PRIVATE);
                    String true_password=preferences.getString("password", "defaultpassword");
                    String string2ed = password2ed.getText().toString();
                    if (string2ed.equals(true_password)){
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, TextActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(MainActivity.this, "Invalid Password.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        clear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                password1st.setText("");
                password2ed.setText("");
            }
        });
    }
}
