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

package com.icesoft.applications.faces.auctionMonitor.beans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;

/**
 * Class used to handle all readme interaction (including reading the html file,
 * on page button management, etc.) The readme.html file contains the demo notes
 * for auction monitor
 */
public class ReadmeBean {
    private static Log log = LogFactory.getLog(ReadmeBean.class);
    private String readmeText = "";
    private boolean expanded = false;
    private String buttonLabel = README;
    private static final String SUCCESS = "success";
    private static final String README = "./images/view-notes-button.gif";
    private static final String CLOSEME = "./images/hide-notes-button.gif";

    public ReadmeBean() {
        loadDefaultReadmeFile();
    }

    public ReadmeBean(String readmeText) {
        this.readmeText = readmeText;
    }

    public String getReadmeText() {
        return readmeText;
    }

    public String getButtonLabel() {
        return buttonLabel;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public String pressExpandButton() {
        expanded = !expanded;
        if (expanded) {
            this.buttonLabel = CLOSEME;
        } else {
            this.buttonLabel = README;
        }
        return SUCCESS;
    }

    /**
     * Method to load a default readme file (at readme.html) The file will be
     * read in and converted to a displayable string
     *
     * @return boolean true on successful read
     */
    private boolean loadDefaultReadmeFile() {
        try {
            Reader readmeReader =
                    new InputStreamReader(this.getClass().getClassLoader()
                            .getResourceAsStream(
                            "com/icesoft/applications/faces/auctionMonitor/readme.html"));
            StringWriter readmeWriter = new StringWriter();

            char[] buf = new char[2000];
            int len;
            try {
                while ((len = readmeReader.read(buf)) > -1) {
                    readmeWriter.write(buf, 0, len);
                }
            } catch (IOException e) {
                if (log.isErrorEnabled()) {
                    log.error(
                            "Something went wrong while parsing the readme file, likely because of " +
                            e);
                }
            }
            // clean up the stream reader
            readmeReader.close();

            this.readmeText = readmeWriter.toString();

            // clean up the writer
            readmeWriter.close();

            return true;

        } catch (Exception e) {
            if (log.isWarnEnabled()) {
                log.warn(
                        "General error while attempting to load the readme file, cause may be " +
                        e);
            }
        }

        return false;
    }
}
