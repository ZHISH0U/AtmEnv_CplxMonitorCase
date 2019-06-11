package com.fro.atmenv_cplxmonitorcase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.fro.atmenv_cplxmonitorcase.view.Myview;

import java.util.ArrayList;
import java.util.List;

public class ZheXianActivity extends Activity {
    private Myview myview;
    private FrameLayout V_text;
    private Context context;
    static Context father;
    private Button flash;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        System.out.println("OnCreate............................................");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zhexian);
        context=this;
        initView();
        initEvent();
    }
    //初始化各个view
    private void initView(){
        V_text=(FrameLayout)findViewById(R.id.V_text);
        WindowManager wm = ((Activity) context).getWindowManager();         //获取屏幕长宽
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        System.out.println(height+" "+width);
        int canasWidth = width * 9 / 10;                  //定义画布所占比例
        int canasHeight = height * 4 / 5;
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) V_text
                .getLayoutParams(); // 取控件textView当前的布局参数
        linearParams.height = canasHeight;// 控件的高强制设成20
        linearParams.width = canasWidth;
        V_text.setLayoutParams(linearParams);
        myview=new Myview(this,getData(),canasWidth,canasHeight);
        V_text.addView(myview);
    }
    public void initEvent(){
        flash=(Button)findViewById(R.id.flash);
        flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                ZheXianActivity.startThisActivity((Activity)father);
            }
        });
    }
    public List<Float> getData(){
        return Const.curRecorder;
    }
    public static void startThisActivity(Activity activity) {
        System.out.println("#####################################");
        if(father==null)father=(Context)activity;
        activity.startActivity(new Intent((Activity)father, ZheXianActivity.class));
    }
    public void finish(){
        super.finish();
    }
}
