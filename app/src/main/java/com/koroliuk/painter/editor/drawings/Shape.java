package com.koroliuk.painter.editor.drawings;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ColorDrawable;
import android.view.MotionEvent;

import com.koroliuk.painter.editor.PainterView;

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
