package com.example.weatherapp321;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.service.wallpaper.WallpaperService;
import android.text.TextUtils;
import android.util.Log;
import android.util.TimeUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp321.adapter.FutureWeatherAdapter;
import com.example.weatherapp321.bean.DayWeatherBean;
import com.example.weatherapp321.bean.WeatherBean;
import com.example.weatherapp321.util.NetUtil;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private AppCompatSpinner mSpinner;
    private ArrayAdapter<String> mSpinnerAdapter;
    private  String[] mCities;

    private TextView tvWeather,tvTem,tvTemLowHigh,tvWin,tvAir;
    private ImageView ivWeather;
    private RecyclerView rlvFutureWeather;
    private FutureWeatherAdapter mWeatherAdapter;
    private  DayWeatherBean toDayWeather;

     //子线程没有Looper，不传入会导致handler默认主线程Looper
    private Handler mHandler = new Handler(Looper.myLooper()){


        //发送消息后会在这里接收到
       @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what==0){
              String weather = (String) msg.obj;
                Log.d("fan","---主线程收到了天气---weather"+weather);
                //检查传入的字符串是否为空，并给用户提示
                if (TextUtils.isEmpty(weather)) {
                    Toast.makeText(MainActivity.this, "没有收到天气数据", Toast.LENGTH_SHORT).show();
                    return;
                }

                //防止一些数据是空的（返回的接口或者名字变了）
                try {
                    //将复杂的字符串解析成了java对象放到了各个bean里面
                    Gson gson =new Gson();
                    WeatherBean weatherBean =gson.fromJson(weather, WeatherBean.class);
                    Log.d("fan","---解析后的数据---weatherBean"+weatherBean.toString());
                    updateUiOfWeather(weatherBean) ;

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }


            }
        }

    };

    //将数据填充到界面上
    private void updateUiOfWeather(WeatherBean weatherBean) {
        //判空
        if (weatherBean ==null){
            return;
        }
        List<DayWeatherBean> dayWeathers = weatherBean.getDayWeathers();

        if (dayWeathers != null && !dayWeathers.isEmpty()){
             toDayWeather = dayWeathers.get(0);
        }

        if (toDayWeather == null){
            Toast.makeText(MainActivity.this, "没有收到今日天气数据", Toast.LENGTH_SHORT).show();
            return;
        }

        tvTem.setText(toDayWeather.getTem2()+"℃");
        tvWeather.setText(toDayWeather.getWea()+"("+toDayWeather.getDate()+")");
        tvTemLowHigh.setText(toDayWeather.getTem1()+"℃"+"~"+toDayWeather.getTem2()+"℃");

        if (toDayWeather.getWin() != null && toDayWeather.getWin().isEmpty()){
            tvWin.setText(toDayWeather.getWin()+toDayWeather.getWinSpeed());
        }
        //防止使用的api没有提供空气数据
        if(toDayWeather.getAir() != null) {
            tvAir.setVisibility(View.VISIBLE);
            tvAir.setText("空气:" + toDayWeather.getAir() + toDayWeather.getAirLevel() + "\n" + toDayWeather.getAirTips());
        }
        else{
            tvAir.setVisibility(View.GONE);
        }
        ivWeather.setImageResource(getImgResOfWeather(toDayWeather.getWeaImg()));
        //移除的前提是不为空
        if (dayWeathers != null && !dayWeathers.isEmpty()) {

            dayWeathers.remove(0);//因为是未来天气，所以去掉当天天气

        }
        //未来天气的部分(用了一个RecyclerView)
        mWeatherAdapter = new FutureWeatherAdapter(this,dayWeathers);
        rlvFutureWeather.setAdapter(mWeatherAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);

        rlvFutureWeather.setLayoutManager(layoutManager);
    }

    //将返回的字符串映射到图片上
    private  int getImgResOfWeather(String weaStr){
        int result=0;

        switch (weaStr){
            case "qing":
                result = R.drawable.qing;
                break;
            case "yin":
                result= R.drawable.yin;
                break;
            case "yu":
                result= R.drawable.xiaoyu;
                break;
            case "yun":
                result= R.drawable.duoyun;
                break;
            case "bingbao":
                result= R.drawable.binbao;
                break;
            case "xue":
                result= R.drawable.daxue;
                break;
            case "lei":
                result= R.drawable.lei;
                break;
            default:
                result= R.drawable.yin;
                break;


        }
        return result;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        initview();

    }
    //初始化下拉页面，用来调下拉城市的细节方面（字体颜色等等）
    private void initview() {
        mSpinner = findViewById(R.id.sp_city);
        //定义的一个城市数组来获取城市资源用于填入下面适配器
        mCities = getResources().getStringArray(R.array.cities);
        //用来改下拉区域背景以及字体颜色的适配器
        mSpinnerAdapter = new ArrayAdapter<>(this,R.layout.sp_item_layout,mCities);
        mSpinner.setAdapter(mSpinnerAdapter);
        //点击事件(用来选城市)
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selectedCity = mCities[position];
                getWeatherOfCity(selectedCity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //绑定各个控件为网络请求做准备
        tvWeather = findViewById(R.id.tv_weather);
        tvAir = findViewById(R.id.tv_air);
        tvTem= findViewById(R.id.tv_tem);
        tvTemLowHigh = findViewById(R.id.tv_low_high);
        tvWin = findViewById(R.id.tv_win);
        ivWeather = findViewById(R.id.iv_weather);
        rlvFutureWeather = findViewById(R.id.rlv_future_weather);
    }

    private void getWeatherOfCity(String selectedCity) {
        //开启子线程并请求网络
        new Thread(new Runnable() {
            @Override
            public void run() {
                //请求网络
                String weatherOfCity = NetUtil.getWeatherOfCity(selectedCity);
                //使用handler将数据传递给主线程
                Message message = Message.obtain();//从消息池拿消息，有利于重用
                message.what=0;
                message.obj = weatherOfCity;


                mHandler.sendMessage(message);

            }
        }).start();
    }


}
