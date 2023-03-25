package com.github.tvbox.osc.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;


import com.github.tvbox.osc.BuildConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class WLogUtil {
    private static final boolean IS_DEBUG = BuildConfig.DEBUG;
    public static String TAG = "LogUtil";
    private static final String NAME_LOG = "saveLogs";// 日志名称
    private static final int SDCARD_LOG_FILE_SAVE_DAYS = 100;// sd卡中日志文件的最多保存天数
    private static final String MYLOGFILEName = ".txt";// 本类输出的日志文件名称
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat myLogSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");// 日志的输出格式
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat logfile = new SimpleDateFormat("yyyy-MM-dd");// 日志文件格式

    @SuppressLint("StaticFieldLeak")
    private static WLogUtil mInstance;
    private Context mContext;
    private String fileDir;
    private Handler mThreadHandler;
    //用于线程间同步
    private static final Object obj = new Object();
    private boolean isThreadHanlderCompleteInit = false;
    private FileUtils2 mFileUtils;

    private WLogUtil() {
    }

    public static WLogUtil getInstance() {
        if (mInstance == null) {
            synchronized (WLogUtil.class) {
                if (mInstance == null) {
                    mInstance = new WLogUtil();
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化方法
     */
    public void init(Context context) {
        if (mContext != null) {
            return;
        }
        mContext = context.getApplicationContext();
        mFileUtils = new FileUtils2(mContext);
        LogThread thread = new LogThread();
        thread.start();
        String path = getFileDir();
        WLogUtil.d(TAG, "日志保存路径： " + path);
    }


    public static void i(String message) {
        i2(TAG, message, false);
    }

    public static void i(String tag, String message) {
        i2(tag, message, false);
    }

    public static void i(String message, boolean save) {
        i2(TAG, message, save);
    }

    public static void i(String tag, String message, boolean save) {
        String tag2 = tag + "-->:" + getTargetStackTraceElement(false);
        if (IS_DEBUG) {
            Log.i(tag2, message);
        }
        if (save) {
            getInstance().saveLog("I", tag2, message);
        }
    }

    private static void i2(String tag, String message, boolean save) {
        String tag2 = tag + "-->:" + getTargetStackTraceElement(true);
        if (IS_DEBUG) {
            Log.i(tag2, message);
        }
        if (save) {
            getInstance().saveLog("I", tag2, message);
        }
    }

    public static void e(String message) {
        e2(TAG, message, null, false);
    }

    public static void e(String tag, String message) {
        e2(tag, message, null, false);
    }

    public static void e(String message, boolean save) {
        e2(TAG, message, null, save);
    }

    public static void e(String tag, String message, boolean save) {
        e2(tag, message, null, save);
    }

    public static void e(String message, Throwable tr, boolean save) {
        e2(TAG, message, tr, save);
    }

    public static void e(String tag, String message, Throwable tr, boolean save) {
        String tag2 = tag + "-->" + getTargetStackTraceElement(false);
        if (IS_DEBUG) {
            Log.e(tag2, message, tr);
        }
        if (save) {
            getInstance().saveLog("E", tag2, message);
        }
    }

    private static void e2(String tag, String message, Throwable tr, boolean save) {
        String tag2 = tag + "-->" + getTargetStackTraceElement(true);
        if (IS_DEBUG) {
            Log.e(tag2, message, tr);
        }
        if (save) {
            getInstance().saveLog("E", tag2, message);
        }
    }

    public static void w(String message) {
        w2(TAG, message, false);
    }

    public static void w(String tag, String message) {
        w2(tag, message, false);
    }

    public static void w(String message, boolean save) {
        w2(TAG, message, save);
    }

    public static void w(String tag, String message, boolean save) {
        String tag2 = tag + "-->:" + getTargetStackTraceElement(false);
        if (IS_DEBUG) {
            Log.w(tag2, message);
        }
        if (save) {
            getInstance().saveLog("W", tag2, message);
        }
    }

    private static void w2(String tag, String message, boolean save) {
        String tag2 = tag + "-->:" + getTargetStackTraceElement(true);
        if (IS_DEBUG) {
            Log.w(tag2, message);
        }
        if (save) {
            getInstance().saveLog("W", tag2, message);
        }
    }


    public static void v(String message) {
        v2(TAG, message, false);
    }

    public static void v(String tag, String message) {
        v2(tag, message, false);
    }

    public static void v(String message, boolean save) {
        v2(TAG, message, save);
    }

    public static void v(String tag, String message, boolean save) {
        String tag2 = tag + "-->:" + getTargetStackTraceElement(false);
        if (IS_DEBUG) {
            Log.v(tag2, message);
        }
        if (save) {
            getInstance().saveLog("V", tag2, message);
        }
    }

    private static void v2(String tag, String message, boolean save) {
        String tag2 = tag + "-->:" + getTargetStackTraceElement(true);
        if (IS_DEBUG) {
            Log.v(tag2, message);
        }
        if (save) {
            getInstance().saveLog("V", tag2, message);
        }
    }

    public static void d(String message) {
        d2(TAG, message, false);
    }

    public static void d(String message, boolean save) {
        d2(TAG, message, save);
    }

    public static void d(String tag, String message) {
        d2(tag, message, false);
    }

    public static void d(String tag, String message, boolean save) {
        String tag2 = tag + "-->:" + getTargetStackTraceElement(false);
        if (IS_DEBUG) {
            Log.d(tag2, message);
        }
        if (save) {
            getInstance().saveLog("D", tag2, message);
        }
    }

    private static void d2(String tag, String message, boolean save) {
        String tag2 = tag + "-->:" + getTargetStackTraceElement(true);
        if (IS_DEBUG) {
            Log.d(tag2, message);
        }
        if (save) {
            getInstance().saveLog("D", tag2, message);
        }
    }

    private String getFileDir() {
        if (fileDir == null) {
            fileDir = mFileUtils.getSavePublicFileDir(NAME_LOG);
        }
        return fileDir;
    }

    private static String getTargetStackTraceElement(boolean isNext) {
        StackTraceElement targetStackTrace = null;
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int num = 4;
        if (isNext) {
            num = 5;
        }
        if (stackTrace.length >= num) {
            targetStackTrace = stackTrace[num];
        }
        String s = "";
        if (null != targetStackTrace) {
            s = "(" + targetStackTrace.getFileName() + ":"
                    + targetStackTrace.getLineNumber() + ")";
        }
        return s;
    }

    //写日志的子线程
    private static class LogThread extends Thread {
        @Override
        public void run() {
            Looper.prepare();
            getInstance().mThreadHandler = new Handler(Looper.myLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    if (msg.what == 0) { //保存日志
                        String text = (String) msg.obj;
                        if (text != null) {
                            getInstance().writeLogToSdcard(text);
                        }
                    } else if (msg.what == 1) { //删除过期日志
                        File file = (File) msg.obj;
                        if (file != null && file.exists()) {
                            file.delete();
                        }
                    }
                }
            };
            if (!getInstance().isThreadHanlderCompleteInit) {
                getInstance().isThreadHanlderCompleteInit = true;
                synchronized (obj) {
                    obj.notify();//唤起
                }
            }
            Looper.loop();
        }
    }

    private void saveLog(String logType, String tag, String text) {
        if (mThreadHandler != null) {
            String formatText = getFormatText(logType, tag, text);
            Message message = mThreadHandler.obtainMessage();
            message.what = 0;
            message.obj = formatText;
            mThreadHandler.sendMessage(message);
        }
    }

    private String getFormatText(String logType, String tag, String text) {
        Date nowTime = new Date();
        return myLogSdf.format(nowTime) + "    " + logType + "/" + tag + ": " + text;
    }

    /**
     * 打开日志文件并写入日志
     */
    private void writeLogToSdcard(String needWriteMessage) {// 新建或打开日志文件

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Date nowTime = new Date();
            String needWriteFile = getNeedWriteFileWithDate(nowTime);
            String path = getFileDir();
            File file = new File(path, needWriteFile + MYLOGFILEName);
            if (!file.exists()) {
                try {
                    //在指定的文件夹中创建文件
                    file.createNewFile();
                } catch (Exception e) {
                    Log.e(TAG, "创建文件失败：" + e.toString());
                }
            }

            try {
                FileWriter filerWriter = new FileWriter(file, true);// 后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
                BufferedWriter bufWriter = new BufferedWriter(filerWriter);
                bufWriter.write(needWriteMessage);
                bufWriter.newLine();
                bufWriter.close();
                filerWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private String getNeedWriteFileWithDate(Date date) {
        return NAME_LOG + "-" + logfile.format(date);
    }

    /**
     * 删除过期的日志文件
     */
    public void delExpiredFile() {
        new Thread(this::delFileWithThread).start();
    }

    /*
    * 防止文件过多，耗时过长
    * */
    private void delFileWithThread() {

        File dirFile = new File(getFileDir());
        if (dirFile.exists()) {
            File[] files = dirFile.listFiles();
            if (files != null) {
                long endInvalidTime = beforeTime();
                for (File file : files) {
                    if (file.lastModified() < endInvalidTime) {
                        if (!getInstance().isThreadHanlderCompleteInit) {
                            synchronized (obj) {
                                try {
                                    obj.wait();//唤起
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        Message message = mThreadHandler.obtainMessage();
                        message.what = 1;
                        message.obj = file;
                        mThreadHandler.sendMessage(message);
                    }
                }
            }
        }
    }


    private long beforeTime() {
        return System.currentTimeMillis() - (24 * 3600 * SDCARD_LOG_FILE_SAVE_DAYS);
    }

    /**
     * 得到现在时间前的几天日期，用来得到需要删除的日志文件名
     */
    private Date getDateBefore() {
        Date nowTime = new Date();
        Calendar now = Calendar.getInstance();
        now.setTime(nowTime);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - SDCARD_LOG_FILE_SAVE_DAYS);
        return now.getTime();
    }

}
