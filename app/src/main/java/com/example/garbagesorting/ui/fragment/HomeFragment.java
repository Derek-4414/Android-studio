package com.example.garbagesorting.ui.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.garbagesorting.R;
import com.example.garbagesorting.adapter.ItemResultAdapter;
import com.example.garbagesorting.bean.ItemResult;
import com.example.garbagesorting.bean.PictureResult;
import com.example.garbagesorting.ui.activity.MapActivity;
import com.example.garbagesorting.ui.activity.NewsActivity;
import com.example.garbagesorting.ui.activity.RubbishActivity;
import com.example.garbagesorting.ui.activity.UserMessageActivity;
import com.example.garbagesorting.util.Base64Util;
import com.example.garbagesorting.util.GlideEngine;
import com.example.garbagesorting.util.KeyBoardUtil;
import com.example.garbagesorting.util.MySqliteOpenHelper;
import com.example.garbagesorting.util.OkHttpTool;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.youth.banner.Banner;
import com.youth.banner.loader.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页
 */
public class HomeFragment extends Fragment {
    MySqliteOpenHelper helper = null;
    private Activity myActivity;
    private Banner mBanner;//轮播顶部
    private LinearLayout llPhotograph;
    private LinearLayout llNews;
    private LinearLayout llMap;
    private LinearLayout llFeedback;
    private EditText etQuery;//搜索内容
    private ImageView ivSearch;//搜索图标
    private ImageView ivPhotograph;//搜索图标
    private String imagePath;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myActivity = (Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        helper = new MySqliteOpenHelper(myActivity);
        mBanner = view.findViewById(R.id.banner);
        llPhotograph = view.findViewById(R.id.ll_photograph);
        llNews = view.findViewById(R.id.ll_news);
        llMap = view.findViewById(R.id.ll_map);
        llFeedback = view.findViewById(R.id.ll_feedback);
        etQuery = view.findViewById(R.id.et_query);
        ivSearch = view.findViewById(R.id.iv_search);
        ivPhotograph = view.findViewById(R.id.iv_photograph);

        initView();
        initEvent();
        return view;
    }

    private void initEvent() {
        llPhotograph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectClick();
            }
        });
        llNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(myActivity, NewsActivity.class);
                startActivity(intent);
            }
        });
        llMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(myActivity, MapActivity.class);
                startActivity(intent);
            }
        });
        llFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(myActivity, UserMessageActivity.class);
                startActivity(intent);
            }
        });
        //搜搜
        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyBoardUtil.hideKeyboard(view);//隐藏软键盘
                loadData();
            }
        });
        //拍照
        ivPhotograph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectClick();
            }
        });
        //点击软键盘中的搜索
        etQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    KeyBoardUtil.hideKeyboard(v);//隐藏软键盘
                    loadData();//加载数据
                    return true;
                }
                return false;
            }
        });
    }


    private void initView() {
        //图片资源
        int[] imageResourceID = new int[]{R.drawable.ic_a, R.drawable.ic_b, R.drawable.ic_c, R.drawable.ic_d, R.drawable.ic_f};
        List<Integer> imgeList = new ArrayList<>();
        //轮播标题
        for (int i = 0; i < imageResourceID.length; i++) {
            imgeList.add(imageResourceID[i]);//把图片资源循环放入list里面
            //设置图片加载器，通过Glide加载图片
            mBanner.setImageLoader(new ImageLoader() {
                @Override
                public void displayImage(Context context, Object path, ImageView imageView) {
                    Glide.with(myActivity).load(path).into(imageView);
                }
            });
            //设置轮播的动画效果,里面有很多种特效,可以到GitHub上查看文档。
            mBanner.setImages(imgeList);//设置图片资源
            //设置指示器位置（即图片下面的那个小圆点）
            mBanner.setDelayTime(3000);//设置轮播时间3秒切换下一图
            mBanner.start();//开始进行banner渲染
        }


    }

    private void photoLoadData(String image) {
        String url = "http://apis.juhe.cn/voiceRubbish/imgDisti";
        Map<String, Object> map = new HashMap<>();
        map.put("type", 1);//1:模糊 2:精确，默认1
        map.put("image", Base64Util.imageToBase64(image));
        map.put("key", "1113c947654337def72b0ca62b32300d");
        ProgressDialog progressDialog = new ProgressDialog(myActivity);
        progressDialog.setMessage("正在加载...");
        progressDialog.show();
        OkHttpTool.httpPost(url, map, new OkHttpTool.ResponseCallback() {
            @Override
            public void onResponse(final boolean isSuccess, final int responseCode, final String response, Exception exception) {
                myActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isSuccess && responseCode == 200) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String reason = jsonObject.getString("reason");
                                if ("success".equals(reason)) {
                                    String result = jsonObject.getString("result");
                                    Intent intent = new Intent(myActivity,RubbishActivity.class);
                                    intent.putExtra("result",result);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(myActivity, "识别失败，可能上传图片太大了", Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                Toast.makeText(myActivity, "识别失败，可能上传图片太大了", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(myActivity, "识别失败，可能上传图片太大了", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }


    private void loadData() {
        String contentStr = etQuery.getText().toString();//获取搜索内容
        if ("".equals(contentStr)) {
            Toast.makeText(myActivity, "垃圾名称不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        String url = "http://apis.juhe.cn/voiceRubbish/search";
        Map<String, Object> map = new HashMap<>();
        map.put("type", 1);//1:模糊 2:精确，默认1
        map.put("q", contentStr);
        map.put("key", "1113c947654337def72b0ca62b32300d");
        ProgressDialog progressDialog = new ProgressDialog(myActivity);
        progressDialog.setMessage("正在加载...");
        progressDialog.show();
        OkHttpTool.httpPost(url, map, new OkHttpTool.ResponseCallback() {
            @Override
            public void onResponse(final boolean isSuccess, final int responseCode, final String response, Exception exception) {
                myActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isSuccess && responseCode == 200) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String result = jsonObject.getString("result");
                                Intent intent = new Intent(myActivity,RubbishActivity.class);
                                intent.putExtra("result",result);
                                intent.putExtra("isPhoto",false);
                                startActivity(intent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }

    /**
     * 选择图片
     */
    private void selectClick() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .imageEngine(GlideEngine.createGlideEngine())
                .maxSelectNum(1)
                .forResult(new OnResultCallbackListener<LocalMedia>() {
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
                                    path = PictureFileUtils.getPath(myActivity, Uri.parse(media.getPath()));
                                } else {
                                    path = media.getPath();
                                }
                            }
                            imagePath = path;
                            photoLoadData(imagePath);
                        }
                    }

                    @Override
                    public void onCancel() {
                        // onCancel Callback
                    }
                });
    }


}
