package fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.pigeon.EditCircleActivity;
import com.example.administrator.pigeon.FriendCircleActivity;
import com.example.administrator.pigeon.R;

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
    }


    @Override
    public void onClick(View v) {
        startActivity(new Intent(getActivity(), FriendCircleActivity.class));
    }
}
