package com.example.garbagesorting.ui.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.garbagesorting.R;
import com.example.garbagesorting.adapter.ItemResultAdapter;
import com.example.garbagesorting.bean.ItemResult;
import com.example.garbagesorting.bean.PictureResult;
import com.example.garbagesorting.util.MySqliteOpenHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 垃圾识别结果
 */
public class RubbishActivity extends AppCompatActivity {
    MySqliteOpenHelper helper = null;
    private Activity myActivity;
    private RecyclerView rvList;
    private LinearLayout llEmpty;
    private ItemResultAdapter itemResultAdapter;
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    private TextView tvTitle;
    private String result;
    private Boolean isPhoto=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myActivity = this;
        setContentView(R.layout.activity_rubbish);
        helper = new MySqliteOpenHelper(myActivity);
        rvList =findViewById(R.id.rv_list);
        llEmpty =findViewById(R.id.ll_empty);
        tvTitle =findViewById(R.id.tv_title);
        LinearLayoutManager layoutManager = new LinearLayoutManager(myActivity);
        //=1.2、设置为垂直排列，用setOrientation方法设置(默认为垂直布局)
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //=1.3、设置recyclerView的布局管理器
        rvList.setLayoutManager(layoutManager);
        //==2、实例化适配器
        //=2.1、初始化适配器
        itemResultAdapter = new ItemResultAdapter();
        //=2.3、设置recyclerView的适配器
        rvList.setAdapter(itemResultAdapter);
        result = getIntent().getStringExtra("result");
        isPhoto = getIntent().getBooleanExtra("isPhoto",true);
        if (isPhoto){
            Type type = new TypeToken<List<PictureResult>>() {
            }.getType();//列表信息
            List<PictureResult> list = gson.fromJson(result, type);
            if (list.get(0).getList() != null && list.get(0).getList().size() > 0) {
                itemResultAdapter.addItem(list.get(0).getList());
                rvList.setVisibility(View.VISIBLE);
                llEmpty.setVisibility(View.GONE);
                Toast.makeText(myActivity, "识别成功", Toast.LENGTH_SHORT).show();
            } else {
                rvList.setVisibility(View.GONE);
                llEmpty.setVisibility(View.VISIBLE);
                Toast.makeText(myActivity, "暂无数据", Toast.LENGTH_SHORT).show();
            }
        }else {
            Type type = new TypeToken<List<ItemResult>>() {
            }.getType();//列表信息
            List<ItemResult> list = gson.fromJson(result, type);
            if (list != null && list.size() > 0) {
                itemResultAdapter.addItem(list);
                rvList.setVisibility(View.VISIBLE);
                llEmpty.setVisibility(View.GONE);
                Toast.makeText(myActivity, "识别成功", Toast.LENGTH_SHORT).show();
            } else {
                rvList.setVisibility(View.GONE);
                llEmpty.setVisibility(View.VISIBLE);
                Toast.makeText(myActivity, "暂无数据", Toast.LENGTH_SHORT).show();
            }
        }

    }

    //返回
    public void back(View view){
        finish();
    }
}