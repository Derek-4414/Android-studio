package com.example.garbagesorting.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * description :数据库管理类,负责管理数据库的创建、升级工作
 */
public class MySqliteOpenHelper extends SQLiteOpenHelper {
    //数据库名字
    public static final String DB_NAME = "hotel.db";

    //数据库版本
    public static final int DB_VERSION = 1;
    private Context context;

    public MySqliteOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    /**
     * 在数据库首次创建的时候调用，创建表以及可以进行一些表数据的初始化
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建表
        //_id为主键并且自增长一般命名为_id
        String userSql = "create table user(id integer primary key autoincrement,account, password,name,sex, phone,address,photo)";
        String messageSql = "create table message(id integer primary key autoincrement,userId,content,date)";
        String rubbishSql = "create table rubbish(id integer primary key autoincrement,name,img,latitude,longitude)";
        db.execSQL(userSql);
        db.execSQL(messageSql);
        db.execSQL(rubbishSql);
        String[] names =new String[]{"垃圾桶1","垃圾桶2","垃圾桶3","垃圾桶4","垃圾桶5"};
        String[] imgs =new String[]{"https://img2.baidu.com/it/u=3062704535,948981848&fm=253&fmt=auto&app=138&f=JPEG?w=578&h=500",
                "https://img2.baidu.com/it/u=1828198920,3687784491&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500",
                "https://img1.baidu.com/it/u=2249181441,43884302&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=333",
                "https://img1.baidu.com/it/u=3826428368,1642935618&fm=253&fmt=auto&app=138&f=PNG?w=500&h=538",
                "https://img0.baidu.com/it/u=1317921828,3028049490&fm=253&fmt=auto&app=138&f=JPEG?w=667&h=500"};
        String[] longitudes =new String[]{"116.466935","116.400244","116.627911","116.258815","116.29446"};
        String[] latitudes =new String[]{"39.909179","39.977331","39.887037","40.010939","39.849823"};
        String insertSql = "insert into rubbish(name,img,latitude,longitude) values(?,?,?,?)";
        for (int i = 0; i < names.length; i++) {
            db.execSQL(insertSql,new Object[]{names[i],imgs[i],latitudes[i],longitudes[i]});
        }
    }

    /**
     * 数据库升级的时候回调该方法，在数据库版本号DB_VERSION升级的时候才会调用
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //给表添加一个字段
        //db.execSQL("alter table person add age integer");
    }

    /**
     * 数据库打开的时候回调该方法
     *
     * @param db
     */
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
}

