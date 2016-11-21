package com.example.yunyao.lab8;

/**
 * Created by yunyao on 2016/11/20.
 */
public class Content {
    private String InfoObjectNameText;      //姓名
    private String InfoObjectBirthdayText; //生日
    private String InfoObjectGiftText;      //礼物
    private String InfoObjectPhoneText;     //号码
    private int flag;                         //是否显示在主界面

    public void setInfoObjectNameText(String temp){ InfoObjectNameText = temp;}
    public void setInfoObjectBirthdayText(String temp){ InfoObjectBirthdayText = temp;}
    public void setInfoObjectGiftText(String temp){ InfoObjectGiftText = temp;}
    public void setInfoObjectPhoneText(String temp){ InfoObjectPhoneText = temp;}
    public void setFlag(int i){flag = i;}

    public String getInfoObjectNameText(){return InfoObjectNameText;}
    public String getInfoObjectBirthdayText(){return InfoObjectBirthdayText;}
    public String getInfoObjectGiftText(){return InfoObjectGiftText;}
    public String getInfoObjectPhoneText(){return InfoObjectPhoneText;}
    public int getFlag(){return flag;}

    Content(){
        InfoObjectNameText = null;
        InfoObjectBirthdayText = null;
        InfoObjectGiftText = null;
        InfoObjectPhoneText = null;
    }
    Content(String Name, String Bir, String Gift, String PN, int flag){
        InfoObjectNameText = Name;
        InfoObjectBirthdayText = Bir;
        InfoObjectGiftText = Gift;
        InfoObjectPhoneText = PN;
        this.flag = flag;
    }
}
