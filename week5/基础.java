package com.example.yunyao.lab4;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    //对应四个RadioButton
    RadioButton StudentRadioButton;
    RadioButton CorporationRadioButton;
    RadioButton TeacherRadioButton;
    RadioButton AdministratorRadioButton;
    //对应整个RadioGroup
    RadioGroup radioGroup;

    //对应两个button
    Button RegisterButton;
    Button LoginButton;

    //声明一个AlterDialog构造器
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //对应四个RadioButton
        StudentRadioButton = (RadioButton) findViewById(R.id.StudentRadioButton);                 //学生单选按钮
        CorporationRadioButton = (RadioButton)findViewById(R.id.CorporationRadioButton);         //社团单选按钮
        TeacherRadioButton = (RadioButton)findViewById(R.id.TeacherRadioButton);                  //老师单选按钮
        AdministratorRadioButton = (RadioButton)findViewById(R.id.AdministratorRadioButton);    //管理者单选按钮
        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);                                     //整个单选
        //对应两个button
        RegisterButton = (Button)findViewById(R.id.RegisterButton);            //注册按钮
        LoginButton = (Button)findViewById(R.id.LoginButton);                   //登陆按钮

        //监听点击事件
        StudentRadioButton.setOnClickListener(this);
        TeacherRadioButton.setOnClickListener(this);
        CorporationRadioButton.setOnClickListener(this);
        AdministratorRadioButton.setOnClickListener(this);
        //radioGroup.setOnCheckedChangeListener(this);
        LoginButton.setOnClickListener(this);
        RegisterButton.setOnClickListener(this);
    }

    public void showLoginDialog(View target) {
        final EditText username = (EditText) findViewById(R.id.inputBox);       //用户名
        final EditText password = (EditText) findViewById(R.id.passwordBox);   //密码
        boolean usernameEmptyFlag = TextUtils.isEmpty(username.getText().toString());
        boolean passwordEmptyFlag = TextUtils.isEmpty(password.getText().toString());
        if(usernameEmptyFlag){
            Toast.makeText(MainActivity.this, R.string.EmptyUsername,
                    Toast.LENGTH_LONG).show();
        }
        else if(passwordEmptyFlag){
            Toast.makeText(MainActivity.this, R.string.EmptyPassword,
                    Toast.LENGTH_LONG).show();
        }
        else {
            boolean UsernameFlag = username.getText().toString().equals("Android");
            boolean PassWordFlag = password.getText().toString().equals("123456");
            builder=new AlertDialog.Builder(this);
            builder.setTitle(R.string.AlertDialogTitle);
            if(UsernameFlag && PassWordFlag)
                builder.setMessage(R.string.LoginSuccess);
            else builder.setMessage(R.string.LoginFail);

            //监听下方button点击事件
            builder.setPositiveButton(R.string.AlertDialogOK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(getApplicationContext(), R.string.SelectOK, Toast.LENGTH_LONG).show();
                }
            });
            builder.setNegativeButton(R.string.AlertDialogCancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(getApplicationContext(), R.string.SelectCanCel, Toast.LENGTH_LONG).show();
                }
            });
            //设置对话框是可取消的
            builder.setCancelable(true);
            builder.create().show();
        }
    }


    public void showRegisterToast(View view){
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton ChildRadioButton = (RadioButton) radioGroup.getChildAt(i);
            if (ChildRadioButton.isChecked()) {
                Toast.makeText(getApplicationContext(), ChildRadioButton.getText()+"身份注册功能尚未开启", Toast.LENGTH_LONG).show();
                break;
            }
        }
    }

    public void showRadioToast(View view){
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton ChildRadioButton = (RadioButton) radioGroup.getChildAt(i);
            if (ChildRadioButton.isChecked()) {
                Toast.makeText(getApplicationContext(), ChildRadioButton.getText()+"身份被选中", Toast.LENGTH_LONG).show();
                break;
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.RegisterButton:
                showRegisterToast(view);
                break;
            case R.id.LoginButton:
                showLoginDialog(view);
                break;
            case R.id.StudentRadioButton:
            case R.id.TeacherRadioButton:
            case R.id.CorporationRadioButton:
            case R.id.AdministratorRadioButton:
                showRadioToast(view);
                break;
            default:
                Toast.makeText(getApplication(), "你摸的哪？//手动黑人问号脸",
                        Toast.LENGTH_LONG).show();
        }
    }
}
