package com.example.yunyao.lab4;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
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

    public void showLoginDialog(View view) {
        final TextInputLayout usernameLayout = (TextInputLayout) findViewById(R.id.username);       //用户名
        final TextInputLayout passwordLayout = (TextInputLayout) findViewById(R.id.password);       //密码
        final EditText username = (EditText) findViewById(R.id.inputBox);           //用户名
        final EditText password = (EditText) findViewById(R.id.passwordBox);       //密码
        boolean usernameEmptyFlag = TextUtils.isEmpty(username.getText().toString());
        boolean passwordEmptyFlag = TextUtils.isEmpty(password.getText().toString());
        if(usernameEmptyFlag){
            usernameLayout.setErrorEnabled(true);
            usernameLayout.setError("用户名不能为空");
        }
        else if(passwordEmptyFlag){
            usernameLayout.setErrorEnabled(false);
            passwordLayout.setErrorEnabled(true);
            passwordLayout.setError("密码不能为空");
        }
        else {
            usernameLayout.setErrorEnabled(false);
            passwordLayout.setErrorEnabled(false);
            boolean UsernameFlag = username.getText().toString().equals("Android");
            boolean PassWordFlag = password.getText().toString().equals("123456");

            if (UsernameFlag && PassWordFlag) {
                Snackbar.make(view, "登录成功", Snackbar.LENGTH_SHORT).setAction("按钮", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(), "Snackbar的按钮被点击了", Toast.LENGTH_SHORT).show();
                    }
                }).show();
            } else {
                Snackbar.make(view, "登录失败", Snackbar.LENGTH_SHORT).setAction("按钮", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(), "Snackbar的按钮被点击了", Toast.LENGTH_SHORT).show();
                    }
                }).show();
            }
        }
    }


    public void showRegisterToast(View view){
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton ChildRadioButton = (RadioButton) radioGroup.getChildAt(i);
            if (ChildRadioButton.isChecked()) {
                Snackbar.make(radioGroup,ChildRadioButton.getText().toString()+"身份注册尚未开启", Snackbar.LENGTH_SHORT).setAction("按钮", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(), "Snackbar的按钮被点击了", Toast.LENGTH_SHORT).show();
                    }
                }).show();
                break;
            }
        }
    }

    public void showRadioToast(View view){
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton ChildRadioButton = (RadioButton) radioGroup.getChildAt(i);
            if (ChildRadioButton.isChecked()) {
                Snackbar.make(radioGroup,ChildRadioButton.getText().toString()+"身份被选中", Snackbar.LENGTH_SHORT).setAction("按钮", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(), "Snackbar的按钮被点击了", Toast.LENGTH_SHORT).show();
                    }
                }).show();
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
