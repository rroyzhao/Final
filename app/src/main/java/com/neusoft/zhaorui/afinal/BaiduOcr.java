package com.neusoft.zhaorui.afinal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.IDCardParams;
import com.baidu.ocr.sdk.model.IDCardResult;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.baidu.ocr.ui.camera.CameraNativeHelper;
import com.baidu.ocr.ui.camera.CameraView;

import java.io.File;

public class BaiduOcr {
    public static final int REQUEST_CODE_PICK_IMAGE_FRONT = 201;
    public static final int REQUEST_CODE_PICK_IMAGE_BACK = 202;
    public static final int REQUEST_CODE_CAMERA = 102;

    private static final String YOUR_BAIDU_API_KEY = "uEovynGWkFfA9aswV5xB5PQQ";
    private static final String YOUR_BAIDU_SECRET_KEY = "4RDLsTueRt5B5XgaEDycuxxAmIyIE8tV";
    public static final int ID_CARD_FRONT = 0;
    public static final int ID_CARD_BACK = 1;

    private boolean hasGotToken = false;
    private Activity context;
    static private BaiduOcr instance = null;
    private AlertDialog.Builder alertDialog;
    private OnShotImageListener onGotImageListener;
    private OnGotRecgResultListener onGotRecgResultListener;

    public BaiduOcr(Activity context) {
        this.context = context;
        instance = this;
        alertDialog = new AlertDialog.Builder(context);

    }

    static public BaiduOcr getInstance() {
        return instance;
    }

    /**
     * 用明文ak，sk初始化
     */
    public void initAccessTokenWithAkSk() {
        OCR.getInstance(context).initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                String token = result.getAccessToken();
                hasGotToken = true;
                //  初始化本地质量控制模型,释放代码在onDestory中
                //  调用身份证扫描必须加上 intent.putExtra(CameraActivity.KEY_NATIVE_MANUAL, true); 关闭自动初始化和释放本地模型
                CameraNativeHelper.init(context, OCR.getInstance(context).getLicense(),
                        new CameraNativeHelper.CameraNativeInitCallback() {
                            @Override
                            public void onError(int errorCode, Throwable e) {
                                String msg;
                                switch (errorCode) {
                                    case CameraView.NATIVE_SOLOAD_FAIL:
                                        msg = "加载so失败，请确保apk中存在ui部分的so";
                                        break;
                                    case CameraView.NATIVE_AUTH_FAIL:
                                        msg = "授权本地质量控制token获取失败";
                                        break;
                                    case CameraView.NATIVE_INIT_FAIL:
                                        msg = "本地质量控制";
                                        break;
                                    default:
                                        msg = String.valueOf(errorCode);
                                }
                                alertText("百度OCK初始化失败", "本地质量控制初始化错误，错误原因： " + msg);
                            }
                        });
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                alertText("AK，SK方式获取token失败", error.getMessage());
            }
        }, context, YOUR_BAIDU_API_KEY, YOUR_BAIDU_SECRET_KEY);
    }

    public void startCameraActivityForResult(boolean isFront, boolean isAuto) {
        Intent intent = new Intent(context, CameraActivity.class);
        intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                FileUtil.getSaveFile(context).getAbsolutePath());
        intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_ID_CARD_FRONT);
        if(isAuto) {
            intent.putExtra(CameraActivity.KEY_NATIVE_ENABLE, true);
            // KEY_NATIVE_MANUAL设置了之后CameraActivity中不再自动初始化和释放模型
            // 请手动使用CameraNativeHelper初始化和释放模型
            // 推荐这样做，可以避免一些activity切换导致的不必要的异常
            intent.putExtra(CameraActivity.KEY_NATIVE_MANUAL, true);
        }
        if (isFront)
            intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_ID_CARD_FRONT);
        else
            intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_ID_CARD_BACK);

        context.startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

    private void alertText(final String title, final String message) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.setTitle(title)
                        .setMessage(message)
                        .setPositiveButton("确定", null)
                        .show();
            }
        });
    }

    public boolean isHasGotToken() {
        return hasGotToken;
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public void setImageResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_IMAGE_FRONT && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            String filePath = getRealPathFromURI(uri);
            recIDCard(IDCardParams.ID_CARD_SIDE_FRONT, filePath);
        }

        if (requestCode == REQUEST_CODE_PICK_IMAGE_BACK && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            String filePath = getRealPathFromURI(uri);
            recIDCard(IDCardParams.ID_CARD_SIDE_BACK, filePath);
        }

        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String contentType = data.getStringExtra(CameraActivity.KEY_CONTENT_TYPE);
                String filePath = FileUtil.getSaveFile(context).getAbsolutePath();
                if (!TextUtils.isEmpty(contentType)) {
                    if (CameraActivity.CONTENT_TYPE_ID_CARD_FRONT.equals(contentType)) {
                        recIDCard(IDCardParams.ID_CARD_SIDE_FRONT, filePath);
                    } else if (CameraActivity.CONTENT_TYPE_ID_CARD_BACK.equals(contentType)) {
                        recIDCard(IDCardParams.ID_CARD_SIDE_BACK, filePath);
                    }
                }
            }
        }
    }

    private void recIDCard(String idCardSide, String filePath) {
        onGotImageListener.imageResult(filePath);

        IDCardParams param = new IDCardParams();
        param.setImageFile(new File(filePath));
        // 设置身份证正反面
        param.setIdCardSide(idCardSide);
        // 设置方向检测
        param.setDetectDirection(true);
        // 设置图像参数压缩质量0-100, 越大图像质量越好但是请求时间越长。 不设置则默认值为20
        param.setImageQuality(20);

        OCR.getInstance(context).recognizeIDCard(param, new OnResultListener<IDCardResult>() {
            @Override
            public void onResult(IDCardResult result) {
                onGotRecgResultListener.idCardResult(result);
            }

            @Override
            public void onError(OCRError error) {
                alertText("", error.getMessage());
            }
        });
    }

    public void setOnGotImageListener(OnShotImageListener listener) {
        onGotImageListener = listener;
    }

    public void setOnGotRecgResultListener(OnGotRecgResultListener listener) {
        onGotRecgResultListener = listener;
    }

    public interface OnShotImageListener {
        void imageResult(String fpath);
    }

    public interface OnGotRecgResultListener {
        void idCardResult(IDCardResult result);
    }

    public void destroy() {
        // 释放本地质量控制模型
        CameraNativeHelper.release();
    }
}
