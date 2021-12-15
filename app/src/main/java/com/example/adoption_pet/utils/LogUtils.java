package com.example.adoption_pet.utils;

import android.util.Log;

public class LogUtils {

    public static final boolean LOG_MODE = true;

    private static final String TAG = "Pet Adoption";


    private LogUtils() {

    }

    public static void methodIn() {
        outputLog(Log.DEBUG, "[S] " + methodNameString(), null);
    }

    public static void methodIn(String msg) {
        outputLog(Log.DEBUG, "[S] " + methodNameString() + " " + msg, null);
    }


    public static void methodOut() {
        outputLog(Log.DEBUG, "[E] " + methodNameString(), null);
    }


    public static void methodOut(String msg) {
        outputLog(Log.DEBUG, "[E] " + methodNameString() + " " + msg, null);
    }


    public static void d(String msg) {
        outputLog(Log.DEBUG, "[DEBUG] " + methodNameString() + " " + msg, null);
    }

    public static void d(String msg, Throwable tr) {
        outputLog(Log.DEBUG, "[DEBUG] " + methodNameString() + " " + msg, tr);
    }


    public static void i(String msg) {
        outputLog(Log.INFO, "[INFO] " + methodNameString() + " " + msg, null);
    }

    public static void w(String msg) {
        outputLog(Log.WARN, "[WARN] " + methodNameString() + " " + msg, null);
    }


    public static void e(String msg) {
        outputLog(Log.ERROR, "[ERROR] " + methodNameString() + " " + msg, null);
    }


    private static void outputLog(int type, String msg, Throwable tr) {
        if (!LOG_MODE) return;

        switch (type) {
            case Log.ERROR:
                Log.e(TAG, msg, tr);
                break;
            case Log.WARN:
                Log.w(TAG, msg);
                break;
            case Log.DEBUG:
                Log.d(TAG, msg, tr);
                break;
            case Log.INFO:
                Log.i(TAG, msg);
                break;
            case Log.VERBOSE:
                Log.v(TAG, msg);
                break;
            default:
                break;
        }
        outputFileLog(type, msg);
    }

    private static void outputFileLog(int type, String msg) {
        // DEBUG以下のtypeは出力しない。
        switch (type) {
            case Log.ASSERT:
            case Log.ERROR:
            case Log.WARN:
            case Log.DEBUG:
                // [S][E]から始まるログは省く
                if (!msg.startsWith("[S]") && !msg.startsWith("[E]")) {
                    //TODO Save log file
                }
                break;
            default:
                Log.v(TAG, "No Input Log Type");
                break;
        }
    }

    public static String methodNameString() {
        final StackTraceElement element = Thread.currentThread().getStackTrace()[4];
        final String fullClassName = element.getClassName();
        final String simpleClassName = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        final String methodName = element.getMethodName();
        final int lineNumber = element.getLineNumber();
        final String ret = simpleClassName + "#" + methodName + ":" + lineNumber;
        return ret;
    }

}
