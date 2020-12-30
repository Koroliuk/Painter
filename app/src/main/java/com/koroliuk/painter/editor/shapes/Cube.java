package com.koroliuk.painter.editor.shapes;

import android.graphics.Paint;
import android.graphics.Path;

public class Cube extends Shape {


    public Cube(Paint pStroke, Paint pFill, int type) {
        super(pStroke, pFill, type);
    }

    public void drawRect(float sx, float sy, float ex, float ey) {
        float right = Math.max(sx, ex);
        float left = Math.min(sx, ex);
        float bottom = Math.max(sy, ey);
        float top = Math.min(sy, ey);
        canvas.drawRect(left, top, right, bottom, pStroke);
        if (pFill != null) {
            canvas.drawRect(left, top, right, bottom, pFill);
        }
    }

    public void drawLine(float sx, float sy, float ex, float ey) {
        Path linePath = new Path();
        linePath.moveTo(sx, sy);
        linePath.lineTo(ex, ey);
        canvas.drawPath(linePath, pStroke);
    }

    @Override
    public void draw() {
        float sideLength = (float) Math.sqrt(Math.pow(startX-endX, 2)+Math.pow(startY-endY, 2))/1.86f;
        float right = Math.max(startX, endX);
        float left = Math.min(startX, endX);
        float bottom = Math.max(startY, endY);
        float top = Math.min(startY, endY);
        drawRect(left, bottom-sideLength, left+sideLength, bottom);
        drawRect(right-sideLength, top, right, top+sideLength);
        drawLine(left, bottom-sideLength, right-sideLength, top);
        drawLine(left+sideLength, bottom-sideLength, right, top);
        drawLine(left+sideLength, bottom, right, top+sideLength);
        drawLine(left, bottom, right-sideLength, top+sideLength);
    }
}
