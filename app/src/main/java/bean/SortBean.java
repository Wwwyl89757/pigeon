package bean;

/**
 * Created by Administrator on 2017/7/27.
 */

public class SortBean implements Comparable<SortBean>{

    private String name;//好友
    private String firstLetter;//好友名字拼音首字母
    private String friendPortrait;//好友头像

    public SortBean(String name, String firstLetter, String friendPortrait) {
        this.name = name;
        this.firstLetter = firstLetter;
        this.friendPortrait = friendPortrait;
    }

    public SortBean(String name, String firstLetter) {
        this.name = name;
        this.firstLetter = firstLetter;
    }

    public SortBean() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstLetter() {
        return firstLetter;
    }

    public void setFirstLetter(String firstLetter) {
        this.firstLetter = firstLetter;
    }

    public String getFriendPortrait() {
        return friendPortrait;
    }

    public void setFriendPortrait(String friendPortrait) {
        this.friendPortrait = friendPortrait;
    }

    @Override
    public int compareTo(SortBean o) {
        return (o.getFirstLetter().charAt(0) - this.getFirstLetter().charAt(0));
    }
}
