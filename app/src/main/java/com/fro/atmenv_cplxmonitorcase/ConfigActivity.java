package com.fro.atmenv_cplxmonitorcase;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.fro.util.FROPm25;
import com.fro.util.FROSun;
import com.fro.util.FROTemHum;
import com.fro.util.StreamUtil;

public class ConfigActivity extends Activity {

    private Context context;
    private SharedPreferences sp;
    private EditText sunIp_et;
    private EditText sunPort_et;
    private EditText temHumIp_et;
    private EditText temHumPort_et;
    private EditText pm25Ip_et;
    private EditText pm25Port_et;

    private EditText time_et;
    private Button back;
    /*private TextView sun_tv;
    private TextView tem_tv;
    private TextView hum_tv;
    private TextView pm25_tv;
    private Button graph_bt;*/
    private Button connect_tb;
    //private TextView info_tv;

    //private ConnectTask connectTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
        context = this;
        sp=getSharedPreferences("config",MODE_PRIVATE);
        // 绑定控件
        bindView();
        // 初始化数据
        initData();
        // 事件监听
        initEvent();
    }

    /**
     * 绑定控件
     */
    private void bindView() {
        sunIp_et = (EditText) findViewById(R.id.sunIp_et);
        sunPort_et = (EditText) findViewById(R.id.sunPort_et);
        temHumIp_et = (EditText) findViewById(R.id.temHumIp_et);
        temHumPort_et = (EditText) findViewById(R.id.temHumPort_et);
        pm25Ip_et = (EditText) findViewById(R.id.pm25Ip_et);
        pm25Port_et = (EditText) findViewById(R.id.pm25Port_et);

        time_et = (EditText) findViewById(R.id.time_et);
        connect_tb = (Button) findViewById(R.id.connect_tb);
        //info_tv = (TextView) findViewById(R.id.info_tv);
        back=(Button)findViewById(R.id.back);
        /*sun_tv = (TextView) findViewById(R.id.sun_tv);
        tem_tv = (TextView) findViewById(R.id.tem_tv);
        hum_tv = (TextView) findViewById(R.id.hum_tv);
        pm25_tv = (TextView) findViewById(R.id.pm25_tv);
        graph_bt = (Button) findViewById(R.id.graph_bt);*/
    }

    /**
     * 初始化数据
     */
    private void initData() {
        sunIp_et.setText(sp.getString("SUN_IP",Const.SUN_IP));
        sunPort_et.setText(String.valueOf(sp.getInt("SUN_PORT",Const.SUN_PORT)));
        temHumIp_et.setText(sp.getString("TEMHUM_IP",Const.TEMHUM_IP));
        temHumPort_et.setText(String.valueOf(sp.getInt("TEMHUM_PORT",Const.TEMHUM_PORT)));
        pm25Ip_et.setText(sp.getString("PM25_IP",Const.PM25_IP));
        pm25Port_et.setText(String.valueOf(sp.getInt("PM25_PORT",Const.PM25_PORT)));

        time_et.setText(String.valueOf(sp.getInt("time",Const.time)));
    }

    /**
     * 按钮监听
     */
    private void initEvent() {

        // 连接
        connect_tb.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                    // 获取IP和端口
                    String SUN_IP = sunIp_et.getText().toString().trim();
                    String SUN_PORT = sunPort_et.getText().toString().trim();
                    String TEMHUM_IP = temHumIp_et.getText().toString().trim();
                    String TEMHUM_PORT = temHumPort_et.getText().toString().trim();
                    String PM25_IP = pm25Ip_et.getText().toString().trim();
                    String PM25_PORT = pm25Port_et.getText().toString().trim();
                    String time = time_et.getText().toString().trim();
                    if (checkIpPort(SUN_IP, SUN_PORT) && checkIpPort(TEMHUM_IP, TEMHUM_PORT)
                            && checkIpPort(PM25_IP, PM25_PORT) && !time.equals("")) {
                        Const.SUN_IP = SUN_IP;
                        Const.SUN_PORT = Integer.parseInt(SUN_PORT);
                        Const.TEMHUM_IP = TEMHUM_IP;
                        Const.TEMHUM_PORT = Integer.parseInt(TEMHUM_PORT);
                        Const.PM25_IP = PM25_IP;
                        Const.PM25_PORT = Integer.parseInt(PM25_PORT);
                        Const.time = Integer.parseInt(time);
                        //记住输入
                        SharedPreferences.Editor ed=sp.edit();
                        ed.putString("SUN_IP",SUN_IP);
                        ed.putString("TEMHUM_IP",TEMHUM_IP);
                        ed.putString("PM25_IP",PM25_IP);
                        ed.putInt("SUN_PORT",Const.SUN_PORT);
                        ed.putInt("TEMHUM_PORT",Const.TEMHUM_PORT);
                        ed.putInt("PM25_PORT",Const.PM25_PORT);
                        ed.putInt("time",Const.time);
                        ed.apply();
                        Toast.makeText(context,"配置已自动保存",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "配置信息不正确,请重输！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // 开启任务
                    /*connectTask = new ConnectTask(context, tem_tv, hum_tv, sun_tv, pm25_tv, info_tv ,connect_tb);
                    connectTask.setCIRCLE(true);
                    connectTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);*/
                //}
                /*else {
                    // 取消任务
                    if (connectTask != null && connectTask.getStatus() == AsyncTask.Status.RUNNING) {
                        connectTask.setCIRCLE(false);
                        // 如果Task还在运行，则先取消它
                        connectTask.cancel(true);
                        connectTask.closeSocket();
                    }
                    info_tv.setText("请点击连接！");
                    info_tv.setTextColor(context.getResources().getColor(R.color.gray));
                }*/
            }
        });
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 柱状图
        /*graph_bt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.startThisActivity((Activity) context);
            }
        });*/
    }
    public static void startThisActivity(Activity activity) {
        activity.startActivity(new Intent(activity, ConfigActivity.class));
    }
    /**
     * IP地址可用端口号验证，可用端口号（1024-65536）
     *
     * @param IP
     * @param port
     * @return
     */
    private boolean checkIpPort(String IP, String port) {
        try {
            String[] ip = IP.split("\\.");
            int min = Integer.MAX_VALUE, max = 0;
            for (int i = 0; i < 4; i++) {
                int tmp = Integer.parseInt(ip[i]);
                min = Math.min(min, tmp);
                max = Math.max(max, tmp);
            }
            int portInt = Integer.parseInt(port);
            if (min >= 0 && max <= 255 && portInt > 1024 && portInt < 65536) return true;
            else return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void finish() {
        super.finish();
        // 取消任务
        /*if (connectTask != null && connectTask.getStatus() == AsyncTask.Status.RUNNING) {
            connectTask.setCIRCLE(false);
            // 如果Task还在运行，则先取消它
            connectTask.cancel(true);
            connectTask.closeSocket();
        }*/
    }
}
