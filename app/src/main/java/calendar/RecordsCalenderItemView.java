package calendar;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.pigeon.PedometerActivity;
import com.example.administrator.pigeon.R;


/**
 * Created by Administrator on 2017/8/7.
 */

public class RecordsCalenderItemView extends RelativeLayout {

    private Context context;

    private LinearLayout itemLl;
    private View lineView;
    private TextView weekTv;
    private RelativeLayout dateRl;
    private TextView dateTv;
    //日期时间
    private String weekStr,dateStr;
    private int position;

    //当前item时间，用来判断item是否被点击
    protected String curItemDate;


    OnCalenderItemClick itemClick = null;

    public interface OnCalenderItemClick{
        public void onCalenderItemClick();
    }

    public void setOnCalenderItemClick(OnCalenderItemClick itemClick){
        this.itemClick = itemClick;
    }


    public RecordsCalenderItemView(Context context, String week, String date, int position, String curItemDate) {
        super(context);
        this.context = context;
        this.weekStr = week;
        this.dateStr = date;
        this.position = position;
        this.curItemDate = curItemDate;
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View itemView = inflater.inflate(R.layout.records_calender_item_view,this);
        itemLl = (LinearLayout) itemView.findViewById(R.id.records_calender_item_ll);
        weekTv = (TextView) itemView.findViewById(R.id.records_calender_item_week_tv);
        lineView = itemView.findViewById(R.id.calender_item_line_view);
        dateRl = (RelativeLayout) itemView.findViewById(R.id.records_calender_item_date_rl);
        dateTv = (TextView) itemView.findViewById(R.id.records_calender_item_date_tv);


        weekTv.setTextSize(15);
        lineView.setVisibility(GONE);

        weekTv.setText(weekStr);
        dateTv.setText(dateStr);

        itemView.setLayoutParams(new LayoutParams((PedometerActivity.screenWidth) / 7,
                ViewGroup.LayoutParams.MATCH_PARENT));

        itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClick.onCalenderItemClick();
            }
        });
    }

    //返回当前项的ID
    public int getPosition(){
        return position;
    }

    public void setChecked(boolean checkedFlag){
        if(checkedFlag){
            //当前item被选中后的样式
            weekTv.setTextColor(getResources().getColor(R.color.main_text_color));
            dateTv.setTextColor(getResources().getColor(R.color.white));
            dateRl.setBackgroundResource(R.drawable.ic_blue_round_bg);
        }else {
            //当前item未被选中的样式
            weekTv.setTextColor(getResources().getColor(R.color.gray_default_dark));
            dateTv.setTextColor(getResources().getColor(R.color.gray_default_dark));
            //设置背景透明
            dateRl.setBackgroundColor(Color.TRANSPARENT);
        }
    }

}
