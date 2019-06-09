package com.fro.atmenv_cplxmonitorcase;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Random;

import com.fro.util.FROPm25;
import com.fro.util.FROSun;
import com.fro.util.FROTemHum;
import com.fro.util.StreamUtil;

/**
 * Created by Jorble on 2016/3/4.
 */
public class ConnectTask extends AsyncTask<Void, Boolean, Void> {

    private Context context;
    TextView sun_tv;  //光照
    TextView tem_tv;    //温度
    TextView hum_tv;    //湿度
    TextView pm25_tv;
    TextView info_tv;
    //TextView info_tv;
    ToggleButton connect_tb;
    long cur=0;
    private Float sun;
    private Float tem;
    private Float hum;
    private Float pm25;

    private byte[] read_buff;

    private Socket sunSocket;
    private Socket temHumSocket;
    private Socket pm25Socket;

    private boolean CIRCLE = false;

    public ConnectTask(Context context, TextView tem_tv,TextView hum_tv,TextView sun_tv,TextView pm25_tv, TextView info_tv,ToggleButton btn) {
        this.context = context;
        this.info_tv=info_tv;
        this.sun_tv = sun_tv;
        this.tem_tv = tem_tv;
        this.hum_tv = hum_tv;
        this.pm25_tv = pm25_tv;
        //this.info_tv = info_tv;
        connect_tb=btn;
    }

    /**
     * 更新界面
     */
    @Override
    protected void onProgressUpdate(Boolean... values) {
        if(values[0]) {//连接正常
//		if (temHumSocket!=null ) {
            //info_tv.setTextColor(context.getResources().getColor(R.color.green));
            info_tv.setText("连接正常！");
            //Toast.makeText(context,"连接正常！",Toast.LENGTH_SHORT).show();
            //显示数据
            if (Const.sun != null && sun_tv != null) {
                sun_tv.setText(String.valueOf(Const.sun));
            }
            if (Const.tem != null && tem_tv != null) {
                tem_tv.setText(String.valueOf(Const.tem));
            }
            if (Const.hum != null && hum_tv != null) {
                hum_tv.setText(String.valueOf(Const.hum));
            }
            if (Const.pm25 != null && pm25_tv != null) {
                pm25_tv.setText(String.valueOf(Const.pm25));
            }
            if(System.currentTimeMillis()-cur>=Const.RecordingInterval){
                record();
                cur=System.currentTimeMillis();
            }
        }else {//连接断开
            //info_tv.setTextColor(context.getResources().getColor(R.color.red));
            //info_tv.setText("无法获取数据！");

            Toast.makeText(context,"连接断开！",Toast.LENGTH_SHORT).show();
            connect_tb.setChecked(false);
        }
    }
    //定期记录数据
    private void record(){
        Const.sun_recorder.add(Const.sun.floatValue());
        if(Const.sun_recorder.size()>15)Const.sun_recorder.remove(0);
        Const.tem_recorder.add(Const.tem.floatValue());
        if(Const.tem_recorder.size()>15)Const.tem_recorder.remove(0);
        Const.hum_recorder.add(Const.hum.floatValue());
        if(Const.hum_recorder.size()>15)Const.hum_recorder.remove(0);
        Const.pm25_recorder.add(Const.pm25.floatValue());
        if(Const.pm25_recorder.size()>15)Const.pm25_recorder.remove(0);
    }

    /**
     * 准备
     */
    @Override
    protected void onPreExecute() {
        info_tv.setText("正在连接...");
    }

