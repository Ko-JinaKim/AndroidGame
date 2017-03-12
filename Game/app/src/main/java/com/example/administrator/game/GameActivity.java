package com.example.administrator.game;

import android.content.Intent;
import android.icu.text.DateFormat;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;

import java.lang.reflect.Field;

public class GameActivity extends AppCompatActivity {
    private GameView mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = new GameView(this, savedInstanceState); // 저장된 데이터 저장

        try{
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menukeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if(menukeyField != null){
                menukeyField.setAccessible(true);
                menukeyField.setBoolean(config, false);
            }
        } catch (Exception e){

        }
        setContentView(mView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mView.stop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mView.onSaveInstanceState(outState);
    }


}
