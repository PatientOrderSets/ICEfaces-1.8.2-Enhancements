/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * "The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Original Code is ICEfaces 1.5 open source software code, released
 * November 5, 2006. The Initial Developer of the Original Code is ICEsoft
 * Technologies Canada, Corp. Portions created by ICEsoft are Copyright (C)
 * 2004-2006 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 * Contributor(s): _____________________.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"
 * License), in which case the provisions of the LGPL License are
 * applicable instead of those above. If you wish to allow use of your
 * version of this file only under the terms of the LGPL License and not to
 * allow others to use your version of this file under the MPL, indicate
 * your decision by deleting the provisions above and replace them with
 * the notice and other provisions required by the LGPL License. If you do
 * not delete the provisions above, a recipient may use your version of
 * this file under either the MPL or the LGPL License."
 *
 */

package com.icesoft.faces.context.effects;

import com.icesoft.faces.util.CoreUtils;

import java.io.Serializable;

/**
 * Base class for all javascript effects
 */
public abstract class Effect implements Serializable{


    protected EffectsArguments ea = new EffectsArguments();
    private boolean queued;
    private boolean queueEnd;
    private boolean fired;
    private boolean transitory = true;
    private float duration;
    private float fps; // Max 25fps
    private float from;
    private float to;
    private boolean sync;
    private float delay;
    private String queue;
    private boolean submit;
    private String id;
    private String sequence;
    private int sequenceId;


    /**
     * Transitory effects return a component to its original state. This flag is
     * used to stop javascript from performing a partial submit at the end of an
     * effect.
     * <p/>
     * Example highlight.
     *
     * @return
     */
    public boolean isTransitory() {
        return transitory;
    }

    /**
     * Transitory effects do not alter the display state. (Example: pulsate, shake)
     * However other effects change css style properties, and in some cases
     * the application needs to be aware of these changes. (For refreshing a page for example)
     *
     * When css changes need to be sent to the application you need to set the transitory
     * property to false. The framework will then populate a hidden field with the new style
     * information. Ready to be sent on the next submit.
     *
     * However, if you need to send the new style information immediately you need to set the submit attribute
     * to true as well. This will fire a partial submit at the end of the effect.
     *
     *
     * Default is true
     *
     * @param transitory true to not populate the css style changes, false to populate css style changes
     */
    public void setTransitory(boolean transitory) {
        this.transitory = transitory;
    }

    /**
     * @return
     * @deprecated
     */
    public boolean isQueued() {
        return queued;
    }

    /**
     * @param queued
     * @deprecated
     */
    public void setQueued(boolean queued) {
        this.queued = queued;
    }

    /**
     * @return
     * @deprecated
     */
    public boolean isQueueEnd() {
        return queueEnd;
    }

    /**
     * @param queueEnd
     * @deprecated
     */
    public void setQueueEnd(boolean queueEnd) {
        this.queueEnd = queueEnd;
    }

    /**
     * Has this effect been fired. ONly effects that have not been fired are
     * sent to the browser
     *
     * @return
     */
    public boolean isFired() {
        return fired;
    }

    /**
     * Set if this effect has been fired. When this flag is set to false
     * the effect will be sent to the browser on the next render pass.
     *
     * After being fired Icefaces will set this flag to true. To resend this
     * effect set the flag to false. 
     *
     * @param fired
     */
    public void setFired(boolean fired) {
        this.fired = fired;
    }

    /**
     * Get the duration of the effect (In seconds)
     *
     * @return
     */
    public float getDuration() {
        return duration;
    }

    /**
     * Set the duration of the effect (In seconds)
     *
     * @param duration
     */
    public void setDuration(float duration) {
        this.duration = duration;
        ea.add("duration", duration);
    }

    /**
     * Get the frames per second of this effect
     *
     * @return
     */
    public float getFps() {
        return fps;
    }

    /**
     * Set the frames per second of this effect. max is 25
     *
     * @param fps
     */
    public void setFps(float fps) {
        if (fps > 25f) {
            throw new IllegalArgumentException("FPS max is 25");
        }
        this.fps = fps;
        ea.add("fps", fps);
    }

    /**
     * Gets the starting point of the transition, a float between 0.0 and 1.0.
     * Defaults to 0.0.
     *
     * @return
     */
    public float getFrom() {
        return from;
    }

    /**
     * Sets the starting point of the transition, a float between 0.0 and 1.0.
     * Defaults to 0.0.
     *
     * @param from
     */
    public void setFrom(float from) {
        this.from = from;
        ea.add("from", from);
    }

    /**
     * Gets the end point of the transition, a float between 0.0 and 1.0.
     * Defaults to 1.0.
     *
     * @return
     */
    public float getTo() {
        return to;
    }

    /**
     * Sets the end point of the transition, a float between 0.0 and 1.0.
     * Defaults to 1.0.
     *
     * @param to
     */
    public void setTo(float to) {
        this.to = to;
        ea.add("to", to);
    }

