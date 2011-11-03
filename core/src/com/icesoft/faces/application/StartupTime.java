package com.icesoft.faces.application;

public class StartupTime {

    private static long started = System.currentTimeMillis();;
    private static String inc = "/" + started + "/";

    public static long getStartupTime() {
        return started;
    }

    public static String getStartupInc() {
        return inc;
    }

    public static String removeStartupTimeFromPath(String path) {
        return getPath(path, inc);
    }

    private static String getPath(String path, String inc) {
        int start = path.indexOf(inc);
        if (start == -1) return path;
        int end = start + inc.length() - 1;
        if (start > 0) {
            String stringStart = path.substring(0, start);
            String stringEnd = path.substring(end);
            path = stringStart + stringEnd;
        } else {
            path = path.substring(end);
        }
        return path;
    }
}
