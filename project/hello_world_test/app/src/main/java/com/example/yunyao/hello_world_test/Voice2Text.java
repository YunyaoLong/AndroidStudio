/**
 * 目前该代码实现有问题，禁用
 */

package com.example.yunyao.hello_world_test;

import android.content.Context;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by yunyao on 2017/4/15.
 */

public class Voice2Text {
    private Context context;

    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

    public String text_out;
    Voice2Text(Context context){
        this.context = context;
        text_out = "";
    }
    public String getText(){
        return text_out;
    }
    public void change() {
        //1. 创建RecognizerDialog对象
        RecognizerDialog mDialog = new RecognizerDialog(context, new MyInitListener());
        //2. 设置accent、 language等参数
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");// 设置中文
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        // 若要将UI控件用于语义理解，必须添加以下参数设置，设置之后 onResult回调返回将是语义理解
        // 结果
        // mDialog.setParameter("asr_sch", "1");
        // mDialog.setParameter("nlp_version", "2.0");
        //3.设置回调接口
        MyRecognizerDialogListener myreco = new MyRecognizerDialogListener();
        mDialog.setListener(myreco);
        text_out = myreco.getText();

        if (text_out!=null){
            Log.d("text_out", text_out);
        }else {
            Log.d("text_out", "others");
        }
        //4. 显示dialog，接收语音输入
        mDialog.show();

    }
    class MyInitListener implements InitListener {

        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                showTip(context, "初始化失败 ");
            }
        }
    }

    private void showTip (Context context, String data) {
        Toast.makeText( context, data, Toast.LENGTH_SHORT).show() ;
    }

    class MyRecognizerDialogListener implements RecognizerDialogListener {

        /**
         * @param results
         * @param isLast  是否说完了
         */
        String s;
        String getText(){
            if (s != null){
                Log.d("getText_text_out", s);
            }
            return  s;
        }
        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            String result = results.getResultString(); //为解析的
            showTip(context, result);
            Log.d(" 没有解析的 :", result);
            String text_temp = JsonParser.parseIatResult(result);//解析过后的
            Log.d(" 解析后的 :", text_temp);

            String sn = null;
            // 读取json结果中的 sn字段
            try {
                JSONObject resultJson = new JSONObject(results.getResultString());
                sn = resultJson.optString("sn");
            } catch (JSONException e) {
                //e.printStackTrace();
                System.out.println(e.getMessage().toString());
            }

            mIatResults.put(sn, text_temp);//没有得到一句，添加到

            StringBuffer resultBuffer = new StringBuffer();
            for (String key : mIatResults.keySet()) {
                resultBuffer.append(mIatResults.get(key));
            }

            s = resultBuffer.toString();
            //if (text_out!=null){
            //    Log.d("text_out", text_out);
            //}else {
            //    Log.d("text_out", "others");
            //}
            //et_input.setText(resultBuffer.toString());// 设置输入框的文本
            //et_input.setSelection(et_input.length());//把光标定位末尾

        }

        @Override
        public void onError(SpeechError speechError) {

        }
    }
}
