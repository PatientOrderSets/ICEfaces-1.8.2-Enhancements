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
package org.icefaces.application.showcase.view.bean.examples.component.progressBar;

/**
 * <p>The OutputProgressModel class stores properties that are used to dynamically
 * change the state of the respective inputProgressBar component</p>
 *
 * @see org.icefaces.application.showcase.view.bean.examples.component.progressBar.OutputProgressController
 * @since 1.7
 */
public class OutputProgressModel {

    // true indicates intermdiate mode, false indicates standard output progress
    // mode.
    private boolean intermediatMode;

    // Default value for progress bar label position. can be one of many different
    // types, see the TLD for outputProgress for more information.
    private String labelPosition = "embed";

    // Custom progress bar progress label and disable flag
    private String progressMessage;
    private boolean progressMessageEnabled;

    // Custom progress bar completed label and disable flag
    private String completedMessage;
    private boolean completedMessageEnabled;

    // flat indicating process is running, disables start button to avoid
    // queuing of actions.
    private boolean pogressStarted;

    // percent completed so far, value used directly by outputProgress
    // component.
    private int percentComplete;

    public boolean isIntermediatMode() {
        return intermediatMode;
    }

    public void setIntermediatMode(boolean intermediatMode) {
        this.intermediatMode = intermediatMode;
    }

    public String getLabelPosition() {
        return labelPosition;
    }

    public void setLabelPosition(String labelPosition) {
        this.labelPosition = labelPosition;
    }

    /**
     * Gets the Progress message. If the progressMessageEnabled attribute is
     * set to false an empty String is returned.  This ensure that the component
     * will use its default percent value as the progress message.
     *
     * @return current value of progress bar if progressMessageEnabled is true,
     *         otherwise an empty string is returned.
     */
    public String getProgressMessage() {
        if (progressMessageEnabled) {
            return progressMessage;
        } else {
            return "";
        }
    }

    public void setProgressMessage(String progressMessage) {
        this.progressMessage = progressMessage;
    }

    /**
     * Gets the Progress completed message. If the completedMessageEnabled attribute is
     * set to false an empty String is returned.  This ensure that the component
     * will use its default completed message instead of the last entered
     * custom message.
     *
     * @return current value of progress bar if progressMessageEnabled is true,
     *         otherwise an empty string is returned.
     */
    public String getCompletedMessage() {
        if (completedMessageEnabled) {
            return completedMessage;
        } else {
            return "";
        }
    }

    public void setCompletedMessage(String completedMessage) {
        this.completedMessage = completedMessage;
    }

    public boolean isPogressStarted() {
        return pogressStarted;
    }

    public void setPogressStarted(boolean pogressStarted) {
        this.pogressStarted = pogressStarted;
    }

    public int getPercentComplete() {
        return percentComplete;
    }

    public void setPercentComplete(int percentComplete) {
        this.percentComplete = percentComplete;
    }

    public boolean isProgressMessageEnabled() {
        return progressMessageEnabled;
    }

    public void setProgressMessageEnabled(boolean progressMessageEnabled) {
        this.progressMessageEnabled = progressMessageEnabled;
    }

    public boolean isCompletedMessageEnabled() {
        return completedMessageEnabled;
    }

    public void setCompletedMessageEnabled(boolean completedMessageEnabled) {
        this.completedMessageEnabled = completedMessageEnabled;
    }
}
