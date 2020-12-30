package com.koroliuk.painter.editor.shapes;

import android.graphics.Paint;

public class Line extends Shape {


    public Line(Paint pStroke, Paint pFill, int type) {
        super(pStroke, pFill, type);
    }

    @Override
    public void draw() {
        canvas.drawLine(startX, startY, endX, endY, pStroke);
    }
}
