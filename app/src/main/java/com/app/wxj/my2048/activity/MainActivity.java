package com.app.wxj.my2048.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.app.wxj.my2048.R;
import com.app.wxj.my2048.config.Config;
import com.app.wxj.my2048.view.MyGridView;

public class MainActivity extends Activity implements View.OnClickListener{

    //当前activity引用
    private static MainActivity mainActivity;
    // 记录分数
    private TextView mTvCurrentScore;
    private int mHighScore;
    // 历史记录分数
    private TextView mTvRecord;
    private int mRecord;


    private Button btn_back,btn_restart,btn_option;
    private MyGridView myGridView;



    //构造
    public MainActivity(){
        mainActivity=this;
    }

    /**
     * 获取当前Activity的引用
     *
     * @return Activity.this
     */
    public static MainActivity getGameActivity() {
        return mainActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        Config.activity = this;



        initView();
        myGridView = (MyGridView) findViewById(R.id.game_view);

    }

    private void initView() {
        btn_back= (Button) findViewById(R.id.btn_back);
        btn_restart= (Button) findViewById(R.id.btn_restart);
        btn_option= (Button) findViewById(R.id.btn_option);
        mTvCurrentScore= (TextView) findViewById(R.id.tv_currentscore);
        mTvRecord= (TextView) findViewById(R.id.tv_record);
        btn_back.setOnClickListener(this);
        btn_restart.setOnClickListener(this);
        btn_option.setOnClickListener(this);

        mHighScore = Config.msp.getInt(Config.KEY_HIGH_SCORE, 0);
        mRecord = Config.msp.getInt(Config.KEY_GOAL, 2048);
        mTvCurrentScore.setText("" + mHighScore);
        mTvRecord.setText("" + mRecord);
        setScore(0);
    }

    //修改当前分数
    public void setScore(int i) {
        mTvCurrentScore.setText(String.valueOf(i));
    }

    //修改目标分数
    public void setGoal(int num) {
        mTvRecord.setText(String.valueOf(num));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:
                Log.i("test","revert");
                myGridView.revert();
                break;
            case R.id.btn_restart:
                Log.i("test","restart");
                myGridView.startGame();
                setScore(0);
                break;
            case R.id.btn_option:
                Intent intent=new Intent(MainActivity.this,OptionActivity.class);
                startActivityForResult(intent,0);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mRecord = Config.msp.getInt(Config.KEY_GOAL, 2048);
            mTvRecord.setText("" + mRecord);
            getHighScore();
            myGridView.startGame();
        }
    }

    /**
     * 获取最高记录
     */
    private void getHighScore() {
        int score = Config.msp.getInt(Config.KEY_HIGH_SCORE, 0);
        setScore(score);
    }
}
