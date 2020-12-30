package com.koroliuk.painter.editor.shape_editor;

import android.view.MotionEvent;

import com.koroliuk.painter.editor.PainterView;
// клас редактора
public abstract class Editor {

    public PainterView myEditor;

    public  Editor(PainterView myEditor) {
        this.myEditor = myEditor;
    }

    public abstract void edit(MotionEvent event);
}