    /**
     * Gets whether the effect should render new frames automatically (which it
     * does by default). If true, you can render frames manually by calling the
     * render() instance method of an effect. This is used by
     * Effect.Parallel().
     *
     * @return
     */
    public boolean isSync() {
        return sync;
    }

    /**
     * Sets whether the effect should render new frames automatically (which it
     * does by default). If true, you can render frames manually by calling the
     * render() instance method of an effect. This is used by
     * Effect.Parallel().
     *
     * @param sync
     */
    public void setSync(boolean sync) {
        this.sync = sync;
        ea.add("sync", sync);
    }


    /**
     * Gets the delay before invoking the effect
     *
     * @return
     */
    public float getDelay() {
        return delay;
    }

    /**
     * Sets the delay before invoking the effect
     *
     * @param delay
     */
    public void setDelay(float delay) {
        this.delay = delay;
        ea.add("delay", delay);
    }

    /**
     * Gets queuing options. When used with a string, can be 'front' or 'end' to
     * queue the effect in the global effects queue at the beginning or end, or
     * a queue parameter object that can have {position:'front/end',
     * scope:'scope', limit:1}
     *
     * @return
     */
    public String getQueue() {
        return queue;
    }

    /**
     * Sets queuing options. When used with a string, can be 'front' or 'end' to
     * queue the effect in the global effects queue at the beginning or end, or
     * a queue parameter object that can have {position:'front/end',
     * scope:'scope', limit:1}
     *
     * @param queue
     */
    public void setQueue(String queue) {
        this.queue = queue;
        ea.add("queue", queue);
    }

    /**
     * Gets is this effect should fire partial submit when finished
     *
     * @return
     */
    public boolean isSubmit() {
        return submit;
    }

    /**
     * Sets is this effect should fire partial submit when finished.
     * Transitory also needs to be set to true.
     *
     * default is false
     *
     * @param submit
     */
    public void setSubmit(boolean submit) {
        this.submit = submit;
        ea.add("submit", submit);
    }


    /**
     * get the name of the Function call to invoke the effect
     *
     * @return
     */
    public abstract String getFunctionName();


    public String toString(String var, String lastCall) {
        if (queued) {
            ea.add("queue", "front");
        }
        if (queueEnd) {
            ea.add("queue", "end");
        }
        if (!transitory) {
            ea.add("uploadCSS", "true");
        }
        if (lastCall != null) {
            ea.addFunction("iceFinish", "function(){" + lastCall + "}");
        }
        return getFunctionName() + "(" + var + ea.toString();
    }

    public String toString() {
        return toString("id", null);
    }

    /**
     * Get the HTML ID of the element t
     *
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Set the HTML ID of the element t
     *
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }


    /**
     * Get the name of the sequence this effect is in
     *
     * @return
     */
    public String getSequence() {
        return sequence;
    }

    /**
     * Set the name of the sequence this effect is in.
     *
     * @param sequence
     */
    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    /**
     * Get the ID or position of this effect in a sequence
     *
     * @return
     */
    public int getSequenceId() {
        return sequenceId;
    }

    /**
     * Set the ID or position of this effect in a sequence
     *
     * @param sequenceId
     */
    public void setSequenceId(int sequenceId) {
        this.sequenceId = sequenceId;
    }

    public void setOptions(String options){       
        ea.setOptions(options);
    }

    public int hashCode() {
        return getIntfromString(toString());
    }

    public int getIntfromString(String s) {
        int r = 0;
        char[] ca = s.toCharArray();
        for (int i = 0; i < ca.length; i++) {
            r += (int) ca[i];
        }
        return r;
    }
    
    public boolean equals(Object obj) {
        if (!(obj instanceof Effect)) {
            return false;
        }
        Effect effect = (Effect) obj;
        if (!CoreUtils.objectsEqual(ea, effect.ea)) {
            return false;
        }
        if (queued != effect.queued) {
            return false;
        }
        if (queueEnd != effect.queueEnd) {
            return false;
        }
        if (fired != effect.fired) {
            return false;
        }
        if (transitory != effect.transitory) {
            return false;
        }
        if (duration != effect.duration) {
            return false;
        }
        if (fps != effect.fps) {
            return false;
        }
        if (from != effect.from) {
            return false;
        }
        if (to != effect.to) {
            return false;
        }
        if (sync != effect.sync) {
            return false;
        }
        if (delay != effect.delay) {
            return false;
        }
        if (!CoreUtils.objectsEqual(queue, effect.queue)) {
            return false;
        }
        if (submit != effect.submit) {
            return false;
        }
        if (!CoreUtils.objectsEqual(id, effect.id)) {
            return false;
        }
        if (!CoreUtils.objectsEqual(sequence, effect.sequence)) {
            return false;
        }
        if (sequenceId != effect.sequenceId) {
            return false;
        }
        return true;
    }
}
