package com.neusoft.zhaorui.afinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ocr_Activity extends AppCompatActivity {

    Button button_back;
    Button button_ok;
    Button button_search;
    EditText name;
    EditText sex;
    EditText nation;
    EditText birthday;
    EditText address;
    EditText number;
    TextView show;
    SimpleAdapter adapter;

    private void findViewById() {
        button_back = findViewById(R.id.button_ok);
        button_ok = findViewById(R.id.button_ok);
        name = findViewById(R.id.editTextName);
        sex = findViewById(R.id.editTextSex);
        nation = findViewById(R.id.editTextNa);
        birthday = findViewById(R.id.editTextDate);
        address = findViewById(R.id.editTextAddress);
        number = findViewById(R.id.editTextNumber);
        show = findViewById(R.id.textView_show);
        button_search = findViewById(R.id.button_search);
    }


    private static final String TABLE_NAME = "Info";
//    public List<Map<String, String>> datas = new ArrayList<Map<String, String>>();
//
//    private void dataUpdate() {
//        try {
//            DataBase db = new DataBase(getBaseContext());
//            SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
//            Cursor cursor = sqLiteDatabase.rawQuery(" select * from "
//                    + TABLE_NAME, null);
//            datas = new ArrayList<Map<String, String>>();
//            if (cursor == null) {
//
//            } else {
//                while (cursor.moveToNext()) {
//                    String name1 = cursor.getString(0);
//                    Map<String, String> map = new HashMap<String, String>();
//                    map.put("name", name1);
//                    datas.add(map);
//                }
//                adapter = new SimpleAdapter(Ocr_Activity.this, datas, R.layout.activity_ocr_,
//                        new String[]{"name"}, new int[]{R.id.textView_show});
//
//            }
//        }
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr_);
        findViewById();

        // 按钮跳转_主界面
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Ocr_Activity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // 确认按钮
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById();
                String namet = name.getText().toString();
                String sext = sex.getText().toString();
                String nationt = nation.getText().toString();
                String birthdayt = birthday.getText().toString();
                String addresst = address.getText().toString();
                String numbert = number.getText().toString();

                DataBase db = new DataBase(getBaseContext());
                SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
                Cursor cursor = sqLiteDatabase.rawQuery(" select * from " +
                        TABLE_NAME + " where name like ? ", new String[]{namet});

                ContentValues contentValues = new ContentValues();
                contentValues.put("name", namet);
                contentValues.put("sex", sext);
                contentValues.put("nation", nationt);
                contentValues.put("birthday", birthdayt);
                contentValues.put("address", addresst);
                contentValues.put("number", numbert);

                sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
                sqLiteDatabase.close();
                setResult(99, new Intent());
                finish();
            }
        });


    }
}