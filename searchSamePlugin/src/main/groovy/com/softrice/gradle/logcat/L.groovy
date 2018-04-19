package com.softrice.gradle.logcat

import org.gradle.internal.impldep.org.apache.http.util.TextUtils

/**
 * Created by huangzhangshuai on 18/3/14.
 */

public class L {

    private static boolean sDebug = true;
    private static String sTag = "hzs";

    public static void init(boolean debug, String tag){
        L.sDebug = debug;
        L.sTag = tag;
    }

    public static void e(String msg, Object... params){
        e(null, msg, params);
    }

    public static void e(String tag, String msg, Object[] params){
        if (!sDebug) return;
        String finalTag = getFinalTag(tag);
        StackTraceElement targetStackTraceElement = getTargetStackTraceElement();
        PrintUtil.info(finalTag, "(" + targetStackTraceElement.getFileName() + ":"
                + targetStackTraceElement.getLineNumber() + ")");
        PrintUtil.info(finalTag, String.format(msg, params));
    }

    private static String getFinalTag(String tag){
        if (!TextUtils.isEmpty(tag)){
            return tag;
        }
        return sTag;
    }

    private static StackTraceElement getTargetStackTraceElement() {
        // find the target invoked method
        StackTraceElement targetStackTrace = null;
        boolean shouldTrace = false;
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            boolean isLogMethod = stackTraceElement.getClassName().equals(L.class.getName());
            if (shouldTrace && !isLogMethod) {
                targetStackTrace = stackTraceElement;
                break;
            }
            shouldTrace = isLogMethod;
        }
        return targetStackTrace;
    }
}
