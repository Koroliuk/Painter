package com.koroliuk.painter.editor.drawing_editor;

import android.view.MotionEvent;

import com.koroliuk.painter.editor.DrawerView;

public class BrushEditor extends Editor {

    public BrushEditor(DrawerView myEditor) {
        super(myEditor);
    }

    @Override
    public void edit(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                myEditor.lastEdited.startX = x;
                myEditor.lastEdited.startY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                myEditor.lastEdited.endX = x;
                myEditor.lastEdited.endY = y;
                myEditor.addToDrawen();
                myEditor.lastEdited.startX = x;
                myEditor.lastEdited.startY = y;
                break;
            case MotionEvent.ACTION_UP:
                myEditor.lastEdited.endX = x;
                myEditor.lastEdited.endY = y;
                myEditor.addToDrawen();
                break;
        }
    }
}
