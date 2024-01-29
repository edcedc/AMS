package com.yyc.ams.util;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author nike
 * @Date 2023/6/14 13:20
 * @Description
 */
public class LogcatHelper {
    private static LogcatHelper INSTANCE = null;
    private String PATH_LOGCAT;
    private LogDumper mLogDumper = null;
    private int mPId;

    /**
     * 初始化目录
     */
    public void init(Context context) {
        //存储路径
        PATH_LOGCAT = context.getExternalCacheDir() + File.separator + "log" + File.separator;
        File file = new File(PATH_LOGCAT);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 单例的生成方法
     * @param context
     * @return
     */
    public static LogcatHelper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new LogcatHelper(context);
        }
        return INSTANCE;
    }
    private LogcatHelper(Context context) {
        init(context);
        mPId = android.os.Process.myPid();
    }

    public void start() {
        if (mLogDumper == null){
            mLogDumper = new LogDumper(String.valueOf(mPId), PATH_LOGCAT);
        }
        mLogDumper.start();
    }

    public void stop() {
        if (mLogDumper != null) {
            mLogDumper.stopLogs();
            mLogDumper = null;
        }
    }

    private class LogDumper extends Thread {
        private Process logcatProc;
        private BufferedReader mReader = null;
        private boolean mRunning = true;
        private String mCommand = null;
        private String mPID;
        private FileOutputStream out = null;
        public LogDumper(String pid, String dir) {
            mPID = pid;
            try {
                File dirFile = new File(dir);
                while(!dirFile.exists()){
                    dirFile.mkdirs();
                }
                File logFile = new File(dirFile.getAbsolutePath()+File.separator+getFileName()+".txt");
                while(!logFile.exists()){
                    logFile.createNewFile();
                }
                out = new FileOutputStream(logFile);
                Log.e("============", "LogDumper: "+logFile.getAbsolutePath());
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block

                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            /**
             *
             * 日志等级：*:v , *:d , *:w , *:e , *:f , *:s
             *
             * 显示当前mPID程序的 E和W等级的日志.
             *|grep 后面的是要过滤的格式 前面的代表日志的等级
             * */
            mCommand = "logcat *:e *:w | grep \"(" + mPID + ")\"";
//            mCommand = "logcat  | grep \"(" + mPID + ")\"";//打印所有日志信息
//            mCommand = "logcat -s way";//打印标签过滤信息
//            mCommand = "logcat *:e *:i | grep \"(" + mPID + ")\"";

        }
        public void stopLogs() {
            mRunning = false;
        }
        @Override
        public void run() {
            try {
                //在一个单独的进程中执行指定的字符串命令。
                logcatProc = Runtime.getRuntime().exec(mCommand);
                //返回连接到子进程的正常输出的输入流。该流从该进程对象所代表的进程的标准输出中获取管道数据。
                mReader = new BufferedReader(new InputStreamReader(
                        logcatProc.getInputStream()), 1024);
                String line = null;
                //读取并输出
                while (mRunning && (line = mReader.readLine()) != null) {
                    if (!mRunning) {
                        break;
                    }
                    if (line.length() == 0) {
                        continue;
                    }
                    if (out != null && line.contains(mPID)) {
                        out.write((line + "\n").getBytes());
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (logcatProc != null) {
                    logcatProc.destroy();
                    logcatProc = null;
                }
                if (mReader != null) {
                    try {
                        mReader.close();
                        mReader = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    out = null;
                }
            }
        }
    }
    /**
     * 确定文件名的函数，通过时间来产生不同的名字
     * @return
     */
    public String getFileName() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = format.format(new Date(System.currentTimeMillis()));
        return date;
    }
}
