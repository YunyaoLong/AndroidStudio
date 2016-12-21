package com.example.yunyao.lab9;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String url = "http://ws.webxml.com.cn/WebServices/WeatherWS.asmx/getWeather";
    private static final String WebServiceId = "2ac00d1fc2f34d74b07b4177d12d0431";
    private static final String TooFastInput = "发现错误：免费用户不能使用高速访问。http://www.webxml.com.cn/";
    private static final String TooOftenInput = "发现错误：免费用户24小时内访问超过规定数量。http://www.webxml.com.cn/";
    private static final String NullCity = "查询结果为空";
    private static final String NullCityWeb = "查询结果为空。http://www.webxml.com.cn/";
    private static final int NoUserId = 1, SomethingWrong = 2;

    private static int Static_Flag = 0;
    private static String XMLString = null;
    private static String WebUserId;
    private static TextView CityNameText, City_editText;
    private static Button Search_Button;
    private static TextView XMLtext;
    private static Weather weather;
    private static ListView IndexListView;
    private static TextView City_detail, Current_Time, Temperature, Today_Temrature, Dampness, Air_quality, Wind;
    private static LinearLayout visible_box;
    private static RecyclerView recycler;

    private static SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Static_Flag = 0;
        WebUserId = "";
        bindViews();
        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        City_editText.setText(preferences.getString("CityName", "广州"));
        WebUserId = preferences.getString("WebUserId", "");
        try {
            getWeather();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindViews() {
        CityNameText = (TextView)findViewById(R.id.CityNameText);
        City_editText = (EditText)findViewById(R.id.City_editText);
        Search_Button = (Button)findViewById(R.id.Search_Button);
        IndexListView = (ListView)findViewById(R.id.IndexListView);
        City_detail = (TextView)findViewById(R.id.City_detail);
        Current_Time = (TextView)findViewById(R.id.Current_Time);
        Temperature = (TextView)findViewById(R.id.Temperature);
        Today_Temrature = (TextView)findViewById(R.id.Today_Temrature);
        Dampness = (TextView)findViewById(R.id.Dampness);
        Air_quality = (TextView)findViewById(R.id.Air_quality);
        Wind = (TextView)findViewById(R.id.Wind);
        visible_box = (LinearLayout)findViewById(R.id.visible_box);
        recycler = (RecyclerView)findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recycler.setLayoutManager(layoutManager);

        Search_Button.setOnClickListener(this);
        CityNameText.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Search_Button:
                try {
                    //输入框不能为空
                    if (City_editText.getText().toString().equals("")){
                        Toast.makeText(MainActivity.this, "城市名字不能为空！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //检查是不是有网络
                    if (!isNetworkConnected(MainActivity.this)){
                        Toast.makeText(MainActivity.this, "当前没有可用网络！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("CityName", City_editText.getText().toString());
                    editor.apply();
                    //开始网络连接
                    Static_Flag = 0;
                    getWeather();
                    Log.i("OnClickDebug", weather.City+Static_Flag);
                    //如果网络连接中出现了某些问题，将错误信息和一个存放在weather.City中的String变量进行比对，检查错误原因

                    Log.i("OnClickDebug", weather.City);
                    if (weather.City.indexOf("高速访问") != -1){
                        Log.i("OnClickDebug", "高速访问");
                        Toast.makeText(MainActivity.this, "您的点击速度过快，二次查询间隔<600ms", Toast.LENGTH_SHORT).show();
                    }else if(weather.City.indexOf("超过规定") != -1){
                        Log.i("OnClickDebug", "超过次数");
                        Toast.makeText(MainActivity.this, "免费用户24小时内访问超过规定数量50次", Toast.LENGTH_SHORT).show();
                    }else if(weather.City.equals(NullCity.toString()) || weather.City.equals(NullCityWeb.toString())){
                    //注册用户和非注册用户返回的XML文件不一样，导致有两种判断情况
                        Log.i("OnClickDebug", "无城市");
                        Toast.makeText(MainActivity.this, "当前城市不存在，请重新输入", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.i("Click_Debug", e.getMessage());
                }
                break;
            case R.id.CityNameText:
                //切换5天和7天
                if (WebUserId.equals("")){
                    WebUserId = WebServiceId.toString();
                    Toast.makeText(MainActivity.this, "切换为7天", Toast.LENGTH_SHORT).show();
                }
                else {
                    WebUserId = "";
                    Toast.makeText(MainActivity.this, "切换为5天", Toast.LENGTH_SHORT).show();
                }
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("WebUserId", WebUserId);
                editor.apply();
                break;
        }
    }

    public void getWeather() throws Exception {
        new Thread(runable).start();
        Log.i("getWeather", weather.City+Static_Flag);
    }

	private void parserXML(InputStream instream) throws Exception {

		String[] weatherData = new String[52];
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(instream, "UTF-8");
		int event = parser.getEventType();
		int i = 0;
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_TAG:
				if ("string".equals(parser.getName())) {
                    //讲每一个标签内的内容再次进行字符串的切割，方便之后的debug
                    String temp[] = parser.nextText().split("[；]");
                    for(String temptemp : temp){
                        if (temptemp.equals("") || temptemp.equals("\n")) continue;
                        weatherData[i] = temptemp;
                        Log.i("parserXML"+i, weatherData[i]);
                        i++;
                    }
				}
				break;
			case XmlPullParser.END_TAG:
				if ("string".equals(parser.getName())) {
				}
				break;
			}
			event = parser.next();
		}
        //如果返回的XML只有一个标签，就说明城市名称出错，点击按钮频率过快，每天免费查询次数用尽
        if(i == 1) Static_Flag = SomethingWrong;
        //如果返回的XML有34个标签，就说明本次查询没有使用ID
        else if (i == 34) Static_Flag = NoUserId;
        Log.i("parserXML", "Flag设置完成" + Static_Flag);
		weather = new Weather();

        //将第一个标签内的信息存进weather.City中，方便之后进行debug操作
        weather.City = weatherData[0];
        if(Static_Flag == SomethingWrong){
            //如果Static_Flag显示本次访问出现了错误，就直接return，否则会出现数组越界报错
            Log.i("ParserXMLDebug", "不正常"+weather.City);
            return;
        }
        //如果正常获取了XML文件
        weather.City_detail = weatherData[1]; weather.City_code = weatherData[2];
        weather.Current_Time = weatherData[3].split(" ")[1]+" 更新"; weather.Temperature = weatherData[4].split("：")[2]; weather.Wind = weatherData[5].split("：")[1];
        weather.Dampness = weatherData[6];weather.Ult_Air = weatherData[7].split("。")[1];weather.Index = weatherData[8];
        weather.Day1_Date = weatherData[9].split(" ")[0];weather.Day1_Temrature = weatherData[10];weather.Day1_Weather = weatherData[11];
        weather.Day2_Date = weatherData[14].split(" ")[0];weather.Day2_Temrature = weatherData[15];weather.Day2_Weather = weatherData[16];
        weather.Day3_Date = weatherData[19].split(" ")[0];weather.Day3_Temrature = weatherData[20];weather.Day3_Weather = weatherData[21];
        weather.Day4_Date = weatherData[24].split(" ")[0];weather.Day4_Temrature = weatherData[25];weather.Day4_Weather = weatherData[26];
        weather.Day5_Date = weatherData[29].split(" ")[0];weather.Day5_Temrature = weatherData[30];weather.Day5_Weather = weatherData[31];
        if (Static_Flag == NoUserId){
            //如果没有使用注册的ID功能，就直接将第6/7天设置为空，否则会出现数组越界
            weather.Day6_Date = "";weather.Day6_Temrature = "";weather.Day6_Weather = "";
            weather.Day7_Date = "";weather.Day7_Temrature = "";weather.Day7_Weather = "";
            return;
        }
        //如果使用了注册的ID，就继续往下获取第6天和第7天
        weather.Day6_Date = weatherData[34].split(" ")[0];weather.Day6_Temrature = weatherData[35];weather.Day6_Weather = weatherData[36];
        weather.Day7_Date = weatherData[39].split(" ")[0];weather.Day7_Temrature = weatherData[40];weather.Day7_Weather = weatherData[41];
        Log.i("Debug_weather", weather.toString());
	}

    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            //获取手机当前所有的联网硬件
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            //如果存放联网硬件的数组为空，就说明手机未联网
            if (mNetworkInfo != null) {
                return true;
            }
        }
        return false;
    }
    Runnable runable = new Runnable() {
        @Override
        public void run() {
            // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
            try {
                Log.i("runableDebug", "Runable");
                //String Path = "http://www.webxml.com.cn/WebServices/WeatherWebService.asmx/getWeatherbyCityName?theCityName=";
                //URL url = new URL(Path + java.net.URLEncoder.encode(CityName, "UTF-8"));
                //HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                //建立HTTP协议，连接互联网
                HttpURLConnection connection = null;
                connection = (HttpURLConnection) (new URL(url.toString()).openConnection());
                connection.setConnectTimeout(8000);
                connection.setReadTimeout(8000);
                connection.setRequestMethod("POST");
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                String request = URLEncoder.encode(City_editText.getText().toString(), "utf-8");
                out.writeBytes("theCityCode=" + request + "&theUserID=" + WebUserId);

                //获得XML文件，存进instream
                InputStream instream = connection.getInputStream();

                //解析xml文件
                parserXML(instream);
                
                if(Static_Flag == SomethingWrong) {
                    Log.i("runableDebug", "有点问题"+Static_Flag);
                    return;
                }

                Log.i("runableDebug", "解析完成");
                handler.sendMessage(new Message());

                //将instream转成string
                /*
                BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
                StringBuilder response = new StringBuilder();
                String line;

                while((line = reader.readLine()) != null){
                    response.append(line);
                    Log.i("response", line);
                }
                XMLString = response.toString();
                handler.sendMessage(new Message());
                Log.i("reader", XMLString);
                */

                //Toast.makeText(MainActivity.this, XMLString, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                //Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();;
            }
        }
    };
    //定义一个Handler用来更新页面：
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Log.i("HandlerDebug", weather.Day5_Date);
            //XMLtext.setText("结果显示：\n" + XMLString);
            String[] strings = {"title","content"};//Map的key集合数组
            int[] ids = {R.id.Index_title,R.id.Index_content};//对应布局文件的id
            SimpleAdapter simpleAdapter = new SimpleAdapter(MainActivity.this, getData(), R.layout.list_item, strings, ids);
            IndexListView.setAdapter(simpleAdapter);//绑定适配器
            //RecycleAdapter的适配器
            RecycleAdapter recycleAdapter = new RecycleAdapter(MainActivity.this, getRecyclerDate());
            recycler.setAdapter(recycleAdapter);

            //更新其他的TextView
            City_detail.setText(weather.City_detail);
            Current_Time.setText(weather.Current_Time);
            Temperature.setText(weather.Temperature);
            Today_Temrature.setText(weather.Day1_Temrature);
            if (Static_Flag == NoUserId) Today_Temrature.setText(weather.Day2_Temrature);
            Dampness.setText(weather.Dampness);
            Air_quality.setText(weather.Ult_Air);
            Wind.setText(weather.Wind);
            visible_box.setVisibility(View.VISIBLE);
            Toast.makeText(MainActivity.this, "获取天气信息成功", Toast.LENGTH_SHORT).show();
        }
        private List<Map<String, Object>> getData() {
            // 新建一个集合类，用于存放多条数据
            ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            Map<String, Object> map = null;
            String temp[] = weather.Index.split("[：。\n]");
            //有五个指数信息
            for(int i = 0; i<5; ++i){
                map = new HashMap<>();
                //每3次会有一个空字符串，要将其过滤掉
                map.put("title", temp[i*3+0]);
                map.put("content", temp[i*3+1]);
                list.add(map);
            }
            return list;
        }
        private ArrayList<RecyclerWeather> getRecyclerDate(){
            ArrayList<RecyclerWeather> list = new ArrayList<RecyclerWeather>();
            list.add(new RecyclerWeather(weather.Day1_Date, weather.Day1_Temrature, weather.Day1_Weather));
            list.add(new RecyclerWeather(weather.Day2_Date, weather.Day2_Temrature, weather.Day2_Weather));
            list.add(new RecyclerWeather(weather.Day3_Date, weather.Day3_Temrature, weather.Day3_Weather));
            list.add(new RecyclerWeather(weather.Day4_Date, weather.Day4_Temrature, weather.Day4_Weather));
            list.add(new RecyclerWeather(weather.Day5_Date, weather.Day5_Temrature, weather.Day5_Weather));
            //Log.i("getRecyclerDateDebug", weather.Day5_Date);
            //如果没有使用了注册ID，就只有5天的天气信息，我们也不需要Day6和Day7的信息（这两天的信息被我们设置为空）
            if(Static_Flag == NoUserId){ return list;}
            list.add(new RecyclerWeather(weather.Day6_Date, weather.Day6_Temrature, weather.Day6_Weather));
            list.add(new RecyclerWeather(weather.Day7_Date, weather.Day7_Temrature, weather.Day7_Weather));
            return list;
        }
    };
}
