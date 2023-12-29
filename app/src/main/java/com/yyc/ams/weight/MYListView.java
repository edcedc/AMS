package com.yyc.ams.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * @Author nike
 * @Date 2023/6/9 15:25
 * @Description
 */

public class MYListView extends ListView {
    public MYListView(Context context) {
        super(context);
    }

    public MYListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MYListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MYListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }


}









