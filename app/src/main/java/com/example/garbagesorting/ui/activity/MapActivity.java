package com.example.garbagesorting.ui.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.garbagesorting.R;
import com.example.garbagesorting.adapter.RubbishAdapter;
import com.example.garbagesorting.bean.Rubbish;
import com.example.garbagesorting.util.MPermissionUtils;
import com.example.garbagesorting.util.MySqliteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity {
    private Activity myActivity;
    MySqliteOpenHelper helper = null;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private double stratLatitude;//定位纬度
    private double stratLongitude;//定位经度
    private MyLocationListener mLocationListener;
    private LocationClient mLocationClient;
    private BitmapDescriptor othersCurrentMarker;
    private RubbishAdapter rubbishAdapter;//适配器
    private RecyclerView mRecyclerView;//列表
    private List<Rubbish> list;
    private RequestOptions headerRO = new RequestOptions().circleCrop();//圆角变换

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        myActivity=this;
        mMapView=findViewById(R.id.mapView);
        mRecyclerView = findViewById(R.id.rv_list);
        mBaiduMap=mMapView.getMap();
        helper = new MySqliteOpenHelper(myActivity);
        initView();

    }

    private void initView() {
        //开启定位
        mBaiduMap.setMyLocationEnabled(true);
        //声明LocationClient类
        LocationClient mLocationClient = new LocationClient(myActivity);
        //注册监听函数
        mLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mLocationListener);
        initLocation();//定位
        //获取对方的位置
        loadData();

    }

    private void loadData() {
        list = new ArrayList<>();
        Rubbish rubbish = null;
        String sql = "select * from rubbish";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null && cursor.getColumnCount() > 0) {
            while (cursor.moveToNext()) {
                Integer dbId = cursor.getInt(0);
                String name = cursor.getString(1);
                String img = cursor.getString(2);
                Double latitude = cursor.getDouble(3);
                Double longitude = cursor.getDouble(4);
                rubbish = new Rubbish(dbId,name, img, latitude,longitude);
                list.add(rubbish);
            }
        }
        if (list != null){
            for (Rubbish rubbish1 : list) {
                addOthersLocation(rubbish1.getLatitude(),rubbish1.getLongitude(),rubbish1.getImg());
            }
        }
        //============RecyclerView 初始化=========
        //=1.1、创建布局管理器
        LinearLayoutManager layoutManager=new LinearLayoutManager(myActivity);
        //=1.2、设置为垂直排列，用setOrientation方法设置(默认为垂直布局)
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //=1.3、设置recyclerView的布局管理器
        mRecyclerView.setLayoutManager(layoutManager);
        //==2、实例化适配器
        //=2.1、初始化适配器
        rubbishAdapter =new RubbishAdapter();
        //=2.3、设置recyclerView的适配器
        mRecyclerView.setAdapter(rubbishAdapter);
        rubbishAdapter.addItem(list);
        rubbishAdapter.setItemListener(new RubbishAdapter.ItemListener() {
            @Override
            public void ItemClick(Rubbish rubbish) {
                Intent intent = new Intent(myActivity,AddRubbishActivity.class);
                intent.putExtra("rubbish",rubbish);
                startActivity(intent);
            }

            @Override
            public void ItemLongClick(Rubbish rubbish) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(myActivity);
                dialog.setMessage("确认要删除该数据吗");
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db = helper.getWritableDatabase();
                        if (db.isOpen()) {
                            db.execSQL("delete from rubbish where id = "+rubbish.getId());
                            db.close();
                        }
                        Toast.makeText(myActivity,"删除成功",Toast.LENGTH_LONG).show();
                        loadData();
                    }
                });
                dialog.setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    //定位自己
    private void initLocation() {
        //开启定位
        mBaiduMap.setMyLocationEnabled(true);
        //声明LocationClient类
        mLocationClient = new LocationClient(myActivity);
        //注册监听函数
        mLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mLocationListener);

        //==配置参数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系---注意和初始化时的设置对应
        //设置定位间隔为10s，不能小于1s即1000ms
        option.setScanSpan(10000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
        //调用LocationClient的start()方法，便可发起定位请求
        //start()：启动定位SDK；stop()：关闭定位SDK。调用start()之后只需要等待定位结果自动回调即可。
        mLocationClient.start();
    }

    class MyLocationListener extends BDAbstractLocationListener {
        boolean isZoomMap = true;//标识是否以定位位置为中心缩放地图
        @Override
        public void onReceiveLocation(BDLocation location) {
            //显示当前位置
           // Toast.makeText(myActivity, location.getAddress().address,Toast.LENGTH_LONG).show();
            //获取当前位置纬度
            stratLatitude = location.getLatitude();
            //获取当前位置经度
            stratLongitude = location.getLongitude();
            Log.e("----------------->", "定位SDK回调 当前位置纬度：" + stratLatitude + "当前位置经度：" + stratLongitude);
            //第一次定位时调整地图缩放
            if (isZoomMap) {
                //纬度，经度
                LatLng latLng = new LatLng(stratLatitude, stratLongitude);
                // 改变地图状态，使地图以定位地址为目标，显示缩放到恰当的大小
                MapStatus mapStatus = new MapStatus.Builder()
                        .target(latLng)//目标
                        .zoom(16.0f)//缩放
                        .build();
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatus));
                isZoomMap = false;
            }
            //在地图上显示当前位置
            MyLocationData locationData = new MyLocationData.Builder()
                    .latitude(stratLatitude)//纬度
                    .longitude(stratLongitude)//经度
                    .build();
            mBaiduMap.setMyLocationData(locationData);
        }
    }


    // 添加他人位置
    public void addOthersLocation(double latitute,double longtitute, String img) {
        mBaiduMap.clear();
        Glide.with(myActivity)
                .asBitmap()
                .load(img)
                .apply(headerRO)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        //定义Maker坐标点
                        LatLng point = new LatLng(latitute, longtitute);
                        //构建MarkerOption，用于在地图上添加Marker

                        othersCurrentMarker = BitmapDescriptorFactory
                                .fromBitmap(zoomImage(resource,100,100));
                        OverlayOptions option = new MarkerOptions()  //构建Marker图标
                                .position(point)
                                .icon(othersCurrentMarker);
                        mBaiduMap.addOverlay(option);
                    }
                });
    }
    //缩放头像图片
    public Bitmap zoomImage(Bitmap bgimage, double newWidth,
                             double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }

    //申请权限的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MPermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
        if (!mLocationClient.isStarted()) {
            mLocationClient.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }



    public void add(View view){
        Intent intent = new Intent(MapActivity.this,AddRubbishActivity.class);
        startActivityForResult(intent,100);
    }

    public void back(View view){
        finish();
    }
}