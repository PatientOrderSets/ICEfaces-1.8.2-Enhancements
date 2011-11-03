package org.icefaces.tutorial.easyajaxpush;

import javax.faces.event.ActionEvent;

public class Counter {

    private int count;

    public Counter() {
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void increment(ActionEvent event) {
        count++;
    }

    public void decrement(ActionEvent event) {
        count--;
    }

}
