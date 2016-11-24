package com.example.developer.custom.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.developer.custom.R;

import java.util.ArrayList;

/**
 * Created by Developer on 2016/9/9.
 */
public class Arc extends View {

    private static int INVALID_PROGRESS_VALUE = -1;
    // The initial rotational offset -90 means we start at 12 o'clock
    private final int mAngleOffset = -90;
    public static final int POINT_THREE = 3;
    public static final int POINT_FOUR = 4;
    public static final int POINT_FIVE = 5;
    public static final int POINT_SIX_START = 6;
    public static final int POINT_SIX_END = 7;
    public static final int POINT_SEVEN = 8;
    ArrayList<String> text4Point5;

    private int mPointNum = 5;


    public void setPointNum(int pointNum, ArrayList<String> texts) {
        mPointNum = pointNum;
        text4Point5.clear();
        text4Point5.addAll(texts);
        invalidate();
    }

    public int getPointNum() {

        return mPointNum;
    }

    /**
     * The Maximum value that this T3SeekArc can be set to
     */
    private int mMax = 100;

    /**
     * The Current value that the T3SeekArc is set to
     */
    private int mProgress = 0;

    /**
     * The width of the progress line for this T3SeekArc
     */
    private int mProgressWidth = 4;

    /**
     * The Width of the background arc for the T3SeekArc
     */
    private int mArcWidth = 2;

    /**
     * The Angle to start drawing this Arc from
     */
    private int mStartAngle = 0;

    /**
     * The Angle through which to draw the arc (Max is 360)
     */
    private int mSweepAngle = 360;

    /**
     * The rotation of the T3SeekArc- 0 is twelve o'clock
     */
    private int mRotation = 0;

    /**
     * Give the T3SeekArc rounded edges
     */
    private boolean mRoundedEdges = false;

    /**
     * Enable touch inside the T3SeekArc
     */
    private boolean mTouchInside = true;

    /**
     * Will the progress increase clockwise or anti-clockwise
     */
    private boolean mClockwise = true;

    // Internal variables
    private int mArcRadius = 0;
    private float mProgressSweep = 0.01f;
    private RectF mArcRect = new RectF();
    private Paint mArcPaint;
    private Paint mProgressPaint;
    private Paint textPaint;
    private int mTranslateX;
    private int mTranslateY;
    private int mThumbXPos;
    private int mThumbYPos;


    public Arc(Context context) {
        super(context);
        init(context, null, 0);
    }

