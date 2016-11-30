package com.example.yunyao.lab9;

/**
 * Created by yunyao on 2016/11/30.
 */

public class RecyclerWeather {
    private String Date, Tem, Weather;
    RecyclerWeather(String Date, String Tem, String Weather){
        this.Date = Date;
        this.Tem = Tem;
        this.Weather = Weather;
    }
    String getDate(){return Date;}
    String getTem(){return Tem;}
    String getWeather(){return Weather;}
}
