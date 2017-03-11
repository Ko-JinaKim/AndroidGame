package com.example.administrator.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017-03-11.
 */

public class Block {

    private final float mTop;
    private final float mLeft;
    private final float mBottom;
    private final float mRight;
    private int mHard;

    public Block (float top, float left, float bottom, float right){
        mTop = top;
        mLeft = left;
        mBottom = bottom;
        mRight = right;
        mHard = 1;
    }

    public void draw(Canvas canvas, Paint paint){
        if (mHard > 0) {
            // 내구성이 0 이상이 경우 그린다.
            paint.setColor(Color.BLUE);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(mLeft,mTop,mRight,mBottom,paint);
            //테두리를 그린다.
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(4f);
            canvas.drawRect(mLeft,mTop,mRight,mBottom,paint);
        }
    }

    private ArrayList<Block> mItemList;
    public void readyObjects(int width, int height){
        float blockWidth = width/10;
        float blockHeight = height/20;
        mItemList = new ArrayList<Block>(); // mItemList 초기화
        for (int i = 0; i< 100;i++){
            float blockTop = i/10*blockHeight;
            float blockLeft = i %10*blockWidth;
            float blockBottom = blockTop + blockHeight;
            float blockRight = blockLeft + blockWidth;
            mItemList.add(new Block(blockTop,blockLeft,blockBottom,blockRight));
        }
    }
}