    public Arc(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, R.attr.seekArcStyle);
    }

    public Arc(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        mPointNum = POINT_SEVEN;
        final Resources res = getResources();
        float density = context.getResources().getDisplayMetrics().density;

        // Defaults, may need to link this into theme settings
        int arcColor = res.getColor(R.color.cooker_progress_gray);
        int progressColor = res.getColor(R.color.text_prompt_color);
        // Convert progress width to pixels for current density
        mProgressWidth = (int) (mProgressWidth * density);

        if (attrs != null) {
            // Attribute initialization
            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.cooker_SeekArc, defStyle, 0);

            mMax = a.getInteger(R.styleable.cooker_SeekArc_cooker_max, mMax);
            mProgress = a.getInteger(R.styleable.cooker_SeekArc_cooker_progress, mProgress);
            mProgressWidth = (int) a.getDimension(R.styleable.cooker_SeekArc_cooker_progressWidth, mProgressWidth);
            mArcWidth = (int) a.getDimension(R.styleable.cooker_SeekArc_cooker_arcWidth, mArcWidth);
            mStartAngle = a.getInt(R.styleable.cooker_SeekArc_cooker_startAngle, mStartAngle);
            mSweepAngle = a.getInt(R.styleable.cooker_SeekArc_cooker_sweepAngle, mSweepAngle);
            mRotation = a.getInt(R.styleable.cooker_SeekArc_cooker_rotation, mRotation);
            mRoundedEdges = a.getBoolean(R.styleable.cooker_SeekArc_cooker_roundEdges, mRoundedEdges);
            mTouchInside = a.getBoolean(R.styleable.cooker_SeekArc_cooker_touchInside, mTouchInside);
            mClockwise = a.getBoolean(R.styleable.cooker_SeekArc_cooker_clockwise, mClockwise);

            arcColor = a.getColor(R.styleable.cooker_SeekArc_cooker_arcColor, arcColor);
            progressColor = a.getColor(R.styleable.cooker_SeekArc_cooker_progressColor, progressColor);

            a.recycle();
        }

        mProgress = (mProgress > mMax) ? mMax : mProgress;
        mProgress = (mProgress < 0) ? 0 : mProgress;

        mSweepAngle = (mSweepAngle > 360) ? 360 : mSweepAngle;
        mSweepAngle = (mSweepAngle < 0) ? 0 : mSweepAngle;

        mStartAngle = (mStartAngle > 360) ? 0 : mStartAngle;
        mStartAngle = (mStartAngle < 0) ? 0 : mStartAngle;

        mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//
        mArcPaint.setColor(arcColor);
        mArcPaint.setAntiAlias(true);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeWidth(mArcWidth);
        // mArcPaint.setAlpha(45);

        mProgressPaint = new Paint();//
        mProgressPaint.setColor(progressColor);
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeWidth(mProgressWidth);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//
        textPaint.setColor(progressColor);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(14);

        if (mRoundedEdges) {
            mArcPaint.setStrokeCap(Paint.Cap.ROUND);
            mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        }
        text4Point5 = new ArrayList<String>();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (!mClockwise) {
            canvas.scale(-1, 1, mArcRect.centerX(), mArcRect.centerY());
        }

        // Draw the arcs
        final int arcStart = mStartAngle + mAngleOffset + mRotation;
        final int arcSweep = mSweepAngle;
        canvas.drawArc(mArcRect, (float) arcStart, arcSweep, false, mArcPaint);
        canvas.drawArc(mArcRect, (float) arcStart, mProgressSweep, false, mProgressPaint);


        drawPoint(canvas, mPointNum, mArcRect.centerX(), mArcRect.centerY());

        // Draw the thumb nail
        canvas.translate(mTranslateX - mThumbXPos, mTranslateY - mThumbYPos);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int min = Math.min(width, height);
        float top = 0;
        float left = 0;
        //
        int arcDiameter = 0;

        mTranslateX = (int) (width * 0.5f);
        mTranslateY = (int) (height * 0.5f);

        arcDiameter = min - getPaddingLeft();
        //
        mArcRadius = arcDiameter / 2;
        top = height / 2 - (arcDiameter / 2);
        left = width / 2 - (arcDiameter / 2);
        mArcRect.set(left, top, left + arcDiameter, top + arcDiameter);

        int arcStart = (int) mProgressSweep + mStartAngle + mRotation + 90;
        mThumbXPos = (int) (mArcRadius * Math.cos(Math.toRadians(arcStart)));
        mThumbYPos = (int) (mArcRadius * Math.sin(Math.toRadians(arcStart)));

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        invalidate();
    }


    /**
     * @param xPos
     * @param yPos
     * @return
     */
    private double getTouchDegrees(float xPos, float yPos) {
        float x = xPos - mTranslateX;
        float y = yPos - mTranslateY;
        x = (mClockwise) ? x : -x;
        double angle = Math.toDegrees(Math.atan2(y, x) + (Math.PI / 2) - Math.toRadians(mRotation));
        if (angle < 0) {
            angle = 360 + angle;
        }
        angle -= mStartAngle;
        return angle;
    }

    /**
     * @param angle
     * @return
     */
    private int getProgressForAngle(double angle) {
        int touchProgress = (int) Math.round(valuePerDegree() * angle);
        touchProgress = (touchProgress < 0) ? INVALID_PROGRESS_VALUE : touchProgress;
        touchProgress = (touchProgress > mMax) ? INVALID_PROGRESS_VALUE : touchProgress;
        return touchProgress;
    }

    private float valuePerDegree() {
        return (float) mMax / mSweepAngle;
    }

    /**
     * @param progress
     * @param fromUser
     */
    private void onProgressRefresh(int progress, boolean fromUser) {
        updateProgress(progress, fromUser);
    }

    /**
     */
    private void updateThumbPosition() {
        int thumbAngle = (int) (mStartAngle + mProgressSweep + mRotation + 90);
        mThumbXPos = (int) (mArcRadius * Math.cos(Math.toRadians(thumbAngle)));
        mThumbYPos = (int) (mArcRadius * Math.sin(Math.toRadians(thumbAngle)));
    }

    /**
     * @param progress
     * @param fromUser
     */
    private void updateProgress(int progress, boolean fromUser) {
        if (progress == INVALID_PROGRESS_VALUE) {
            return;
        }

        progress = (progress > mMax) ? mMax : progress + 5;
        progress = (mProgress < 0) ? 0 : progress - 5;

        mProgress = progress;
        mProgressSweep = (float) progress / mMax * mSweepAngle;
        if (mProgressSweep <= 0) {
            mProgressSweep = 0.01f;
        }
        updateThumbPosition();
        invalidate();
    }

    public void setProgress(int progress) {
        updateProgress(progress, false);
    }

    public int getProgressWidth() {
        return mProgressWidth;
    }

    public void setProgressWidth(int mProgressWidth) {
        this.mProgressWidth = mProgressWidth;
        mProgressPaint.setStrokeWidth(mProgressWidth);
    }

    public int getArcWidth() {
        return mArcWidth;
    }

    public void setArcWidth(int mArcWidth) {
        this.mArcWidth = mArcWidth;
        mArcPaint.setStrokeWidth(mArcWidth);
    }

    public int getArcRotation() {
        return mRotation;
    }

    public void setArcRotation(int mRotation) {
        this.mRotation = mRotation;
        updateThumbPosition();
    }

    public int getStartAngle() {
        return mStartAngle;
    }

    public void setStartAngle(int mStartAngle) {
        this.mStartAngle = mStartAngle;
        updateThumbPosition();
    }

    public int getSweepAngle() {
        return mSweepAngle;
    }

    public void setSweepAngle(int mSweepAngle) {
        this.mSweepAngle = mSweepAngle;
        updateThumbPosition();
    }

    public void setRoundedEdges(boolean isEnabled) {
        mRoundedEdges = isEnabled;
        if (mRoundedEdges) {
            mArcPaint.setStrokeCap(Paint.Cap.ROUND);
            mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        } else {
            mArcPaint.setStrokeCap(Paint.Cap.SQUARE);
            mProgressPaint.setStrokeCap(Paint.Cap.SQUARE);
        }
    }


    public void setClockwise(boolean isClockwise) {
        mClockwise = isClockwise;
    }

    public void writeText(Canvas canvas, float x, float y, String text, float angle, boolean isChangeColor) {
        if (canvas != null) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.BLACK);
            if (isChangeColor)
                paint.setARGB(255, 252, 111, 71);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(30f);
            if (angle != 0) {
                canvas.rotate(angle, x, y);
            }
            canvas.drawText(text, x, y, paint);
            if (angle != 0) {
                canvas.rotate(-angle, x, y);
            }
        }

    }

    @SuppressWarnings("unused")
    private void drawText(Canvas canvas, float x, float y, String text) {
        if (canvas != null) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.BLACK);

            paint.setTextSize(50f);

            canvas.drawText(text, x, y, paint);
        }

    }

    /*
     * 在进度条上画点和写字
     */
    private void drawPoint(Canvas canvas, int num, float cx, float cy) {
        Paint paintFirst = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintFirst.setARGB(255, 216, 216, 216);
        Paint paintSecond = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintSecond.setARGB(255, 252, 111, 71);
        Paint tempPaint = paintFirst;
        if (num == POINT_THREE) {
            if (mProgress >= 50) {
                tempPaint = paintSecond;
            }
            canvas.drawCircle((float) (cx), (float) (cy - mArcRadius), 10f, tempPaint);
            tempPaint = paintFirst;

            writeText(canvas, (float) (cx - mArcRadius * Math.sin(3.14 / (180 / 60))),
                    (float) (cy + mArcRadius * Math.cos(3.14 / (180 / 60))) + 60, text4Point5.get(0), 0,
                    mProgress == 0);
            writeText(canvas, (float) (cx), (float) (cy - mArcRadius) - 20, text4Point5.get(1), 0f, mProgress == 50);

            writeText(canvas, (float) (cx + mArcRadius * Math.sin(3.14 / (180 / 60))),
                    (float) (cy + mArcRadius * Math.cos(3.14 / (180 / 60))) + 60, text4Point5.get(2), 0,
                    mProgress == 100);
        }
        if (num == POINT_FOUR) {
            if (mProgress >= 32)
                tempPaint = paintSecond;
            canvas.drawCircle(cx - (float) (mArcRadius * Math.sin(3.14 / (180 / 40))),
                    cy - (float) (mArcRadius * Math.cos(3.14 / (180 / 40))), 10f, tempPaint);// progress
            tempPaint = paintFirst;

            if (mProgress >= 68)
                tempPaint = paintSecond;
            canvas.drawCircle(cx + (float) (mArcRadius * Math.sin(3.14 / (180 / 40))),
                    cy - (float) (mArcRadius * Math.cos(3.14 / (180 / 40))), 10f, tempPaint);// progress
            tempPaint = paintFirst; // 77

            writeText(canvas, (float) (cx - mArcRadius * Math.sin(3.14 / (180 / 60))),
                    (float) (cy + mArcRadius * Math.cos(3.14 / (180 / 60))) + 60, text4Point5.get(0), 0,
                    mProgress == 0);

            writeText(canvas, cx - (float) (mArcRadius * Math.sin(3.14 / (180 / 40))) - 20,
                    cy - (float) (mArcRadius * Math.cos(3.14 / (180 / 40))) - 10, text4Point5.get(1), -50f,
                    mProgress == 32);

            writeText(canvas, cx + (float) (mArcRadius * Math.sin(3.14 / (180 / 40))) + 20,
                    cy - (float) (mArcRadius * Math.cos(3.14 / (180 / 40))) - 10, text4Point5.get(2), 50f,
                    mProgress == 69);

            writeText(canvas, (float) (cx + mArcRadius * Math.sin(3.14 / (180 / 60))),
                    (float) (cy + mArcRadius * Math.cos(3.14 / (180 / 60))) + 60, text4Point5.get(3), 0,
                    mProgress == 100);
        }
        if (num == POINT_FIVE) {
            if (mProgress >= 25)
                tempPaint = paintSecond;
            canvas.drawCircle((float) (cx - mArcRadius * Math.cos(3.14 / (180 / 30))),
                    (float) (cy - mArcRadius * Math.sin(3.14 / (180 / 30))), 10f, tempPaint);// progress
            // 32
            tempPaint = paintFirst;

            if (mProgress >= 50)
                tempPaint = paintSecond;
            canvas.drawCircle((float) (cx), (float) (cy - mArcRadius), 10f, tempPaint);// progress
            // 69
            tempPaint = paintFirst;

            if (mProgress >= 75)
                tempPaint = paintSecond;
            canvas.drawCircle((float) (cx + mArcRadius * Math.cos(3.14 / (180 / 30))),
                    (float) (cy - mArcRadius * Math.sin(3.14 / (180 / 30))), 10f, tempPaint);// progress
            // 69

            writeText(canvas, (float) (cx - mArcRadius * Math.sin(3.14 / (180 / 60))),
                    (float) (cy + mArcRadius * Math.cos(3.14 / (180 / 60))) + 60, text4Point5.get(0), 0,
                    mProgress == 0);
            writeText(canvas, (float) (cx - mArcRadius * Math.cos(3.14 / (180 / 30))) - 30,
                    (float) (cy - mArcRadius * Math.sin(3.14 / (180 / 30))) - 10, text4Point5.get(1), -60f,
                    mProgress == 25);
            writeText(canvas, (float) (cx), (float) (cy - mArcRadius) - 20, text4Point5.get(2), 0f, mProgress == 50);
            writeText(canvas, (float) (cx + mArcRadius * Math.cos(3.14 / (180 / 30))) + 20,
                    (float) (cy - mArcRadius * Math.sin(3.14 / (180 / 30))) - 10, text4Point5.get(3), 60f,
                    mProgress == 75);

            writeText(canvas, (float) (cx + mArcRadius * Math.sin(3.14 / (180 / 60))),
                    (float) (cy + mArcRadius * Math.cos(3.14 / (180 / 60))) + 60, text4Point5.get(4), 0,
                    mProgress == 100);
        }
        if (num == POINT_SIX_START) {
            tempPaint = paintFirst;
            if (mProgress >= 20)
                tempPaint = paintSecond;
            canvas.drawCircle(cx - (float) (mArcRadius * Math.cos(3.14 / (180 / 18))),
                    cy - (float) (mArcRadius * Math.sin(3.14 / (180 / 18))), 10f, tempPaint);// progress
            // 23
            tempPaint = paintFirst;
            if (mProgress >= 40)
                tempPaint = paintSecond;
            canvas.drawCircle(cx - (float) (mArcRadius * Math.sin(3.14 / (180 / 24))),
                    cy - (float) (mArcRadius * Math.cos(3.14 / (180 / 24))), 10f, tempPaint);// progress
            tempPaint = paintFirst;
            if (mProgress >= 60)
                tempPaint = paintSecond;
            canvas.drawCircle(cx + (float) (mArcRadius * Math.sin(3.14 / (180 / 24))),
                    cy - (float) (mArcRadius * Math.cos(3.14 / (180 / 24))), 10f, tempPaint);// progress
            // 77
            tempPaint = paintFirst;
            if (mProgress >= 80)
                tempPaint = paintSecond;
            canvas.drawCircle(cx + (float) (mArcRadius * Math.cos(3.14 / (180 / 18))),
                    cy - (float) (mArcRadius * Math.sin(3.14 / (180 / 18))), 10f, tempPaint);// progress
            // 77
            writeText(canvas, (float) (cx - mArcRadius * Math.sin(3.14 / (180 / 60))),
                    (float) (cy + mArcRadius * Math.cos(3.14 / (180 / 60))) + 60, text4Point5.get(0), 0,
                    mProgress == 0);
            writeText(canvas, cx - (float) (mArcRadius * Math.cos(3.14 / (180 / 18))) - 20,
                    cy - (float) (mArcRadius * Math.sin(3.14 / (180 / 18))), text4Point5.get(1), -72f, mProgress == 20);
            writeText(canvas, cx - (float) (mArcRadius * Math.sin(3.14 / (180 / 24))) - 10,
                    cy - (float) (mArcRadius * Math.cos(3.14 / (180 / 24))) - 20, text4Point5.get(2), -24f,
                    mProgress == 40);
            writeText(canvas, cx + (float) (mArcRadius * Math.sin(3.14 / (180 / 24))),
                    cy - (float) (mArcRadius * Math.cos(3.14 / (180 / 24))) - 20, text4Point5.get(3), 24f,
                    mProgress == 60);
            writeText(canvas, cx + (float) (mArcRadius * Math.cos(3.14 / (180 / 18))) + 20,
                    cy - (float) (mArcRadius * Math.sin(3.14 / (180 / 18))) - 10, text4Point5.get(4), 72f,
                    mProgress == 80);

            writeText(canvas, (float) (cx + mArcRadius * Math.sin(3.14 / (180 / 60))),
                    (float) (cy + mArcRadius * Math.cos(3.14 / (180 / 60))) + 60, text4Point5.get(5), 0,
                    mProgress == 100);

        }
        if (num == POINT_SIX_END) {
            tempPaint = paintFirst;
            if (mProgress >= 20)
                tempPaint = paintSecond;
            canvas.drawCircle(cx - (float) (mArcRadius * Math.cos(3.14 / (180 / 18))),
                    cy - (float) (mArcRadius * Math.sin(3.14 / (180 / 18))), 10f, tempPaint);// progress
            // 23
            tempPaint = paintFirst;
            if (mProgress >= 40)
                tempPaint = paintSecond;
            canvas.drawCircle(cx - (float) (mArcRadius * Math.sin(3.14 / (180 / 24))),
                    cy - (float) (mArcRadius * Math.cos(3.14 / (180 / 24))), 10f, tempPaint);// progress
            tempPaint = paintFirst;
            if (mProgress >= 60)
                tempPaint = paintSecond;
            canvas.drawCircle(cx + (float) (mArcRadius * Math.sin(3.14 / (180 / 24))),
                    cy - (float) (mArcRadius * Math.cos(3.14 / (180 / 24))), 10f, tempPaint);// progress
            // 77
            tempPaint = paintFirst;
            if (mProgress >= 80)
                tempPaint = paintSecond;
            canvas.drawCircle(cx + (float) (mArcRadius * Math.cos(3.14 / (180 / 18))),
                    cy - (float) (mArcRadius * Math.sin(3.14 / (180 / 18))), 10f, tempPaint);// progress
            // 77

            writeText(canvas, cx - (float) (mArcRadius * Math.cos(3.14 / (180 / 18))) - 20,
                    cy - (float) (mArcRadius * Math.sin(3.14 / (180 / 18))), text4Point5.get(2), -72f, mProgress == 20);
            writeText(canvas, cx - (float) (mArcRadius * Math.sin(3.14 / (180 / 24))) - 10,
                    cy - (float) (mArcRadius * Math.cos(3.14 / (180 / 24))) - 20, text4Point5.get(3), -24f,
                    mProgress == 40);
            writeText(canvas, cx + (float) (mArcRadius * Math.sin(3.14 / (180 / 24))),
                    cy - (float) (mArcRadius * Math.cos(3.14 / (180 / 24))) - 20, text4Point5.get(4), 24f,
                    mProgress == 60);
            writeText(canvas, cx + (float) (mArcRadius * Math.cos(3.14 / (180 / 18))) + 20,
                    cy - (float) (mArcRadius * Math.sin(3.14 / (180 / 18))) - 10, text4Point5.get(5), 72f,
                    mProgress == 80);

            writeText(canvas, (float) (cx - mArcRadius * Math.sin(3.14 / (180 / 60))),
                    (float) (cy + mArcRadius * Math.cos(3.14 / (180 / 60))) + 60, text4Point5.get(1), 0,
                    mProgress == 0);
            writeText(canvas, (float) (cx + mArcRadius * Math.sin(3.14 / (180 / 60))),
                    (float) (cy + mArcRadius * Math.cos(3.14 / (180 / 60))) + 60, text4Point5.get(6), 0,
                    mProgress == 100);

        }
        if (num == POINT_SEVEN) {
            tempPaint = paintFirst;
            if (mProgress >= 17)
                tempPaint = paintSecond;
            canvas.drawCircle(cx - (float) (mArcRadius * Math.cos(3.14 / (180 / 10))),
                    cy - (float) (mArcRadius * Math.sin(3.14 / (180 / 10))), 10f, tempPaint);// progress
            // 23
            tempPaint = paintFirst;
            if (mProgress >= 34)
                tempPaint = paintSecond;
            canvas.drawCircle(cx - (float) (mArcRadius * Math.sin(3.14 / (180 / 40))),
                    cy - (float) (mArcRadius * Math.cos(3.14 / (180 / 40))), 10f, tempPaint);// progress
            tempPaint = paintFirst;
            if (mProgress >= 50)
                tempPaint = paintSecond;
            canvas.drawCircle(cx, cy - (float) (mArcRadius), 10f, tempPaint);// progress
            // 77
            tempPaint = paintFirst;
            if (mProgress >= 66)
                tempPaint = paintSecond;
            canvas.drawCircle(cx + (float) (mArcRadius * Math.sin(3.14 / (180 / 40))),
                    cy - (float) (mArcRadius * Math.cos(3.14 / (180 / 40))), 10f, tempPaint);// progress
            // 77
            if (mProgress >= 83)
                tempPaint = paintSecond;
            canvas.drawCircle(cx + (float) (mArcRadius * Math.cos(3.14 / (180 / 10))),
                    cy - (float) (mArcRadius * Math.sin(3.14 / (180 / 10))), 10f, tempPaint);// progress
            // 77

            writeText(canvas, cx - (float) (mArcRadius * Math.cos(3.14 / (180 / 10))) - 20,
                    cy - (float) (mArcRadius * Math.sin(3.14 / (180 / 10))), text4Point5.get(1), -80f, mProgress == 17);
            writeText(canvas, cx - (float) (mArcRadius * Math.sin(3.14 / (180 / 40))) - 20,
                    cy - (float) (mArcRadius * Math.cos(3.14 / (180 / 40))) - 10, text4Point5.get(2), -50f,
                    mProgress == 34);
            writeText(canvas, cx, cy - (float) (mArcRadius) - 20, text4Point5.get(3), 0f, mProgress == 50);
            writeText(canvas, cx + (float) (mArcRadius * Math.sin(3.14 / (180 / 40))) + 20,
                    cy - (float) (mArcRadius * Math.cos(3.14 / (180 / 40))) - 10, text4Point5.get(4), 50f,
                    mProgress == 66);
            writeText(canvas, cx + (float) (mArcRadius * Math.cos(3.14 / (180 / 10))) + 20,
                    cy - (float) (mArcRadius * Math.sin(3.14 / (180 / 10))), text4Point5.get(5), 80f, mProgress == 83);

            writeText(canvas, (float) (cx - mArcRadius * Math.sin(3.14 / (180 / 60))),
                    (float) (cy + mArcRadius * Math.cos(3.14 / (180 / 60))) + 60, text4Point5.get(0), 0,
                    mProgress == 0);
            writeText(canvas, (float) (cx + mArcRadius * Math.sin(3.14 / (180 / 60))),
                    (float) (cy + mArcRadius * Math.cos(3.14 / (180 / 60))) + 60, text4Point5.get(6), 0,
                    mProgress == 100);
        }
        canvas.drawCircle((float) (cx - mArcRadius * Math.sin(3.14 / (180 / 60))),
                (float) (cy + mArcRadius * Math.cos(3.14 / (180 / 60))), 10f, paintSecond);
        tempPaint = paintFirst;
        if (mProgress >= 100)
            tempPaint = paintSecond;
        canvas.drawCircle((float) (cx + mArcRadius * Math.sin(3.14 / (180 / 60))),
                (float) (cy + mArcRadius * Math.cos(3.14 / (180 / 60))), 10f, tempPaint);

    }

    public void progressChange(int i) {

        if (mPointNum == POINT_THREE) {
            switch (i) {
                case 0:
                case 1:
                    setProgress(0);
                    break;
                case 2:
                    setProgress(50);
                    break;
                case 3:
                    setProgress(100);
                    break;

            }
        } else if (mPointNum == POINT_FOUR) {
            switch (i) {
                case 0:
                case 1:
                    setProgress(0);
                    break;
                case 2:
                    setProgress(32);
                    break;
                case 3:
                    setProgress(69);
                    break;
                case 4:
                    setProgress(100);
                    break;
            }
        } else if (mPointNum == POINT_FIVE) {
            switch (i) {
                case 0:
                case 1:
                    setProgress(0);
                    break;
                case 2:
                    setProgress(25);
                    break;
                case 3:
                    setProgress(50);
                    break;
                case 4:
                    setProgress(75);
                    break;
                case 5:
                    setProgress(100);
                    break;
            }
        } else {
            if (mPointNum == POINT_SEVEN) {
                switch (i) {
                    case 0:
                        setProgress(0);
                        break;
                    case 1:
                        setProgress(17);
                        break;
                    case 2:
                        setProgress(34);
                        break;
                    case 3:
                        setProgress(50);
                        break;
                    case 4:
                        setProgress(66);
                        break;
                    case 5:
                        setProgress(83);
                        break;
                    case 6:
                        setProgress(100);
                        break;
                    case 7:
                        setProgress(0);
                        break;
                }
            } else {
                if (mPointNum == POINT_SIX_START) {
                    switch (i) {
                        case 0:
                            setProgress(0);
                            break;
                        case 1:
                            setProgress(20);
                            break;
                        case 2:
                            setProgress(40);
                            break;
                        case 3:
                            setProgress(60);
                            break;
                        case 4:
                            setProgress(80);
                            break;
                        case 5:
                            setProgress(100);
                            break;
                        case 6:
                            setProgress(100);
                            break;
                    }
                } else {
                    if (mPointNum == POINT_SIX_END) {
                        switch (i) {
                            case 1:
                                setProgress(0);
                                break;
                            case 2:
                                setProgress(20);
                                break;
                            case 3:
                                setProgress(40);
                                break;
                            case 4:
                                setProgress(60);
                                break;
                            case 5:
                                setProgress(80);
                                break;
                            case 6:
                                setProgress(100);
                                break;
                            case 0:
                                setProgress(0);
                                break;
                        }
                    }
                }
            }
        }
    }

    /**
     * 进度步进
     */
    public void progressAdd() {

        if (mPointNum == POINT_FIVE) {
            if (mProgress == 0)
                setProgress(25);
            else if (mProgress == 25)
                setProgress(50);
            else if (mProgress == 50)
                setProgress(75);
            else if (mProgress == 75)
                setProgress(100);
            else if (mProgress == 100)
                ;
            // setProgress(23);
        } else {
            if (mPointNum == POINT_SEVEN) {
                if (mProgress == 0)
                    setProgress(17);
                else if (mProgress == 17)
                    setProgress(34);
                else if (mProgress == 34)
                    setProgress(50);
                else if (mProgress == 50)
                    setProgress(66);
                else if (mProgress == 66)
                    setProgress(83);
                else if (mProgress == 83)
                    setProgress(100);
                else if (mProgress == 100)
                    ;
            } else {
                if (mPointNum == POINT_SIX_START) {
                    if (mProgress == 0)
                        setProgress(20);
                    else if (mProgress == 20)
                        setProgress(40);
                    else if (mProgress == 40)
                        setProgress(60);
                    else if (mProgress == 60)
                        setProgress(80);
                    else if (mProgress == 80)
                        setProgress(100);
                    else if (mProgress == 100)
                        ;
                } else {
                    if (mProgress == 0)
                        setProgress(20);
                    else if (mProgress == 20)
                        setProgress(40);
                    else if (mProgress == 40)
                        setProgress(60);
                    else if (mProgress == 60)
                        setProgress(80);
                    else if (mProgress == 80)
                        setProgress(100);
                    else if (mProgress == 100)
                        ;
                }
            }
        }
    }
}
