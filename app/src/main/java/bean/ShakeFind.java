package bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2017/8/4.
 */

public class ShakeFind extends BmobObject {
    private User userid;

    public User getUserid() {
        return userid;
    }

    public void setUserid(User userid) {
        this.userid = userid;
    }
    public ShakeFind(){}
}
