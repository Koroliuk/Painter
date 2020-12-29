package com.koroliuk.painter.editor.drawing_editor;

import android.view.MotionEvent;

import com.koroliuk.painter.editor.PainterView;

public class CubeEditor extends Editor {
    public CubeEditor(PainterView myEditor) {
        super(myEditor);
    }

    @Override
    public void edit(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                myEditor.isDrawing = true;
                myEditor.lastEdited.startX = x;
                myEditor.lastEdited.startY = y;
                myEditor.lastEdited.endX = x;
                myEditor.lastEdited.endY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                myEditor.lastEdited.endX = x;
                myEditor.lastEdited.endY = y;
                myEditor.invalidate();
                break;
            case MotionEvent.ACTION_UP:
                myEditor.isDrawing = false;
                myEditor.addToDrawen();
                break;
        }
    }
}
