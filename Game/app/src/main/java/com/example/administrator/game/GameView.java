package com.example.administrator.game;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
//import java.util.logging.Handler;

/**
 * Created by Administrator on 2017-03-11.
 */

public class GameView extends TextureView implements
        TextureView.SurfaceTextureListener, View.OnTouchListener {

    static final int BLOCK_COUNT = 100;
    private Thread mThread;
    volatile private boolean mIsRunnable; // thread 갱신 변수를 읽어온다.

    volatile private float mTouchedX;
    volatile private float mTouchedY;

    private Pad mPad;
    private float mPadHalfWidth;

    private Ball mBall;
    private float mBallRadius;

    private  float mBlockWidth;
    private  float mBlockHeight;

    private int mLife;

    private ArrayList<DrawableItem> mItemList; //mBlockList

    private ArrayList<Block> mBlockList;
    private long mGameStartTime;


    private Handler mHandler;


    public GameView(final Context context){
        super(context);
        setSurfaceTextureListener(this);
        setOnTouchListener(this);
        mHandler = new Handler(){
          //UI Thread 에서 실행되는 Handler
            @Override
            public void handleMessage(Message message) {
                //처리
                Intent intent = new Intent(context, ClearActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtras(message.getData());

                context.startActivity(intent);
            }
        };
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

                while (true) { // 반복

                    long startTime = System.currentTimeMillis();

                    synchronized (GameView.this) {
                        if (!mIsRunnable) {
                            break;
                        }


                        Canvas canvas = lockCanvas();
                        if (canvas == null) {
                            continue; // 캔버스를 가져올 수 없을 때는 루프를 다시 시작.
                        }
                        canvas.drawColor(Color.BLACK);

                        float padLeft = mTouchedX - mPadHalfWidth;
                        float padRight = mTouchedX + mPadHalfWidth;
                        mPad.setLeftRight(padLeft, padRight);
                        mBall.move();

                        float ballTop = mBall.getY() - mBallRadius;
                        float ballLeft = mBall.getX() - mBallRadius;
                        float ballBottom = mBall.getY() + mBallRadius;
                        float ballRight = mBall.getX() + mBallRadius;

                        if (ballLeft < 0 && mBall.getSpeedX() < 0 || ballRight >= getWidth() && mBall.getSpeedX() > 0) {
                            mBall.setSpeedX(-mBall.getSpeedX());
                        }// 가로 방향 벽에 부딪혔으므로, 가로 속도를 반전
                        if (ballTop < 0 ) {
                            mBall.setSpeedY(-mBall.getSpeedY());
                        }// 세로 방향 벽에 부딪혔으므로, 세로 속도를 반전

                        if(ballTop > getHeight()){
                            if(mLife >0) {
                                mLife--;
                                mBall.reset();
                            }else {
                                    unlockCanvasAndPost(canvas);
                                    Message message = Message.obtain();
                                    Bundle bundle = new Bundle();
                                    bundle.putBoolean(ClearActivity.EXTRA_IS_CLEAR,false);
                                    bundle.putInt(ClearActivity.EXTRA_BLOCK_COUNT, getBlockCount());
                                    bundle.putLong(ClearActivity.EXTRA_TIME, System.currentTimeMillis()-mGameStartTime);
                                    message.setData(bundle);
                                    mHandler.sendMessage(message);
                                    return;
                            }

                        }
                        //블록과 공의 충돌 판정

                        Block leftBlock = getBlock(ballLeft,mBall.getY());
                        Block topBlock = getBlock(mBall.getX(),ballTop);
                        Block rightBlock = getBlock(ballRight, mBall.getY());
                        Block bottomBlock = getBlock(mBall.getX(),ballBottom);

                        // 클리어 처리
                        boolean isCollision = false;
                        // 충돌한 블록이 있으면, 충돌 처리 한다.

                        if(leftBlock != null){
                            mBall.setSpeedX(-mBall.getSpeedX());
                            leftBlock.collision();
                            isCollision = true;
                        }
                        if(topBlock != null){
                            mBall.setSpeedY(-mBall.getSpeedY());
                            topBlock.collision();
                            isCollision = true;
                        }
                        if(rightBlock != null){
                            mBall.setSpeedX(-mBall.getSpeedX());
                            rightBlock.collision();
                            isCollision = true;
                        }
                        if(bottomBlock != null){
                            mBall.setSpeedY(-mBall.getSpeedY());
                            bottomBlock.collision();
                            isCollision = true;
                        }

                        // 패드의 윗면 좌표와 공의 속도 획득
                        float padTop = mPad.getTop();
                        float ballSpeedY = mBall.getSpeedY();

                        if( ballBottom > padTop && ballBottom-ballSpeedY <padTop && padLeft < ballRight && padRight >ballLeft){
                            // 속도 올리기
                            if(ballSpeedY < mBlockHeight /3){
                                ballSpeedY *=-1.05f;
                            } else {
                                ballSpeedY = -ballSpeedY;
                            }

                            float ballSpeedX = mBall.getSpeedX() + (mBall.getX() - mTouchedX /10);
                            if(ballSpeedX > mBlockWidth /5){
                                ballSpeedX = mBlockWidth /5;
                            }
                            mBall.setSpeedY(ballSpeedY);
                            mBall.setSpeedX(ballSpeedX);
                        }



                        for (DrawableItem item : mItemList) {
                            //mItemList의 내용이 하나씩 block에 전달된다.
                            item.draw(canvas, paint);
                        }
                        unlockCanvasAndPost(canvas); // 그린다.

                        // UI Thread Handler 호출
                        if(isCollision && getBlockCount() == 0){
                            Message message = Message.obtain();
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(ClearActivity.EXTRA_IS_CLEAR,true);
                            bundle.putInt(ClearActivity.EXTRA_BLOCK_COUNT,0);
                            bundle.putLong(ClearActivity.EXTRA_TIME, System.currentTimeMillis()-mGameStartTime);
                            message.setData(bundle);

                            mHandler.sendMessage(message);

                        }

                    }//synchon end

                    long sleepTime = 16 - (System.currentTimeMillis() - startTime); // 16 = 1/60 초
                    if (sleepTime > 0) {
                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {
                        }
                    }// if
                }
            }
        });
        mIsRunnable = true;
        mThread.start();
    }
    public void stop(){
        mIsRunnable = false;

    }



    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mTouchedX = event.getX();
        mTouchedY = event.getY();

        return true;
    }




    public void readyObjects(int width, int height){
        mBlockWidth = width/10;
        mBlockHeight = height/20;
        mLife = 5;

        mItemList = new ArrayList<DrawableItem>(); // mItemList 초기화
        mBlockList = new ArrayList<Block>();
        for (int i = 0; i< BLOCK_COUNT ;i++){
            float blockTop = i/10*mBlockHeight;
            float blockLeft = i %10*mBlockWidth;
            float blockBottom = blockTop + mBlockHeight;
            float blockRight = blockLeft + mBlockWidth;
            mBlockList.add(new Block(blockTop,blockLeft,blockBottom,blockRight));

        }
        mItemList.addAll(mBlockList);
        mPad = new Pad(height*0.8f, height*0.85f);
        mItemList.add(mPad);
        mPadHalfWidth = width/10;

        mBallRadius = width < height ? width/40 : height /40;
        mBall = new Ball(mBallRadius,width/2, height/2);
        mItemList.add(mBall);
        mLife = 5;
        mGameStartTime = System.currentTimeMillis();
    }

    //특정 좌표에 있는 블록을 가져오는 메소드
    private Block getBlock(float x, float y){
        int index = (int)(x / mBlockWidth)+ (int)(y/mBlockHeight) *10;
        if(0<=index && index < BLOCK_COUNT){
            Block block = (Block) mItemList.get(index);
            if(block.isExist()){
                return block;
            }
        }
        return null;
    }


    private int getBlockCount(){
        int count =0;
        for(Block block : mBlockList){
            if(block.isExist()){
                count++;
            }
        }
        return count;
    }


}
