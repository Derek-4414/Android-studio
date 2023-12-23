package com.example.garbagesorting.ui.activity;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.garbagesorting.R;
import com.example.garbagesorting.bean.Rubbish;
import com.example.garbagesorting.bean.User;
import com.example.garbagesorting.util.GlideEngine;
import com.example.garbagesorting.util.MySqliteOpenHelper;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.luck.picture.lib.tools.PictureFileUtils;
import java.util.List;

public class AddRubbishActivity extends AppCompatActivity {
    private Activity mActivity;
    private EditText etName;
    private EditText etLongitude;
    private EditText etLatitude;
    private String imagePath;//图片地址
    private ImageView ivPhoto;//图片
    MySqliteOpenHelper helper = null;
    private Rubbish rubbish;
    private RequestOptions headerRO = new RequestOptions().circleCrop();//圆角变换
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        helper=new MySqliteOpenHelper(mActivity);
        setContentView(R.layout.activity_add_rubbish);
        etName = findViewById(R.id.et_name);
        ivPhoto = findViewById(R.id.iv_photo);
        etLongitude = findViewById(R.id.et_longitude);
        etLatitude = findViewById(R.id.et_latitude);
        rubbish =(Rubbish)getIntent().getSerializableExtra("rubbish");
        if (rubbish!=null){
            etName.setText(rubbish.getName());
            imagePath =rubbish.getImg();
            Glide.with(mActivity)
                    .load(rubbish.getImg())
                    .apply(headerRO)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(ivPhoto);
            etLatitude.setText(String.valueOf(rubbish.getLatitude()));
            etLongitude.setText(String.valueOf(rubbish.getLongitude()));
        }
        //选择图片
        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectClick();
            }
        });
    }
    public void save(View view){
        String name = etName.getText().toString();
        String latitude = etLatitude.getText().toString();
        String longitude = etLongitude.getText().toString();
        if ("".equals(imagePath)) {
            Toast.makeText(mActivity, "图片不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        if ("".equals(name)) {
            Toast.makeText(mActivity,"姓名不能为空",Toast.LENGTH_LONG).show();
            return;
        }
        if ("".equals(latitude)) {
            Toast.makeText(mActivity,"纬度不能为空",Toast.LENGTH_LONG).show();
            return;
        }
        if ("".equals(longitude )) {
            Toast.makeText(mActivity,"经度不能为空",Toast.LENGTH_LONG).show();
            return;
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        if (rubbish == null) {
            String insertSql = "insert into rubbish(name,img,latitude,longitude) values(?,?,?,?)";
            db.execSQL(insertSql, new Object[]{name,imagePath,latitude,longitude});
        } else {
            String updateSql = "update rubbish set name=?,img=?,latitude=?,longitude=? where id = ?";
            db.execSQL(updateSql, new Object[]{name,imagePath,latitude,longitude, rubbish.getId()});
        }
        Toast.makeText(AddRubbishActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
        finish();
        mActivity.setResult(1);
    }

    /**
     * 选择图片
     */
    private void selectClick() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())//只选择图片
                .imageEngine(GlideEngine.createGlideEngine())
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选
                .imageEngine(GlideEngine.createGlideEngine())
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @RequiresApi(api = Build.VERSION_CODES.Q)
                    @Override
                    public void onResult(List<LocalMedia> result) {
                        for (int i = 0; i < result.size(); i++) {
                            // onResult Callback
                            LocalMedia media = result.get(i);
                            String path;
                            // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                            boolean compressPath = media.isCompressed() || (media.isCut() && media.isCompressed());
                            // 裁剪过
                            boolean isCutPath = media.isCut() && !media.isCompressed();

                            if (isCutPath) {
                                path = media.getCutPath();
                            } else if (compressPath) {
                                path = media.getCompressPath();
                            } else if (!TextUtils.isEmpty(media.getAndroidQToPath())) {
                                // AndroidQ特有path
                                path = media.getAndroidQToPath();
                            } else if (!TextUtils.isEmpty(media.getRealPath())) {
                                // 原图
                                path = media.getRealPath();
                            } else {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    path = PictureFileUtils.getPath(AddRubbishActivity.this, Uri.parse(media.getPath()));
                                } else {
                                    path = media.getPath();
                                }
                            }
                            imagePath = path;
                            Glide.with(AddRubbishActivity.this).load(imagePath).into(ivPhoto);
                        }
                    }

                    @Override
                    public void onCancel() {
                        // onCancel Callback
                    }
                });
    }

    public void back(View view){
        finish();
    }
}
