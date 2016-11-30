package com.example.yunyao.lab9;

import android.content.Context;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransport;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by yunyao on 2016/11/30.
 */
/*
public class WeatherService {
    String CityName;
    Context context;
    private static final String url = "http://ws.webxml.com.cn/WebServices/WeatherWS.asmx/getWeather";
    private String URL = "http://www.webxml.com.cn/WebServices/WeatherWebService.asmx";
    private String NAMESPACE = "http://WebXml.com.cn/";
    private String METHOD_NAME = "getWeatherbyCityName";
    private String SOAP_ACTION = "http://WebXml.com.cn/getWeatherbyCityName";
    private static final String WebServiceId = "2ac00d1fc2f34d74b07b4177d12d0431";
    WeatherService(String CityName, Context context){
        this.CityName = CityName;
        this.context = context;
    }



    public SoapObject getSoapObject()  throws IOException, XmlPullParserException {
        SoapObject result = null;
        SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
        rpc.addProperty("theCityName", CityName);
        HttpTransport ht = new HttpTransport(URL);

        ht.debug = true;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.bodyOut = rpc;
        envelope.dotNet = true;
        envelope.setOutputSoapObject(rpc);

        ht.call(SOAP_ACTION, envelope);
        result = (SoapObject) envelope.getResponse();
        return result;
    }
    public String parserWeatherData(SoapObject rr) {

        if (rr != null) {
            int count = rr.getPropertyCount();
            return rr.getProperty(count - 1).toString();
        }
        return null;
    }
    public Weather getWeather() throws Exception {
        //String Path = "http://www.webxml.com.cn/WebServices/WeatherWebService.asmx/getWeatherbyCityName?theCityName=";
        //URL url = new URL(Path + java.net.URLEncoder.encode(CityName, "UTF-8"));
        //HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) (new URL(url.toString()).openConnection());
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            connection.setRequestMethod("POST");
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            String request = URLEncoder.encode(CityName, "utf-8");
            out.writeBytes("theCityCode="+request+"&theUserID=");

            InputStream instream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
            StringBuilder response = new StringBuilder();
            String line;

            while((line = reader.readLine()) != null){
                response.append(line);
            }
            return parserXML(instream);
        }catch (Exception e){
            Toast.makeText(context, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }

        return null;
    }
    private Weather parserXML(InputStream instream) throws Exception {

        String[] weatherData = new String[23];
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(instream, "UTF-8");
        int event = parser.getEventType();
        int i = 0;
        while (event != XmlPullParser.END_DOCUMENT) {
            switch (event) {
                case XmlPullParser.START_TAG:
                    if ("string".equals(parser.getName())) {
                        weatherData[i++] = parser.nextText();
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if ("string".equals(parser.getName())) {

                    }
                    break;
            }
            event = parser.next();
        }

        Weather weatherBean = new Weather();
        weatherBean.City = weatherData[1];
        String[] ss = weatherData[10].split("；");
        String[] ssT = ss[0].split("：");
        weatherBean.Temperature = ssT[2];
        int length = ss.length;
        for (int j = 0; j < length; j++) {
            Log.d("weatherService", ss[j]);
        }

        return weatherBean;

    }

    private byte[] read(InputStream instream) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = instream.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
        instream.close();
        return out.toByteArray();
    }
}
*/