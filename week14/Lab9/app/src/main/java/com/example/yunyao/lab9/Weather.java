package com.example.yunyao.lab9;

import java.io.Serializable;

/**
 * Created by yunyao on 2016/11/30.
 */

public class Weather  {
    public String City;//城市名称
    public String City_detail;//城市名称_具体
    public String City_code;//城市代码
    public String Current_Time;//查询时间
    public String Temperature;//温度
    public String Wind;//风力
    public String Dampness;//湿度
    public String Ult_Air;//紫外线强度
    public String Index;
    public String Ult_Index;//紫外线指数
    public String Cold_Index;//感冒指数
    public String Dressing_Index;//穿衣指数
    public String Washing_Index;//洗车指数
    public String Exercise_Index;//运动指数
    public String Air_quality;//空气质量
    public String Day1_Date;//今日日期
    public String Day1_Weather;//今日天气
    public String Day1_Temrature;//今日气温
    public String Day2_Date;//明天日期
    public String Day2_Weather;//明天天气
    public String Day2_Temrature;//明天气温
    public String Day3_Date;//后天日期
    public String Day3_Weather;//后天天气
    public String Day3_Temrature;//后天气温
    public String Day4_Date;//后天日期
    public String Day4_Weather;//后天天气
    public String Day4_Temrature;//后天气温
    public String Day5_Date;//后天日期
    public String Day5_Weather;//后天天气
    public String Day5_Temrature;//后天气温
    public String Day6_Date;//后天日期
    public String Day6_Weather;//后天天气
    public String Day6_Temrature;//后天气温
    public String Day7_Date;//后天日期
    public String Day7_Weather;//后天天气
    public String Day7_Temrature;//后天气温

    public String toString() {
        return "WeatherBean [City=" + City + ", City_detail=" + City_detail + ", City_code=" + City_code
                + ", Current_Time=" + Current_Time + ", Temperature=" + Temperature + ", Wind=" + Wind
                + ", Dampness=" + Dampness + ", Ult_Index=" + Ult_Index + ", Air_quality" + Air_quality + ", Cold_Index=" + Cold_Index
                + ", Dressing_Index=" + Dressing_Index + ", Washing_Index=" + Washing_Index
                + ", Exercise_Index=" + Exercise_Index + "]";
    }

}
