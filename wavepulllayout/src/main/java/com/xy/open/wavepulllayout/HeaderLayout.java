package com.xy.open.wavepulllayout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.annotation.RequiresApi;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

/**
 * Created by 171842474@qq.com on 2017/3/14.
 */
class HeaderLayout extends FrameLayout {

    private Paint mPaint;
    private Path mPath;
    @ColorInt
    private int mColor = Color.GRAY;
    private ViewOutlineProvider mViewOutlineProvider;
    private float mPointX;
    float mHeaderHeight = 0;
    float mPullHeight = 0;
    private Bitmap rbmp;
    private Bitmap lbmp;
    private static DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator(10);
    private float offsetY;
    private Bitmap cbmp;
    private TextPaint mTpaint;
    private StringBuilder tBuilder;

    static final int STATE_IDLE = 0;
    static final int STATE_DRAGGING = 1;
    static final int STATE_REFRESHING = 2;
    static final int STATE_RELEASE = 3;
    static final int STATE_PERREFRESHING = 4;
    static final int STATE_LOADFINISH = 5;
    private Rect tRect;
    String tips = "";
    private int color;
    private int textColor;
    private float textSize;

    public void setState(int state) {
        this.mState = state;
    }

    public void setColor(int color) {
        this.color = color;
        setBackgroundColor(color);
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        if (mTpaint !=null){
            mTpaint.setColor(textColor);
        }
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
        if (mTpaint !=null){
            mTpaint.setTextSize(textSize);
        }
    }

    public int getColor() {
        return color;
    }

    public float getTextSize() {
        return textSize;
    }

    public int getTextColor() {
        return textColor;
    }

    @IntDef({STATE_IDLE,
            STATE_DRAGGING,
            STATE_REFRESHING,
            STATE_RELEASE,
            STATE_PERREFRESHING,
            STATE_LOADFINISH})
    @interface State {
    }

    @HeaderLayout.State
    private int mState = STATE_IDLE;

    public HeaderLayout(Context context) {
        this(context, null);
    }

    public HeaderLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        setBackgroundColor(Color.GRAY);
    }

    private void init() {
        float dSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                15,
                getContext().getResources().getDisplayMetrics());
        tBuilder = new StringBuilder("");
        setText("");
        mTpaint = new TextPaint();
        mTpaint.setColor(Color.BLUE);
        mTpaint.setAntiAlias(true);
        mTpaint.setTextSize(dSize);
        mTpaint.setStyle(Paint.Style.FILL);
        tRect = new Rect();
        setWillNotDraw(false);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setFilterBitmap(true);


        mPath = new Path();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mViewOutlineProvider = new ViewOutlineProvider() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void getOutline(View view, Outline outline) {
                    if (mPath.isConvex()) outline.setConvexPath(mPath);
                }
            };

        }
    }

    private String setText(String text) {
        tBuilder.delete(0, tBuilder.length());
        tBuilder.append(text);
        return tBuilder.toString();
    }

    public void setCenterText(String text) {
        if (tBuilder != null) {
            tips = setText(text);
            postInvalidate();

        }
    }

    public void setWaveColor(int color) {
        mColor = color;
    }

    public void setPointX(float pointX) {
        boolean needInvalidate = pointX != mPointX;
        mPointX = pointX;
        if (needInvalidate) invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPulling(canvas);
    }

    private void drawPulling(Canvas canvas) {

        final int width = canvas.getWidth();
        mPaint.setColor(mColor);

        int headerHeight = (int) mHeaderHeight;
        int pullHeight = (int) mPullHeight;

        mPath.rewind();
        mPath.moveTo(0, 0);
        mPath.lineTo(0, headerHeight);
        mPath.quadTo(width / 2, pullHeight, width, headerHeight);
        mPath.lineTo(width, 0);
        mPath.close();

        canvas.drawPath(mPath, mPaint);

        offsetY = decelerateInterpolator.getInterpolation(mHeaderHeight / mPullHeight / 2) * mHeaderHeight / 4;
        if (lbmp != null) {
            canvas.drawBitmap(lbmp, width / 4 - lbmp.getWidth() / 2, (mPullHeight - mHeaderHeight - lbmp.getHeight() - offsetY), mPaint);
        }

        if (cbmp != null) {
            switch (mState) {
                case STATE_RELEASE:
                    canvas.drawBitmap(cbmp, width / 2 - cbmp.getWidth() / 2, (mPullHeight/* - mHeaderHeight */ - cbmp.getHeight() - offsetY), mPaint);
                    LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                    params.height = pullHeight;
                    setLayoutParams(params);
                    break;
                case STATE_DRAGGING:
                    tips = setText(getResources().getString(R.string.pulling));
                    int x = width / 2 - cbmp.getWidth() / 2;
                    float y = mPullHeight/* - mHeaderHeight */ - cbmp.getHeight() - offsetY;
                    canvas.drawBitmap(cbmp, x, (y), mPaint);
                    setLocation(x, y);
                    Log.d("LOCATION", "X = " + x + "Y = " + y);
                    break;
                case STATE_PERREFRESHING:
                    tips = setText(getResources().getString(R.string.release_loading));
                    break;
                case STATE_REFRESHING:
                    tips = setText(getResources().getString(R.string.refreshing));
                    break;
                case STATE_LOADFINISH:

                    break;
            }
        }
        mTpaint.getTextBounds(tips, 0, tips.length(), tRect);
        canvas.drawText(tips, width / 2 - tRect.width() / 2, mPullHeight - offsetY / 2 + tRect.height() / 2, mTpaint);
        if (rbmp != null) {
            canvas.drawBitmap(rbmp, 3 * width / 4 - rbmp.getWidth() / 2, (mPullHeight - mHeaderHeight - lbmp.getHeight() - offsetY), mPaint);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setOutlineProvider(mViewOutlineProvider);
        }

    }

    private float[] location = new float[2];

    private float[] reverse(float[] data) {
        float temp = 0;
        temp = data[0];
        data[1] = data[0];
        data[0] = temp;
        return data;
    }

    private void setLocation(float x, float y) {
        this.location[0] = x;
        this.location[1] = y;
    }

    public float[] getLocation() {
        return location;
    }

    public void setLeftBitmap(Bitmap lbmp) {
        this.lbmp = lbmp;
        postInvalidate();

    }

    public void setRightBitmap(Bitmap rbmp) {
        this.rbmp = rbmp;
        postInvalidate();


    }


    public void setCenterBitmap(Bitmap cbmp) {
        this.cbmp = cbmp;
        postInvalidate();
    }
}
