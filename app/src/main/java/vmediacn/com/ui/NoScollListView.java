package vmediacn.com.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by Administrator on 2016/4/20.
 * 自定义高度的ListView
 */
public class NoScollListView extends ListView {
    public NoScollListView(Context context) {
        super(context);
    }

    public NoScollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, height);
    }
}
