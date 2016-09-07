package com.app.wxj.my2048.config;

import android.app.Application;
import android.content.SharedPreferences;

import com.app.wxj.my2048.activity.MainActivity;

/**
 * Created by wxj on 2016/8/22 0022.
 * <p/>
 * 创建一个继承Application的类作为程序的入口
 * 每次进入的时候读取SharedPreference数据
 */
public class Config extends Application {

    /**
     * SharedPreference对象
     */
    public static SharedPreferences msp;

    /**
     * 游戏目标
     */
    public static int mGoal;

    /**
     * 游戏的行和列，由于是正方块，行列相同
     */
    public static int mLines;

    /**
     * 方块的宽高
     */
    public static int mItemSize;

    /**
     * 记录分数
     */
    public static int score = 0;

    //下面是各个数据存入SharedPreferences使用的键名
    public static final String SP_HIGH_SCORE = "SP_HIGHSCORE";
    public static final String KEY_HIGH_SCORE = "KEY_HIGHSCORE";
    public static final String KEY_LINES = "KEY_LINES";
    public static final String KEY_GOAL = "KEY_GOAL";

    //activity对象
    public static MainActivity activity;

    //是否第一次进入
    public static int count=0;
    @Override
    public void onCreate() {
        super.onCreate();

        //初始化操作
        msp = getSharedPreferences(SP_HIGH_SCORE, 0);
        mLines = msp.getInt(KEY_LINES, 4);
        mGoal = msp.getInt(KEY_GOAL, 2048);
        mItemSize = 0;
    }
}
