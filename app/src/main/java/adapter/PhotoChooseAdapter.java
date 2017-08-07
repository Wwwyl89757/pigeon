package adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.administrator.pigeon.R;

import java.util.ArrayList;
import java.util.List;

import bean.PhotoItem;
import cn.bmob.v3.datatype.BmobFile;
import config.Config;
import io.rong.imageloader.core.DisplayImageOptions;
import io.rong.imageloader.core.ImageLoader;
import io.rong.imageloader.core.ImageLoaderConfiguration;
import io.rong.imageloader.core.display.FadeInBitmapDisplayer;
import io.rong.imageloader.core.display.RoundedBitmapDisplayer;

public class PhotoChooseAdapter extends BaseAdapter {

    private List<PhotoItem> albumBeanList;
    private Context mContext;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    public PhotoChooseAdapter(Context mContext) {
        albumBeanList = new ArrayList<>();
        albumBeanList.add(new PhotoItem("", true));
        this.mContext = mContext;
        imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
    }

    @Override
    public int getCount() {
        return albumBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return albumBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final PhotoItem albumBean = albumBeanList.get(position);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_photo, null);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.item_image_addphoto);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (albumBean.isPick()) {
            viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.add_photo));
        } else {
            imageLoader.displayImage("file:///" + albumBean.getFilePath(), viewHolder.imageView, Config.options);
        }
        return convertView;
    }

    public void addData(List<PhotoItem> mAlbumBeanList) {
        albumBeanList.remove(albumBeanList.size() - 1);
        albumBeanList.addAll(mAlbumBeanList);
        albumBeanList.add(new PhotoItem("", true));
        notifyDataSetChanged();
    }

    public void removeData(int position) {
        removeData(albumBeanList.get(position));
    }

    public void removeData(PhotoItem albumBean) {
        if (albumBeanList != null && albumBeanList.contains(albumBean)) {
            //判断当前的数量
            switch (albumBeanList.size()) {
                case 1:
                case 2:
                case 3:
                    albumBeanList.remove(albumBean);
                    break;
                case 4:
                    albumBeanList.remove(albumBean);
                    if (!albumBeanList.get(albumBeanList.size() - 1).isPick()) {
                        albumBeanList.add(new PhotoItem("", true));
                    }
                    break;
            }
            this.notifyDataSetInvalidated();
        }
    }

    public List<PhotoItem> getData() {
        return albumBeanList;
    }

    private static class ViewHolder {
        public ImageView imageView;
    }
}
