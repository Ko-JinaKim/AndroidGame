package com.example.administrator.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017-03-11.
 */

public class Block implements DrawableItem {

    private final float mTop;
    private final float mLeft;
    private final float mBottom;
    private final float mRight;
    private int mHard;

    private boolean mIsCollision = false; // 충돌 상태를 기록 하는 플래그
    private boolean mIsExist = true;

    public Block (float top, float left, float bottom, float right){
        mTop = top;
        mLeft = left;
        mBottom = bottom;
        mRight = right;
        mHard = 1;
    }

    public void draw(Canvas canvas, Paint paint){
        if (mIsExist) {
            // 내구성이 0 이상이 경우 그린다.

            if (mIsCollision) {
                mHard--;
                mIsCollision = false;
                if (mHard <= 0) {
                    mIsExist = false;
                    return;
                }
            }
            // 색칠 부분 그리기

            paint.setColor(Color.BLUE);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(mLeft, mTop, mRight, mBottom, paint);
            //테두리를 그린다.
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(4f);
            canvas.drawRect(mLeft, mTop, mRight, mBottom, paint);

        }
    }

    public void collision () {
        mIsCollision = true; // 충돌 사실만 기록하고 실제 파괴는 draw() 시에 한다.


    }
    // 블록이 존재하는가 ? 그러면 true
    public boolean ismIsExist (){
        return mIsExist;
    }

}
