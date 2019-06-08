package com.fro.atmenv_cplxmonitorcase;

import java.util.Timer;
import java.util.TimerTask;

import com.fro.atmenv_cplxmonitorcase.view.VerticalSeekBar;

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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

public class MainActivity extends Activity {

	private Context context;
	private GraphTask graphTask;
	private SharedPreferences sp;

	private VerticalSeekBar tem_sb;
	private VerticalSeekBar hum_sb;
	private VerticalSeekBar sun_sb;
	private VerticalSeekBar pm25_sb;

	private Button tem_b,hum_b,pm25_b,sun_b;

	private TextView tem_graph_tv;
	private TextView hum_graph_tv;
	private TextView sun_graph_tv;
	private TextView pm25_graph_tv;
	private ToggleButton cone_b;
	private TextView info_tv;
	Button conf_b;
    private ConnectTask connectTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graph_new);
		context = this;
		sp=getSharedPreferences("config",MODE_PRIVATE);
		// 绑定控件
		bindView();
		// 开启任务,延时1s后开始定时任务,每2s执行一次
		initData();
		initEvent();
		graphTask = new GraphTask(context, tem_sb, hum_sb, sun_sb, pm25_sb, tem_graph_tv, hum_graph_tv, sun_graph_tv,
				pm25_graph_tv);
		graphTask.setCIRCLE(true);
		graphTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	/**
	 * 绑定控件
	 */

	private void bindView() {
		tem_sb = (VerticalSeekBar) findViewById(R.id.tem_sb);
		hum_sb = (VerticalSeekBar) findViewById(R.id.hum_sb);
		sun_sb = (VerticalSeekBar) findViewById(R.id.sun_sb);
		pm25_sb = (VerticalSeekBar) findViewById(R.id.pm25_sb);
		cone_b=(ToggleButton)findViewById(R.id.cone_b);
        conf_b=(Button)findViewById(R.id.conf_b);
		info_tv=(TextView)findViewById(R.id.info_tv);
		tem_graph_tv = (TextView) findViewById(R.id.tem_graph_tv);
		hum_graph_tv = (TextView) findViewById(R.id.hum_graph_tv);
		sun_graph_tv = (TextView) findViewById(R.id.sun_graph_tv);
		pm25_graph_tv = (TextView) findViewById(R.id.pm25_graph_tv);

		tem_b=(Button)findViewById(R.id.tem_b);
		hum_b=(Button)findViewById(R.id.hum_b);
		sun_b=(Button)findViewById(R.id.sun_b);
		pm25_b=(Button)findViewById(R.id.pm25_b);
		TextView _text=findViewById(R.id._text);
		WindowManager wm=getWindowManager();
		int h=wm.getDefaultDisplay().getHeight();
		h=h-tem_b.getHeight()-_text.getHeight();
		setHeight(tem_sb,h);
		setHeight(hum_sb,h);
		setHeight(sun_sb,h);
		setHeight(pm25_sb,h);
	}
	private void setHeight(View v,int h){
		ViewGroup.LayoutParams params=v.getLayoutParams();
		params.height=h;
		v.setLayoutParams(params);
	}

	/**
	 * 初始化数据
	 */
	private void initData() {

		tem_sb.setMax(40);// 设置最大值
		tem_sb.setProgress(0);// 设置进度

		hum_sb.setMax(100);// 设置最大值
		hum_sb.setProgress(0);// 设置进度

		sun_sb.setMax(2000);// 设置最大值
		sun_sb.setProgress(0);// 设置进度

		pm25_sb.setMax(250);// 设置最大值
		pm25_sb.setProgress(0);// 设置进度

	}
	private void update(){
		Const.SUN_IP=sp.getString("SUN_IP",Const.SUN_IP);
		Const.SUN_PORT=sp.getInt("SUN_PORT",Const.SUN_PORT);
		Const.TEMHUM_IP=sp.getString("TEMHUM_IP",Const.TEMHUM_IP);
		Const.TEMHUM_PORT=sp.getInt("TEMHUM_PORT",Const.TEMHUM_PORT);
		Const.PM25_IP=sp.getString("PM25_IP",Const.PM25_IP);
		Const.PM25_PORT=sp.getInt("PM25_PORT",Const.PM25_PORT);
		Const.time=sp.getInt("time",Const.time);
	}

    private void initEvent() {

        // 连接
        cone_b.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // 获取IP和端口
					update();
                    // 开启任务
                    connectTask = new ConnectTask(context, tem_graph_tv, hum_graph_tv, sun_graph_tv, pm25_graph_tv,info_tv,cone_b);
                    connectTask.setCIRCLE(true);
                    connectTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    // 取消任务
                    if (connectTask != null && connectTask.getStatus() == AsyncTask.Status.RUNNING) {
                        connectTask.setCIRCLE(false);
                        // 如果Task还在运行，则先取消它
                        connectTask.cancel(true);
                        connectTask.closeSocket();
                    }
					//info_tv.setTextColor(context.getResources().getColor(R.color.gray));
					info_tv.setText("请点击连接");
                }
            }
        });

        // 柱状图
        conf_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfigActivity.startThisActivity((Activity) context);
            }
        });

        tem_b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Const.graph_title="温度折线图";
				Const.curRecorder=Const.tem_recorder;
				ZheXianActivity.startThisActivity((Activity)context);
			}
		});
		hum_b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Const.graph_title="湿度折线图";
				Const.curRecorder=Const.hum_recorder;
				ZheXianActivity.startThisActivity((Activity)context);
			}
		});
		sun_b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Const.graph_title="光照折线图";
				Const.curRecorder=Const.sun_recorder;
				ZheXianActivity.startThisActivity((Activity)context);
			}
		});
		pm25_b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Const.graph_title="PM2.5折线图";
				Const.curRecorder=Const.pm25_recorder;
				ZheXianActivity.startThisActivity((Activity)context);
			}
		});
    }
	/**
	 * 启动自身
	 *
	 * @param activity
	 */
	public static void startThisActivity(Activity activity) {
		activity.startActivity(new Intent(activity, MainActivity.class));
	}

	@Override
	public void finish() {
		super.finish();
		// 取消任务
		if (graphTask != null && graphTask.getStatus() == AsyncTask.Status.RUNNING) {
			graphTask.setCIRCLE(false);
			// 如果Task还在运行，则先取消它
			graphTask.cancel(true);
		}
		if (connectTask != null && connectTask.getStatus() == AsyncTask.Status.RUNNING) {
			connectTask.setCIRCLE(false);
			// 如果Task还在运行，则先取消它
			connectTask.cancel(true);
			connectTask.closeSocket();
		}
	}

}
