package bean;

/**
 * Created by Administrator on 2017/8/7.
 */

public class StepCounter {
    private String curDate; //当天的日期
    private String steps;   //当天的步数

    public StepCounter() {
    }

    public StepCounter(String curDate, String steps) {
        this.curDate = curDate;
        this.steps = steps;
    }

    public String getCurDate() {
        return curDate;
    }

    public void setCurDate(String curDate) {
        this.curDate = curDate;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    @Override
    public String toString() {
        return "StepCounter{" +
                "curDate='" + curDate + '\'' +
                ", steps='" + steps + '\'' +
                '}';
    }
}