    /**
     * 子线程任务
     *
     * @param params
     * @return
     */
    @Override
    protected Void doInBackground(Void... params) {
        // 连接
        System.out.println("%%%  "+Const.SUN_IP);
        sunSocket = getSocket(Const.SUN_IP, Const.SUN_PORT);
        temHumSocket = getSocket(Const.TEMHUM_IP, Const.TEMHUM_PORT);
        pm25Socket = getSocket(Const.PM25_IP, Const.PM25_PORT);
        int fail=0;
        //System.out.println(fail);
        /*
        while (CIRCLE) {
            Const.hum = (int)( Math.random() * 100);
            Const.sun = (int) (Math.random() * 1000);
            Const.tem = (int) (Math.random() * 40);
            Const.pm25 = (int) (Math.random() * 250);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            publishProgress(true);
            //System.out.println(Const.sun);
        }*/
        if (sunSocket != null && temHumSocket != null && pm25Socket != null) {
            // 循环读取数据
            while (CIRCLE) {
                try {
                    // 如果全部连接成功
//			if (temHumSocket!=null) {
                    // 查询光照度
                    StreamUtil.writeCommand(sunSocket.getOutputStream(), Const.SUN_CHK);
                    //Thread.sleep(Const.time/3);
                    read_buff = StreamUtil.readData(sunSocket.getInputStream());
                    sun = FROSun.getData(Const.SUN_LEN, Const.SUN_NUM, read_buff);

                    // 查询温湿度
                    StreamUtil.writeCommand(temHumSocket.getOutputStream(), Const.TEMHUM_CHK);
                    //Thread.sleep(Const.time/3);
                    read_buff = StreamUtil.readData(temHumSocket.getInputStream());
                    tem = FROTemHum.getTemData(Const.TEMHUM_LEN, Const.TEMHUM_NUM, read_buff);
                    hum = FROTemHum.getHumData(Const.TEMHUM_LEN, Const.TEMHUM_NUM, read_buff);
                    // 查询PM2.5
                    StreamUtil.writeCommand(pm25Socket.getOutputStream(), Const.PM25_CHK);
                    //Thread.sleep(Const.time/3);
                    read_buff = StreamUtil.readData(pm25Socket.getInputStream());
                    pm25 = FROPm25.getData(Const.PM25_LEN, Const.PM25_NUM, read_buff);

                    if (sun != null&&tem != null && hum != null&&pm25 != null) {
                        Const.sun = sun.intValue();
                        Const.tem = tem.intValue();
                        Const.hum = hum.intValue();
                        Const.pm25 = pm25.intValue();
                        fail=0;
                    }else fail++;

                    if((fail-1)*Const.time>5000)throw new Exception("Connect fail");
                    Log.i(Const.TAG, "Const.sun=" + Const.sun);
                    Log.i(Const.TAG, "Const.tem=" + Const.tem);
                    Log.i(Const.TAG, "Const.hum=" + Const.hum);
                    Log.i(Const.TAG, "Const.pm25=" + Const.pm25);
                    // 更新界面
                    publishProgress(true);
                    Thread.sleep(Const.time);

                } catch (Exception e) {
                    e.printStackTrace();
                    publishProgress(false);
                    break;
                }
            }
        }else publishProgress(false);
        return null;
    }

    /**
     * 建立连接并返回socket，若连接失败返回null
     *
     * @param ip
     * @param port
     * @return
     */
    private Socket getSocket(String ip, int port) {
        Socket mSocket = new Socket();
        InetSocketAddress mSocketAddress = new InetSocketAddress(ip, port);
        // socket连接
        try {
            // 设置连接超时时间为3秒
            mSocket.connect(mSocketAddress, 3000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 检查是否连接成功
        if (mSocket.isConnected()) {
            Log.i(Const.TAG, ip+"连接成功！");
            return mSocket;
        } else {
            Log.i(Const.TAG, ip+"连接失败！");
            return null;
        }
    }

    public void setCIRCLE(boolean cIRCLE) {
        CIRCLE = cIRCLE;
    }

    @Override
    protected void onCancelled() {
        //info_tv.setTextColor(context.getResources().getColor(R.color.gray));
        //info_tv.setText("请点击连接！");
    }

    /**
     * 关闭socket
     */
    void closeSocket() {
        try {
            if (sunSocket != null) {
                sunSocket.close();
            }
            if (temHumSocket != null) {
                temHumSocket.close();
            }
            if (pm25Socket != null) {
                pm25Socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

