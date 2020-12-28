package com.koroliuk.painter.editor.drawing_editor;

import android.view.MotionEvent;

import com.koroliuk.painter.editor.PainterView;

public abstract class Editor {

    public PainterView myEditor;

    public  Editor(PainterView myEditor) {
        this.myEditor = myEditor;
    }

    public abstract void edit(MotionEvent event);
}
