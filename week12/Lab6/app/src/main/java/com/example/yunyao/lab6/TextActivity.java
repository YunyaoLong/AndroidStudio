package com.example.yunyao.lab6;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by yunyao on 2016/11/14.
 */
public class TextActivity extends Activity {

    static final String Password_txt = "Password.txt";
    static final String Context_txt = "Context.txt";
    //传入文件名，取出文件中的数据
    String ReadFromStorage(String filename){
        String fileContent = "";
        try {
            FileInputStream fis;
            fis = openFileInput(filename);
            byte[] buffer = new byte[1024];
            fis.read(buffer);
            fileContent = new String(buffer, "UTF-8");
            Log.i("ReadFromStorage", "Successfully read file.");
            Toast.makeText(TextActivity.this, "Load Successfully.", Toast.LENGTH_SHORT).show();
            // 对读取的数据进行编码以防止乱码
        }  catch (FileNotFoundException e) {
            Toast.makeText(TextActivity.this, "Fail to load file.", Toast.LENGTH_SHORT).show();
            return "";
        } catch (IOException e) {
            // 如果文件读取失败
            Log.i("ReadFromStorage", "Load fail.");
            e.printStackTrace();
            return "";
        }
        return fileContent;
    }

    //传入一个文件名和写入的字符串，返回boolean类型告知是否成功
    boolean WriteToStorage(String filename, String input_str){
        try {
            FileOutputStream fos = openFileOutput(filename,
                    Context.MODE_PRIVATE);
            fos.write(input_str.getBytes());
            fos.close();
            Log.i("ReadFromStorage", "Successfully saved file.");
        } catch (IOException e) {
            //如果文件创建失败
            e.printStackTrace();
            Log.i("ReadFromStorage", "Create file.");
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_layout);
        final EditText editText = (EditText)findViewById(R.id.editText);
        Button save_button = (Button) findViewById(R.id.save_button);
        Button load_button = (Button) findViewById(R.id.load_button);
        Button clear_button = (Button) findViewById(R.id.clear_button);

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String context = editText.getText().toString();
                if(WriteToStorage(Context_txt, context))
                    Toast.makeText(TextActivity.this, "Save Successfully.", Toast.LENGTH_SHORT).show();
            }
        });

        load_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText(ReadFromStorage(Context_txt));
            }
        });

        clear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
            }
        });
    }
}
