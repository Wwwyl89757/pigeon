package view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import bean.Weather;

/**
 * Created by Administrator on 2016/12/27.
 */

public class WeatherView extends View {


    Paint paint;
    Weather[] w;
    boolean isSet = false;
    public WeatherView(Context context,  AttributeSet attrs){
        super(context,attrs);
        paint = new Paint();
        paint.setAntiAlias(true);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.drawColor(Color.GRAY);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setTextSize(25);
        for(int i=1;i<7;i++){
            canvas.drawLine(canvas.getWidth()/9,canvas.getHeight()-i*canvas.getHeight()/7,canvas.getWidth()-canvas.getWidth()/9,getHeight()-i*canvas.getHeight()/7,paint);
            canvas.drawText(-20+(i*10)+"℃", canvas.getWidth()/9-paint.measureText(-20+(i*10)+"℃"), canvas.getHeight()-i*canvas.getHeight()/7, paint);
        }

        canvas.drawLine(canvas.getWidth()/9,canvas.getHeight()-canvas.getHeight()/7,canvas.getWidth()/9,canvas.getHeight()/7,paint);

        if(isSet){
            Path pathHigh = new Path();
            for(int i=0;i<w.length;i++){
                if(i==0){
                    pathHigh.moveTo(2*canvas.getWidth()/9, -w[i].getHigh()*canvas.getHeight()/70+canvas.getHeight()*5/7);
                }else{
                    pathHigh.lineTo((i+2)*canvas.getWidth()/9, -w[i].getHigh()*canvas.getHeight()/70+canvas.getHeight()*5/7);
                }
                canvas.drawText(w[i].getDateInt()+"日", (i+2)*canvas.getWidth()/9-paint.measureText(w[i].getDateInt()+"日")/2, 6*canvas.getHeight()/7+paint.getTextSize(), paint);
                canvas.drawText(w[i].getType(), (i+2)*canvas.getWidth()/9-paint.measureText(w[i].getType())/2, 6*canvas.getHeight()/7+paint.getTextSize()*2, paint);
            }
            canvas.drawPath(pathHigh,paint);
            Path pathLow = new Path();
            for(int i=0;i<w.length;i++){
                if(i==0){
                    pathLow.moveTo(2*canvas.getWidth()/9, -w[i].getLow()*canvas.getHeight()/70+canvas.getHeight()*5/7);
                }else{
                    pathLow.lineTo((i+2)*canvas.getWidth()/9, -w[i].getLow()*canvas.getHeight()/70+canvas.getHeight()*5/7);
                }
            }
            canvas.drawPath(pathLow,paint);
            canvas.drawText("高温", 7*canvas.getWidth()/9+5, -w[w.length-1].getHigh()*canvas.getHeight()/70+canvas.getHeight()*5/7+paint.getTextSize()/2, paint);
            canvas.drawText("低温", 7*canvas.getWidth()/9+5, -w[w.length-1].getLow()*canvas.getHeight()/70+canvas.getHeight()*5/7+paint.getTextSize()/2, paint);
        }

    }


    public void setWeatherInfo(Weather[] w){
        this.w = w;
        isSet = true;
        this.invalidate();
    }
}
