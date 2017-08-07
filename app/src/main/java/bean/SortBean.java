package bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/7/27.
 */

public class SortBean implements Serializable{

    private String firstLetter;//好友名字拼音首字母
    private User user;//好友对象
    private boolean checkBox;

    public SortBean(User user,String firstLetter) {
        this.user = user;
        this.firstLetter = firstLetter;
    }


    public SortBean() {
    }

    public void setCheckBox(boolean checkBox) {
        this.checkBox = checkBox;
    }

    public boolean isCheckBox() {
        return checkBox;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFirstLetter() {
        return firstLetter;
    }

    public void setFirstLetter(String firstLetter) {
        this.firstLetter = firstLetter;
    }

}
