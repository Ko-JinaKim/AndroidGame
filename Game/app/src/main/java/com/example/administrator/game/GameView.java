package com.example.administrator.game;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.os.Vibrator;
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

    private static final String KEY_LIFE ="life";
    private static final String KEY_GAME_START_TIME = "game_start_time";
    private static final String KEY_BALL = "ball";
    private static final String KEY_BLOCK = "block";

    private final Bundle mSavedInstanceState;
    Vibrator vibrator = (Vibrator)getContext().getSystemService(Context.VIBRATOR_SERVICE);


    public GameView(final Context context,Bundle savedInstanceState){
        super(context);
        setSurfaceTextureListener(this);
        setOnTouchListener(this);
        mSavedInstanceState = savedInstanceState;
        //인수로 가져온 Bundle을 멤버 변수에 저장한다.
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


                //스트림의 종류 지정
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

                ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC,
                        ToneGenerator.MAX_VOLUME);
                paint.setColor(Color.RED);
                paint.setStyle(Paint.Style.FILL);

                //부딪힐 때 효과음
                int collisionTime = 0;
                int soundIndex = 0;

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
                            toneGenerator.startTone(ToneGenerator.TONE_DTMF_0,10);
                            vibrator.vibrate(3);
                        }// 가로 방향 벽에 부딪혔으므로, 가로 속도를 반전

                        if (ballTop < 0) {
                            mBall.setSpeedY(-mBall.getSpeedY());
                            toneGenerator.startTone(ToneGenerator.TONE_DTMF_0,10);
                            vibrator.vibrate(3);
                        }// 세로 방향 벽에 부딪혔으므로, 세로 속도를 반전

                        if (ballTop > getHeight()) {
                            if (mLife > 0) {
                                mLife--;
                                mBall.reset();
                            } else {
                                toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE);
                                vibrator.vibrate(3);
                                unlockCanvasAndPost(canvas);
                                Message message = Message.obtain();
                                Bundle bundle = new Bundle();
                                bundle.putBoolean(ClearActivity.EXTRA_IS_CLEAR, false);
                                bundle.putInt(ClearActivity.EXTRA_BLOCK_COUNT, getBlockCount());
                                bundle.putLong(ClearActivity.EXTRA_TIME, System.currentTimeMillis() - mGameStartTime);
                                message.setData(bundle);
                                mHandler.sendMessage(message);
                                return;
                            }

                        }
                        //블록과 공의 충돌 판정

                        Block leftBlock = getBlock(ballLeft, mBall.getY());
                        Block topBlock = getBlock(mBall.getX(), ballTop);
                        Block rightBlock = getBlock(ballRight, mBall.getY());
                        Block bottomBlock = getBlock(mBall.getX(), ballBottom);

                        // 클리어 처리
                        boolean isCollision = false;
                        // 충돌한 블록이 있으면, 충돌 처리 한다.

                        if (leftBlock != null) {
                            mBall.setSpeedX(-mBall.getSpeedX());
                            leftBlock.collision();
                            isCollision = true;
                        }
                        if (topBlock != null) {
                            mBall.setSpeedY(-mBall.getSpeedY());
                            topBlock.collision();
                            isCollision = true;
                        }
                        if (rightBlock != null) {
                            mBall.setSpeedX(-mBall.getSpeedX());
                            rightBlock.collision();
                            isCollision = true;
                        }
                        if (bottomBlock != null) {
                            mBall.setSpeedY(-mBall.getSpeedY());
                            bottomBlock.collision();
                            isCollision = true;
                        }

                        if(isCollision){
                            //부딪힌 경우
                            if(collisionTime > 0){
                                // 일정 시간 내에 부딪힌 경우 효과음을 바꾼다.
                                if(soundIndex < 15){
                                    soundIndex++;
                                }
                            }else {
                                //일정 시간 내에 부딪히지 않은 경우  효과음을 되돌린다.
                                soundIndex = 1;
                            }
                            collisionTime = 10;
                            toneGenerator.startTone(soundIndex,10);
                            vibrator.vibrate(3);

                        } else if(collisionTime > 0){
                            // 블록에 부딪히지 않은 경우 남은 시간을 줄인다.
                            collisionTime--;
                        }

                        // 패드의 윗면 좌표와 공의 속도 획득
                        float padTop = mPad.getTop();
                        float ballSpeedY = mBall.getSpeedY();

                        if (ballBottom > padTop && ballBottom - ballSpeedY < padTop && padLeft < ballRight && padRight > ballLeft) {
                            // 속도 올리기

                            //효과음

                            toneGenerator.startTone(ToneGenerator.TONE_DTMF_0,10);
                            vibrator.vibrate(3);

                            if (ballSpeedY < mBlockHeight / 3) {
                                ballSpeedY *= -1.05f;
                            } else {
                                ballSpeedY = -ballSpeedY;
                            }

                            float ballSpeedX = mBall.getSpeedX() + (mBall.getX() - mTouchedX / 10);
                            if (ballSpeedX > mBlockWidth / 5) {
                                ballSpeedX = mBlockWidth / 5;
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
                        if (isCollision && getBlockCount() == 0) {
                            Message message = Message.obtain();
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(ClearActivity.EXTRA_IS_CLEAR, true);
                            bundle.putInt(ClearActivity.EXTRA_BLOCK_COUNT, 0);
                            bundle.putLong(ClearActivity.EXTRA_TIME, System.currentTimeMillis() - mGameStartTime);
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
                }//while 종료

            toneGenerator.release();
            }
        });// runnable 종료
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




    public void readyObjects(int width, int height) {
        mBlockWidth = width / 10;
        mBlockHeight = height / 20;
        mLife = 5;

        mItemList = new ArrayList<DrawableItem>(); // mItemList 초기화
        mBlockList = new ArrayList<Block>();
        for (int i = 0; i < BLOCK_COUNT; i++) {
            float blockTop = i / 10 * mBlockHeight;
            float blockLeft = i % 10 * mBlockWidth;
            float blockBottom = blockTop + mBlockHeight;
            float blockRight = blockLeft + mBlockWidth;
            mBlockList.add(new Block(blockTop, blockLeft, blockBottom, blockRight));

        }
        mItemList.addAll(mBlockList);
        mPad = new Pad(height * 0.8f, height * 0.85f);
        mItemList.add(mPad);
        mPadHalfWidth = width / 10;

        mBallRadius = width < height ? width / 40 : height / 40;
        mBall = new Ball(mBallRadius, width / 2, height / 2);
        mItemList.add(mBall);
        mLife = 5;
        mGameStartTime = System.currentTimeMillis();

        if (mSavedInstanceState != null) {
            mLife = mSavedInstanceState.getInt(KEY_LIFE);
            mGameStartTime = mSavedInstanceState.getLong(KEY_GAME_START_TIME);

            mBall.restore(mSavedInstanceState.getBundle(KEY_BALL), width, height);
            for (int i = 0; i < BLOCK_COUNT; i++) {
                mBlockList.get(i).restore(mSavedInstanceState.getBundle(KEY_BLOCK +
                        String.valueOf(i)));
            }
        }
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


    // 게임 정보를 저장하는 메서드를 만든다.
    public void onSaveInstanceState(Bundle outState){
        outState.putInt(KEY_LIFE,mLife);
        outState.putLong(KEY_GAME_START_TIME, mGameStartTime);
        outState.putBundle(KEY_BALL, mBall.save(getWidth(),getHeight()));
        for(int i=0;i<BLOCK_COUNT;i++){
            outState.putBundle(KEY_BLOCK+String.valueOf(i), mBlockList.get(i).save());
        }
    }


}































