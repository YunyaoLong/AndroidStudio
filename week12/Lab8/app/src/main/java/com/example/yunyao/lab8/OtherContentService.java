package com.example.yunyao.lab8;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yunyao on 2016/11/21.
 */
public class OtherContentService {
    private myDB mydb;
    private static final String DB_NAME = "lab8";
    private static final String TABLE_NAME = "Content";

    public OtherContentService(Context context) {
        this.mydb = new myDB(context);
    }

    public void save(Content content){
        SQLiteDatabase db = mydb.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", content.getInfoObjectNameText());
        values.put("birthday", content.getInfoObjectBirthdayText());
        values.put("gift", content.getInfoObjectGiftText());
        values.put("number", content.getInfoObjectPhoneText());
        values.put("flag", content.getFlag());
        //插入语句，第一个参数是表名，第三个参数是字段值
        //SQL规定一定要传入一个字段，当values为null时，第二个参数可以设为“name”，不过是个空字段
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void update(Content content){
        // update person set name =? where personid =?
        SQLiteDatabase db = mydb.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", content.getInfoObjectNameText());
        values.put("birthday", content.getInfoObjectBirthdayText());
        values.put("gift", content.getInfoObjectGiftText());
        values.put("number",content.getInfoObjectPhoneText());
        values.put("flag", content.getFlag());

        String whereClause = "name = ?";
        String[] whereArgs = {content.getInfoObjectNameText()};
        //第三个参数代表条件部分的sql语句
        db.update(TABLE_NAME, values, whereClause, whereArgs);
        db.close();
    }

    public void delete(Content content){
        SQLiteDatabase db = mydb.getWritableDatabase();
        String whereClause = "name = ?";
        String[] whereArgs = {content.getInfoObjectNameText()};
        db.delete(TABLE_NAME, whereClause, whereArgs);
        db.close();
    }

    public Content find(String name){
        //如果只对数据进行读取，建议使用此方法
        SQLiteDatabase db = mydb.getReadableDatabase();
        String whereClause = "name = ?";
        Cursor cursor = db.query(TABLE_NAME, new String[]{"name", "birthday", "gift", "number", "flag"},
                whereClause, new String[]{name}, null, null, null);

        if(cursor.moveToFirst()){
            String ObjectName = cursor.getString(cursor.getColumnIndex("name"));
            String ObjectBirthday = cursor.getString(cursor.getColumnIndex("birthday"));
            String ObjectGift = cursor.getString(cursor.getColumnIndex("gift"));
            String ObjectNumber = cursor.getString(cursor.getColumnIndex("number"));
            int flag = cursor.getInt(cursor.getColumnIndex("flag"));
            return new Content(ObjectName, ObjectBirthday, ObjectGift, ObjectNumber, flag);
        }
        return null;
    }

    public List<Content> findall(){

        List<Content> contents = new ArrayList<Content>();
        SQLiteDatabase db = mydb.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{"*"}, null, null, null, null, null);
        String ObjectName, ObjectBirthday, ObjectGift, ObjectNumber;
        while(cursor.moveToNext()){
            ObjectName = cursor.getString(cursor.getColumnIndex("name"));
            ObjectBirthday = cursor.getString(cursor.getColumnIndex("birthday"));
            ObjectGift = cursor.getString(cursor.getColumnIndex("gift"));
            ObjectNumber = cursor.getString(cursor.getColumnIndex("number"));
            int flag = cursor.getInt(cursor.getColumnIndex("flag"));
            Content content = new Content(ObjectName, ObjectBirthday, ObjectGift, ObjectNumber, flag);
            contents.add(content);
        }
        cursor.close();
        return contents;
    }

