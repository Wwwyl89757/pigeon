package bean;

/**
 * Created by Administrator on 2017/7/27.
 * 发送朋友圈照片对象
 */

public class PhotoItem {
    private String filePath;
    private boolean isPick;//标识是不是+

    public PhotoItem() {
    }

    public PhotoItem(String filePath, boolean isPick) {
        this.filePath = filePath;
        this.isPick = isPick;
    }


    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isPick() {
        return isPick;
    }

    public void setPick(boolean pick) {
        isPick = pick;
    }
}
