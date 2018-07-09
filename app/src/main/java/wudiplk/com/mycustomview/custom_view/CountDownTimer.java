package wudiplk.com.mycustomview.custom_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import wudiplk.com.mycustomview.R;
import wudiplk.com.mycustomview.util.DensityUtils;

/**
 * Created by Wu di on 2017/9/6.
 * Description:
 */

public class CountDownTimer extends RelativeLayout {

    private Context context;
    /**
     * 前景、背景形状
     */
    private BgView bgView;
    private ForeView foreView;
    /**
     * 中间文字
     */
    private TextView tvTimer, tvSecondTimer;
    private int radius, rectWith, rectHeight;
    private Rect rect;
    private int time =6;


    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            tvTimer.setText("" + time);
            return false;
        }
    });

    public CountDownTimer(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public CountDownTimer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public CountDownTimer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {

        // 定义形状
        rectWith = DensityUtils.dp2px(context, 3);
        rectHeight = DensityUtils.dp2px(context, 20);

        // 中间文字
        tvTimer = new TextView(context);
        tvTimer.setTextColor(ContextCompat.getColor(context, R.color.c_FF9E00));
        tvTimer.setTextSize(100);
        tvTimer.setGravity(Gravity.CENTER);
        tvTimer.setText(time + "");
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        addView(tvTimer, layoutParams);

        // 添加字符“秒”的位置
        tvSecondTimer = new TextView(context);
        tvSecondTimer.setTextColor(ContextCompat.getColor(context, R.color.c_FF9E00));
        tvSecondTimer.setTextSize(22);
        tvSecondTimer.setText("秒");
        layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(DensityUtils.dp2px(context, 145), DensityUtils.dp2px(context, 130), 0, 0);
        addView(tvSecondTimer, layoutParams);

        // 添加到容器
        foreView = new ForeView(context);
        addView(foreView);
        bgView = new BgView(context);
        addView(bgView);

    }

    /**
     * 测量获取屏幕的宽高
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        radius = getMeasuredWidth() / 2;
        rect = new Rect();
        rect.left = radius - rectWith;
        rect.right = radius + rectWith;
        rect.top = 0;
        rect.bottom = rectHeight;


    }

    private boolean isDraw = false;

    /**
     * 背景形状
     */
    private class BgView extends View {
        private Paint rectPaint;

        public BgView(Context context) {
            super(context);
            rectPaint = new Paint();
            rectPaint.setStyle(Paint.Style.FILL);
            rectPaint.setColor(ContextCompat.getColor(context, R.color.c_5A492F));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (!isDraw) {
                for (int i = 0; i < 60; i++) {
                    canvas.rotate(6, radius, radius);
                    canvas.drawRect(rect, rectPaint);
                    canvas.save();
                }
                isDraw = true;
            }
        }
    }

    /**
     * 完成的回掉接口
     */
    public interface OnFinishListener {
        /**
         * 完成结束
         */
        void onFinish();
    }

    private OnFinishListener onFinishListener;

    public void setOnFinishListener(OnFinishListener onFinishListener) {
        this.onFinishListener = onFinishListener;
    }

    /**
     * 倒计时开始
     */
    public void onStart() {
        foreView.timerThread.isStop = false;
        foreView.timerThread.start();
    }

    /**
     * 倒计时结束
     */
    public void onStop() {
        if (foreView.timerThread != null) {
            foreView.timerThread.onStop();
        }
    }

    /**
     * 前面倒计时动画
     */
    protected class ForeView extends SurfaceView implements SurfaceHolder.Callback {
        private Context context;
        private SurfaceHolder mSurfaceHolder;
        private Paint forePaint;
        private TimerThread timerThread;

        public ForeView(Context context) {
            super(context);
            this.context = context;
            mSurfaceHolder = getHolder();
            mSurfaceHolder.addCallback(this);
            mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
            setZOrderOnTop(true);

            forePaint = new Paint();
            forePaint.setStyle(Paint.Style.FILL);

            forePaint.setColor(ContextCompat.getColor(context, R.color.c_FFB700));
            timerThread = new ForeView.TimerThread();
        }


        @Override
        public void surfaceCreated(final SurfaceHolder surfaceHolder) {
            // 自动开始
            timerThread.start();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }

        /**
         * 倒计时动画更新线程
         */
        private class TimerThread extends Thread {
            private boolean isStop = false;

            public TimerThread() {
                super();
            }

            @Override
            public void run() {
                while (time > 0 && !isStop && mSurfaceHolder != null) {
                    for (int i = 0; i <= 31; i++) {
                        Canvas canvas = mSurfaceHolder.lockCanvas();
                        if (canvas == null) {
                            break;
                        }
                        // 重绘进度
                        for (int d = 0; d <= i; d++) {
                            canvas.drawRect(rect, forePaint);
                            canvas.rotate(-6, radius, radius);
                            canvas.drawRect(rect, forePaint);
                            canvas.rotate(-6, radius, radius);
                        }
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                        // 暂停时间
                        try {
                            Thread.sleep(34);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    time--;
                    // 当旋转一圈改变进度条颜色
                    handler.sendMessage(new Message());
                    if (time % 2 == 0) {
                        forePaint.setColor(ContextCompat.getColor(context, R.color.c_5A492F));
                    } else {
                        forePaint.setColor(ContextCompat.getColor(context, R.color.c_FFB700));
                    }

                }

                if (onFinishListener != null) {
                    onFinishListener.onFinish();
                }
            }

            public void onStop() {
                isStop = true;
            }
        }

    }


}
