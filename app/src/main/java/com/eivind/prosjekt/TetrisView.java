package com.eivind.prosjekt;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class TetrisView extends SurfaceView implements SurfaceHolder.Callback {

    private int mWidth;
    private int mHeight;
    private TetrisThread mThread=null;
    //private ArrayList<Position> mSnake = new ArrayList<Position>();
    private ArrayList<Position> currPos = new ArrayList<Position>();
    Random rand = new Random();


    int step = 0;
    int stepX = 4;
    int width = 100;
    int increment = 0;
    int x = 0;
    int y = 0;
    boolean stopped = false;
    int n = rand.nextInt(7);
    private ArrayList<Position> simulatedPosRight = new ArrayList<Position>();
    private ArrayList<Position> simulatedPosLeft = new ArrayList<Position>();

    int[][] shape1 = {
            {1, 1, 0},
            {0, 1, 1},
            {0, 0, 0}
    };
    int[][] shape2 = {
            {0, 0, 0},
            {0, 1, 1},
            {1, 1, 0}
    };
    int[][] shape3 = {
            {0, 1, 0},
            {1, 1, 1},
            {0, 0, 0}
    };
    int[][] shape4 = {
            {1, 1},
            {1, 1}
    };
    int[][] shape5 = {
            {1, 0, 0},
            {1, 1, 1},
            {0, 0, 0}
    };
    int[][] shape6 = {
            {0, 0, 1},
            {1, 1, 1},
            {0, 0, 0}
    };
    int[][] shape7 = {
            {0, 1, 0, 0},
            {0, 1, 0, 0},
            {0, 1, 0, 0},
            {0, 1, 0, 0}
    };

    int[][][] shapes = {copyMatrix(shape1), copyMatrix(shape2), copyMatrix(shape3), copyMatrix(shape4), copyMatrix(shape5), copyMatrix(shape6), copyMatrix(shape7) };
    ArrayList<Position> finished = new ArrayList<>();


    public TetrisView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        holder.setFixedSize(1000, 2000);
        mThread = new TetrisThread(holder, this);
        setFocusable(true); // make sure we get key events
        addWalls(finished);
    }
    public TetrisThread getThread() {
        return mThread;
    }

    private int[][] copyMatrix(int[][] matrix){
        int[][] clone = new int[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++){
            System.arraycopy(matrix[i], 0 , clone[i], 0, matrix.length);
        }
        return clone;
    }

    public void moveRight(){

        if(checkCollison(simulatedPosRight)){
            stepX++;
        }
    }

    public void moveLeft(){
        if(checkCollison(simulatedPosLeft)){
            stepX--;
        }
    }

    @Override
    public void onDraw(Canvas canvas){
        if (!mThread.isPaused()) {
            canvas.drawColor(Color.GRAY);
            drawShape(canvas);
            drawFinished(canvas);
        }
    }


    public void drawShape(Canvas canvas) {

        Paint paint = new Paint();
        paint.setColor(Color.YELLOW);
        increment++;
        currPos = new ArrayList<>();
        simulatedPosRight = new ArrayList<>();
        simulatedPosLeft = new ArrayList<>();

        int width = 100;
        RectF rect = null;
        checkLost();
        for(int i = 0; i<shapes[n].length; i++){
            for (int j = 0; j < shapes[n].length; j++){
                if (shapes[n][i][j] == 1) {

                    x = 100 * stepX + j * 100;
                    y = 100 * step + i * 100;
                    if (stopped){
                        //Log.e("test", String.valueOf(n));
                        finished.add(new Position(x, y));
                        //resetRotation();
                    }
                    currPos.add(new Position(x, y + 100));
                    simulatedPosRight.add(new Position(x + 100, y));
                    simulatedPosLeft.add(new Position(x - 100, y));
                    //Log.e("Test2", x + "," + y);
                    rect = new RectF(x, y, x+width, y+width);
                    canvas.drawRect(rect, paint);
                }
            }
        }

        if(stopped){
            stepX = 4;
            step = 0;
            stopped = false;
            Random rng = new Random();
            n = rng.nextInt(7);
        }

        if(increment % 5 == 0){
            if(y < 1900 && checkCollison(currPos)){
                step++;
            }else{
                stopped = true;
            }
        }
    }

    public void checkLost(){
        for (Position p1 : finished){
            if(p1.getyPos() < 100){
                //Log.e("???", finished.size() + " ");
                finished = new ArrayList<>();
                addWalls(finished);
            }
        }
    }

    public void resetRotation(){
        shapes[0] = copyMatrix(shape1);
        shapes[1] = copyMatrix(shape2);
        shapes[2] = copyMatrix(shape3);
        shapes[3] = copyMatrix(shape4);
        shapes[4] = copyMatrix(shape5);
        shapes[5] = copyMatrix(shape6);
        shapes[6] = copyMatrix(shape7);
    }

    public void addWalls(ArrayList<Position> finished){
        for (int i = 1; i < 21; i++){
            finished.add(new Position(-100, 100 * i));
        }
        for (int i = 1; i < 21; i++){
            finished.add(new Position(1000, 100 * i));
        }
        for (int i = 1; i < 10; i++){
            finished.add(new Position(-100, 2000));
        }
    }

    public boolean checkCollison(ArrayList<Position> pos){
        for (Position p : pos){
            for (Position p1 : finished){
                //Log.e("???", p1.getxPos() + "," + p1.getyPos());
                if(p.equals(p1)){
                    return false;
                }
            }
        }
        return true;
    }

    public void drawFinished(Canvas canvas){
        RectF finishedBlocks = null;
        Paint paint = new Paint();
        paint.setColor(Color.YELLOW);

        for (Position pos : finished){
            finishedBlocks = new RectF(pos.getxPos(), pos.getyPos(), pos.getxPos()+width, pos.getyPos()+width);
            canvas.drawRect(finishedBlocks, paint);
        }
    }

    void rotate90Clockwise(int matrix[][]){

        int num = shapes[n].length;

        for (int i = 0; i < num / 2; i++) {
            for (int j = i; j < num - i - 1; j++) {

                int temp = matrix[i][j];
                matrix[i][j] = matrix[num - 1 - j][i];
                matrix[num - 1 - j][i] = matrix[num - 1 - i][num - 1 - j];
                matrix[num - 1 - i][num - 1 - j] = matrix[j][num - 1 - i];
                matrix[j][num - 1 - i] = temp;
            }
        }
    }


    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        mWidth=width;
        mHeight=height;
    }
    public void surfaceCreated(SurfaceHolder holder) {
        mThread.setRunning(true);
        mThread.start();
    }
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        mThread.setRunning(false);
        while (retry) {
            try {
                mThread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }
    public class Position {
        private int xPos;
        private int yPos;
        public Position(int x, int y) {
            xPos=x;
            yPos=y;
        }

        public void setxPos(int xPos) {
            this.xPos = xPos;
        }

        public void setyPos(int yPos) {
            this.yPos = yPos;
        }

        public Position(Position orig) {
            xPos=orig.xPos;
            yPos=orig.yPos;
        }

        public int getxPos() {
            return xPos;
        }

        public int getyPos() {
            return yPos;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof Position)) return false;
            Position theOther = (Position)other;
            return xPos==theOther.xPos && yPos==theOther.yPos;
        }
    }

}
