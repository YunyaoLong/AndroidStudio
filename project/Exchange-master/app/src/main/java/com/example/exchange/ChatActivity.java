package com.example.exchange;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {

    TextView history,name;
    EditText edit;
    Button send;
    String myusername,username;
    String stu_name;
    ScrollView scrollView;
    String str;
    StringBuffer sb;

    private final String		DEBUG_TAG	= "chat";
    //服务器IP、端口
    private static final String SERVERIP = "172.18.57.143";
    private static final int SERVERPORT = 54321;
    private static final String DIVIDER="/　/";
    private Thread mThread = null;
    private Socket             mSocket		= null;
    private BufferedReader mBufferedReader	= null;
    private PrintWriter mPrintWriter = null;
    private  String mStrMSG = "";
    private ExchangeDatabase db;

    private static final int SEND_CODE=1752,GET_CODE=1596,CONNECT_FAIL=5789;

    boolean lock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        history=(TextView)findViewById(R.id.history);
        edit=(EditText)findViewById(R.id.edit);
        name=(TextView)findViewById(R.id.name);
        send=(Button)findViewById(R.id.send);
        scrollView=(ScrollView)findViewById(R.id.scrollView);

        Bundle bundle=this.getIntent().getExtras();
        myusername=bundle.getString("myusername");
        username=bundle.getString("username");

        db=new ExchangeDatabase();
        db.connect();
        stu_name=(String)db.selectStu(username).get("stu_name");
        name.setText(stu_name);

        new Thread(new Runnable() {
            @Override
            public void run() {
                //登陆
                try
                {
                    //连接服务器
                    mSocket = new Socket(SERVERIP, SERVERPORT);
                    //取得输入、输出流
                    mBufferedReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream(),"GB2312"));
                    mPrintWriter=new PrintWriter(new OutputStreamWriter(mSocket.getOutputStream(), "GB2312"),true);

                    //启动监听线程
                    mThread = new Thread(mRunnable);
                    mThread.start();

                    //初始化
                    mPrintWriter.println("initial"+DIVIDER+myusername);

                }
                catch (Exception e)
                {
                    // TODO: handle exception
                    Log.e(DEBUG_TAG, e.toString());
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(CONNECT_FAIL);
                    //Toast.makeText(ChatActivity.this, "无法连接服务器", Toast.LENGTH_SHORT).show();
                    //ChatActivity.this.finish();
                }
                lock=false;
            }
        }).start();


        //发送消息
        send.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                lock=true;
                //取得编辑框中我们输入的内容
                str = edit.getText().toString();
                sb=new StringBuffer();
                sb.append("msg").append(DIVIDER)
                        .append(username).append(DIVIDER)
                        .append(myusername).append(DIVIDER)
                        .append(str).append(DIVIDER)
                        .append(db.getDate()).append(DIVIDER)
                        .append(db.getTime());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try
                        {
                            //发送给服务器
                            mPrintWriter.println(sb.toString());
                            Message message=new Message();
                            message.what=SEND_CODE;
                            message.obj=" "+db.getDate()+" "+db.getTime()+"\n我:"+str+"\n";
                            mHandler.sendMessage(message);

                            //history.append(" "+db.getDate()+" "+db.getTime()+"\n我:"+str+"\n");
                            //scrollView.fullScroll(ScrollView.FOCUS_DOWN); //滚动到底部
                        }
                        catch (Exception e)
                        {
                            // TODO: handle exception
                            //Toast.makeText(ChatActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
                            mHandler.sendEmptyMessage(CONNECT_FAIL);
                            Log.e(DEBUG_TAG, e.toString());
                            e.printStackTrace();
                        }
                        lock=false;
                    }
                }).start();
                while (lock);
            }
        });
        //mThread = new Thread(mRunnable);
        //mThread.start();
    }
    //线程:监听服务器发来的消息
    private Runnable	mRunnable	= new Runnable()
    {
        public void run()
        {
                try
                {
                    while (true)
                    if ( (mStrMSG = mBufferedReader.readLine()) != null )
                    {
                        Log.d("get",mStrMSG);
                        //接收消息并处理
                        Message message=new Message();
                        message.what=GET_CODE;
                        message.obj=mStrMSG;
                        mHandler.sendMessage(message);
                    }
                    // 发送消息
                }
                catch (Exception e)
                {
                    Log.e(DEBUG_TAG, e.toString());
                    e.printStackTrace();
                }

        }
    };

    Handler	mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what){
                case GET_CODE:
                    String tmp=msg.obj.toString();
                    String arr[]=tmp.split(DIVIDER);
                    if(arr.length!=6)break;
                    //将聊天记录添加进来
                    history.append(" "+arr[4]+" "+arr[5]+"\n"+stu_name+":"+arr[3]+"\n");
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN); //滚动到底部
                    break;
                case SEND_CODE:
                    String tmp2=msg.obj.toString();
                    history.append(tmp2);
                    edit.setText("");
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN); //滚动到底部
                    break;
                case CONNECT_FAIL:
                    Toast.makeText(ChatActivity.this, "无法连接服务器", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        try {
            mPrintWriter.close();
            mBufferedReader.close();
            mThread.interrupt();
            mThread.stop();
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
