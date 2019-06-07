package com.fro.atmenv_cplxmonitorcase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;

import com.fro.atmenv_cplxmonitorcase.view.Myview;

import java.util.ArrayList;
import java.util.List;

public class ZheXianActivity extends Activity {
    private Myview myview;
    private Context context;
    @Override

    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        System.out.println("OnCreate............................................");
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.zhexian);
        context=this;
        List numble = new ArrayList();
        numble.add(74.0F);
        numble.add(65.0F);
        numble.add(23.0F);
        numble.add(34.0F);
        numble.add(12.0F);
        numble.add(8.0F);
        numble.add(56.0F);
        numble.add(23.0F);
        numble.add(27.0F);
        numble.add(78.0F);
        numble.add(67.0F);
        numble.add(45.0F);
        numble.add(98.0F);
        numble.add(2.0F);
        numble.add(8.0F);
        myview=new Myview(this,numble);

        myview.findViewById(R.id.myview);
    }
    public static void startThisActivity(Activity activity) {
        System.out.println("#####################################");
        activity.startActivity(new Intent(activity, ZheXianActivity.class));
    }
    public void finish(){
        super.finish();
    }
}
