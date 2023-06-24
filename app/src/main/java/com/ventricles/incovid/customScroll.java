package com.ventricles.incovid;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class customScroll extends ScrollView {

    private boolean scrollable = true;

    public customScroll(Context context) {
        super(context);
    }

    public customScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public customScroll(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setscrollenabled(boolean b){
       this.scrollable = b;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (scrollable)
            return super.onTouchEvent(ev);
        else
            return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (scrollable)
            return super.onInterceptTouchEvent(ev);
        else
            return false;
    }
}
