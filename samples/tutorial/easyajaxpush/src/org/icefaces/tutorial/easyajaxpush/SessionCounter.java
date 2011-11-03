package org.icefaces.tutorial.easyajaxpush;

//In ICEfaces 1.7, the SessionRenderer is in an "experimental" package
//import org.icefaces.x.core.push.SessionRenderer;

//In ICEfaces 1.8, the SessionRenderer now resides in the official package
import com.icesoft.faces.async.render.SessionRenderer;

public class SessionCounter extends Counter {

    public SessionCounter() {
        SessionRenderer.addCurrentSession("all");
    }

}
