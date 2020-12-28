package com.koroliuk.painter.editor.drawings;

import android.graphics.Paint;

import com.koroliuk.painter.editor.PainterView;

public class Line extends Shape {


    public Line(Paint pStroke, Paint pFill, int type) {
        super(pStroke, pFill, type);
    }

    @Override
    public void draw() {
        canvas.drawLine(startX, startY, endX, endY, pStroke);
    }
}
