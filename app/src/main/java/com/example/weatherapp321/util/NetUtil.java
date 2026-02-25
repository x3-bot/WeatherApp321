package com.example.weatherapp321.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NetUtil {

    public static final String  URL_WEATHER_WITH_FUTURE ="https://v1.yiketianqi.com/free/week?appid=87482998&appsecret=MSV611JC&unescape=1";


    public static String doGet(String urlStr) {
        String result = "";
        HttpURLConnection connection = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        //连接网络
        //创建的URL可能不规范或者有错误
        try {
            URL url = new URL(urlStr);
            connection =(HttpURLConnection) url.openConnection();
            //获取方法和获取时间
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);

            
            //从连接中读取数据(输入流是二进制）
            InputStream inputStream = connection.getInputStream();
            //对数据流进行加工变得人能看懂
             inputStreamReader = new InputStreamReader(inputStream);
             //先放到一个缓冲区
             bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            //一行行读取
            String line = bufferedReader.readLine();
            while (line != null){
                //把读取的内容拼接起来
                stringBuilder.append(line);
                //进入下一行继续读，防止一直在第一行一直读下去导致的死循环
                line = bufferedReader.readLine();
            }
            result = stringBuilder.toString();

        } catch (IOException e){
            e.printStackTrace();

        }
        //关闭网络连接和输入流以及读取器
        finally {
            if (connection != null){
                connection.disconnect();
            }
            if (inputStreamReader != null){
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            if(bufferedReader != null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return  result;
    }

    public  static   String getWeatherOfCity(String city) {
        //拼接出天气的URL
        String weatherUrl = URL_WEATHER_WITH_FUTURE + "&city=" + city;
        Log.d("fan", "----weatherUrl----" + weatherUrl);
        String weatherResult = doGet(weatherUrl);
        Log.d("fan", "----weatherResult----" + weatherResult);

        return  weatherResult;

    }

}
