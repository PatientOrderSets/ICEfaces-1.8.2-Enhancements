package com.icesoft.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Method;

/**
 * A class for keeping track of the time that a job takes to complete.
 * The job may be a single execution task, or it may be made up of subjobs that
 * can be executed on separate threads, making direct observation of CPU time difficult.
 * <p>
 * An example of this is server push where a single
 * rendering job is made up of potentially several RunnableRenders executing in
 * a ThreadedExecutor with a core number of threads. This utility helps measure
 * the elapsed real time from the start of the Render Group submission for rendering
 * to the completion of the render tasks on all threads. It also can accumulate
 * the total CPU time for the rendering sub jobs. 
 *
 * As an example, with multiple renderers in a server push operation: <ol>
 * <li> call newJob(renderCount);</li>
 * <li> call startJobTmer() before submitting the renderables as a batch to the executor</li>
 * <li> call startSubjobTimer() before each RunnableRender runs </li>
 * <li> call subJobTimerComplete() when RunnableRender returns </li>
 * <li> enjoy the output when all are done! </li>
 * </ol>
 *
 * One caveat. This particular implementation is not capable of sorting multiple
 * jobs. If a GroupRender is started (for example) with 100 Renderables, and only some
 * have been completed before the next GroupRender is started, this class will log
 * a message and start monitoring the new Job. 
 *
 */
public class StaticTimerUtility {

    private static int timerIndex;
    public static Log Log = LogFactory.getLog(StaticTimerUtility.class);
    private static int jobId;

    // Overall subJob count
    private static int totalTimerCount;

    // overall startJobTmer time.
    private static long startTime;

    // keeping track of the time for each sub job and overall job time.
    private static long timerStartTime;
    private static long timerAccumulatedTime;

    // number that are done
    private static int timersCompleted;
    private static Method timerMethod;

    private static boolean hiRes;

    static {
        try {
            timerMethod = System.class.getMethod("currentTimeMillis", null);
            timerMethod = System.class.getMethod("nanoTime", null);
            hiRes=true;
        } catch (NoSuchMethodException nsm) {}
    } 
    /**
     * Start a new Timer job. This defines the conceptual start (no timers are started)
     * and allows configuration of the number of subjobs making up the job
     * 
     * @param subJobCount number of subjobs that make up the main job
     */
    public static void newJob(int subJobCount) {
        if (!Log.isTraceEnabled()) {
            return;
        }
        
        if (subJobCount < 0) {
            throw new IllegalArgumentException("Timer count can't be < 0");
        }

        if (subJobCount == 0) {
            return;
        }
        // If the job id hasn't been cleared before the next job is started, it usually
        // means that a renderer is saturated. New jobs are being created before the old
        // jobs are done. It's not a functional problem, but it's very good to know
        if (jobId != 0) {
            Log.trace(" ==> Terminating job: " + jobId + " early, " +
                               timersCompleted + " sub-jobs of " +
                               totalTimerCount + " completed" );
        }
        Log.trace("============= New Timer Job with: " + subJobCount + " subjobs ===============");

        jobId = ++timerIndex;
        totalTimerCount = subJobCount;
    }

    /**
     * Call this before the startJobTmer of any single timeable job. Realtime measurement
     * of overall time to completion starts from this point
     */
    public static void startJobTmer() {

        try {
            startTime = ((Long)timerMethod.invoke(null, null)).longValue();
        } catch (Exception e) {
        } 

        timerAccumulatedTime = 0;
    }


    /**
     * Call this when a single timeable event is about to startJobTmer
     */
    public static void startSubjobTimer() {
        try {
            timerStartTime = ((Long)timerMethod.invoke(null, null)).longValue();
        } catch (Exception e) {
        }
    }


    /**
     * Call this method as soon as possible after a subjob is complete.
     * When the number of calls reaches the sub job count, the overall job
     * statistics are printed. 
     */
    public static void subJobTimerComplete() {
        if (!Log.isTraceEnabled()) {
            return;
        }

        timerAccumulatedTime += (getSystemTime() - timerStartTime);

        // count can be greater than total if new users join during rendering
        if (++timersCompleted >= totalTimerCount) {

            float factor = (hiRes) ? 1e9f : 1000f;
            Log.trace(" ==> Timer job: " + jobId + " containing: " + totalTimerCount + " subjobs" + 
                               ", elapsed real time: " + (getSystemTime() - startTime) / factor + " seconds");
            Log.trace("   ==> Timer job: " + jobId + " accumulated cpu time: " + timerAccumulatedTime / factor + " seconds");
            Log.trace("============================");

            reset();
        }
    }

    private static void reset() {
        jobId = 0;
        timersCompleted = 0;
        totalTimerCount = 0;
        timerAccumulatedTime = 0;
        startTime = 0;
    }

    private static long getSystemTime() {

         try {
            return ((Long)timerMethod.invoke(null, null)).longValue();
        } catch (Exception e) {
             return System.currentTimeMillis();
         }
    }
}
