package com.example.yunyao.hello_world_test;

/**
 * Created by yunyao on 2017/4/15.
 */

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class ButlerActivity  extends AppCompatActivity implements View.OnClickListener  {

    private ChatListviewAdapter adapter = null;

    private ListView listview;
    private Button btn_left;
    private EditText et_meg;
    private Button btn_right;


    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_butler) ;

        initActivity();
        initSpeech();
    }

    private void initActivity() {
        listview = (ListView) findViewById(R.id.listview);
        btn_left = (Button) findViewById(R.id.btn_left);
        et_meg = (EditText) findViewById(R.id.et_meg);
        btn_right = (Button) findViewById(R.id.btn_right);

        btn_left.setOnClickListener(this);
        btn_right.setOnClickListener(this);

        adapter = new ChatListviewAdapter(this);
        listview.setAdapter(adapter);
    }

    private void initSpeech() {
        // 将“12345678”替换成您申请的 APPID，申请地址： http://www.xfyun.cn
        // 请勿在 “ =”与 appid 之间添加任务空字符或者转义符
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=58f11662");
    }

    @Override
    public void onClick(View v) {

        String msg = et_meg.getText().toString().trim();

        switch (v.getId()) {
            case R.id.btn_left:
                et_meg.setText("");
                //startSpeechDialog();
                WorkThread workThread1 = new WorkThread();
                workThread1.run();
                try {
                    workThread1.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_right:
                Text2Voice text2Voice = new Text2Voice(ButlerActivity.this, msg);
                text2Voice.change();

                adapter.addDataToAdapter(new MsgInfo(null, msg));
                adapter.notifyDataSetChanged();

                break;
        }

        listview.smoothScrollToPosition(listview.getCount() - 1);

        et_meg.setText("");

    }

    public class WorkThread extends Thread {
        @Override
        public void run() {
            startSpeechDialog();
        }
    }

    private void startSpeechDialog() {
        //1. 创建RecognizerDialog对象
        RecognizerDialog mDialog = new RecognizerDialog(this, new MyInitListener());
        //2. 设置accent、 language等参数
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");// 设置中文
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        // 若要将UI控件用于语义理解，必须添加以下参数设置，设置之后 onResult回调返回将是语义理解
        // 结果
        // mDialog.setParameter("asr_sch", "1");
        // mDialog.setParameter("nlp_version", "2.0");
        //3.设置回调接口
        mDialog.setListener(new MyRecognizerDialogListener());
        //4. 显示dialog，接收语音输入
        mDialog.show();
    }

    class MyInitListener implements InitListener {

        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败 ");
            }

        }
    }

    class MyRecognizerDialogListener implements RecognizerDialogListener {

        /**
         * @param results
         * @param isLast  是否说完了
         */
        private int count = 0;
        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            String result = results.getResultString(); //为解析的
            showTip(result);
            System.out.println(" 没有解析的 :" + result);

            String text = JsonParser.parseIatResult(result);//解析过后的
            System.out.println(" 解析后的 :" + text);

            String sn = null;
            // 读取json结果中的 sn字段
            try {
                JSONObject resultJson = new JSONObject(results.getResultString());
                sn = resultJson.optString("sn");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mIatResults.put(sn, text);//没有得到一句，添加到

            StringBuffer resultBuffer = new StringBuffer();
            for (String key : mIatResults.keySet()) {
                resultBuffer.append(mIatResults.get(key));
            }

            et_meg.setText(resultBuffer.toString());// 设置输入框的文本
            et_meg.setSelection(et_meg.length());//把光标定位末尾

            count++;

            if (count%2 != 0){
                adapter.addDataToAdapter(new MsgInfo(et_meg.getText().toString(),null));
                adapter.notifyDataSetChanged();
                et_meg.setText("");
            }else{
                et_meg.setText("");
            }
        }

        @Override
        public void onError(SpeechError speechError) {

        }
    }



    // 听写监听器
    private RecognizerListener mRecoListener = new RecognizerListener() {
        // 听写结果回调接口 (返回Json 格式结果，用户可参见附录 13.1)；
        //一般情况下会通过onResults接口多次返回结果，完整的识别内容是多次结果的累加；
        //关于解析Json的代码可参见 Demo中JsonParser 类；
        //isLast等于true 时会话结束。
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.e("true", results.getResultString());
            System.out.println(results.getResultString());
            showTip(results.getResultString());
        }

        // 会话发生错误回调接口
        public void onError(SpeechError error) {
            showTip(error.getPlainDescription(true));
            // 获取错误码描述
            Log.e("error", "error.getPlainDescription(true)==" + error.getPlainDescription(true));
        }

        // 开始录音
        public void onBeginOfSpeech() {
            showTip(" 开始录音 ");
        }

        //volume 音量值0~30， data音频数据
        public void onVolumeChanged(int volume, byte[] data) {
            showTip(" 声音改变了 ");
        }

        // 结束录音
        public void onEndOfSpeech() {
            showTip(" 结束录音 ");
        }

        // 扩展用接口
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        }
    };

    private void showTip(String data) {
        Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
    }

}
