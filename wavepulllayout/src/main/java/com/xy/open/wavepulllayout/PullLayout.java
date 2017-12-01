package com.xy.open.wavepulllayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

import static com.xy.open.wavepulllayout.util.ViewUtil.dp2px;


/**
 * Created by 171842474@qq.com on 2017/3/14.
 */

public class PullLayout extends FrameLayout {
    private HeaderLayout mHeadLayout;
    private float mStartTouchY;
    private View mChildView;
    private float mCurrentY;
    private float mPullHeight;
    private float mHeaderHeight;
    private static DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator(10);
    private RefreshListener refreshListener;
    private int bgColor;
    private int waveColor;
    private int defaultMode = 2;
    private int mode;

    public PullLayout(@NonNull Context context) {
        this(context, null);
    }

    public PullLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        //add header
        if (attrs == null) return;
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.waveRefreshLayout);

        bgColor = array.getColor(R.styleable.waveRefreshLayout_xy_bgColor, Color.TRANSPARENT);
        waveColor = array.getColor(R.styleable.waveRefreshLayout_xy_waveColor, Color.GRAY);
        float headerHeight = array.getDimension(R.styleable.waveRefreshLayout_xy_headerHeight, mHeaderHeight);
        float pullHeight = array.getDimension(R.styleable.waveRefreshLayout_xy_pullHeight, mPullHeight);
        int textColor = array.getColor(R.styleable.waveRefreshLayout_xy_textColor, Color.GRAY);
        float textSize = array.getDimension(R.styleable.waveRefreshLayout_xy_textSize, dp2px(getContext(), 15));
        int headerLeftImage = array.getResourceId(R.styleable.waveRefreshLayout_xy_headLeftImage, 0);
        int headerRightImage = array.getResourceId(R.styleable.waveRefreshLayout_xy_headRightImage, 0);
        int headerCenterImage = array.getResourceId(R.styleable.waveRefreshLayout_xy_headCenterImage, 0);
        int headerCenterSuccessImage = array.getResourceId(R.styleable.waveRefreshLayout_xy_headCenterSuccessImage, 0);
        mode = array.getInt(R.styleable.waveRefreshLayout_xy_mode,defaultMode);
        mHeaderHeight = headerHeight == 0 ? dp2px(getContext(), 150) : headerHeight;

        mPullHeight = pullHeight == 0 ? dp2px(getContext(), 100) : pullHeight;

        if (mHeadLayout == null) {
            mHeadLayout = new HeaderLayout(getContext(),mode);
            mHeadLayout.setWaveColor(waveColor);
            mHeadLayout.setColor(bgColor);
            mHeadLayout.setTextColor(textColor);
            mHeadLayout.setTextSize(textSize);
            mHeadLayout.setLeftBitmap(BitmapFactory.decodeResource(getResources(), headerLeftImage));
            mHeadLayout.setRightBitmap(BitmapFactory.decodeResource(getResources(), headerRightImage));
            mHeadLayout.setMode(mode);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mHeadLayout.setElevation(getElevation());
            }
            //添加头部
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.TOP | Gravity.CENTER;
            mHeadLayout.setLayoutParams(params);
            Bitmap normal = BitmapFactory.decodeResource(getResources(), headerCenterImage);
            Bitmap success = BitmapFactory.decodeResource(getResources(), headerCenterSuccessImage);
            initCenter(normal, success);
            this.addView(mHeadLayout);
        }
        array.recycle();
    }


    private Bitmap[] centerBits;

    private void initCenter(Bitmap normal, Bitmap success) {
        if (centerBits == null)
            centerBits = new Bitmap[2];
        centerBits[0] = normal;
        centerBits[1] = success;
        setHeaderCenter(centerBits[0]);
    }

    private void setState(State state) {
        this.state = state;
    }

    private State getState() {
        return state;
    }

    private enum State {
        IDLE, DRAGGING, RELEASE, REFRESHING, PERREFRESHING, LOADFINISH;
    }

    private State state = State.IDLE;

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // get child
        mChildView = getChildAt(1);
    }

    private boolean canChildScrollUp() {
        return ViewCompat.canScrollVertically(mChildView, -1);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartTouchY = ev.getY();
                mHeadLayout.setCenterBitmap(centerBits[0]);
                if (state != State.IDLE) {
                    return true;
                }

                break;
            case MotionEvent.ACTION_MOVE:
                float dy = ev.getY() - mStartTouchY;
                if (dy > 0 && !canChildScrollUp()) {
                    state = State.REFRESHING;
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private float constrains(float input, float a, float b) {
        float result = input;
        final float min = Math.min(a, b);
        final float max = Math.max(a, b);
        result = result > min ? result : min;
        result = result < max ? result : max;
        return result;
    }

    public void setOnRefreshListener(RefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }


    private void onTranslationYChanged(float translationY) {
        switch (getState()) {
            case DRAGGING:
            case RELEASE:
                mHeadLayout.mHeaderHeight = Math.min(translationY / 2, mHeaderHeight);
                mHeadLayout.mPullHeight = translationY;
                break;
            case REFRESHING:
                mHeadLayout.mHeaderHeight = mHeaderHeight;
                mHeadLayout.mPullHeight = mPullHeight;
                break;

            case IDLE:
                mHeadLayout.mHeaderHeight = 0;
                mHeadLayout.mPullHeight = 0;
                break;
        }
        mHeadLayout.postInvalidate();
    }

    private float offsetY;

    @Override
    public final boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                mCurrentY = e.getY();
                float dy = constrains(
                        0,
                        mPullHeight * 2,
                        mCurrentY - mStartTouchY);
                if (mChildView != null) {
                    offsetY = decelerateInterpolator.getInterpolation(dy / mPullHeight / 2) * dy / 2;
                    State state = State.DRAGGING;
                    LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                    params.height = (int) offsetY;
                    mHeadLayout.setLayoutParams(params);
                    animChildView(state, offsetY, 0);
                }

                return true;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mChildView != null) {
                    if (offsetY >= mPullHeight) {
                        //bottom
                        animChildView(State.REFRESHING, offsetY, 0);
                        if (refreshListener != null) {
                            refreshListener.onRefresh(this);
                        }
                    } else {//no bottom
                        animChildView(State.RELEASE, 0, 300);
                    }

                } else {
                    setState(State.IDLE);
                }
                return true;
            default:
                return super.onTouchEvent(e);
        }
    }


    public void setRefreshing(boolean refreshing) {
        if (refreshing) {
            animChildView(State.DRAGGING, mPullHeight, 300);
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    setState(State.REFRESHING);
                }
            }, 300);
        } else {
            animChildView(State.RELEASE, 0, 300);
        }
    }

    public void setRefreshing(boolean refreshing, String success) {
        if (refreshing) {
            animChildView(State.DRAGGING, mPullHeight, 300);
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    setState(State.REFRESHING);
                }
            }, 300);
        } else {
            setState(State.LOADFINISH);
            mapHeaderState(State.LOADFINISH);
            mHeadLayout.setCenterText(success);
            mHeadLayout.setCenterBitmap(centerBits[1]);
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    animChildView(State.RELEASE, 0, 300);
                }
            }, 1000);
        }
    }

    public void setRefreshing(boolean refreshing, String success, Bitmap successIcon) {
        if (refreshing) {
            animChildView(State.DRAGGING, mPullHeight, 300);
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    setState(State.REFRESHING);
                }
            }, 300);
        } else {
            mHeadLayout.setCenterText(success);
            mHeadLayout.setCenterBitmap(successIcon);
            mapHeaderState(State.LOADFINISH);
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    animChildView(State.RELEASE, 0, 300);
                }
            }, 1000);
        }
    }

    private void animChildView(State state, float endValue, long duration) {
        setState(state);
        mapHeaderState(state);
        ObjectAnimator oa = ObjectAnimator.ofFloat(mChildView, "translationY", endValue);
        oa.setDuration(duration);
        oa.setInterpolator(new DecelerateInterpolator());
        oa.addListener(new com.nineoldandroids.animation.Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(com.nineoldandroids.animation.Animator animation) {
                switch (getState()) {
                    case RELEASE:
                        break;
                }

            }

            @Override
            public void onAnimationEnd(com.nineoldandroids.animation.Animator animation) {
                switch (getState()) {
                    case REFRESHING:
                        setState(State.REFRESHING);
                        break;
                    case RELEASE:
                        setState(State.IDLE);
                        break;
                    case DRAGGING:
                        setState(State.IDLE);
                        break;
                    default:
                        setState(State.IDLE);
                        break;

                }

            }

            @Override
            public void onAnimationCancel(com.nineoldandroids.animation.Animator animation) {

            }

            @Override
            public void onAnimationRepeat(com.nineoldandroids.animation.Animator animation) {

            }
        });
        oa.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                Log.d("PULL", value + "");
                if (getState() != State.REFRESHING)
                    onTranslationYChanged(value);
            }
        });
        oa.start();
    }

    private void mapHeaderState(State state) {
        switch (state) {
            case LOADFINISH:
                mHeadLayout.setState(5);
                break;
            case PERREFRESHING:
                mHeadLayout.setState(4);
                break;
            case RELEASE:
                mHeadLayout.setState(3);
                break;
            case REFRESHING:
                mHeadLayout.setState(2);
                break;
            case DRAGGING:
                mHeadLayout.setState(1);
                break;
            case IDLE:
                mHeadLayout.setState(0);
                break;

        }
    }

    public void setHeaderLeft(Bitmap bitmap) {
        if (mHeadLayout != null) {
            mHeadLayout.setLeftBitmap(bitmap);
        }
    }

    public void setHeaderRight(Bitmap bitmap) {
        if (mHeadLayout != null) {
            mHeadLayout.setRightBitmap(bitmap);
        }
    }

    public void setHeaderCenter(Bitmap bitmap) {
        if (mHeadLayout != null) {
            mHeadLayout.setCenterBitmap(bitmap);
            if (centerBits != null) {
                centerBits[0] = bitmap;
            }
        }
    }

}
