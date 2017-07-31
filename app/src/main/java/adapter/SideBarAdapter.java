package adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.example.administrator.pigeon.R;

import java.util.List;

import bean.SortBean;
import config.Config;
import io.rong.imageloader.core.ImageLoader;

/**
 * Created by Administrator on 2017/7/27.
 */

public class SideBarAdapter extends BaseAdapter implements SectionIndexer {


    private List<SortBean> list = null;
    private Context context;

    public SideBarAdapter(Context context, List<SortBean> list) {
        this.context = context;
        this.list = list;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     * @param list
     */
    public void updateListView(List<SortBean> list){
        this.list = list;
        notifyDataSetChanged();
    }

    //返回总列表项数
    public int getCount() {
        return this.list.size();
    }
    //返回position行的数据元素
    public Object getItem(int position) {
        return list.get(position);
    }
    //返回position行号对应的id号
    public long getItemId(int position) {
        return position;
    }


    /**
     * 返回position位置上的列表项视图
     * position：需要返回列表项的行号
     * view：被系统复用的列表项
     * arg2：用来装载列表项的listView对象
     **/
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        final SortBean model = list.get(position);
        //第一次显示listView调用
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_sidebar,null);
            viewHolder.letter = (TextView) convertView.findViewById(R.id.letter);
            viewHolder.friend_icon = (ImageView) convertView.findViewById(R.id.friend_icon);
            viewHolder.friend_name = (TextView) convertView.findViewById(R.id.friend_name);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.friend_name.setText(list.get(position).getUser().getUsername());
        if(position == 0){
            viewHolder.friend_icon.setImageResource(R.drawable.new_friend);
        }else if(position == 1){
            viewHolder.friend_icon.setImageResource(R.drawable.group_chat);
        }else {
            ImageLoader.getInstance().displayImage(list.get(position).getUser().getAvatar().getUrl(), viewHolder.friend_icon, Config.options);
        }


        //根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);
        Log.e("error","section="+section);
        /**
         *
         * VISIBLE:0  意思是可见的
         * INVISIBLE:4 意思是不可见的，但还占着原来的空间
         * GONE:8  意思是不可见的，不占用原来的布局空间
         * **/
        if(position == getPositionForSection(section)){
            viewHolder.letter.setVisibility(View.VISIBLE);
            if(list.get(position).getFirstLetter().equals("↑")){
                viewHolder.letter.setVisibility(View.GONE);
            }
        }else {
            viewHolder.letter.setVisibility(View.GONE);
        }
        viewHolder.letter.setText(model.getFirstLetter());
        Log.d("test"," "+viewHolder.letter);
        return convertView;
    }



    final static class ViewHolder {
        TextView letter;
        TextView friend_name;
        ImageView friend_icon;
    }



    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return list.get(position).getFirstLetter().charAt(0);
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getFirstLetter();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }


    /**
     * 提取英文的首字母，非英文字母用#代替。
     *
     * @param str
     * @return
     */
    private String getAlpha(String str) {
        String  sortStr = str.trim().substring(0, 1).toUpperCase();
        // 正则表达式，判断首字母是否是英文字母
        if (sortStr.matches("[A-Z]")) {
            return sortStr;
        } else {
            return "#";
        }
    }
}
