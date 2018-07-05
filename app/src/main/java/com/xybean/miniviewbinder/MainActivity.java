package com.xybean.miniviewbinder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.xybean.viewbinder.ViewBinder;
import com.xybean.viewbinder.annotations.BindView;

public class MainActivity extends AppCompatActivity {

    @BindView(viewId = R.id.btn)
    public Button btn;

    @BindView(viewId = R.id.tv)
    public TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewBinder.bind(this);

        System.out.println("==========>>>>> btn " + btn);
        System.out.println("==========>>>>> tv " + tv);

    }
}
