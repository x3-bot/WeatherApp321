package com.example.weatherapp321.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp321.R;
import com.example.weatherapp321.bean.DayWeatherBean;
import com.example.weatherapp321.bean.WeatherBean;

import java.util.List;

public class FutureWeatherAdapter extends RecyclerView.Adapter <FutureWeatherAdapter.WeatherViewHolder>{
    //viewHolder里面的一些数据
    private Context mcontext;//上下文
    private List<DayWeatherBean> mWeatherBeans;


    //方便onBindViewHolder能访问到这个上下文和拿到的数据
    public FutureWeatherAdapter(Context mcontext,List<DayWeatherBean> weatherBeans) {
        this.mcontext = mcontext;
        this.mWeatherBeans = weatherBeans;
    }

    //把提前写的布局文件弄过来
    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

      View view =  LayoutInflater.from(mcontext).inflate(R.layout.weather_item_layout,parent,false);
        WeatherViewHolder weatherViewHolder = new WeatherViewHolder(view);



        return weatherViewHolder;
    }


    //用来将mWeatherBeans的数据填到控件上
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
      //拿到当前这个位置的javaBean对象
      DayWeatherBean weatherBean =  mWeatherBeans.get(position);
      //对holder里面的控件设置文本
        holder.tvWeather.setText(weatherBean.getWea());
        holder.tvTem.setText(weatherBean.getTem2()+"℃");
        holder.tvTemLowHigh.setText(weatherBean.getTem1()+"℃"+"~"+weatherBean.getTem2()+"℃");
        holder.tvWin.setText(weatherBean.getWin()+weatherBean.getWinSpeed());
        if( weatherBean.getAir() != null) {
            holder.tvAir.setVisibility(View.VISIBLE);
            holder.tvAir.setText("空气:" + weatherBean.getAir() + weatherBean.getAirLevel());
        }
        else{
            holder.tvAir.setVisibility(View.GONE);
        }
        holder.tvDate.setText(weatherBean.getDate());
        holder.ivWeather.setImageResource(getImgResOfWeather(weatherBean.getWeaImg()));


    }

    //用于表示多少条数据要显示到RecyclerView列表
    @Override
    public int getItemCount() {
        if (mWeatherBeans == null) {
          return 0;
        }
        return mWeatherBeans.size();
    }
    //统一把控件放到WeatherViewHolder里面，避免重复创建这些，弄在这里就直接复用
    //为了上面能直接访问到ViewHolder里面的那些控件（布置成主布局那样）
    class WeatherViewHolder extends  RecyclerView.ViewHolder{
        TextView tvWeather,tvTem,tvTemLowHigh,tvWin,tvAir,tvDate;
        ImageView ivWeather;
        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWeather = itemView.findViewById(R.id.tv_weather);
            tvAir = itemView.findViewById(R.id.tv_air);
            tvTem=itemView.findViewById(R.id.tv_tem);
            tvTemLowHigh = itemView.findViewById(R.id.tv_low_high);
            tvWin =itemView.findViewById(R.id.tv_win);
            tvDate = itemView.findViewById(R.id.tv_date);

            ivWeather = itemView.findViewById(R.id.iv_weather);

        }
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
}
