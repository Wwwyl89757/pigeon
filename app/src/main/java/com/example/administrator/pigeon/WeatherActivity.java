package com.example.administrator.pigeon;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import bean.Weather;
import view.WeatherView;

public class WeatherActivity extends AppCompatActivity {

    @ViewInject(R.id.weatherview)
    WeatherView weatherView;
    @ViewInject(R.id.edit_weather)
    EditText edit_weather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        ViewUtils.inject(this);
    }

    @OnClick(R.id.button_searchcity)
    public void seacherCity(View view){
        String city = edit_weather.getText().toString().trim();
        if (city == null){
            Toast.makeText(this,"请输入城市",Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            new AsyncTask<String, Void, String>() {
                @Override
                protected String doInBackground(String... params) {
                    try {
                        URL url = new URL(params[0]);
                        HttpURLConnection con = (HttpURLConnection) url
                                .openConnection();
                        InputStream is = con.getInputStream();
                        byte[] b = new byte[1024];
                        StringBuffer buf = new StringBuffer();
                        int len = 0;
                        while ((len = is.read(b)) != -1) {
                            String str = new String(b, 0, len);
                            buf.append(str);
                        }
                        return buf.toString();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    try {
                        String desc = (new JSONObject(s)).getString("desc");
                        if ( desc.equals("OK")){
                            Weather[] w = doJson(s);
                            weatherView.setWeatherInfo(w);
                        }else {
                            Toast.makeText(WeatherActivity.this,desc,Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                private Weather[] doJson(String s) {
                    Weather[] w = new Weather[6];
                    try {
                        JSONObject obj = new JSONObject(s);
                        JSONObject dataObj = obj.getJSONObject("data");
                        JSONObject yesObj = dataObj.getJSONObject("yesterday");
                        String date01 = yesObj.getString("date");
                        String high01 = yesObj.getString("high");
                        String low01 = yesObj.getString("low");
                        String type01 = yesObj.getString("type");
                        String[] highs01 = high01.replace("℃", "").split(" ");
                        String[] lows01 = low01.replace("℃", "").split(" ");
                        w[0] = new Weather(date01, Integer.valueOf(lows01[1]),
                                Integer.valueOf(highs01[1]), type01);
                        JSONArray foreArray = dataObj.getJSONArray("forecast");
                        for (int i = 0; i < foreArray.length(); i++) {
                            JSONObject foreObj = foreArray.getJSONObject(i);
                            String[] highs = foreObj.getString("high")
                                    .replace("℃", "").split(" ");
                            String[] lows = foreObj.getString("low")
                                    .replace("℃", "").split(" ");
                            int high = Integer.valueOf(highs[1]);
                            int low = Integer.valueOf(lows[1]);
                            String type = foreObj.getString("type");
                            String date = foreObj.getString("date");
                            w[i + 1] = new Weather(date, low, high, type);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return w;
                }
            }.execute("http://wthrcdn.etouch.cn/weather_mini?city="
                    + URLEncoder.encode(city, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
