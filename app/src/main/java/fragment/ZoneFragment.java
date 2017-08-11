package fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.example.administrator.pigeon.FriendCircleActivity;
import com.example.administrator.pigeon.Game2048Activity;
import com.example.administrator.pigeon.PedometerActivity;
import com.example.administrator.pigeon.R;
import com.example.administrator.pigeon.ShakeActivity;
import com.example.administrator.pigeon.WeatherActivity;
import com.xys.libzxing.zxing.activity.CaptureActivity;

/**
 * Created by Administrator on 2017/7/21.
 */

public class ZoneFragment extends Fragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_zone,container,false);
        init(view);
        return view;
    }

    private void init(View view) {
        view.findViewById(R.id.layout_tocircle).setOnClickListener(this);
        view.findViewById(R.id.layout_scan).setOnClickListener(this);
        view.findViewById(R.id.layout_shake).setOnClickListener(this);
        view.findViewById(R.id.layout_weather).setOnClickListener(this);
        view.findViewById(R.id.layout_shopping).setOnClickListener(this);
        view.findViewById(R.id.layout_game).setOnClickListener(this);
        view.findViewById(R.id.layout_run).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_tocircle:
                startActivity(new Intent(getActivity(), FriendCircleActivity.class));
                break;
            case R.id.layout_scan:
                startActivityForResult(new Intent(getActivity(),CaptureActivity.class),1);
                break;
            case R.id.layout_shake:
                startActivity(new Intent(getActivity(),ShakeActivity.class));
                break;
            case R.id.layout_weather:
                startActivity(new Intent(getActivity(), WeatherActivity.class));
                break;
            case R.id.layout_shopping:
                WebView myWebView = (WebView) getActivity().findViewById(R.id.webview_shop);
                myWebView.loadUrl("http://www.taobao.com");
                break;
            case R.id.layout_game:
                startActivity(new Intent(getActivity(), Game2048Activity.class));
                break;
            case R.id.layout_run:
                startActivity(new Intent(getActivity(), PedometerActivity.class));
                break;
        }
    }

}
