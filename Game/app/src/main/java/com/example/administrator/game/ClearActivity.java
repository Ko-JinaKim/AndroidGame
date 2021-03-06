package com.example.administrator.game;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Field;

public class ClearActivity extends AppCompatActivity {

    public static final String EXTRA_IS_CLEAR = "com.example.administrator.game.EXTRA.IS_CLEAR";
    public static final String EXTRA_BLOCK_COUNT = "com.example.administrator.game.EXTRA.BLOCK_COUNT";
    public static final String EXTRA_TIME = "com.example.administrator.game.EXTRA.TIME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear);

        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if(menuKeyField != null){
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config,false);
            }
        } catch (Exception e){
            setContentView(R.layout.activity_clear);
        }


        Intent receiveIntent = getIntent();
        if(receiveIntent == null){
            finish();
        }
        // 만약 인텐트를 받지 못하면 종료할 것

        Bundle receiveExtras = receiveIntent.getExtras();

        if(receiveExtras == null){
            finish();
        }

        boolean isClear = receiveExtras.getBoolean(EXTRA_IS_CLEAR, false);
        int blockCount = receiveExtras.getInt(EXTRA_BLOCK_COUNT, 0);
        long clearTime = receiveExtras.getLong(EXTRA_TIME, 0);

        TextView textTitle = (TextView) findViewById(R.id.textTitle);
        TextView textBlockCount = (TextView) findViewById(R.id.textBlockCount);
        TextView textClearTime = (TextView) findViewById(R.id.textClearTime);
        Button gameStart = (Button) findViewById(R.id.buttonGameStart);

        if(isClear){
            textTitle.setText(R.string.clear);
        }else {
            textTitle.setText(R.string.game_over);
        }

        // 남은 블록 수를 표기
        textBlockCount.setText(getString(R.string.block_count, blockCount));
        //경과 시간

        textClearTime.setText(getString(R.string.time, clearTime/1000, clearTime % 1000));

        gameStart.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClearActivity.this, GameActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });



    }// onCreate

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.action_settings){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
