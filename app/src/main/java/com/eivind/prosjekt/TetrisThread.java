package com.eivind.prosjekt;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class TetrisThread extends Thread {
    private final static int sleepInterval = 100;
    private SurfaceHolder mSurfaceHolder;
    private TetrisView mTetrisView;
    private boolean mRun = false;
    private boolean mPause = false;
    public TetrisThread(SurfaceHolder holder, TetrisView TetrisView){
        mSurfaceHolder = holder;
        mTetrisView = TetrisView;
    }


    @Override
    public void run() {
        Canvas c;
        while (isRunning()) {
            c=null;
            try {
                c=mSurfaceHolder.lockCanvas(null);
                synchronized(mSurfaceHolder) {
                    mTetrisView.onDraw(c);
                }
            } finally {
                if (c!=null) {
                    mSurfaceHolder.unlockCanvasAndPost(c);
                }
            }
            mySleep(sleepInterval);
        }
    }
    public void mySleep(int length) {
        try {
            sleep(length);
        }
        catch (InterruptedException e) {
            Log.i(TetrisActivity.TAG,"mySleep interrupted");
        }
    }
    public void setRunning(boolean run) {
        mRun=run;
    }
    public boolean isRunning() {
        return mRun;
    }
    public void setPaused(boolean p) {
        synchronized(mSurfaceHolder) {
            mPause = p;
        }
    }
    public boolean isPaused() {
        synchronized(mSurfaceHolder) {
            return mPause;
        }
    }
}
