package com.neusoft.zhaorui.afinal;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.baidu.ocr.sdk.model.IDCardResult;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class Activity_baidu extends AppCompatActivity {

    private static final String TABLE_NAME = "Info";

    private TextView infoTextView;
    private ImageView imageView;
    private BaiduOcr baiduOcr;
    Button button_save;
    //TextView name;

    private void find() {
        button_save = findViewById(R.id.button_save);
        //name = findViewById(R.id.info_text_view);
        infoTextView = findViewById(R.id.info_text_view);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        find();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidu);

        //infoTextView = findViewById(R.id.info_text_view);
        imageView = findViewById(R.id.imageView);

        baiduOcr = new BaiduOcr(this);
        baiduOcr.initAccessTokenWithAkSk();
        //设置获取相机图片监听器
        baiduOcr.setOnGotImageListener(new BaiduOcr.OnShotImageListener() {
            @Override
            public void imageResult(String fpath) {
                imageView.setImageURI(Uri.fromFile(new File(fpath)));
            }
        });
        //设置获取识别结果监听器
        baiduOcr.setOnGotRecgResultListener(new BaiduOcr.OnGotRecgResultListener() {
            @Override
            public void idCardResult(IDCardResult result) {
                infoTextView.setText("");
                if (result != null) {
                    //显示识别结果
                    infoTextView.append("姓名：" + result.getName().toString());
                    infoTextView.append("\n性别：" + result.getGender().toString());
                    infoTextView.append("\n民族：" + result.getEthnic().toString());
                    infoTextView.append("\n出生：" + result.getBirthday().toString());
                    infoTextView.append("\n住址：" + result.getAddress().toString());
                    infoTextView.append("\n身份证号码：" + result.getIdNumber().toString());
                }
            }
        });

        //按钮事件
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //创建对话框，选择拍正面还是反面
                new AlertDialog.Builder(Activity_baidu.this)
                        .setTitle("请选择身份证正反面：")
                        .setItems(new String[]{"正面（手动）","正面（自动）", "反面（手动）", "反面（自动）"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (!baiduOcr.isHasGotToken()) {
                                    Toast.makeText(getApplicationContext(), "token还未成功获取", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                switch (i) {
                                    case 0://正面 手动
                                        baiduOcr.startCameraActivityForResult(true, false);
                                        break;
                                    case 1://正面 自动
                                        baiduOcr.startCameraActivityForResult(true, true);
                                        break;
                                    case 2://反面 手动
                                        baiduOcr.startCameraActivityForResult(false, false);
                                        break;
                                    case 3://反面 自动
                                        baiduOcr.startCameraActivityForResult(false, true);
                                        break;
                                }
                            }
                        })
                        .create()
                        .show();
            }
        });

        // 保存按钮
//        button_save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                find();
//                String namet = infoTextView.getText().toString();
//
//                DataBase db = new DataBase(getBaseContext());
//                SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
////                Cursor cursor = sqLiteDatabase.rawQuery(" select * from " +
////                        TABLE_NAME + " where name like ? ", new String[]{namet});
//
//                ContentValues contentValues = new ContentValues();
//                contentValues.put("name", namet);
//
//                sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
//                sqLiteDatabase.close();
//                setResult(99, new Intent());
//                finish();
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        baiduOcr.setImageResult(requestCode,resultCode,data);

    }
}