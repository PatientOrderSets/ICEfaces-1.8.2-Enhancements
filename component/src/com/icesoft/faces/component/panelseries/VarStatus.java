package com.icesoft.faces.component.panelseries;

import java.io.Serializable;

/**
 * @author mcollette
 * @since 1.8
 */
public class VarStatus implements Serializable {
    private int begin;
    private int end;
    private int index;

    VarStatus(int begin, int end, int index) {
        this.begin = begin;
        this.end = end;
        this.index = index;
    }

    public int getBegin() {
        return begin;
    }

    public int getEnd() {
        return end;
    }

    public int getIndex() {
        return index;
    }

    public boolean isFirst() {
        return (begin == index);
    }

    public boolean isLast() {
        return (end == index);
    }
}
