package vmediacn.com.activity.mine;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.kymjs.kjframe.KJBitmap;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpParams;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.utils.KJLoger;
import org.kymjs.kjframe.utils.PreferenceHelper;
import org.kymjs.kjframe.widget.RoundImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import vmediacn.com.Common;
import vmediacn.com.R;
import vmediacn.com.activity.BaseActivity;
import vmediacn.com.util.StringCallBack;
/*
* 用户信息
*
* */
public class ActUserInfo extends BaseActivity {

    @BindView(id = R.id.act_user_info_niChengLayId, click = true)
    private LinearLayout nichengLay;

    @BindView(id = R.id.act_user_info_niChengId)
    private TextView nichengTv;

    @BindView(id = R.id.act_user_info_accountId)
    private TextView accountTv;

    @BindView(id = R.id.act_user_info_imageId,click = true)
    private RoundImageView circleImageView;

    private KJBitmap kjBitmap = new KJBitmap();

    private Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"image.jpg"));

    public static final int REQUEST_SIGNATURE_CODE = 100;
    public static final int REQUEST_NICEKNAME_CODE = REQUEST_SIGNATURE_CODE+100;
    public static final int REQUEST_PHOTO_CODE = REQUEST_NICEKNAME_CODE + 100;
    private static final int TAKE_PICTURE = REQUEST_PHOTO_CODE+100;
    private static final int CROP_PICTURE = TAKE_PICTURE + 100;
    private static final int UPDATA_USER_PHOTO = CROP_PICTURE + 100;
    private static final int REQUEST_LOCAL_PHOTO = UPDATA_USER_PHOTO + 100;
    private String TAG = "--ActUserInfo--";

    private KJBitmap bitmap = new KJBitmap();
    @Override
    public void setRootView() {

        setContentView(R.layout.act_user_info);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        changeText("账户信息");

        getBackData();
        Intent intent = getIntent();
        if (intent != null) {
            String niCheng = intent.getStringExtra("niCheng");
            String accout = intent.getStringExtra("account");
            nichengTv.setText(niCheng);
            accountTv.setText(accout);
        }
        ImageView imageView = (ImageView) findViewById(R.id.head_title_view_backIgId);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(100);
                onBackPressed();
            }
        });

    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
            case R.id.act_user_info_niChengLayId:
                getPopWindow();
                break;

            case R.id.act_user_info_imageId:
                KJLoger.log(TAG,"--image被点击了--");
                showMenu();
                break;
        }
    }
    private void showMenu() {
        View popView = View.inflate(ActUserInfo.this, R.layout.pop_pit_home_menu, null);
        PopupWindow popupWindow = new PopupWindow(popView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        ColorDrawable cd = new ColorDrawable(0x000000);
        popupWindow.setBackgroundDrawable(cd);
        WindowManager.LayoutParams lp=getWindow().getAttributes();
        lp.alpha = 0.4f;
        getWindow().setAttributes(lp);
        popupWindow.showAtLocation((View) nichengLay.getParent(),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        popupWindow.update();
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            //在dismiss中恢复透明度
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });

        initPopWidget(popView, popupWindow);

    }
    private void initPopWidget(View popView, final PopupWindow popupWindow) {
        TextView cameraTV = (TextView) popView.findViewById(R.id.tv_pit_home_menu_camera);
        TextView albumTV = (TextView) popView.findViewById(R.id.tv_pit_home_menu_album);
        TextView sysTV = (TextView) popView.findViewById(R.id.tv_pit_home_menu_sys);
        TextView cancelTV = (TextView) popView.findViewById(R.id.tv_pit_home_menu_cancel);

        cameraTV.setOnClickListener(new View.OnClickListener() {//拍照
            @Override
            public void onClick(View v) {//拍照

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PICTURE);
                popupWindow.dismiss();

            }
        });
        albumTV.setOnClickListener(new View.OnClickListener() {//从相册选择
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.putExtra("return-data", true);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_LOCAL_PHOTO);
                popupWindow.dismiss();

            }
        });
        //sysTV.setOnClickListener(this);

        cancelTV.setOnClickListener(new View.OnClickListener() {//取消
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }
    public void getPopWindow() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ActUserInfo.this);
        final EditText editText = new EditText(ActUserInfo.this);
        editText.setHint("请输入昵称");

        dialog.setView(editText);
        dialog.setTitle("设置昵称");
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = editText.getText().toString();
                PreferenceHelper.write(ActUserInfo.this, "UserInfo", "niCheng", name);
                dialog.dismiss();
                nichengTv.setText(name);
                Common.nickName = name;
                putMessage();
            }
        }).setNegativeButton("取消",null).create().show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(100);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        KJLoger.log(TAG, "=======requestCode======" + requestCode);
        boolean t = data==null;//拍了照片的是true，没拍的是false
        KJLoger.log(TAG, "=======requestCode======" + t);

        switch (requestCode){
            case CROP_PICTURE:
                Bitmap cropBitmap = data.getParcelableExtra("data");
                //circleImageView.setImageBitmap(cropBitmap);
//拿到头像-上传头像获取服务器地址-修改本地和服务器的个人信息
                if(null!=cropBitmap){
                    bitmap2uploaded(cropBitmap);
                }
                break;
            case REQUEST_LOCAL_PHOTO:
                if(!t){
                    crop(data.getData());
                }

                break;
            case TAKE_PICTURE:
                if(t){
                    crop(imageUri);
                }
                break;
        }

        if(data!=null){

            switch (resultCode){

                case REQUEST_PHOTO_CODE:
                    String imageUrl = data.getStringExtra("image");
                    Common.photoUri = imageUrl;
                    KJBitmap bitmap = new KJBitmap();
                    bitmap.display(circleImageView,imageUrl);
                    break;

            }
        }
    }
    private void crop(Uri uri) {

        KJLoger.log(TAG,"剪裁头像====="+uri.getPath());
        Intent intent = new Intent();

        intent.setAction("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");//已经选择的图片Uri
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);// 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);// 输出图片大小
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, CROP_PICTURE);
    }
    private void bitmap2uploaded(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] bytes = baos.toByteArray();
        byte[] encode = Base64.encode(bytes, Base64.DEFAULT);
        uploadedPhoto(new String(encode));
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void uploadedPhoto(String file){
        KJHttp http = new KJHttp();
        JSONObject object = new JSONObject();
        try {

            object.put("pic", file);
            object.put("token","97156907880b4e159b757808c2895e36" );
            object.put("userId", "13717899174");
            HttpParams params = new HttpParams();
            params.putJsonParams(object.toString());
//            http.jsonPost(Common.MSUri+"getpic", params,false, new StringCallBack(ActUserInfo.this) {
            http.jsonPost("http://192.168.11.17/api/user/uploadIcon", params,false, new StringCallBack(ActUserInfo.this) {

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                    KJLoger.log(TAG,"==头像上传失败=="+strMsg);
                }

                @Override
                public void onSuccess(String json) {
                    KJLoger.log(TAG,"===头像上传信息===="+ json);
                    JSONTokener tokener = new JSONTokener(json);
                    try {
                        JSONObject resultJson = (JSONObject) tokener.nextValue();
                        String resultState = resultJson.getString("resultState");
                        String message = resultJson.getString("message");
                        if (resultState.equals("true")){
                            String photoUrl = resultJson.getString("imageurl");
                            Common.photoUri = photoUrl;
                            /*Message updataPhotoMsg = handler.obtainMessage();
                            updataPhotoMsg.what=UPDATA_USER_PHOTO;
                            handler.sendMessage(updataPhotoMsg);*/
                            handler.obtainMessage(UPDATA_USER_PHOTO).sendToTarget();
                        }else {
                            note(ActUserInfo.this,"提示",message,"确定");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    super.onFailure(t, errorNo, strMsg);
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    setUserInfo();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setUserInfo() {

    }

    private android.os.Handler handler = new android.os.Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            if(msg.what == UPDATA_USER_PHOTO){
                bitmap.display(circleImageView, Common.photoUri);
                putMessage();

            }
        }
    };
    public  void putMessage() {
        KJHttp http = new KJHttp();
        JSONObject object = new JSONObject();
        try {
            object.put("Telphone", Common.Usr_PhoneNumber);
            object.put("Pic", Common.photoUri);
            object.put("nicename", nichengTv.getText().toString());
            HttpParams params = new HttpParams();
            params.putJsonParams(object.toString());

            http.jsonPost(Common.MSUri + "Edit_personal", params,false, new HttpCallBack() {
                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);
                    KJLoger.log(TAG, "--用户信息保存---" + t);
                    JSONTokener tokener = new JSONTokener(t);
                    try {
                        JSONObject messgae = (JSONObject) tokener.nextValue();
                        boolean state = messgae.getBoolean("resultState");
                        String returnmessage = messgae.getString("message");
                        ShowToast(getApplicationContext(), returnmessage);
                        if (state) {
                            getBackData();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                    KJLoger.log(TAG, "--用户信息保存失败---" + strMsg);
                    ShowToast(getApplicationContext(), "用户信息保存失败");
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getBackData() {
        JSONObject object = new JSONObject();
        try {
            object.put("Tel", Common.Usr_PhoneNumber);
            HttpParams params = new HttpParams();
            params.putJsonParams(object.toString());
            KJHttp http = new KJHttp();
            http.jsonPost(Common.MSUri + "GetPhoto", params,false, new HttpCallBack() {
                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);
                    KJLoger.log(TAG, "--用户信息回调成功--" + t);
                    JSONTokener jsonTokener = new JSONTokener(t);
                    try {
                        JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                        String resultState = jsonObject.getString("resultState");
                        if (resultState.equals("true")) {

                            String photo = jsonObject.getString("Photo");
                            String nickName = jsonObject.getString("nickName");
                            kjBitmap.display(circleImageView, photo);
                            accountTv.setText(Common.Usr_PhoneNumber);
                            nichengTv.setText(nickName);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
