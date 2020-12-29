package com.koroliuk.painter.editor.drawing_editor;

import android.view.MotionEvent;

import com.koroliuk.painter.editor.DrawerView;

public abstract class Editor {

    public DrawerView myEditor;

    public  Editor(DrawerView myEditor) {
        this.myEditor = myEditor;
    }

    public abstract void edit(MotionEvent event);
}