    public List<Content> findprint(){

        List<Content> contents = new ArrayList<Content>();
        SQLiteDatabase db = mydb.getReadableDatabase();
        String whereClause = "flag > ?";
        Cursor cursor = db.query(TABLE_NAME, new String[]{"name", "birthday", "gift", "number", "flag"},
                whereClause, new String[]{"0"}, "flag", null, null);
        String ObjectName, ObjectBirthday, ObjectGift, ObjectNumber;
        while(cursor.moveToNext()){
            ObjectName = cursor.getString(cursor.getColumnIndex("name"));
            ObjectBirthday = cursor.getString(cursor.getColumnIndex("birthday"));
            ObjectGift = cursor.getString(cursor.getColumnIndex("gift"));
            ObjectNumber = cursor.getString(cursor.getColumnIndex("number"));
            int flag = cursor.getInt(cursor.getColumnIndex("flag"));
            Content content = new Content(ObjectName, ObjectBirthday, ObjectGift, ObjectNumber, flag);
            contents.add(content);
        }
        cursor.close();
        return contents;
    }

    public long getCount() {
        // select count(*) from person
        SQLiteDatabase db = mydb.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{"count(*)"}, null, null, null, null, null);
        cursor.moveToFirst();
        return cursor.getLong(0);
    }
}

/**
 根据Android文档，
 public final Cursor query (Uri uri, String[] projection,String selection,String[] selectionArgs, StringsortOrder)

 第一个参数，uri，rui是什么呢？好吧，上面我们提到了Android提供内容的叫Provider，那么在Android中怎么区分各个Provider？有提供联系人的，有提供图片的等等。所以就需要有一个唯一的标识来标识这个Provider，Uri就是这个标识，android.provider.ContactsContract.Contacts.CONTENT_URI就是提供联系人的内容提供者，可惜这个内容提供者提供的数据很少。

 第二个参数，projection，真不知道为什么要用这个单词，这个参数告诉Provider要返回的内容（列Column），比如Contacts Provider提供了联系人的ID和联系人的NAME等内容，如果我们只需要NAME，那么我们就应该使用：
         Cursor cursor = contentResolver.query(android.provider.ContactsContract.Contacts.CONTENT_URI,
         new String[]{android.provider.ContactsContract.Contacts.DISPLAY_NAME}, null, null, null);
 当然，下面打印的你就只能显示NAME了，因为你返回的结果不包含ID。用null表示返回Provider的所有内容（列Column）。

 第三个参数，selection，设置条件，相当于SQL语句中的where。null表示不进行筛选。如果我们只想返回名称为张三的数据，第三个参数应该设置为：
         Cursor cursor = contentResolver.query(android.provider.ContactsContract.Contacts.CONTENT_URI,
         new String[]{android.provider.ContactsContract.Contacts.DISPLAY_NAME},
         android.provider.ContactsContract.Contacts.DISPLAY_NAME + "='张三'", null, null);
 结果：
        11-05 15:30:32.188: I/System.out(10271): 张三

 第四个参数，selectionArgs，这个参数是要配合第三个参数使用的，如果你在第三个参数里面有？，那么你在selectionArgs写的数据就会替换掉？，
         Cursor cursor = contentResolver.query(android.provider.ContactsContract.Contacts.CONTENT_URI,
         new String[]{android.provider.ContactsContract.Contacts.DISPLAY_NAME},
         android.provider.ContactsContract.Contacts.DISPLAY_NAME + "=?",
         new String[]{"张三"}, null);
 效果和上面一句的效果一样。

 第五个参数，sortOrder，按照什么进行排序，相当于SQL语句中的Order by。如果想要结果按照ID的降序排列：

         Cursor cursor = contentResolver.query(android.provider.ContactsContract.Contacts.CONTENT_URI,
         null, null,null, android.provider.ContactsContract.Contacts._ID + " DESC");
 结果：
         11-05 16:00:32.808: I/System.out(12523): 31
         11-05 16:00:32.808: I/System.out(12523): 李四
         11-05 16:00:32.817: I/System.out(12523): 13
         11-05 16:00:32.817: I/System.out(12523): 张三
 升序，其实默认排序是升序，+" ASC"写不写效果都一样：
         Cursor cursor = contentResolver.query(android.provider.ContactsContract.Contacts.CONTENT_URI,
         null, null,null, android.provider.ContactsContract.Contacts._ID + " ASC");
 结果：
         11-05 15:59:10.327: I/System.out(12406): 13
         11-05 15:59:10.327: I/System.out(12406): 张三
         11-05 15:59:10.327: I/System.out(12406): 31
         11-05 15:59:10.327: I/System.out(12406): 李四
 */