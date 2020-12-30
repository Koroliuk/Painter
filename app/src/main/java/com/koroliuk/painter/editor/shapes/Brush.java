package com.koroliuk.painter.editor.shapes;

import android.graphics.Paint;
// клас пензля
public class Brush extends Shape {


    public Brush(Paint pStroke, Paint pFill, int type) {
        super(pStroke, pFill, type);
    }

    @Override
    public void draw() {
        canvas.drawLine(startX, startY, endX, endY, pStroke);    }
}
