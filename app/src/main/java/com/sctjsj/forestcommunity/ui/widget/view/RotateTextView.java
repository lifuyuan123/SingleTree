package com.sctjsj.forestcommunity.ui.widget.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by mayikang on 17/8/11.
 */

public class RotateTextView extends android.support.v7.widget.AppCompatTextView {
    public RotateTextView(Context context) {
        super(context);
    }

    public RotateTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.rotate(-45,getWidth()/2,getHeight()/2);
        super.onDraw(canvas);
    }
}
