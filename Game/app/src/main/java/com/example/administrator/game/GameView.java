package com.example.administrator.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017-03-11.
 */

public class GameView extends TextureView implements
        TextureView.SurfaceTextureListener, View.OnTouchListener {


    private Thread mThread;
    volatile private boolean mIsRunnable; // thread 갱신 변수를 읽어온다.

    public GameView(final Context context){
        super(context);
        setSurfaceTextureListener(this);
        setOnTouchListener(this);
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Paint paint = new Paint();
                paint.setColor(Color.RED);
                paint.setStyle(Paint.Style.FILL);
                Canvas canvas = lockCanvas();
                if(canvas == null){
                    return;
                }
                canvas.drawCircle(100,100,50,paint);
                unlockCanvasAndPost(canvas);
            }
        });
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        readyObjects(width,height);


    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        readyObjects(width, height);

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        //폐기를 동기화 한다.
        synchronized (this){
            return true;
        }


        //return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    public void start(){
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Paint paint = new Paint();
                paint.setColor(Color.RED);
                paint.setStyle(Paint.Style.FILL);

                while (true){ // 반복

                    synchronized (GameView.this) {
                        if (!mIsRunnable) {
                            break;
                        }


                        Canvas canvas = lockCanvas();
                        if (canvas == null) {
                            continue; // 캔버스를 가져올 수 없을 때는 루프를 다시 시작.
                        }
                        canvas.drawColor(Color.BLACK);

                        for (Block item : mItemList) {
                            //mItemList의 내용이 하나씩 block에 전달된다.
                            item.draw(canvas, paint);
                        }
                        unlockCanvasAndPost(canvas);

                    }
                }

            }
        });
        mIsRunnable = true;
        mThread.start();
    }
    public void stop(){
        mIsRunnable = false;

    }

    volatile private float mTouchedX;
    volatile private float mTouchedY;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mTouchedX = event.getX();
        mTouchedY = event.getY();

        return true;
    }


    private ArrayList<Block> mItemList; //mBlockList

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
