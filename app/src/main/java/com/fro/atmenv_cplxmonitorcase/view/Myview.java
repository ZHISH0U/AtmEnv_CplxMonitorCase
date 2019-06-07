package com.fro.atmenv_cplxmonitorcase.view;


import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Myview extends View {
    private float canasWidth;                 //画布宽
    private float canasHeight;
    private float XPoint;                  //定义原点
    private float YPoint;
    private int XScale;                  //间隔
    private int YScale;
    private TextView V_text;
    // private Float[] numble = {22F, 54.3F, 63F, 23F, 87F, 36F};
    private List numble;
    public Myview(Context context, List numble) {
        super(context);
        this.numble = new ArrayList();
        this.numble = numble;
        WindowManager wm = ((Activity) context).getWindowManager();         //获取屏幕长宽
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        System.out.println(height+" "+width);
        canasWidth = (width * 9) / 10;                  //定义画布所占比例
        canasHeight = height / 3;
        XPoint = canasWidth / 14.0F;                       //定义原点
        YPoint = canasHeight - 20;
        XScale = (int) (canasWidth - 50) / (numble.size());
        YScale = (int) ((canasHeight - 50) / 11);

        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) V_text
                .getLayoutParams(); // 取控件textView当前的布局参数
        linearParams.height = (int) canasHeight;// 控件的高强制设成20
        linearParams.width = (int) canasWidth;
        //linearParams.height = (int) 100;// 控件的高强制设成20
        //linearParams.width = (int) 100;
        V_text.setLayoutParams(linearParams);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        System.out.println(canasHeight+" "+canasWidth);
        Paint paint = new Paint();
        paint.setColor(0xffff0000);
        paint.setTextSize(40);
        paint.setStrokeWidth(5);
        paint.setAntiAlias(false);
        Paint paint2 = new Paint();
        paint2.setColor(0xff00ff00);
        paint2.setTextSize(30);
        Path path = new Path();
        canvas.drawLine(XPoint, 30, XPoint, YPoint, paint);
        Paint paint1 = new Paint();             // 文字画笔
        paint1.setTextSize(65);
        paint1.setColor(Color.YELLOW);
        canvas.drawText("黄金G点历史折线", 300, 70, paint1);
        for (int i = 0; i <= 10; i++) {
            canvas.drawLine(XPoint, YPoint - i * YScale, XPoint + 20,
                    YPoint - i * YScale, paint);                     // Y刻度
            canvas.drawText(i * 10 + "", XPoint - 70,
                    YPoint - i * YScale - 10, paint);                // 文字
        }
        canvas.drawLine(XPoint, 30, XPoint + 20, 50, paint);               // 绘制箭头
        canvas.drawLine(XPoint, 30, XPoint - 20, 50, paint);
        for (int j = 0; j < numble.size(); j++) {                 ////// 绘制折线
            try {
                float y0 = (Float) numble.get(j);
                float y1 = (Float) numble.get(j + 1);
                Log.i("y0", y0 + "");
                Log.i("y1", y1 + "");
                canvas.drawText(numble.get(j) + "",
                        XPoint + j * XScale + 10,
                        YPoint - (y0 / 10) * YScale - 20, paint2); // 文
                canvas.drawLine(XPoint + j * XScale,
                        YPoint - (y0 / 10) * YScale,
                        XPoint + (j + 1) * XScale,
                        YPoint - (y1 / 10) * YScale, paint);
            } catch (Exception e) {
            }

        }

    }

}
