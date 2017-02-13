package vmediacn.com.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import org.kymjs.kjframe.utils.KJLoger;

/**
 * Created by Administrator on 2016/4/11.
 * 用于监听布局的变化，改变输入法的显示和隐藏
 */
public class KeyBoardLayout  extends RelativeLayout
{
    private onSizeChangedListener mChangedListener;
    private static final String TAG ="KeyboardLayoutTAG";
    private boolean mShowKeyboard = false;

    public KeyBoardLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public KeyBoardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public KeyBoardLayout(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "onMeasure-----------");
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        // TODO Auto-generated method stub
        super.onLayout(changed, left, top, right, bottom);
        Log.d(TAG, "onLayout-------------------");
        Log.d(TAG, "--left----" +left + "\n" + "--top-----" + top + "\n" + "--right-----" + right + "\n--bottom----" + bottom);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // TODO Auto-generated method stub
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "----------------------onSizeChanged----------------------------------------");
        Log.d(TAG, "w----" + w + "\n" + "h-----" + h + "\n" + "oldW-----" + oldw + "\noldh----" + oldh);
        if (null != mChangedListener && 0 != oldw && 0 != oldh) {
            InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            if (h < oldh) {
                mShowKeyboard = true;
            } else {
                mShowKeyboard = false;
            }
            /*if (inputMethodManager.isActive()) {
                mShowKeyboard = true;
            } else {
                mShowKeyboard = false;
            }*/
            mChangedListener.onChanged(mShowKeyboard);
           KJLoger.log(TAG, "---mShowKeyboard-----      " + mShowKeyboard);
        }
    }

    public void setOnSizeChangedListener(onSizeChangedListener listener) {
        mChangedListener = listener;
    }

    public interface onSizeChangedListener{

        void onChanged(boolean showKeyboard);
    }

}
