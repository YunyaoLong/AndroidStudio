package com.example.yunyao.lab8;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private int totalnumber;
    private List<myDB> myDBList = new ArrayList<>();

    public void initUsers(){
        for (int i=0;i<totalnumber;i++){
            myDB user= myDBList.get(i);
            myDBList.add(user);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        totalnumber = 0;
        MyAdapter myAdapter = new MyAdapter(MainActivity.this, R.layout.listview_item, myDBList);
        ListView MainlistView = (ListView)findViewById(R.id.MainlistView);
        //将自定义适配器与listView绑定
        MainlistView.setAdapter(myAdapter);

        //得到ContentResolver对象
        ContentResolver cr = getContentResolver();
        //取得电话本中开始一项的光标
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        //向下移动光标
        while(cursor.moveToNext()) {
            //取得联系人名字
            int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
            String contact = cursor.getString(nameFieldColumnIndex);
            //取得电话号码
            String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +"="+ ContactId, null, null);

            while(phone.moveToNext())
            {
                String PhoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                myDB date = new myDB(contact, PhoneNumber, null, null);
                myDBList.add(date);
                ++totalnumber;
            }
        }
        cursor.close();
    }
}
