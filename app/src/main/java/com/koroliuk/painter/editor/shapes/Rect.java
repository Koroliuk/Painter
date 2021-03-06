package com.koroliuk.painter.editor.shapes;

import android.graphics.Paint;
// клас приямокутника
public class Rect extends Shape {


    public Rect(Paint pStroke, Paint pFill, int type) {
        super(pStroke, pFill, type);
    }

    @Override
    public void draw() {
        float right = Math.max(startX, endX);
        float left = Math.min(startX, endX);
        float bottom = Math.max(startY, endY);
        float top = Math.min(startY, endY);
        canvas.drawRect(left, top , right, bottom, pStroke);
        if (pFill != null) {
            canvas.drawRect(left, top, right, bottom, pFill);
        }
    }
}
