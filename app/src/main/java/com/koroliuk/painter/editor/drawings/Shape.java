package com.koroliuk.painter.editor.drawings;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public abstract class Shape {

    public Paint pStroke;
    public Paint pFill;
    public float startX;
    public float startY;
    public float endX;
    public float endY;
    public Canvas canvas;
    public int type;
    public Path path = new Path();

    protected Shape(Paint pStroke, Paint pFill, int type) {
        this.pStroke = pStroke;
        this.pFill = pFill;
        this.type = type;
    }

    public abstract void draw();
}
