package com.xy.open.wavepulllayout.util;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by admin on 2017/3/15.
 */

public class ViewUtil {
    public static float dp2px(Context context , int value) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                value,
                context.getResources().getDisplayMetrics());
    }
}
