package vmediacn.com.fragment;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.kymjs.kjframe.ui.KJFragment;

import vmediacn.com.R;

/**
 * Created by Administrator on 2016/4/12.
 * 基本的Fragment 继承类
 */
public class BaseFragment extends KJFragment {
    private TextView titleContentTV;
    private ImageView imageView;
    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return null;
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);
        titleContentTV = (TextView)parentView.findViewById(R.id.head_title_view_textId);
        imageView = (ImageView) parentView.findViewById(R.id.head_title_view_backIgId);
        TextView menu = (TextView) parentView.findViewById(R.id.head_title_view_menuId);
        if (menu != null) {
            menu.setVisibility(View.GONE);
        }
        if (imageView != null) {
            imageView.setVisibility(View.GONE);
        }

    }
    public void changeText(String titleContent){
        if(titleContentTV!=null) {
            titleContentTV.setText(titleContent);
        }
    }

    public void showToast(Context context,String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
    protected void note(Context context,String title, String content, String button) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(content)
                .setPositiveButton(button, null).setIcon(R.mipmap.ic_note)
                .show();
    }
}
