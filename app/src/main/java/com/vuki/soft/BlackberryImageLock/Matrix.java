package com.vuki.soft.BlackberryImageLock;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * use to draw bitmap matrix number.
 */
public class Matrix {
    private List<List<Integer>> numMatrix;
    private static final int COLUMN_NUMBER = 6;
    private static final int MATRIX_SIZE = 30;
    private int imageWidth;
    private int imageHeight;
    private int choosedNumberI = -1;
    private int choosedNumberJ = -1;
    private Paint darkPaint;
    private Paint lightPaint;

    /**
     * @param _imageWidth image width to draw (in pixel)
     * @param _imageHeight image width to draw (in pixel)
     */
    public Matrix(int _imageWidth, int _imageHeight) {
        imageWidth = _imageWidth;
        imageHeight = _imageHeight;
        int squareWidth = imageWidth / COLUMN_NUMBER;
        numMatrix = new ArrayList<>();
        Random mRandom = new Random();
        for (int i = 0; i < MATRIX_SIZE; i++) {
            numMatrix.add(new ArrayList<Integer>());
            for (int j = 0; j < MATRIX_SIZE; j++) {
                numMatrix.get(i).add(mRandom.nextInt(10));
            }
        }
        darkPaint = new Paint();
        darkPaint.setColor(Color.WHITE);
        darkPaint.setShadowLayer(3, 2, 2, Color.BLACK);
        darkPaint.setAlpha(220);
        darkPaint.setTextSize(squareWidth * 2 / 3);
        lightPaint = new Paint();
        lightPaint.setColor(Color.WHITE);
        lightPaint.setShadowLayer(2, 2, 2, Color.BLACK);
        lightPaint.setAlpha(70);
        lightPaint.setTextSize(squareWidth * 2 / 3);
    }

    /**
     * remove the lighting number
     */
    public void deleteChoosedNumber() {
        this.choosedNumberI = -1;
        this.choosedNumberJ = -1;
    }

    /**
     * @param movex
     * @param movey
     * @return bitmap of matrix number.
     */
    public Bitmap drawMatrix(int movex, int movey) {
        int squareWidth = imageWidth / COLUMN_NUMBER;
        Bitmap b = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        for (int i = 0; i < numMatrix.size(); i++)
            for (int j = 0; j < numMatrix.get(0).size(); j++) {
                int x = j * squareWidth + movex;
                int y = i * squareWidth + movey;
                x %= (numMatrix.get(0).size() * squareWidth);
                y %= (numMatrix.size() * squareWidth);
                x++;
                y++;

                if (i == choosedNumberI && j == choosedNumberJ) {
                    c.drawCircle(x, y, squareWidth / 2, lightPaint);
                    c.drawText(String.valueOf(numMatrix.get(i).get(j)), x - (int) (darkPaint.measureText("0") / 2), (int) (y - ((darkPaint.descent() + darkPaint.ascent()) / 2)), darkPaint);

                }

                if (x < -2 * squareWidth || y < -2 * squareWidth || x >= imageWidth + 2 * squareWidth || y >= imageHeight + 2 * squareWidth)
                    continue;

                if (choosedNumberI >= 0)
                    c.drawText(String.valueOf(numMatrix.get(i).get(j)), x - (int) (darkPaint.measureText("0") / 2), (int) (y - ((darkPaint.descent() + darkPaint.ascent()) / 2)), lightPaint);
                else
                    c.drawText(String.valueOf(numMatrix.get(i).get(j)), x - (int) (darkPaint.measureText("0") / 2), (int) (y - ((darkPaint.descent() + darkPaint.ascent()) / 2)), darkPaint);
            }
        return b;
    }

    /**
     *
     * @return if return == -1, no number choosed
     */
    public int getChoosedNumberPosX(){
        int squareWidth = imageWidth / COLUMN_NUMBER;
        return choosedNumberJ*squareWidth;
    }

    /**
     *
     * @return if return == -1, no number choosed
     */
    public int getChoosedNumberPosY(){
        int squareWidth = imageWidth / COLUMN_NUMBER;
        return choosedNumberI*squareWidth;
    }

