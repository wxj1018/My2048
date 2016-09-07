package com.app.wxj.my2048.view;

import android.content.Context;
import android.graphics.Color;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.app.wxj.my2048.config.Config;

/**
 * Created by wxj on 2016/8/22 0022.
 * 滑动方块的自定义View
 * 实现FramLayout，因为它比较轻量级
 */
public class GameItem extends FrameLayout {

    //方块显示的数字
    private int itemNum;

    private TextView mTvNum;

    private LayoutParams mLayoutParams;


    public GameItem(Context context) {
        super(context);
    }

    public GameItem(Context context,int itemNum){
        super(context);
        this.itemNum=itemNum;
        //初始化方块
        initItem();
    }

    public GameItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 初始化方块的操作
     */
    private void initItem() {
        //背景设置颜色
        setBackgroundColor(Color.LTGRAY);
        mTvNum=new TextView(getContext());
        setNum(itemNum);

        //根据布局的不同修改字体大小
        int gameLines= Config.msp.getInt(Config.KEY_LINES,4);
        if (gameLines==4){
            mTvNum.setTextSize(35);
        }else if (gameLines==5){
            mTvNum.setTextSize(25);
        }else {
            mTvNum.setTextSize(20);
        }
        TextPaint tp=mTvNum.getPaint();
        tp.setFakeBoldText(true);

        //设置方块的布局属性
        mTvNum.setGravity(Gravity.CENTER);
        mLayoutParams= new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        mLayoutParams.setMargins(5,5,5,5);
        addView(mTvNum,mLayoutParams);
    }

    public void setNum(int num) {
        this.itemNum=num;
        if (num==0){
            mTvNum.setText("");
        }else {
            mTvNum.setText(""+num);
        }

        //根据数字设置不同的背景颜色
        switch (num){
            case 0:
                mTvNum.setBackgroundColor(0x00000000);
                break;
            case 2:
                mTvNum.setBackgroundColor(0xffeee5db);
                break;
            case 4:
                mTvNum.setBackgroundColor(0xffeee0ca);
                break;
            case 8:
                mTvNum.setBackgroundColor(0xfff2c17a);
                break;
            case 16:
                mTvNum.setBackgroundColor(0xfff59667);
                break;
            case 32:
                mTvNum.setBackgroundColor(0xfff68c6f);
                break;
            case 64:
                mTvNum.setBackgroundColor(0xfff66e3c);
                break;
            case 128:
                mTvNum.setBackgroundColor(0xffedcf74);
                break;
            case 256:
                mTvNum.setBackgroundColor(0xffedcc64);
                break;
            case 512:
                mTvNum.setBackgroundColor(0xffedc54f);
                break;
            case 1024:
                mTvNum.setBackgroundColor(0xffedc32e);
                break;
            case 2048:
                mTvNum.setBackgroundColor(0xff3c4a34);
                break;
            default:
                mTvNum.setBackgroundColor(0xff3c4a34);
                break;
        }
    }

    public int getNum() {
        return this.itemNum;
    }

    public View getItemView() {
        return GameItem.this;
    }
}
