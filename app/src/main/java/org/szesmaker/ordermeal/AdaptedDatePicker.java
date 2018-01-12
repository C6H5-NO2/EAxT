package org.szesmaker.ordermeal;
import android.widget.*;
import android.view.*;
import android.content.*;
import android.util.*;
public class AdaptedDatePicker extends DatePicker {
    public AdaptedDatePicker(Context context) {
        super(context);
    }
    public AdaptedDatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public AdaptedDatePicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override public boolean onInterceptTouchEvent(MotionEvent ev) {
        ViewParent p = getParent();
        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
            if (p != null)
                p.requestDisallowInterceptTouchEvent(true);
        }
        return false;
    }
}
