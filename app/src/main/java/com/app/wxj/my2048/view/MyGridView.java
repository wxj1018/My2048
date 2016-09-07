package com.app.wxj.my2048.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.GridLayout;

import com.app.wxj.my2048.activity.MainActivity;
import com.app.wxj.my2048.config.Config;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/23 0023.
 */
public class MyGridView extends GridLayout implements View.OnTouchListener {

    /**
     * 矩阵信息。
     */
    private int mScoreHistory;
    //历史记录矩阵
    private int[][] mGameMatrixHistory;
    //矩阵行数
    private int mGameLines;
    //矩阵对象
    private GameItem[][] mGameMatrix;
    //合并取值数组，辅助数组
    private ArrayList<Integer> mCalList;
    //空格位置
    private ArrayList<Point> mBlanks;

    private int mStartX;
    private int mStartY;
    private int endX;
    private int endY;
    private int mKeyItemNum = -1;
    //目标分数
    private int mTarget;
    //高分
    private int mHighScore;


    public MyGridView(Context context) {
        super(context);
        initGameMatrix();
        mTarget=Config.msp.getInt(Config.KEY_GOAL,2048);
    }

    public MyGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initGameMatrix();
        mTarget=Config.msp.getInt(Config.KEY_GOAL,2048);
    }


    /**
     * 初始化View
     */
    private void initGameMatrix() {
        //初始化矩阵
        removeAllViews();
        mScoreHistory = 0;
        Config.score = 0;
        Config.mLines = Config.msp.getInt(Config.KEY_LINES, 4);
        mGameLines = Config.mLines;
        mGameMatrix = new GameItem[mGameLines][mGameLines];
        mGameMatrixHistory = new int[mGameLines][mGameLines];
        mCalList = new ArrayList<Integer>();
        mBlanks = new ArrayList<Point>();
        mHighScore = Config.msp.getInt(Config.KEY_HIGH_SCORE, 0);
        setColumnCount(mGameLines);
        setRowCount(mGameLines);
        setOnTouchListener(this);
        //初始化View参数
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getMetrics(metrics);
        Config.mItemSize = metrics.widthPixels / Config.mLines;
        initGameView(Config.mItemSize);
    }

    public void initGameView(int mItemSize) {
        removeAllViews();
        GameItem card;
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                card = new GameItem(getContext(), 0);
                addView(card, mItemSize, mItemSize);
                //初始化矩阵，全为0，空List
                mGameMatrix[i][j] = card;
                mBlanks.add(new Point(i, j));
            }
        }
        //随机添加数字,2个
        addRandomNum();
        addRandomNum();
    }
    /**
     * 开始游戏
     */
    public void startGame() {
        //初始化矩阵，和方阵显示
        initGameMatrix();
        initGameView(Config.mItemSize);
    }

    /**
     * 随机添加数字2,4
     */
    private void addRandomNum() {
        getBlanks();
        if (mBlanks.size() > 0) {
            int randomNum = (int) (Math.random() * mBlanks.size());
            Point randomPoint = mBlanks.get(randomNum);
            mGameMatrix[randomPoint.x][randomPoint.y].setNum(Math.random() > 0.2d ? 2 : 4);
            //生产数字时，产生动画
            animCreate(mGameMatrix[randomPoint.x][randomPoint.y]);
        }
    }

    /**
     * 生成动画
     */
    private void animCreate(GameItem target) {
        ScaleAnimation sa = new ScaleAnimation(0.1f, 1, 0.1f, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(100);
        target.setAnimation(null);
        target.getItemView().startAnimation(sa);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //Log.i("test", "onTouch");

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                saveHistoryMatrix();//保存历史矩阵
                mStartX = (int) event.getX();
                mStartY = (int) event.getY();
                break;
            case MotionEvent.ACTION_UP:
                endX = (int) event.getX();
                endY = (int) event.getY();
                judgeDirection(endX - mStartX, endY - mStartY);
                if (isMoved()) {
                    addRandomNum();
                    //修改分数
                    if(Config.activity != null) {
                        Config.activity.getGameActivity().setScore(Config.score);
                    }

                }
                checkCompleted();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            default:
                break;
        }

        return true;
    }

    /**
     * 判断是否发生移动
     * @return
     */
    private boolean isMoved() {
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                if (mGameMatrixHistory[i][j] != mGameMatrix[i][j].getNum()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void judgeDirection(int offx, int offy) {
        int density = getDiviceDensity();
        //Log.i("test", "" + density);
        int slideDis = 1 / 2 * density;
        int maxDis = 2 * density;
        boolean flagNormal = (Math.abs(offx) > slideDis || Math.abs(offy) > slideDis) && (Math.abs(offx) < maxDis) && (Math.abs(offy) < maxDis);
        boolean flagSuper = Math.abs(offx) > maxDis || Math.abs(offy) > maxDis;

        if (flagNormal && !flagSuper) {
            //Log.i("test", "is in slide");
            if (Math.abs(offx) > Math.abs(offy)) {
                if (offx > slideDis) {
                    swipeRight();//右滑
                } else {
                    swipeLeft();
                }
            } else {
                if (offy > slideDis) {
                    swipeDown();
                } else {
                    swipeUp();
                }
            }
        } else if (flagSuper) {
            //启动外挂，自己设置添加的数字
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            final EditText ed = new EditText(getContext());
            builder.setTitle("外挂功能").setView(ed).setPositiveButton("添加", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!TextUtils.isEmpty(ed.getText())) {
                        addSuperNum(Integer.parseInt(ed.getText().toString()));
                        //TODO
                    }
                }
            }).setNegativeButton("不，我不要使用外挂", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        }
    }

    /**
     * 外挂功能，添加用户想要的数字
     * @param i
     */
    private void addSuperNum(int i) {
        if (checkSuperNum(i)) {
            getBlanks();
            if (mBlanks.size() > 0) {
                int randomNum = (int) (Math.random() * mBlanks.size());
                Point randomPoint = mBlanks.get(randomNum);
                mGameMatrix[randomPoint.x][randomPoint.y].setNum(i);
                animCreate(mGameMatrix[randomPoint.x][randomPoint.y]);
            }
        }
    }
    /**
     * 检查添加的数是否是指定的数,针对外挂功能
     *
     * @param num num
     * @return 添加的数
     */
    private boolean checkSuperNum(int num) {
        boolean flag = (num == 2 || num == 4 || num == 8 || num == 16
                || num == 32 || num == 64 || num == 128 || num == 256
                || num == 512 || num == 1024);
        return flag;
    }

    /**
     * 检测所有数字 看是否有满足条件的
     *
     * @return 0:结束 1:正常 2:成功
     */
    private int checkNums() {
        getBlanks();
        if (mBlanks.size() == 0) {
            //所有格子都有相应的数字
            for (int i = 0; i < mGameLines; i++) {
                for (int j = 0; j < mGameLines; j++) {
                    //遍历矩阵
                    if (j < mGameLines - 1) {
                        //前后两列数字相同，则可正常游戏
                        if (mGameMatrix[i][j].getNum() == mGameMatrix[i][j + 1]
                                .getNum()) {
                            return 1;
                        }
                    }
                    if (i < mGameLines - 1) {
                        //前后两行的数字相同，也可以正常游戏
                        if (mGameMatrix[i][j].getNum() == mGameMatrix[i + 1][j]
                                .getNum()) {
                            return 1;
                        }
                    }
                }
            }
            //否则就代表没有数字相同，而且全部格子都被占满，此时GameOver
            return 0;
        }
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                //遍历数字里存在和目标分数相同的数字就代表已经完成游戏目标，提示用户继续游戏还是开始新游戏
                if (mGameMatrix[i][j].getNum() == mTarget) {
                    return 2;
                }
            }
        }
        return 1;
    }

    /**
     * 判断是否结束
     * <p/>
     * 0:结束 1:正常 2:成功
     */
    private void checkCompleted() {
        int result = checkNums();
        if (result == 0) {
            //GameOver，结束游戏，开始新游戏
            if (Config.score > mHighScore) {
                SharedPreferences.Editor editor = Config.msp.edit();
                editor.putInt(Config.KEY_HIGH_SCORE, Config.score);
                editor.apply();
                //保存结果，刷新分数面板
                MainActivity.getGameActivity().setScore(Config.score);
                //重置分数
                Config.score = 0;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("游戏失败！")
                    .setPositiveButton("再来一局",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                    startGame();
                                }
                            }).create().show();
            Config.score = 0;
        } else if (result == 2) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("达到目标分数了！")
                    .setPositiveButton("重新开始",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                    // 重新开始
                                    startGame();
                                }
                            })
                    .setNegativeButton("继续游戏",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                    // 继续游戏 修改target
                                    SharedPreferences.Editor editor = Config.msp.edit();
                                    if (mTarget == 1024) {
                                        editor.putInt(Config.KEY_GOAL, 2048);
                                        mTarget = 2048;
                                        MainActivity.getGameActivity().setGoal(2048);
                                    } else if (mTarget == 2048) {
                                        editor.putInt(Config.KEY_GOAL, 4096);
                                        mTarget = 4096;
                                        MainActivity.getGameActivity().setGoal(4096);
                                    } else {
                                        editor.putInt(Config.KEY_GOAL, 4096);
                                        mTarget = 4096;
                                        MainActivity.getGameActivity().setGoal(4096);
                                    }
                                    editor.apply();
                                }
                            }).create().show();
            Config.score = 0;
        }
    }




    /**
     * 判断方向后的合并操作算法。。
     */
    private void swipeRight() {
        //Log.i("test", "right");
        for (int i = mGameLines - 1; i >= 0; i--) {
            for (int j = mGameLines - 1; j >= 0; j--) {
                int currentNum = mGameMatrix[i][j].getNum();
                //Log.i("test", "" + i + ";" + j + ";" + currentNum);
                if (currentNum != 0) {
                    if (mKeyItemNum == -1) {
                        mKeyItemNum = currentNum;
                        //Log.i("test", "mKeyItemNum" + mKeyItemNum + currentNum);
                    } else {
                        if (mKeyItemNum == currentNum) {
                            mCalList.add(mKeyItemNum * 2);
                            Config.score += mKeyItemNum * 2;

                            mKeyItemNum = -1;
                        } else {
                            //Log.i("test", "mKeyItemNum" + mKeyItemNum);
                            mCalList.add(mKeyItemNum);
                            mKeyItemNum = currentNum;
                        }
                    }
                } else {
                    continue;
                }
            }
            if (mKeyItemNum != -1) {
                //Log.i("test", "mKeyItemNum" + mKeyItemNum);
                mCalList.add(mKeyItemNum);
            }
            //改变item的值
            //反向设置，前面的设为0
            for (int j = 0; j < mGameLines - mCalList.size(); j++) {
                //Log.i("test", "mCalList.size()" + mCalList.size());
                mGameMatrix[i][j].setNum(0);
            }
            int index = mCalList.size() - 1;//得到获取数值的位置下标
            for (int k = mGameLines - mCalList.size(); k < mGameLines; k++) {
                //Log.i("test", "mCalList.size()2" + mCalList.size());
                mGameMatrix[i][k].setNum(mCalList.get(index));//取值遍历时反向，这里也倒着设置值。后取的先设置
                index--;
            }
            //重置行参数
            mKeyItemNum = -1;
            mCalList.clear();
            index = 0;
        }
    }

    /**
     * this is ok
     */
    private void swipeLeft() {
        //Log.i("test", "left");
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                int currentNum = mGameMatrix[i][j].getNum();
                //Log.i("test", "" + i + ";" + j + ";" + currentNum);
                if (currentNum != 0) {
                    if (mKeyItemNum == -1) {
                        mKeyItemNum = currentNum;
                        //Log.i("test", "mKeyItemNum" + mKeyItemNum + currentNum);
                    } else {
                        if (mKeyItemNum == currentNum) {
                            mCalList.add(mKeyItemNum * 2);
                            Config.score += mKeyItemNum * 2;

                            mKeyItemNum = -1;
                        } else {
                            //Log.i("test", "mKeyItemNum" + mKeyItemNum);
                            mCalList.add(mKeyItemNum);
                            mKeyItemNum = currentNum;
                        }
                    }
                } else {
                    continue;
                }
            }
            if (mKeyItemNum != -1) {
                //Log.i("test", "mKeyItemNum" + mKeyItemNum);
                mCalList.add(mKeyItemNum);
            }
            //改变item的值
            for (int j = 0; j < mCalList.size(); j++) {
                //Log.i("test", "mCalList.size()" + mCalList.size());
                mGameMatrix[i][j].setNum(mCalList.get(j));
            }
            for (int k = mCalList.size(); k < mGameLines; k++) {
                //Log.i("test", "mCalList.size()2" + mCalList.size());
                mGameMatrix[i][k].setNum(0);
            }
            //重置行参数
            mKeyItemNum = -1;
            mCalList.clear();
        }
    }

    private void swipeDown() {
        for (int i = mGameLines - 1; i >= 0; i--) {
            for (int j = mGameLines - 1; j >= 0; j--) {
                int currentNum = mGameMatrix[j][i].getNum();
                //Log.i("test", "" + i + ";" + j + ";" + currentNum);
                if (currentNum != 0) {
                    if (mKeyItemNum == -1) {
                        mKeyItemNum = currentNum;
                        //Log.i("test", "mKeyItemNum" + mKeyItemNum + currentNum);
                    } else {
                        if (mKeyItemNum == currentNum) {
                            mCalList.add(mKeyItemNum * 2);
                            Config.score += mKeyItemNum * 2;

                            mKeyItemNum = -1;
                        } else {
                            //Log.i("test", "mKeyItemNum" + mKeyItemNum);
                            mCalList.add(mKeyItemNum);
                            mKeyItemNum = currentNum;
                        }
                    }
                } else {
                    continue;
                }
            }
            if (mKeyItemNum != -1) {
                //Log.i("test", "mKeyItemNum" + mKeyItemNum);
                mCalList.add(mKeyItemNum);
            }
            //改变item的值
            //反向设置，前面的设为0
            for (int j = 0; j < mGameLines - mCalList.size(); j++) {
                //Log.i("test", "mCalList.size()" + mCalList.size());
                mGameMatrix[j][i].setNum(0);
            }
            int index = mCalList.size() - 1;//得到获取数值的位置下标
            for (int k = mGameLines - mCalList.size(); k < mGameLines; k++) {
                //Log.i("test", "mCalList.size()2" + mCalList.size());
                mGameMatrix[k][i].setNum(mCalList.get(index));//取值遍历时反向，这里也倒着设置值。后取的先设置
                index--;
            }
            //重置行参数
            mKeyItemNum = -1;
            mCalList.clear();
            index = 0;
        }
    }

    private void swipeUp() {
        //Log.i("test", "up");
        for (int i = 0; i < mGameLines; i++) {

            for (int j = 0; j < mGameLines; j++) {
                int currentNum = mGameMatrix[j][i].getNum();
                //Log.i("test", "" + i + ";" + j + ";" + currentNum);
                if (currentNum != 0) {
                    if (mKeyItemNum == -1) {
                        mKeyItemNum = currentNum;
                        //Log.i("test", "mKeyItemNum" + mKeyItemNum + currentNum);
                    } else {
                        if (mKeyItemNum == currentNum) {
                            mCalList.add(mKeyItemNum * 2);
                            Config.score += mKeyItemNum * 2;

                            mKeyItemNum = -1;
                        } else {
                            //Log.i("test", "mKeyItemNum" + mKeyItemNum);
                            mCalList.add(mKeyItemNum);
                            mKeyItemNum = currentNum;
                        }
                    }
                } else {
                    continue;
                }
            }
            if (mKeyItemNum != -1) {
                //Log.i("test", "mKeyItemNum" + mKeyItemNum);
                mCalList.add(mKeyItemNum);
            }
            //改变item的值
            for (int j = 0; j < mCalList.size(); j++) {
                //Log.i("test", "mCalList.size()" + mCalList.size());
                mGameMatrix[j][i].setNum(mCalList.get(j));
            }
            for (int k = mCalList.size(); k < mGameLines; k++) {
                //Log.i("test", "mCalList.size()2" + mCalList.size());
                mGameMatrix[k][i].setNum(0);
            }
            //重置行参数
            mKeyItemNum = -1;
            mCalList.clear();
        }
    }

    /**
     * 得到屏幕分辨率
     * @return
     */
    private int getDiviceDensity() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getMetrics(displayMetrics);
        return displayMetrics.densityDpi;
    }

    /**
     * 保存历史矩阵，只限上一次
     */
    private void saveHistoryMatrix() {
        mScoreHistory = Config.score;
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                mGameMatrixHistory[i][j] = mGameMatrix[i][j].getNum();
            }
        }
    }

    /**
     * 撤销操作
     */
    public void revert(){
        //刚开局不能撤销
        int sum=0;
        for (int[] element:mGameMatrixHistory){
            for (int i:element){
                sum+=i;
            }
        }
        if (sum!=0){
            MainActivity.getGameActivity().setScore(mScoreHistory);
            Config.score=mScoreHistory;
            for (int i=0;i<mGameLines;i++){
                for (int j=0;j<mGameLines;j++){
                    mGameMatrix[i][j].setNum(mGameMatrixHistory[i][j]);
                }
            }
        }
    }

    /**
     * 得到空格栏
     */
    private void getBlanks() {
        mBlanks.clear();
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                if (mGameMatrix[i][j].getNum() == 0) {
                    mBlanks.add(new Point(i, j));
                }
            }
        }
    }


}
