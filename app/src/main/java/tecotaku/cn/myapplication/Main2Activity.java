package tecotaku.cn.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class Main2Activity extends ActionBarActivity {
    MShadom a;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        a = (MShadom) findViewById(R.id.shadow);
        LinearLayout l = (LinearLayout) findViewById(R.id.sdf);
        a.setTitleHeight(dip2px(Main2Activity.this ,100));
        a.setOnFreshListener(new MShadom.OnFreshListener() {
            @Override
            public void startFresh() {
                Handler aa = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        a.onFreshFinish();
                    }
                };
                aa.sendEmptyMessageDelayed(1,2000);
                //延时代替刷新异步
            }
        });
        l.setOnTouchListener(new View.OnTouchListener() {
            float downY;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        downY = motionEvent.getY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        a.doShadow(motionEvent.getY()-downY,motionEvent.getX());
                        break;
                    case MotionEvent.ACTION_UP:
                        a.showCloseAnimation();
                        break;
                }
                return false;
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