    /**
     * light a number on matrix number.
     *
     * @param posX  position x in pixel
     * @param posY  positionn y in pixel
     * @param moveX
     * @param moveY
     */
    public void chooseNumber(int posX, int posY, int moveX, int moveY) {
        int squareWidth = imageWidth / COLUMN_NUMBER;
        posX -= moveX;
        posY -= moveY;
        double minDistance = -1;
        int ret = -1;
        // check for 4 other
        int nextPointx[] = {0, 1, -1, 1};
        int nextPointy[] = {0, 0, 1, 0};
        for (int i = 0; i < 4; i++) {
            posX += nextPointx[i] * squareWidth;
            posY += nextPointy[i] * squareWidth;
            Double newDistance;
            int tmpx = posX;
            int tmpy = posY;
            if (tmpx < 0) tmpx += squareWidth * ((int) Math.floor(-tmpx / squareWidth + 2));
            if (tmpy < 0) tmpy += squareWidth * ((int) Math.floor(-tmpy / squareWidth + 2));

            if (i == 0)
                newDistance = Math.sqrt((tmpx % squareWidth) * (tmpx % squareWidth) + (tmpy % squareWidth) * (tmpy % squareWidth));
            else if (i == 1)
                newDistance = Math.sqrt((squareWidth - tmpx % squareWidth) * (squareWidth - tmpx % squareWidth) + (tmpy % squareWidth) * (tmpy % squareWidth));
            else if (i == 2)
                newDistance = Math.sqrt((tmpx % squareWidth) * (tmpx % squareWidth) + (squareWidth - tmpy % squareWidth) * (squareWidth - tmpy % squareWidth));
            else
                newDistance = Math.sqrt((squareWidth - tmpx % squareWidth) * (squareWidth - tmpx % squareWidth) + (squareWidth - tmpy % squareWidth) * (squareWidth - tmpy % squareWidth));

            if (posX < 0)
                posX = (posX + numMatrix.get(0).size() * squareWidth) % (numMatrix.get(0).size() * squareWidth);
            if (posY < 0)
                posY = (posY + numMatrix.size() * squareWidth) % (numMatrix.size() * squareWidth);
            int x = (int) Math.floor((double) posX / squareWidth);
            int y = (int) Math.floor((double) posY / squareWidth);
            if (x >= numMatrix.get(0).size()) x = 0;
            if (y >= numMatrix.size()) y = 0;
            if (minDistance == -1) {
                minDistance = newDistance;
                choosedNumberI = y;
                choosedNumberJ = x;
            } else {
                if (minDistance > newDistance) {
                    minDistance = newDistance;
                    choosedNumberJ = x;
                    choosedNumberI = y;
                }
            }

        }
        if (minDistance > squareWidth / 3) ret = -1;

    }

    /**
     * @param numPosx
     * @param numPosy
     * @param movex
     * @param movey
     * @return
     */
    public int getNumber(int numPosx, int numPosy, int movex, int movey) {
        int squareWidth = imageWidth / COLUMN_NUMBER;
        int newPosx = numPosx - movex;
        int newPosy = numPosy - movey;

        double minDistance = -1;
        int ret = -1;
        // check for 4 other
        int nextPointx[] = {0, 1, -1, 1};
        int nextPointy[] = {0, 0, 1, 0};
        for (int i = 0; i < 4; i++) {
            newPosx += nextPointx[i] * squareWidth;
            newPosy += nextPointy[i] * squareWidth;
            Double newDistance;
            int tmpx = newPosx;
            int tmpy = newPosy;
            if (tmpx < 0) tmpx += squareWidth * ((int) Math.floor(-tmpx / squareWidth + 2));
            if (tmpy < 0) tmpy += squareWidth * ((int) Math.floor(-tmpy / squareWidth + 2));

            if (i == 0)
                newDistance = Math.sqrt((tmpx % squareWidth) * (tmpx % squareWidth) + (tmpy % squareWidth) * (tmpy % squareWidth));
            else if (i == 1)
                newDistance = Math.sqrt((squareWidth - tmpx % squareWidth) * (squareWidth - tmpx % squareWidth) + (tmpy % squareWidth) * (tmpy % squareWidth));
            else if (i == 2)
                newDistance = Math.sqrt((tmpx % squareWidth) * (tmpx % squareWidth) + (squareWidth - tmpy % squareWidth) * (squareWidth - tmpy % squareWidth));
            else
                newDistance = Math.sqrt((squareWidth - tmpx % squareWidth) * (squareWidth - tmpx % squareWidth) + (squareWidth - tmpy % squareWidth) * (squareWidth - tmpy % squareWidth));

            if (newPosx < 0)
                newPosx = (newPosx + numMatrix.get(0).size() * squareWidth) % (numMatrix.get(0).size() * squareWidth);
            if (newPosy < 0)
                newPosy = (newPosy + numMatrix.size() * squareWidth) % (numMatrix.size() * squareWidth);
            int x = (int) Math.floor((double) newPosx / squareWidth);
            int y = (int) Math.floor((double) newPosy / squareWidth);
            if (x >= numMatrix.get(0).size()) x = 0;
            if (y >= numMatrix.size()) y = 0;
            if (minDistance == -1) {
                minDistance = newDistance;
                Log.d("vukihai first min dis ", String.valueOf(numMatrix.get(y).get(x)) + " " + String.valueOf(minDistance));
                ret = numMatrix.get(y).get(x);
            } else {
                Log.d("vukihai 3other min dis ", String.valueOf(numMatrix.get(y).get(x)) + " " + String.valueOf(newDistance));
                if (minDistance > newDistance) {
                    minDistance = newDistance;
                    ret = numMatrix.get(y).get(x);
                }
            }

        }
        if (minDistance > squareWidth / 5) ret = -1;
        Log.d("vukihai:", String.valueOf(ret));
        return ret;
    }
}
