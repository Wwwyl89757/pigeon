package bean;

/**
 * Created by Administrator on 2016/12/27.
 */

public class Weather {
    private String date;
    private int low;
    private int high;
    private String type;
    private int dateInt;

    public Weather() {
    }

    public Weather(String date, int low, int high, String type) {
        this.date = date;
        this.low = low;
        this.high = high;
        this.type = type;
        String s[] = this.date.split("日");
        this.dateInt = Integer.valueOf(s[0]);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getLow() {
        return low;
    }

    public void setLow(int low) {
        this.low = low;
    }

    public int getHigh() {
        return high;
    }

    public void setHigh(int high) {
        this.high = high;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDateInt() {
        return dateInt;
    }

    public void setDateInt(int dateInt) {
        this.dateInt = dateInt;
    }
}
