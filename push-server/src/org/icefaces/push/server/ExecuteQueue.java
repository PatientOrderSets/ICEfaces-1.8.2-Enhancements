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
package org.icefaces.push.server;

import edu.emory.mathcs.backport.java.util.concurrent.Executors;
import edu.emory.mathcs.backport.java.util.concurrent.RejectedExecutionException;
import edu.emory.mathcs.backport.java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ExecuteQueue {
    private static final int DEFAULT_MAXIMUM_THREAD_POOL_SIZE = 30;
    private static final Log LOG = LogFactory.getLog(ExecuteQueue.class);

    private ThreadPoolExecutor executorService;

    /**
     * <p>
     *   Constructs an <code>ExecuteQueue</code> object with a default maximum
     *   thread pool size.
     * </p>
     *
     * @see        #ExecuteQueue(int)
     */
    public ExecuteQueue() {
        this(DEFAULT_MAXIMUM_THREAD_POOL_SIZE);
    }

    /**
     * <p>
     *   Constructs an <code>ExecuteQueue</code> object with the specified
     *   <code>maximumThreadPoolSize</code>.
     * </p>
     *
     * @param      maximumThreadPoolSize
     * @throws     IllegalArgumentException
     *                 if the specified <code>maximumThreadPoolSize</code> is
     *                 lesser than or equal to <code>0</code>.
     */
    public ExecuteQueue(final int maximumThreadPoolSize)
    throws IllegalArgumentException {
        if (maximumThreadPoolSize <= 0) {
            throw new IllegalArgumentException("maximumThreadPoolSize <= 0");
        }
        executorService =
            (ThreadPoolExecutor)
                Executors.newFixedThreadPool(maximumThreadPoolSize);
    }

    /**
     * <p>
     *   Executes the specified <code>runnable</code> using a thread from the
     *   containing thread pool.
     * </p>
     *
     * @param      runnable
     *                 the runnable to be executed.
     */
    public void execute(final Runnable runnable) {
        if (runnable != null) {
            try {
                executorService.execute(runnable);
            } catch (RejectedExecutionException exception) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("Execution of the runnable rejected!", exception);
                }
            }
        }
    }

    /**
     * <p>
     *   Gets the maximum thread pool size of this <code>ExecuteQueue</code>.
     * </p>
     *
     * @return     the maximum thread pool size.
     */
    public int getMaximumThreadPoolSize() {
        return executorService.getMaximumPoolSize();
    }
}
