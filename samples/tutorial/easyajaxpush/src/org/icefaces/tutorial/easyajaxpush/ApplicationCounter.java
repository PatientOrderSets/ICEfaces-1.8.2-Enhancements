package org.icefaces.tutorial.easyajaxpush;

import javax.faces.event.ActionEvent;

//In ICEfaces 1.7, the SessionRenderer is in an "experimental" package
//import org.icefaces.x.core.push.SessionRenderer;

//In ICEfaces 1.8, the SessionRenderer now resides in the official package
import com.icesoft.faces.async.render.SessionRenderer;

public class ApplicationCounter extends Counter {

    public ApplicationCounter() {
    }

    public synchronized void setCount(int count){
        super.setCount(count);
        SessionRenderer.render("all");
    }

    public synchronized void increment(ActionEvent event) {
        super.increment(event);
        SessionRenderer.render("all");
   }

    public synchronized void decrement(ActionEvent event) {
        super.decrement(event);
        SessionRenderer.render("all");
   }

}
