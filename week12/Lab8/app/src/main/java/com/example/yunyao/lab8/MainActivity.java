package com.example.yunyao.lab8;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private int totalnumber;
    private List<myDB> myDBList = new ArrayList<>();

    public void initUsers(){
        totalnumber = 0;
        MyAdapter myAdapter = new MyAdapter(MainActivity.this, R.layout.listview_item, myDBList);
        ListView MainlistView = (ListView)findViewById(R.id.MainlistView);
        int nameFieldColumnIndex = 0;
        String PhoneNumber = "";
        String contact = "";
        //将自定义适配器与listView绑定
        MainlistView.setAdapter(myAdapter);

        //得到ContentResolver对象
        ContentResolver cr = getContentResolver();
        //取得电话本中开始一项的光标
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        //向下移动光标
        while(cursor.moveToNext()) {
            //取得联系人名字
            nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
            contact = cursor.getString(nameFieldColumnIndex);
            //取得电话号码
            String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +"="+ ContactId, null, null);

            PhoneNumber = "";
            while(phone.moveToNext()) {
                if (PhoneNumber.equals("")) PhoneNumber += phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                PhoneNumber += ('\n'+phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            }

            myDB date = new myDB(contact, PhoneNumber, null, null);
            myDBList.add(date);
            ++totalnumber;
        }
        cursor.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUsers();
        Button newObject = (Button)findViewById(R.id.newObject);
        ListView listView = (ListView)findViewById(R.id.MainlistView);
        newObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, newinfo.class);
                startActivity(intent);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LayoutInflater flater = getLayoutInflater();
                View dialogview =  flater.inflate(R.layout.dialoglayout, null);
                final TextView name = (TextView)dialogview.findViewById(R.id.InfoObjectNameText);
                final EditText InfoObjectBirthdayText = (EditText)dialogview.findViewById(R.id.InfoObjectBirthdayText);
                final EditText InfoObjectGiftText = (EditText)dialogview.findViewById(R.id.InfoObjectGiftText);
                final TextView InfoObjectPhoneText = (TextView)dialogview.findViewById(R.id.InfoObjectPhoneText);
                final Button InfoNoSaveButton = (Button)dialogview.findViewById(R.id.InfoNoSaveButton);
                final Button InfoSaveButton = (Button)dialogview.findViewById(R.id.InfoSaveButton);
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(dialogview);
                final AlertDialog dialog = builder.show();
                InfoNoSaveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO
                        dialog.dismiss();
                    }
                });
                InfoSaveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO
                        dialog.dismiss();
                    }
                });
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                LayoutInflater flater = getLayoutInflater();
                View dialogview =  flater.inflate(R.layout.deletedialog, null);
                final Button DeleteNo = (Button)dialogview.findViewById(R.id.DeleteNo);
                final Button DeleteYes = (Button)dialogview.findViewById(R.id.DeleteYes);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(dialogview);
                final AlertDialog dialog = builder.show();
                DeleteNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO
                        dialog.dismiss();
                    }
                });
                DeleteYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO
                        dialog.dismiss();
                    }
                });
                return false;
            }
        });
    }
}
