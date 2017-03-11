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

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {


        return false;
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
                int i = 0;
                while (mIsRunnable){ // 반복

                    Canvas canvas = lockCanvas();
                    if(canvas == null){
                        continue; // 반복문 처음으로 돌아감.
                    }
                    canvas.drawColor(Color.BLACK);
                    // mTouchedX와 mTouchedY 를 중심으로 한다.
                    canvas.drawCircle(mTouchedX,mTouchedY,50,paint);
                    unlockCanvasAndPost(canvas);
                    i++;
                    if(i>1000){
                        i = 0;
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
}
