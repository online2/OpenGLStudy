package com.linfc.opengl;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.linfc.opengl.ac.ShapeActivity;

public class MainActivity extends AppCompatActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        initView();

    }

    private void initView() {
        findViewById(R.id.tvShape).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShapeActivity.toStart(mContext);
            }
        });

    }


}
