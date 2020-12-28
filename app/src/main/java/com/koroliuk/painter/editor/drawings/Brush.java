package com.koroliuk.painter.editor.drawings;

import android.graphics.Color;
import android.graphics.Paint;

public class Brush extends Shape {


    public Brush(Paint pStroke, Paint pFill, int type) {
        super(pStroke, pFill, type);
    }

    @Override
    public void draw() {
        canvas.drawLine(startX, startY, endX, endY, pStroke);    }
}
