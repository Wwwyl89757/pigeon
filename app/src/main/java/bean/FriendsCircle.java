package bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/7/27.
 */

public class FriendsCircle extends BmobObject implements Serializable {

    private User writer;
    private List<BmobFile> photoList;
    private String text;

    public FriendsCircle() {
    }

    public User getWriter() {
        return writer;
    }

    public void setWriter(User writer) {
        this.writer = writer;
    }

    public List<BmobFile> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(List<BmobFile> photoList) {
        this.photoList = photoList;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
