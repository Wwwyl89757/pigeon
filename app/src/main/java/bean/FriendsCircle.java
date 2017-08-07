package bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/27.
 */

public class FriendsCircle extends BmobObject implements Serializable {

    private User writer;
    private List<BmobFile> photoList;
    private String text;
    private BmobRelation likes;
    private List<Comment> comments;
    private List<User> whoLikes;
    private String praiseflag;//点赞标识

    public FriendsCircle() {
        comments = new ArrayList<>();
        whoLikes = new ArrayList<>();
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

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setLikes(BmobRelation likes) {
        this.likes = likes;
    }

    public BmobRelation getLikes() {
        return likes;
    }

    public void setPraiseflag(String praiseflag) {
        this.praiseflag = praiseflag;
    }

    public String getPraiseflag() {
        return praiseflag;
    }

    public void setWhoLikes(List<User> whoLikes) {
        this.whoLikes = whoLikes;
    }

    public List<User> getWhoLikes() {
        return whoLikes;
    }
}
