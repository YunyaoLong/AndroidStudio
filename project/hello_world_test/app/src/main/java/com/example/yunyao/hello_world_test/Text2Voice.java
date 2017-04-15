package com.example.yunyao.hello_world_test;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

/**
 * Created by yunyao on 2017/4/15.
 */

public class Text2Voice {
    private Context context;
    private String text;
    Text2Voice(Context context, String text){
        this.context = context;
        this.text = text;
    }
    public void change(){
        //1.创建SpeechSynthesizer对象, 第二个参数：本地合成时传InitListener
        SpeechSynthesizer mTts= SpeechSynthesizer.createSynthesizer(context, null);
        //2.合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer 类
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");//设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
        //设置合成音频保存位置（可自定义保存位置），保存在“./sdcard/iflytek.pcm”
        //保存在SD卡需要在AndroidManifest.xml添加写SD卡权限
        //如果不需要保存合成音频，注释该行代码
        //mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/iflytek.pcm");
        //3.开始合成
        mTts.startSpeaking(text, new MySynthesizerListener());
        //合成监听器
        SynthesizerListener mSynListener = new SynthesizerListener(){
            //会话结束回调接口，没有错误时，error为null
            public void onCompleted(SpeechError error) {}
            //缓冲进度回调
            //percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在文本中结束位置，info为附加信息。
            public void onBufferProgress(int percent, int beginPos, int endPos, String info) {}
            //开始播放
            public void onSpeakBegin() {}
            //暂停播放
            public void onSpeakPaused() {}
            //播放进度回调
            //percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在文本中结束位置.
            public void onSpeakProgress(int percent, int beginPos, int endPos) {}
            //恢复播放回调接口
            public void onSpeakResumed() {}
            //会话事件回调接口
            public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {}
        };
    }
    class MySynthesizerListener implements SynthesizerListener {

        @Override
        public void onSpeakBegin() {
            showTip(context, " 开始播放 ");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos ,
                                     String info) {
            // 合成进度
        }

        @Override
        public void onSpeakPaused() {
            showTip(context, " 暂停播放 ");
        }

        @Override
        public void onSpeakResumed() {
            showTip(context, " 继续播放 ");
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
        }

        @Override
        public void onCompleted(SpeechError speechError) {
            if (speechError == null) {
                showTip(context, "播放完成 ");
            } else if (speechError != null ) {
                showTip(context, speechError.getPlainDescription( true));
            }
        }

        public void onCompleted(Context context, SpeechError error) {
            if (error == null) {
                showTip(context, "播放完成 ");
            } else if (error != null ) {
                showTip(context, error.getPlainDescription( true));
            }
        }

        @Override
        public void onEvent(int eventType, int arg1 , int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话 id，当业务出错时将会话 id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话 id为null
            //if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //     String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //     Log.d(TAG, "session id =" + sid);
            //}
        }
    }
    private void showTip (Context context, String data) {
        Toast.makeText( context, data, Toast.LENGTH_SHORT).show() ;
    }
}

