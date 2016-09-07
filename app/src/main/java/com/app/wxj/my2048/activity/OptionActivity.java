package com.app.wxj.my2048.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.app.wxj.my2048.R;
import com.app.wxj.my2048.config.Config;

/**
 * Created by Administrator on 2016/8/23 0023.
 */
public class OptionActivity extends Activity implements View.OnClickListener {

    private Button btn_backtoMain, btn_share, btn_sure,btn_gamelines, btn_score;
    private LinearLayout container;

    //点击行列数，和分数按钮的对话框
    private AlertDialog.Builder mBuilder;


    //设置游戏行数，和游戏目标分数
    private String[] mGameLinesList;
    private String[] mGameGoalList;
    //用sharedpreference存储记录，只有第一次启动APP才有引导页
    //private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_option);

        int count = Config.msp.getInt("count",0);
        //判断程序是第几次运行，如果是第一次运行则跳转到引导页面
        if (count == 0){
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), GuideActivity.class);
            startActivity(intent);
            this.finish();
        }
        SharedPreferences.Editor editor = Config.msp.edit();
        //存入数据
        editor.putInt("count",++count);
        //提交修改
        editor.commit();

        initView();
    }

    private void initView() {
        container= (LinearLayout) findViewById(R.id.lin_container);
        container.setBackgroundResource(R.drawable.gamebg);
        btn_backtoMain = (Button) findViewById(R.id.btn_backtoMain);
        btn_share = (Button) findViewById(R.id.btn_share);
        btn_sure = (Button) findViewById(R.id.btn_sure);
        btn_gamelines = (Button) findViewById(R.id.btn_gamelines);
        btn_score = (Button) findViewById(R.id.btn_gamescore);
        btn_backtoMain.setOnClickListener(this);
        btn_share.setOnClickListener(this);
        btn_sure.setOnClickListener(this);
        btn_gamelines.setOnClickListener(this);
        btn_score.setOnClickListener(this);

        mGameLinesList = new String[]{"4", "5", "6"};
        mGameGoalList = new String[]{"1024", "2048", "4096"};
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_backtoMain:
                this.finish();
                break;
            case R.id.btn_sure:
                saveInfo();
                setResult(RESULT_OK);
                startActivity(new Intent(OptionActivity.this,MainActivity.class));
                break;
            case R.id.btn_share:
                //TODO
                break;
            case R.id.btn_gamelines:
                mBuilder = new AlertDialog.Builder(this);
                mBuilder.setTitle("choose the lines of the game");
                mBuilder.setItems(mGameLinesList,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                btn_gamelines.setText(mGameLinesList[which]);
                                if (btn_score.getText().equals("")){
                                    Toast.makeText(OptionActivity.this,"请选择目标分数",Toast.LENGTH_SHORT).show();
                                }else {
                                    btn_sure.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                mBuilder.create().show();
                break;
            case R.id.btn_gamescore:
                mBuilder = new AlertDialog.Builder(this);
                mBuilder.setTitle("choose the goal of the game");
                mBuilder.setItems(mGameGoalList,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                btn_score.setText(mGameGoalList[which]);
                                if (btn_gamelines.getText().equals("")){
                                    Toast.makeText(OptionActivity.this,"请选择游戏行数",Toast.LENGTH_SHORT).show();
                                }else {
                                btn_sure.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                mBuilder.create().show();
                break;
            default:
                break;
        }
    }

    private void saveInfo() {
        //传入数据，并修改保存
        SharedPreferences.Editor editor = Config.msp.edit();
        editor.putInt(Config.KEY_LINES, Integer.parseInt(btn_gamelines.getText().toString()));
        editor.putInt(Config.KEY_GOAL, Integer.parseInt(btn_score.getText().toString()));
        editor.commit();
    }
}
