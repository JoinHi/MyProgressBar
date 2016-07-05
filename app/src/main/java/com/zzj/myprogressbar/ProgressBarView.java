package com.zzj.myprogressbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.SystemClock;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bjh on 16/7/1.
 */
public class ProgressBarView extends View {

    private Paint mPaint;
    private int rectPercent = 0;
    private int mWidth;
    private int mHeight;
    private int centerY;
    private float progressWidth = 10;
    private RectF progressRect;
    private Path arrowPath;
    private Matrix matrix;
    private float rotateDegress;
    private Path progressPath;
    private boolean isIdle;
    private float tempDegress;
    private int idleAnimCount;
    private boolean startShowText;
    private TextPaint textPaint;
    private Path gapPath;
    private boolean isReset;

    public ProgressBarView(Context context) {
        this(context, null);
    }

    public ProgressBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        arrowPath = new Path();
        progressPath = new Path();
        matrix = new Matrix();
        textPaint = new TextPaint();
        gapPath = new Path();


    }

    private int rightPercent;
    private int leftPercent;
    private int progressPercent;
    private boolean isFail;
    Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            SystemClock.sleep(500);
            while (rectPercent <100){
                rectPercent+= 5;
                postInvalidate();
                SystemClock.sleep(20);
            }
            while (rightPercent <100){//向右移动
                rightPercent+= 5;
                postInvalidate();
                SystemClock.sleep(20);
            }
            while (leftPercent <100){//向左移动
                leftPercent+= 5;
                postInvalidate();
                SystemClock.sleep(20);
            }
        }
    };

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY){
            mWidth = widthSize;
        }
        if (heightMode == MeasureSpec.EXACTLY){
            mHeight = DensityUtil.dip2px(getContext(),200);
        }
        centerY = mHeight/2;
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBackgroundRect(canvas);
        drawArrow(canvas);
        drawProgress(canvas);
        drawText(canvas);
        super.onDraw(canvas);
    }

    private void drawBackgroundRect(Canvas canvas) {
        int percent = rectPercent;
        if (percent>100){
            percent = 100;
        }
        float progress_left = mWidth/2-centerY/2;
        float progress_top = centerY/2;
        float progress_right = mWidth/2+centerY/2;
        float progress_bottom = centerY*3/2;
        float left = centerY/4;
        mPaint.setColor(Color.parseColor("#FF525253"));
        progressRect = new RectF(getGradientFloat(progress_left,left,percent,100)  ,
                getGradientFloat(progress_top,centerY-progressWidth,percent,100) ,
                getGradientFloat(progress_right,mWidth-left,percent,100),
                getGradientFloat(progress_bottom,centerY+progressWidth,percent,100) );
        canvas.drawRoundRect(progressRect, progressWidth, progressWidth, mPaint);
    }

    private void drawArrow(Canvas canvas) {
        if (rectPercent>0){
            mPaint.setPathEffect(new CornerPathEffect(5));//设置变形后箭头的圆角
        }
        int upPercent = rectPercent;//上升的时间
        int downPercent = 0;//下降的时间
        if (upPercent > 60){
            upPercent = 60;
        }
        if (rectPercent >60 && rectPercent <= 100){
            downPercent = rectPercent -60;
        }
        if (!startShowText) {
            if (leftPercent < 100) {
                if (rightPercent > 0) {
                    rotateDegress = 15.f * rightPercent / 100;
                }
            }
            if (leftPercent == 100) {
                rotateDegress = 0;
                startShowText = true;
            }
        }
        float shapeSize = (1.0f*centerY/8*rectPercent/100);
        float arrowDown = (1.0f*mHeight/4 + 1.2f*centerY/8 - centerY/4 - progressWidth)*(downPercent)/40;
        float arrowUp = 1.0f*mHeight/4*upPercent/60 ;
        float arrowLeft = 1.0f*(/*左边距*/centerY/4 - getWidth()/2 - centerY/4)*leftPercent/100;
        float arrowRight= 1.0f*centerY/4*rightPercent/100  ;
        float moveX = arrowRight + arrowLeft +1.0f*(getWidth() - centerY/2)*progressPercent/100;//x轴方向的移动
        float moveY = arrowUp - arrowDown;//y轴方向的移动
        mPaint.setColor(Color.WHITE);
        arrowPath.reset();
        arrowPath.moveTo((mWidth / 2 - centerY / 8) - shapeSize + moveX, centerY - centerY / 4 - moveY);
        arrowPath.lineTo(mWidth / 2 + centerY / 8 + shapeSize + moveX, centerY - centerY / 4 - moveY);
        arrowPath.lineTo(mWidth / 2 + centerY / 8 + shapeSize + moveX, centerY - moveY);
        arrowPath.lineTo(mWidth / 2 + centerY / 4 - shapeSize*1.2f+moveX, centerY - moveY);
        arrowPath.lineTo(mWidth / 2 + moveX, centerY + centerY / 4 - shapeSize * 1.2f - moveY);
        arrowPath.lineTo(mWidth / 2 - centerY / 4 + shapeSize * 1.2f + moveX, centerY - moveY);
        arrowPath.lineTo(mWidth / 2 - centerY / 8 - shapeSize + moveX, centerY - moveY);
        arrowPath.close();
        matrix.setRotate(rotateDegress, mWidth / 2 + moveX, centerY + centerY / 4 - shapeSize * 1.2f - moveY);
        arrowPath.transform(matrix);
        canvas.drawPath(arrowPath, mPaint);
    }

    private float startY = 0;
    private float endY = 0;
    private int cornerWidth = 0;
    private int failCount = 0;

    private void drawProgress(Canvas canvas) {
        mPaint.setPathEffect(new CornerPathEffect(progressWidth));//设置进度条的圆角，与灰色的progressWidth一致
        float dx = 0;
        int dropY = 0;
        if (isFail && failCount< 100){
            cornerWidth = 5;
            startY = failCount;
            endY = failCount - 50;
            if (startY>50){
                startY = 50;
            }
            if (endY < 0){
                endY = 0;
            }
        }
        float moveX = 1.0f*(getWidth() - centerY/2)*progressPercent/100;
        if (isFail && failCount>100){
            rotateDegress = 20;
            dx =moveX;
            dropY = failCount -100;
        }
        float e = ( 2.0f*progressWidth)*endY/50;
        float s = ( 1.5f*progressWidth)*startY/50 + e*0.25f;
        float dn = 20.0f*dropY;
        /*画缺口*/
        if (isFail){
            gapPath.reset();
            mPaint.setColor(Color.parseColor("#2C97DE"));
            gapPath.moveTo(/*左边距*/centerY / 4 + moveX,centerY);
            gapPath.lineTo(/*左边距*/centerY / 4 + moveX + cornerWidth, centerY + progressWidth );
            gapPath.lineTo(/*左边距*/centerY / 4 + moveX - 10 + cornerWidth, centerY + progressWidth);
            gapPath.close();
            canvas.drawPath(gapPath,mPaint);
        }
        //白色进度条
        progressPath.reset();
        mPaint.setColor(Color.WHITE);
        progressPath.moveTo(/*左边距*/centerY / 4 + dx, centerY - progressWidth + e + dn);
        progressPath.lineTo(/*左边距*/centerY / 4 + moveX, centerY - progressWidth + s+dn);
        progressPath.lineTo(/*左边距*/centerY / 4 + moveX + cornerWidth, centerY + progressWidth+dn);
        progressPath.lineTo(/*左边距*/centerY / 4 + moveX -10 +2* cornerWidth, centerY + progressWidth +3*failCount+dn);
        progressPath.lineTo(/*左边距*/centerY / 4 + moveX -10 + cornerWidth, centerY + progressWidth+dn);
        progressPath.lineTo(/*左边距*/centerY / 4 + dx, centerY + progressWidth + dn);
        progressPath.close();
        canvas.drawPath(progressPath, mPaint);
        if (isFail&&failCount<200) {
            postInvalidate();
            failCount += 2;
        }
        if (failCount>=200){
            resetDownload();
        }
    }
    private void resetDownload() {
        isReset = true;
        reset();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isReset && rectPercent>=0){
                    rectPercent -= 5;
                    if (rectPercent <0){
                        rectPercent = 0;
                    }
                    if (rectPercent == 0){
                        isReset = false;
                    }
                    SystemClock.sleep(20);
                    postInvalidate();
                }
            }
        }).start();
    }
    public void reset(){
        rotateDegress = 0;
        startY = 0;
        endY = 0;
        cornerWidth = 0;
        failCount = 0;
        progressPercent = 0;
        leftPercent = 0;
        rightPercent = 0;
        startShowText = false;
        isFail = false;
        rectPercent = 100;
    }

    private void drawText(Canvas canvas) {
        if (!startShowText){
            return;
        }
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(0.07f * mHeight);
        String text = progressPercent+"%";
        if (isFail && failCount>100){
            textPaint.setColor(Color.RED);
            text = "Failed";
            canvas.drawTextOnPath(text, arrowPath, 15, 35, textPaint);

        }else {
            canvas.drawTextOnPath(text, arrowPath, 30, 35, textPaint);
        }
    }

    /**
     *这个方法的作用是从from到to的距离分成total等分，progress一直在增加等progress等于total时完成了这段距离
     */
    private float getGradientFloat(float from ,float to , int progress , int total){
        return from - (from - to)*progress/total;
    }

    public void start(){
        new Thread(timeRunnable).start();
    }

    public void loadingFail(){
        failCount = 0;
        isFail = true;
        postInvalidate();
    }

    public void setProgressCount(int progress){
        isFail = false;
        failCount = 0;
        if (progress > progressPercent){
            rotateDegress = -15;
        }else {
            rotateDegress = 15;
        }
        startTimerTask();
        progressPercent = progress;
        postInvalidate();
    }

    boolean isTaskRun;
    private Timer timer;
    private TimerTask timerTask;
    private void startTimerTask(){
        if (isTaskRun) {
            timer.cancel();
            timerTask.cancel();
        }
        timer = new Timer();
        timerTask = new MyTimerTask();
        isTaskRun = true;
        timer.schedule(timerTask, 200);
    }

    class MyTimerTask extends TimerTask{
        @Override
        public void run() {
            isTaskRun = false;
            isIdle = true;
            tempDegress = rotateDegress ;
            idleAnimCount = 0;
            setIdleState();
        }
    }
    //进度稍微停止时让箭头旋转复位
    private void setIdleState() {
        if (isIdle) {
            while (idleAnimCount <= 50) {
                int begin = idleAnimCount;
                int end = 0;
                if (begin > 40) {
                    begin = 40;
                }
                if (idleAnimCount > 40) {
                    end = idleAnimCount - 40;
                }
                idleAnimCount += 5;
                SystemClock.sleep(20);
                if (tempDegress > 0) {
                    rotateDegress = getGradientFloat(tempDegress, -10, begin, 40) + end;
                }
                if (tempDegress < 0) {
                    rotateDegress = getGradientFloat(tempDegress, 10, begin, 40) - end;
                }
                if (idleAnimCount > 50) {
                    isIdle = false;
                }
                postInvalidate();
            }
        }
    }
}
