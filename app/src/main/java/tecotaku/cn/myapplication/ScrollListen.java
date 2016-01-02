package tecotaku.cn.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;


public class ScrollListen extends ScrollView {
    int lastScrollY; //上次的scroll 用来实现相对数值计算
    int flag=0; //用来防止惯性滑动到底回弹的时候继续调用onScroll 影响控件动画
    int downY; //按下时候的ScrollY
    float downYs; //按下时候的rawY 因为scroll不会计算为负值的情况，所以为了实现overScroll我们把这个分出来
    boolean canOverScroll; //是否可以OverScroll 只有当down的时候scroll为0才可以 否则不会触发onOverScroll和onOverscrollEnd
    OnScrollListener onScrollListener; //接口
    public ScrollListen(Context context, AttributeSet attrs){super(context, attrs);}//构造函数

    /*
     *fuction : setOnScrollListener
     *
     *设置onScrollListener
     */
    public void setOnScrollListener(OnScrollListener on){
        onScrollListener=on;
    }

    /*
     * Handle :
     * 处理惯性滑动时的onScroll事件
     */
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int scrollY = ScrollListen.this.getScrollY();
            if(onScrollListener != null){
                if(flag==0){
                    if(scrollY-lastScrollY>0) flag=1;
                    else flag=-1;
                }
                int s=scrollY-lastScrollY;
                if((flag==1&&s>0)||(flag==-1&&s<0))
                onScrollListener.onScroll(s,scrollY);
                //防止惯性到底回弹影响结果
            }
            //此时的距离和记录下的距离不相等，在隔5毫秒给handler发送消息
            if(lastScrollY != scrollY){
                lastScrollY = scrollY;
                handler.sendMessageDelayed(handler.obtainMessage(), 5);
            }else{
                if(onScrollListener != null){
                    flag=0;
                }
            }

        };

    };

    /*
     * onTouchEvnet
     * 处理各种事件的回调
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(onScrollListener != null) {
            onScrollListener.onScroll(this.getScrollY() - lastScrollY, lastScrollY = this.getScrollY());
            switch (ev.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    if (canOverScroll) {
                        float overSc = ev.getRawY() - downYs;
                        if (overSc > 0) onScrollListener.onOverScroll(overSc);
                        //过滑动事件触发
                        }
                    break;
                case MotionEvent.ACTION_DOWN:
                    downY = this.getScrollY();
                    downYs = ev.getRawY();
                    if (downY == 0) canOverScroll = true;
                    else canOverScroll = false;
                    //如果不加入第二行容易造成还在惯性滑动 没有将conOverScroll重置 动画一直播放
                    //记录数据并且判断是否可以过滑动
                    break;
                case MotionEvent.ACTION_UP:
                    if (canOverScroll && onScrollListener != null) {
                        float overSc = ev.getRawY() - downYs;
                        if (overSc > 0) onScrollListener.onOverScrollEnd();
                    }
                    canOverScroll = false;
                    handler.sendMessageDelayed(handler.obtainMessage(), 5);
                    // 处理抬起事件
                    break;
            }
        }
        return super.onTouchEvent(ev);
    }

    public interface OnScrollListener{
        public void onOverScroll(float overScroll);
        public void onOverScrollEnd();
        public void onScroll(int relLast,int relStart);
    }
}
