package com.koroliuk.painter;

import android.graphics.Bitmap;

public class State {

    private State prev;
    private Bitmap value;
    private State next;

    public State(State prev, Bitmap value, State next) {
        this.prev = prev;
        setValue(value);
        this.next = next;
    }

    public State getPrev() {
        return prev;
    }

    public void setPrev(State prev) {
        this.prev = prev;
    }

    public Bitmap getValue() {
        return value;
    }

    public void setValue(Bitmap value) {
        this.value = value.copy(Bitmap.Config.ARGB_8888, true);
    }

    public State getNext() {
        return next;
    }

    public void setNext(State next) {
        this.next = next;
    }
}
