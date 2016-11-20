package com.example.yunyao.lab8;

/**
 * Created by yunyao on 2016/11/20.
 */
public class myDB {
    private String InfoObjectNameText;      //姓名
    private String InfoObjectBirthdayText; //生日
    private String InfoObjectGiftText;      //礼物
    private String InfoObjectPhoneText;     //号码

    public void setInfoObjectNameText(String temp){ InfoObjectNameText = temp;}
    public void setInfoObjectBirthdayText(String temp){ InfoObjectBirthdayText = temp;}
    public void setInfoObjectGiftText(String temp){ InfoObjectGiftText = temp;}
    public void setInfoObjectPhoneText(String temp){ InfoObjectPhoneText = temp;}

    public String getInfoObjectNameText(){return InfoObjectNameText;}
    public String getInfoObjectBirthdayText(){return InfoObjectBirthdayText;}
    public String getInfoObjectGiftText(){return InfoObjectGiftText;}
    public String getInfoObjectPhoneText(){return InfoObjectPhoneText;}

    myDB(){
        InfoObjectNameText = null;
        InfoObjectBirthdayText = null;
        InfoObjectGiftText = null;
        InfoObjectPhoneText = null;
    }
    myDB(String Name, String Bir, String Gift, String PN){
        InfoObjectNameText = Name;
        InfoObjectBirthdayText = Bir;
        InfoObjectGiftText = Gift;
        InfoObjectPhoneText = PN;
    }
}
