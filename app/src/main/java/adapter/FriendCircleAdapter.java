package adapter;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.pigeon.R;

import java.util.ArrayList;
import java.util.List;

import bean.Comment;
import bean.FriendsCircle;
import bean.User;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import config.Config;
import io.rong.imageloader.core.ImageLoader;
import io.rong.imageloader.core.ImageLoaderConfiguration;
import model.FriendCircleModel;
import myapp.MyApp;
import view.FixedGridView;
import view.MyCustomDialog;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class FriendCircleAdapter extends BaseAdapter {

    private static final String TAG = "MyListAdapter";
    private boolean praise=false;//是否已经点赞了   true:已经点赞了，这样textView上面应该显示“取消”；false:没有点赞，textView上面应该显示“点赞”；默认为false
    // 定义操作面板状态常量
    public static final int PANEL_STATE_GONE = 0;
    public static final int PANEL_STATE_VISIABLE = 1;
    //操作面板状态
    public static int panelState = PANEL_STATE_GONE;

    String obSid="";//sid表示消息的id
    private String praiseflag="";//点赞标示，判断这个人有没有点过

    private LayoutInflater mInflater;
    private List<FriendsCircle> mDatas;
    private int mLayoutRes;
    private Context mContext;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private FriendCircleModel model = new FriendCircleModel();

    public FriendCircleAdapter(Context context, int layoutRes, List<FriendsCircle> datas) {
        this.mContext=context;
        this.mDatas = datas;
        this.mLayoutRes = layoutRes;
        mInflater = LayoutInflater.from(context);
    }


    public List<FriendsCircle> returnmDatas() {
        return this.mDatas;
    }

    public void addAll(List<FriendsCircle> mDatas) {
        this.mDatas.addAll(mDatas);
    }

    public void setDatas(List<FriendsCircle> mDatas) {
        this.mDatas.clear();
        this.mDatas.addAll(mDatas);
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public FriendsCircle getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final  ViewHolder holder;
        Log.i("bmobFile","friendCirclePosition = " + position);
        if (convertView == null) {
            convertView = mInflater.inflate(mLayoutRes, null);
            holder = new ViewHolder();
            holder.write_photo = (ImageView) convertView.findViewById(R.id.write_photo);
            holder.write_name = (TextView) convertView.findViewById(R.id.write_name);
            holder.write_date = (TextView) convertView.findViewById(R.id.write_date);
            holder.dynamic_text = (TextView) convertView.findViewById(R.id.dynamic_text);
            holder.dynamic_photo = (FixedGridView) convertView.findViewById(R.id.dynamic_photo);

            holder.btnIgnore=(Button) convertView.findViewById(R.id.btnIgnore);
            holder.btnComment=(Button) convertView.findViewById(R.id.btnComment);
            holder.btnPraise=(Button) convertView.findViewById(R.id.btnPraise);
            holder.layoutParise=(LinearLayout) convertView.findViewById(R.id.layoutParise);
            holder.layout01=(LinearLayout) convertView.findViewById(R.id.layout01);
            holder.liearLayoutIgnore=(LinearLayout) convertView.findViewById(R.id.liearLayoutIgnore);
            holder.layout=(LinearLayout) convertView.findViewById(R.id.layout);
            holder.view = (TextView)convertView.findViewById(R.id.view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
//        Log.i("bmobFile","name"+ mDatas.get(position).getWriter().getUsername());
        BmobQuery<User> query = new BmobQuery();
        query.getObject(mDatas.get(position).getWriter().getObjectId(), new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null){
                    imageLoader.displayImage(user.getAvatar().getUrl(),holder.write_photo,Config.options);
                    holder.write_name.setText(user.getUsername());
                }else {
                    Log.i("error",e.getErrorCode()+e.getMessage());
                }
            }
        });

        holder.write_date.setText(mDatas.get(position).getCreatedAt());
        holder.dynamic_text.setText(mDatas.get(position).getText());
//        Log.i("getPhotoList",mDatas.get(position).getPhotoList().size()+"");
        if (mDatas.get(position).getPhotoList() == null || mDatas.get(position).getPhotoList().size() == 0){
            holder.dynamic_photo.setVisibility(View.GONE);
        }else {
            holder.dynamic_photo.setVisibility(View.VISIBLE);
            holder.dynamic_photo.setAdapter(new PhotoAdapter(mContext,R.layout.item_friendcircle_grid,mDatas.get(position).getPhotoList()));
        }
        holder.btnComment.setTag(getItem(position).getObjectId());
        if (mDatas.get(position).getPraiseflag() != null){
            holder.btnPraise.setTag(mDatas.get(position).getPraiseflag());//点赞标示，用来判断是否点过
        }else {
            holder.btnPraise.setTag("N");
        }


        //显示评论、点赞按钮
        holder.btnIgnore.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                obSid= holder.btnComment.getTag().toString();
                praiseflag= holder.btnPraise.getTag().toString();
                if("Y".equals(praiseflag)){
                    praise=true;
                    holder.btnPraise.setText("取消");
                }else if("N".equals(praiseflag)){
                    praise=false;
                    holder.btnPraise.setText("点赞");
                }

                if(1==panelState){
                    panelState = PANEL_STATE_GONE;
                    switchPanelState(holder.liearLayoutIgnore, holder.btnComment, holder.btnPraise);
                }else{
                    panelState = PANEL_STATE_VISIABLE;
                    switchPanelState(holder.liearLayoutIgnore, holder.btnComment, holder.btnPraise);
                }
            }
        });

        //评论按钮
        holder.btnComment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                holder.liearLayoutIgnore.setVisibility(View.GONE);

                //显示评论的对话框
                MyCustomDialog dialog = new MyCustomDialog(mContext,R.style.add_dialog,"",new MyCustomDialog.OnCustomDialogListener() {
                    //点击对话框'提交'以后
                    public void back(String content) {
                        //先隐藏再提交评论
                        panelState = PANEL_STATE_GONE;
                        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                        switchPanelState(holder.liearLayoutIgnore,holder.btnComment,holder.btnPraise);
                        submitComment(content,mDatas.get(position));//提交评论
                    }
                });
                dialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog
                dialog.show();
            }
        });

        //点赞按钮       praise:是否已经点赞了   true:已经点赞了，这样textView上面应该显示“取消”；false:没有点赞，textView上面应该显示“点赞”；默认为false
        holder.btnPraise.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                //先隐藏再提交评论
                panelState = PANEL_STATE_GONE;
                switchPanelState(holder.liearLayoutIgnore,holder.btnComment,holder.btnPraise);
                submitPraise(mDatas.get(position));//提交赞
            }
        });

        //显示点赞holder.layoutParise   friendpraise
        holder.layoutParise.removeAllViews();
        holder.view.setVisibility(View.GONE);
        holder.layout01.setVisibility(View.GONE);
        if(0!=mDatas.get(position).getWhoLikes().size()){//有数据，控件显示
            holder.layout01.setVisibility(View.VISIBLE);
            holder.layoutParise.setVisibility(View.VISIBLE);

            LinearLayout ll=new LinearLayout(mContext);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            ll.layout(3, 3, 3, 3);

            ImageView i1=new ImageView(mContext);
            i1.setBackgroundResource(R.drawable.micro_praise_button);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(35,35);
            params.gravity = Gravity.CENTER_VERTICAL;
            i1.setLayoutParams(params);
            TextView t2=new TextView(mContext);
            t2.setTextColor(0xff2C78B8);
            t2.setTextSize(16);
            t2.setGravity(Gravity.CENTER_VERTICAL);
            ll.addView(i1);

            StringBuffer uName=new StringBuffer();
            uName.append(" ");
            for(User p:mDatas.get(position).getWhoLikes()){
                if(null!=p){
                    uName.append(p.getUsername()+" ,");
                }
            }
            uName.deleteCharAt(uName.length()-1);
            t2.setText(uName);
            ll.addView(t2);
            holder.layoutParise.addView(ll);
        }

        //显示评论
        holder.layout.removeAllViews();
        if(0!=mDatas.get(position).getComments().size()){
            holder.layout01.setVisibility(View.VISIBLE);
            holder.layout.setVisibility(View.VISIBLE);
            if(0!=mDatas.get(position).getWhoLikes().size()){
                holder.view.setVisibility(View.VISIBLE);
            }
            for(Comment f:mDatas.get(position).getComments()){
//                if(null!=f.getObjectId()){
                    LinearLayout ll=new LinearLayout(mContext);
                    ll.setOrientation(LinearLayout.HORIZONTAL);
                    ll.layout(3, 3, 3, 3);
                    TextView t1=new TextView(mContext);
                    TextView t2=new TextView(mContext);
                    t1.setText(" "+f.getAuthor().getUsername()+":");
                    t1.setTextColor(0xff2C78B8);
                    t1.setTextSize(16);
                    t2.setTextSize(16);
                    t2.setText(f.getContent());
                    ll.addView(t1);
                    ll.addView(t2);
                    holder.layout.addView(ll);
//                }
            }
        }

        return convertView;
    }

    /**
     * 提交评论
     * @param content 评论内容
     */
    private void submitComment(final String content, FriendsCircle friendsCircle) {
        // TODO Auto-generated method stub
//        Toast.makeText(mContext, "提交评论", Toast.LENGTH_SHORT).show();
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setAuthor(MyApp.INSTANCE().getCurrentuser());
        model.addComments(comment,friendsCircle,this);
    }

    /**
     * 点赞
     * praise:是否已经点赞了   true:已经点赞了，这样textView上面应该显示“取消”；false:没有点赞，textView上面应该显示“点赞”；默认为false
     */
    private void submitPraise(FriendsCircle friendsCircle) {
        // TODO Auto-generated method stub
        if (("Y").equals(friendsCircle.getPraiseflag())){
            Log.i("submitPraise", "deleteLikes ");
            model.deleteLikes(MyApp.INSTANCE().getCurrentuser(),friendsCircle,this);
        }else {
            model.addLikes(MyApp.INSTANCE().getCurrentuser(),friendsCircle,this);
        }

//        Toast.makeText(mContext, "点赞/取消点赞", Toast.LENGTH_SHORT).show();
    }

    /**
     * 评论点赞，隐藏显示
     * 操作面板显示状态
     */
    private void switchPanelState(LinearLayout liearLayoutIgnore,Button btnComment,Button btnPraise) {
        // TODO Auto-generated method stub
        switch (panelState) {
            case PANEL_STATE_GONE:
                liearLayoutIgnore.setVisibility(View.GONE);
                btnComment.setVisibility(View.GONE);
                btnPraise.setVisibility(View.GONE);
                break;
            case PANEL_STATE_VISIABLE:
//			holder.liearLayoutIgnore.startAnimation(animation);//评论的显示动画
                liearLayoutIgnore.setVisibility(View.VISIBLE);
                btnComment.setVisibility(View.VISIBLE);
                btnPraise.setVisibility(View.VISIBLE);
                break;
        }
    }



    private final class ViewHolder {
        ImageView write_photo;
        TextView write_name;
        TextView write_date;
        TextView dynamic_text;
        TextView view;
        FixedGridView dynamic_photo;
        Button btnIgnore,btnComment,btnPraise;
        LinearLayout liearLayoutIgnore,layout,layoutParise,layout01;
    }
}
