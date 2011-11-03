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
 */
package com.icesoft.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ThreadFactory
implements edu.emory.mathcs.backport.java.util.concurrent.ThreadFactory {
    private static final Log LOG = LogFactory.getLog(ThreadFactory.class);

    private final Object lock = new Object();

    private int counter;
    private boolean daemon = true;
    private String prefix = "Thread";

    public ThreadFactory() {
        // do nothing.
    }

    /**
     * <p>
     *   Gets the prefix of the name of threads to be created through this
     *   <code>ThreadFactory</code>.
     * </p>
     *
     * @return     the prefix.
     * @see        #setPrefix(String)
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * <p>
     *   Checks if threads to be created through this <code>ThreadFactory</code>
     *   are daemon threads.
     * </p>
     *
     * @return     <code>true</code> if threads to be created are daemon
     *             threads, <code>false</code> if not.
     * @see        #setDaemon(boolean)
     */
    public boolean isDaemon() {
        return daemon;
    }

    public Thread newThread(final Runnable runnable) {
        Thread _thread;
        synchronized (lock) {
            _thread = new Thread(runnable, prefix + " [" + ++counter + "]");
        }
        _thread.setDaemon(daemon);
        try {
            /*
             * We attempt to set the context class loader because some J2EE
             * containers don't seem to set this properly, which leads to
             * important classes not being found. However, other J2EE containers
             * see this as a security violation.
             */
            _thread.setContextClassLoader(runnable.getClass().getClassLoader());
        } catch (SecurityException exception) {
            /*
             * If the current security policy does not allow this, we have to
             * hope that the appropriate class loader settings were transferred
             * to this new thread.
             */
            if (LOG.isTraceEnabled()) {
                LOG.trace(
                    "Setting the context class loader is not permitted.",
                    exception);
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("New thread: " + _thread.getName());
        }
        return _thread;
    }

    /**
     * <p>
     *   Sets if threads to be created through this <code>ThreadFactory</code>
     *   are daemon threads according to the specified <code>daemon</code>
     *   value.
     * </p>
     *
     * @param      daemon
     *                 the new daemon value.
     * @see        #isDaemon()
     */
    public void setDaemon(final boolean daemon) {
        this.daemon = daemon;
    }

    /**
     * <p>
     *   Sets the prefix of the name of threads to be created through this
     *   <code>ThreadFactory</code> to the specified <code>prefix</code>.
     * </p>
     *
     * @param      prefix
     *                 the new prefix.
     * @see        #getPrefix()
     */
    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }
}
