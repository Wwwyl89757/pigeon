package adapter;

import android.widget.BaseAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.administrator.pigeon.R;

import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import config.Config;
import io.rong.imageloader.core.ImageLoader;
import io.rong.imageloader.core.ImageLoaderConfiguration;

public class PhotoAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<BmobFile> mDatas;

    private int mLayoutRes;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    public PhotoAdapter(Context context,int LayoutId, List<BmobFile> datas) {
        this.mLayoutRes=LayoutId;
        this.mDatas = datas;
        mInflater = LayoutInflater.from(context);
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(mLayoutRes, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        BmobFile bmobFile = mDatas.get(position);
        imageLoader.displayImage(bmobFile.getUrl(), holder.imageView, Config.options);
        return convertView;
    }

    private final class ViewHolder {
        public ImageView imageView;
    }

}
