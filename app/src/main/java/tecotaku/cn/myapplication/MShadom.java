package tecotaku.cn.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.renderscript.Sampler;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

/**
 * Created by sino on 16-1-2.
 */
public class MShadom extends View {
    int height;
    int width;
    float lastDistance;
    float lastX;
    float minFreshDistance = 300;
    boolean isFreshing = false;
    boolean isRunning = false;
    OnFreshListener freshListener;
    Ball ba;
    Paint paint;
    Paint ball;
    Paint arcP;
    int titleHeight;
    RectF rect = new RectF();
    RectF arcr = new RectF();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int index=0;
            boolean flag = true;
            while(isRunning){
                ba.arcStart += 8;
                if(flag){
                    ba.arc += Math.abs(index%16 - 8);
                    if(ba.arc > 355f){
                        flag = false;
                    }
                }else{
                    ba.arc -= Math.abs(index%12 - 6);
                    if(ba.arc  < 5f){
                        flag = true;
                    }
                }
                index ++ ;
                postInvalidate();
                if(index == 1000) index=0;
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    public MShadom(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setAlpha(30);
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        ball = new Paint();
        ball.setColor(Color.WHITE);
        ball.setAntiAlias(true);
        arcP = new Paint();
        arcP.setColor(Color.WHITE);
        arcP.setStyle(Paint.Style.STROKE);
        arcP.setAntiAlias(true);
        arcP.setStrokeWidth(3);
        arcP.setStrokeCap(Paint.Cap.ROUND);
        ba = new Ball();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        height = h;
        width = w;
    }
    public void setTitleHeight(int height){
        this.titleHeight = height;
    }
    private void startRefreshAnimation(){
        ba = new Ball();
        AnimatorSet set = new AnimatorSet();
        ValueAnimator ballSize = ValueAnimator.ofFloat(0 ,30);
        ValueAnimator ballPosition = ValueAnimator.ofFloat(-10 ,110);
        ValueAnimator shadow = ValueAnimator.ofFloat(0 ,1);
        ba.isShow = true;
        ba.x = lastX;
        paint.setColor(Color.WHITE);
        ballSize.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ba.size = (Float) valueAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        ballPosition.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ba.y = titleHeight - (Float) valueAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        shadow.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                rect.top = -(Float)valueAnimator.getAnimatedValue() * 28 * 0.9f + titleHeight;
                rect.bottom = 12 + titleHeight;
                rect.right = width + lastX;
                rect.left = lastX - width;
                postInvalidate();
            }
        });
        shadow.setDuration(100);
        shadow.setRepeatMode(ValueAnimator.REVERSE);
        shadow.setRepeatCount(1);
        shadow.setInterpolator(new DecelerateInterpolator());
        ballSize.setDuration(450);
        ballPosition.setDuration(300);
        shadow.start();
        set.playTogether(ballPosition,ballSize);
        set.start();
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isRunning = true;
                arcP.setColor(Color.WHITE);
                new Thread(runnable).start();
                isFreshing = true;
                freshListener.startFresh();
            }
        });
    }
    private void startDropAnimation(){
        AnimatorSet as = new AnimatorSet();
        ValueAnimator a = ValueAnimator.ofFloat(ba.arc ,360);
        a.setDuration(100);
        a.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ba.arc = (Float) valueAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        ValueAnimator b = ValueAnimator.ofFloat(0,1);
        b.setDuration(400);
        b.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float percent = (Float) valueAnimator.getAnimatedValue();
                arcP.setARGB((int)(255*(1-percent)),68,68,68);
                ba.arcSize = 20 * percent;
                postInvalidate();
            }
        });
        AnimatorSet as2 = new AnimatorSet();
        ValueAnimator ballSize = ValueAnimator.ofFloat(30 ,0);
        ValueAnimator ballPosition = ValueAnimator.ofFloat(110 ,-10);
        ballSize.setDuration(450);
        ballPosition.setDuration(300);
        ballSize.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ba.size = (Float) valueAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        ballPosition.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ba.y = titleHeight-(Float) valueAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        ValueAnimator shadow = ValueAnimator.ofFloat(0, 100);
        shadow.setRepeatMode(ValueAnimator.REVERSE);
        shadow.setRepeatCount(3);
        shadow.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                doShadow( (Float) valueAnimator.getAnimatedValue(), lastX);
            }
        });
        as2.playTogether(ballSize,ballPosition);
        as.playSequentially(a,b,as2);
        shadow.setStartDelay(750);
        shadow.setDuration(100);
        shadow.setInterpolator(new DecelerateInterpolator());
        shadow.start();
        as.start();
    }
    public void showCloseAnimation(){
        if(!isFreshing) {
            Log.i("TAG", "showCloseANimation");
            final
            float dis = lastDistance;
            ValueAnimator a = ValueAnimator.ofFloat(lastDistance, 0);
            a.setDuration(300);
            a.setInterpolator(new AccelerateDecelerateInterpolator());
            a.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    Log.i("TAG", "last:" + lastDistance + " min:" + minFreshDistance);
                    if (dis > minFreshDistance) {
                        if (freshListener != null) {
                            if (!isFreshing)
                                startRefreshAnimation();
                        } else throw new RuntimeException("You must set a listener first");
                    }
                }
            });
            a.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    doShadow((Float) valueAnimator.getAnimatedValue(), lastX);
                }
            });
            a.start();
        }
    }
    public void doShadow(float distance, float x){
        if(!isFreshing) {
            paint.setColor(Color.BLACK);
            lastDistance = distance;
            lastX = x;
            float per = distance / 400;
            if (per > 1) per = 1;
            rect.top = -60 + titleHeight;
            rect.bottom = per * 0.9f * 100 + titleHeight;
            rect.right = width + x;
            rect.left = x - width;
            postInvalidate();
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawOval(rect, paint);
        if(ba.isShow){
            canvas.drawCircle(ba.x ,ba.y ,ba.size ,ball);
            arcr.top = (int)(ba.y - ba.size -ba.arcSize - 8);
            arcr.bottom = (int)(ba.y + ba.size + ba.arcSize + 8);
            arcr.left = (int)(ba.x - ba.size - ba.arcSize - 8);
            arcr.right = (int)(ba.x + ba.size +ba.arcSize + 8);
            canvas.drawArc(arcr ,ba.arcStart ,ba.arc ,false ,arcP);
        }
    }
    public void setMinFreshDistance (float minFreshDistance){
        this.minFreshDistance = minFreshDistance;
    }
    class Ball{
        boolean isShow = false;
        float x;
        float y;
        float size;
        float arc;
        float arcStart;
        float arcSize;
    }
    public interface OnFreshListener{
       public void startFresh();
    }
    public void setOnFreshListener (OnFreshListener listener){
        this.freshListener = listener;
    }
    public void onFreshFinish(){
        isFreshing = false;
        isRunning = false;
        startDropAnimation();
        Toast.makeText(getContext(),"刷新完成",Toast.LENGTH_LONG).show();
    }
}
