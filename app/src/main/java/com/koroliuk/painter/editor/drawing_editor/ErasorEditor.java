package com.koroliuk.painter.editor.drawing_editor;

import android.view.MotionEvent;

import com.koroliuk.painter.editor.DrawerView;

public class ErasorEditor extends Editor {

    public ErasorEditor(DrawerView myEditor) {
        super(myEditor);
    }

    @Override
    public void edit(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                myEditor.lastEdited.startX = x;
                myEditor.lastEdited.startY = y;
                myEditor.addToDrawen();
                break;
        }
    }
}
