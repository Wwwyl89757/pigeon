package bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2017/8/5.
 */

public class Comment extends BmobObject {

    private User author;
    private String content;

    public Comment() {
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
