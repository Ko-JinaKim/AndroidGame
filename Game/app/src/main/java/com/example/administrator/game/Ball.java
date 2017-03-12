package com.example.administrator.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

/**
 * Created by Administrator on 2017-03-11.
 */

public class Ball implements DrawableItem {

    private float mX;
    private float mY;

    private float mSpeedX;
    private float mSpeedY;
    private final float mRadius;
    // 초기 속도
    private final float mInitialSpeedX;
    private final float mInitialSpeedY;

    // 출현 위치

    private final float mInitialX;
    private final float mInitialY;


    //공에서 저장할 정보를 모은다.

    private static final String KEY_X = "x";
    private static final String KEY_Y = "y";
    private static final String KEY_SPEED_X = "speed_x";
    private static final String KEY_SPEED_Y = "speed_y";

    private static final String KEY_LIFE ="life";
    private static final String KEY_GAME_START_TIME = "game_start_time";
    private static final String KEY_BALL = "ball";


    public Ball(float radius, float initialX, float initialY){
        mRadius = radius;
        mSpeedX = radius/5;
        mSpeedY = -radius/5;
        mX = initialX;
        mY = initialY;
        mInitialSpeedX = mSpeedX;
        mInitialSpeedY = mSpeedY;
        mInitialX = mX;
        mInitialY = mY;

    }

    public void move(){
        mX += mSpeedX;
        mY += mSpeedY;
    }

    public void draw(Canvas canvas, Paint paint){
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mX,mY,mRadius,paint);
    }

    public float getX() {
        return mX;
    }

    public float getY() {
        return mY;
    }

    public float getSpeedX() {
        return mSpeedX;
    }

    public float getSpeedY() {
        return mSpeedY;
    }

    public void setSpeedX(float SpeedX) {
        this.mSpeedX = SpeedX;
    }

    public void setSpeedY(float SpeedY) {
        this.mSpeedY = SpeedY;
    }

    public void reset(){
        mX = mInitialX;
        mY = mInitialY;
        mSpeedX = mInitialSpeedX*((float) Math.random() - 0.5f); // 가로 방향 속도를 랜덤으로 한다. 예측 불가
        mSpeedY = mInitialSpeedY;
    }

/*    public Bundle save(int width, int height){
        Bundle outState = new Bundle();
        outState.putFloat(KEY_X, mX /width);
        outState.putFloat(KEY_Y, mY / height);
        //outState.
    }*/


}
