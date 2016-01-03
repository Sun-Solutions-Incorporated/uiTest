package tecotaku.cn.myapplication;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.renderscript.Sampler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.LineNumberReader;

public class MainActivity extends ActionBarActivity {
    ScrollListen scroller; //自定义的ScrollView
    RelativeLayout total; //title
    RelativeLayout loading; //下拉布局的layout
    TextView fab1;
    TextView fab2;
    TextView loadingtext; //下拉布局里的内容控件
    int height; //title的高度
    private int loading_height; //loading的高度

    //Handler 用来处理下拉刷新的回缩
    Handler a=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==0x123){
                ValueAnimator a=ValueAnimator.ofFloat(1,0);
                a.setDuration(400);
                a.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        showLoading((Float) valueAnimator.getAnimatedValue());
                    }
                });
                a.setInterpolator(new AccelerateDecelerateInterpolator());
                a.start();
            }
        }
    };
    //loading动画传入一个percent表示动画进度
    public void showLoading(float percnet){
        loading.setTranslationY(-loading_height*(1-percnet));
        loadingtext.setAlpha(percnet*255);
        loadingtext.setRotation(360*percnet);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scroller = (ScrollListen) findViewById(R.id.scrollView);
        loading = (RelativeLayout) findViewById(R.id.loading);
        loadingtext = (TextView) findViewById(R.id.loading_text);
        fab1 = (TextView) findViewById(R.id.fab);
        fab2 = (TextView) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(intent);
            }
        });
        fab1.setTranslationY(1000); //将fab1移出屏幕
        loading_height = dip2px(MainActivity.this,200); //因为我们的布局单位是dip但是这里的显示单位是px 所以高度我们都需要进行转换 转换方式见dip2px
        height = dip2px(MainActivity.this,80);
        loading.setTranslationY(-loading_height); //把loading部分上移至屏幕外
        total=(RelativeLayout)findViewById(R.id.linearLayoutmain);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"Create a new Message ",Toast.LENGTH_LONG).show();
            }
        });
        scroller.setOnScrollListener(new ScrollListen.OnScrollListener() {
            float scroll;
            @Override
            public void onOverScroll(float overScroll) {
              float percent = overScroll/900;
                if(percent>1) percent = 1;
                showLoading(percent);
            }
            @Override
            public void onOverScrollEnd() {
                //开启一个获取数据的异步线程 并且在结束之后调用 a.sendEMptyMessage(0x123);
                // 这里用一个延时代替
                a.sendEmptyMessageDelayed(0x123,1000);
            }
            @Override
            public void onScroll(int relativeScroll, int scrollY) {
                if(scrollY>height) showFAB1();
                else hideFAB1();
                //控制回到顶部的fab的显示和隐藏
                scroll -= relativeScroll;
                if(scroll>0) scroll=0;
                else if(scroll<-height) scroll=-height;
                total.setTranslationY(scroll);
                if(scroll==0) showFAB2();
                else hideFAB2();
            }
        });
    }
    boolean isShown=false;
    private void showFAB1(){
        if(!isShown) {
            FAB1Animator(1000, 0);
            isShown = true;
        }
    }
    private void hideFAB1(){
        if(isShown) {
            FAB1Animator(0, 1000);
            isShown = false;
        }
    }
    boolean isShown2=true;
    private void showFAB2(){
        if(!isShown2) {
            FAB2Animator(0, 1);
            isShown2 = true;
        }
    }
    private void hideFAB2(){
        if(isShown2) {
            FAB2Animator(1, 0);
            isShown2 = false;
        }
    }

    private void FAB1Animator(int startValue,int endValue){
        ValueAnimator v = ValueAnimator.ofInt(startValue,endValue);
        v.setDuration(300);
        v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                fab1.setTranslationY(1.0f*(Integer) valueAnimator.getAnimatedValue());
            }
        });
        v.start();
    }
    private void FAB2Animator(float startValue,float endValue){
        ValueAnimator v = ValueAnimator.ofFloat(startValue,endValue);
        v.setDuration(300);
        v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                fab2.setScaleX((Float) valueAnimator.getAnimatedValue());
                fab2.setScaleY((Float) valueAnimator.getAnimatedValue());
            }
        });
        v.start();
    }
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
