package vmediacn.com.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import vmediacn.com.Common;
import vmediacn.com.R;
import vmediacn.com.activity.mine.ActSetPayPwdSet;

/**
 * Created by Administrator on 2016/4/12.
 * 密码键盘
 */
public class MySafetyKeyboard extends PopupWindow {
    private MyPayPasswordListener myPayPasswordListener;
    private TextView verification;
    private TableLayout table;
    private ProgressBar bar;
    private StringBuffer buffer = new StringBuffer();
    private TextView[] texts ;
    private Button[] btns;
    public Context context;
    private Button btn_diss;
    public void setMyPayPasswordListener(MyPayPasswordListener myPayPasswordListener){
        this.myPayPasswordListener = myPayPasswordListener;

    }
    public MySafetyKeyboard (final Context context ){
        super(context);
        this.context =context;

        //initPop(context);
        if (Common.isPaypwd.equals("1")){
            initPop(context);
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("设置新密码");
            builder.setMessage("您还未设置支付密码，请先设置支付密码再进行支付操作");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(context, ActSetPayPwdSet.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
            builder.setCancelable(false);//弹出框不可以换返回键取消
            AlertDialog dialog = builder.create();
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);//将弹出框设置为全局
            dialog.setCanceledOnTouchOutside(false);//失去焦点不会消失
            dialog.show();
        }

    }
    private void initPop(final Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.pop_safely_keyboard,null);
        verification = (TextView) view.findViewById(R.id.pop_safely_keyboard_verification);
        table = (TableLayout) view.findViewById(R.id.pop_safely_keyboard);
        bar = (ProgressBar) view.findViewById(R.id.pop_safely_keyboard_bar);
        btn_diss = (Button) view.findViewById(R.id.pop_safely_keyboard_diss);
        texts = new TextView[]{
                (TextView) view.findViewById(R.id.pop_safely_keyboard_tv1),
                (TextView) view.findViewById(R.id.pop_safely_keyboard_tv2),
                (TextView) view.findViewById(R.id.pop_safely_keyboard_tv3),
                (TextView) view.findViewById(R.id.pop_safely_keyboard_tv4),
                (TextView) view.findViewById(R.id.pop_safely_keyboard_tv5),
                (TextView) view.findViewById(R.id.pop_safely_keyboard_tv6),
        };
        btns = new Button[]{
                (Button) view.findViewById(R.id.pop_safely_keyboard_btn0),
                (Button) view.findViewById(R.id.pop_safely_keyboard_btn1),
                (Button) view.findViewById(R.id.pop_safely_keyboard_btn2),
                (Button) view.findViewById(R.id.pop_safely_keyboard_btn3),
                (Button) view.findViewById(R.id.pop_safely_keyboard_btn4),
                (Button) view.findViewById(R.id.pop_safely_keyboard_btn5),
                (Button) view.findViewById(R.id.pop_safely_keyboard_btn6),
                (Button) view.findViewById(R.id.pop_safely_keyboard_btn7),
                (Button) view.findViewById(R.id.pop_safely_keyboard_btn8),
                (Button) view.findViewById(R.id.pop_safely_keyboard_btn9),
                (Button) view.findViewById(R.id.pop_safely_keyboard_back),
        };
        btn_diss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        for (int i=0;i<btns.length;i++){
            btns[i].setOnClickListener(new MyOnClickListener(i));

        }


        //		 设置
        setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        setFocusable(false);
        //	      setTouchable(true);
        //这句是为了防止弹出菜单获取焦点之后，点击activity的其他组件没有响应
        setBackgroundDrawable(new BitmapDrawable());
        update();
        setContentView(view);
    }

    public class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            if (index==10){
                if (!isNull()) {
                    buffer.deleteCharAt(buffer.length() - 1);
                    texts[buffer.length()].setText("");
                }
            }else {
                buffer.append(String.valueOf(index));
                texts[buffer.length()-1].setText("•");
                if (buffer.length()==6){
                    myPayPasswordListener.Success(buffer.toString());
//                Toast.makeText(context,buffer.toString(),Toast.LENGTH_SHORT).show();
                    table.setVisibility(View.GONE);
                    verification.setVisibility(View.VISIBLE);
                    bar.setVisibility(View.VISIBLE);
                }
            }
        }
    }
    private boolean isNull(){
        if (buffer.toString().equals("")){
            return true;
        }else {
            return false;
        }
    }


}



