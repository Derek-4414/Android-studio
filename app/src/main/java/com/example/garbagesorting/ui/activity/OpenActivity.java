package com.example.garbagesorting.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.garbagesorting.R;
import com.example.garbagesorting.bean.User;
import com.example.garbagesorting.util.MPermissionUtils;
import com.example.garbagesorting.util.SPUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 欢迎页
 */
public class OpenActivity extends AppCompatActivity {
    private final int REQUEST_EXTERNAL_STORAGE = 1;
    //权限组（读写权限）
    private String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,//读取手机专题
            Manifest.permission.ACCESS_COARSE_LOCATION,//利用手机网络和wifi定位
            Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open);
        Integer userId= (Integer) SPUtils.get(OpenActivity.this, SPUtils.USER_ID,0);
        MPermissionUtils.requestPermissionsResult(OpenActivity.this,
                REQUEST_EXTERNAL_STORAGE,
                PERMISSIONS_STORAGE,
                new MPermissionUtils.OnPermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0){
                                    finish();
                                    return;
                                }
                                Intent intent = new Intent();
                                if (userId > 0) {//已登录
                                    intent.setClass(OpenActivity.this, MainActivity.class);
                                }else {
                                    intent.setClass(OpenActivity.this, LoginActivity.class);
                                }
                                startActivity(intent);
                                finish();
                            }
                        }, 2000);
                    }

                    @Override
                    public void onPermissionDenied() {//拒绝的话
                        finish();//退出
                    }
                });
    }

    //权限请求的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MPermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onBackPressed() {

    }
}