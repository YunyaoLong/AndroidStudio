package com.example.yunyao.lab8;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //public static OtherContentService otherContentService;
    private int totalnumber;
    private static final String DB_NAME = "lab8";
    private static final String TABLE_NAME = "Content";
    private static List<Content> contentList = new ArrayList<>();
    private int max_num = 0;
    //myDB mydb = new myDB((MainActivity.this));
    public static OtherContentService otherContentService;
    private MyAdapter myAdapter;

    public void initUsers(){
        int nameFieldColumnIndex = 0;
        String PhoneNumber = "";
        String contact = "";

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
            Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +"="+ ContactId, null, null);

            PhoneNumber = "";
            while(phone.moveToNext()) {
                if (PhoneNumber.equals("")) PhoneNumber += phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                else PhoneNumber += ("\n"+phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            }

            Content date = new Content(contact, null, null, PhoneNumber, 0);
            //contentList.add(date);
            otherContentService.save(date);
        }
        cursor.close();

        //将数据全部存下来备用
        //int totle_num = new Long(otherContentService.getCount()).intValue();

        Toast.makeText(MainActivity.this, "成功导入"+otherContentService.getCount()+"个联系人数据", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        otherContentService = new OtherContentService(MainActivity.this);

        totalnumber = 0;
        myAdapter = new MyAdapter(MainActivity.this, R.layout.listview_item, contentList);
        ListView MainlistView = (ListView)findViewById(R.id.MainlistView);
        Button newObject = (Button)findViewById(R.id.newObject);
        //将自定义适配器与listView绑定
        MainlistView.setAdapter(myAdapter);

        //将联系人导入数据库，如果第一次打开，就导入所有通讯录数据，如果以前导入过，就不再重复导入
        if (otherContentService.getCount() == 0) initUsers();
        else{
            contentList.clear();
            List<Content> temp = otherContentService.findprint();
            Toast.makeText(MainActivity.this, "找到了"+temp.size()+"个元素", Toast.LENGTH_LONG).show();
            for (int i = 0; i<temp.size(); ++i) {
                contentList.add(temp.get(i));
                //更新max_num，得到ListView中最大的flag，这样新插入数据的时候，就能够保证新的flag最大（flag = max_num+1）
                max_num = (max_num > temp.get(i).getFlag() ? max_num : temp.get(i).getFlag());
            }
        }

        newObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, newinfo.class);
                startActivityForResult(intent, 1000);
            }
        });

        MainlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                LayoutInflater flater = getLayoutInflater();
                View dialogview = flater.inflate(R.layout.dialoglayout, null);
                final TextView name = (TextView) dialogview.findViewById(R.id.InfoObjectNameText);
                final EditText InfoObjectBirthdayText = (EditText) dialogview.findViewById(R.id.InfoObjectBirthdayText);
                final EditText InfoObjectGiftText = (EditText) dialogview.findViewById(R.id.InfoObjectGiftText);
                final TextView InfoObjectPhoneText = (TextView) dialogview.findViewById(R.id.InfoObjectPhoneText);
                final Button InfoNoSaveButton = (Button) dialogview.findViewById(R.id.InfoNoSaveButton);
                final Button InfoSaveButton = (Button) dialogview.findViewById(R.id.InfoSaveButton);

                name.setText(contentList.get(i).getInfoObjectNameText());
                InfoObjectBirthdayText.setText(contentList.get(i).getInfoObjectBirthdayText());
                InfoObjectGiftText.setText(contentList.get(i).getInfoObjectGiftText());
                InfoObjectPhoneText.setText(contentList.get(i).getInfoObjectPhoneText());

                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(dialogview);
                final AlertDialog dialog = builder.show();
                InfoNoSaveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                InfoSaveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String Name = name.getText().toString();
                        String Birthday = InfoObjectBirthdayText.getText().toString();
                        String Gift = InfoObjectGiftText.getText().toString();
                        String Phone = InfoObjectPhoneText.getText().toString();
                        int flag = contentList.get(i).getFlag();
                        //一定要保持flag不变，否则可能会造成在ListView中顺序改变
                        Content content = new Content(Name, Birthday, Gift, Phone, flag);
                        otherContentService.update(content);
                        contentList.remove(i);
                        contentList.add(i, content);
                        myAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
            }
        });
        MainlistView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                LayoutInflater flater = getLayoutInflater();
                View dialogview = flater.inflate(R.layout.deletedialog, null);
                final Button DeleteNo = (Button) dialogview.findViewById(R.id.DeleteNo);
                final Button DeleteYes = (Button) dialogview.findViewById(R.id.DeleteYes);
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
                        String Name =contentList.get(i).getInfoObjectNameText();
                        String Phone = contentList.get(i).getInfoObjectPhoneText();
                        //将生日礼物清空，flag清0，以防以后重新插入该条目时出现问题
                        Content content = new Content(Name, "", "", Phone, 0);
                        otherContentService.update(content);
                        contentList.remove(i);
                        //contentList.add(i, content);
                        myAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                return true;
            }
        });
    }
    /**
     * 所有的Activity对象的返回值都是由这个方法来接收
     * requestCode:    表示的是启动一个Activity时传过去的requestCode值
     * resultCode：表示的是启动后的Activity回传值时的resultCode值
     * data：表示的是启动后的Activity回传过来的Intent对象
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000 && resultCode == 1001) {
            String name = data.getStringExtra("name");
            String birthday = data.getStringExtra("birthday");
            String gift = data.getStringExtra("gift");

            //获取电话号码
            String number = "无";
            if (otherContentService.find(name) != null){
                number = otherContentService.find(name).getInfoObjectPhoneText();
            }
            //如果数据库中没有出现过这个人，就插入，如果有这个人，就更新，此时记得更新max_num
            Content content = new Content(name, birthday, gift, number, ++max_num);
            if (otherContentService.find(name) == null) otherContentService.save(content);
            else otherContentService.update(content);

            contentList.add(content);
            myAdapter.notifyDataSetChanged();
        }
    }
}
