package com.example.admin.pulllayout;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.xy.open.wavepulllayout.PullLayout;
import com.xy.open.wavepulllayout.RefreshListener;

public class MainActivity extends Activity implements RefreshListener {

    private PullLayout pull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pull = (PullLayout)findViewById(R.id.pull);
        pull.setOnRefreshListener(this);
    }


    @Override
    public void onRefresh(PullLayout pullLayout) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pull.setRefreshing(false,"首选理财平台");
            }
        }, 3000);
    }
}
