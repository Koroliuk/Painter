package com.koroliuk.painter.editor.drawings;

import android.graphics.Color;
import android.graphics.Paint;

public class Erasor extends Shape {

    public Erasor(Paint pStroke, Paint pFill, int type) {
        super(pStroke, pFill, type);
    }

    @Override
    public void draw() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(startX, startY, 50, paint);
    }
}