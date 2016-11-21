package com.example.yunyao.lab8;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.yunyao.lab8.MainActivity;
/**
 * Created by yunyao on 2016/11/20.
 */
public class newinfo extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newinfo_layout);
        final EditText editText = (EditText)findViewById(R.id.editText);
        final EditText editText2 = (EditText)findViewById(R.id.editText2);
        final EditText editText3 = (EditText)findViewById(R.id.editText3);
        final Button button2 = (Button)findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().equals("")) {
                    Toast.makeText(newinfo.this, "名字为空，请完善", Toast.LENGTH_SHORT).show();
                }else if(MainActivity.otherContentService.find(editText.getText().toString()) == null
                        || (MainActivity.otherContentService.find(editText.getText().toString()).getFlag() == 0)) {
                    Intent intent = new Intent(newinfo.this, MainActivity.class);
                    intent.putExtra("name", editText.getText().toString());
                    intent.putExtra("birthday", editText2.getText().toString());
                    intent.putExtra("gift", editText3.getText().toString());
                    setResult(1001, intent);
                    finish();
                }else{
                    Toast.makeText(newinfo.this, "名字重复啦，请检查", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
