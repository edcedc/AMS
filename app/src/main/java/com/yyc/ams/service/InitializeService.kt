package com.yyc.ams.service

import android.app.Activity
import android.app.Application
import android.app.IntentService
import android.content.Context
import android.content.Intent
import androidx.multidex.MultiDex
import cat.ereza.customactivityoncrash.config.CaocConfig
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.Utils
import com.yyc.ams.MainActivity
import com.kingja.loadsir.callback.SuccessCallback
import com.kingja.loadsir.core.LoadSir
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.smtt.sdk.QbSdk
import com.yyc.ams.weight.loadCallBack.EmptyCallback
import com.yyc.ams.weight.loadCallBack.ErrorCallback
import com.yyc.ams.weight.loadCallBack.LoadingCallback
import me.jessyan.autosize.AutoSize
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.onAdaptListener
import rfid.uhfapi_y2007.ApiApplication

/**
 * Created by mingjun on 16/8/25.
 */
class InitializeService : IntentService("InitializeService") {

    companion object {

        private val ACTION_INIT_WHEN_APP_CREATE = "com.anly.githubapp.service.action.INIT"

        fun start(context: Context) {
            val intent = Intent(context, InitializeService::class.java)
            intent.action = ACTION_INIT_WHEN_APP_CREATE
            context.startService(intent)
        }
    }

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            if (ACTION_INIT_WHEN_APP_CREATE == action) {
                performInit()
            }
        }
    }

    private fun performInit() {
        Utils.init(applicationContext as Application?)
        LogUtils.e("performInit start:" + System.currentTimeMillis())
        //打印LOG日记
//        LogcatHelper.getInstance(this).init(this)
//        LogcatHelper.getInstance(this).start()
        CrashReport.initCrashReport(applicationContext, "5cd0cff6d3", true)
        initAutoSizeConfig()
//        initQbSdk()
        //日记
        LogUtils.getConfig().setLogSwitch(true);
        MultiDex.install(this)
        //激活 RFID
        ApiApplication().init(this)
        //界面加载管理 初始化
        LoadSir.beginBuilder()
            .addCallback(LoadingCallback())//加载
            .addCallback(ErrorCallback())//错误
            .addCallback(EmptyCallback())//空
            .setDefaultCallback(SuccessCallback::class.java)//设置默认加载状态页
            .commit()

        //防止项目崩溃，崩溃后打开错误界面
        CaocConfig.Builder.create()
            .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //default: CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM
            .enabled(true)//是否启用CustomActivityOnCrash崩溃拦截机制 必须启用！不然集成这个库干啥？？？
            .showErrorDetails(true) //是否必须显示包含错误详细信息的按钮 default: true
            .showRestartButton(false) //是否必须显示“重新启动应用程序”按钮或“关闭应用程序”按钮default: true
            .logErrorOnRestart(false) //是否必须重新堆栈堆栈跟踪 default: true
            .trackActivities(true) //是否必须跟踪用户访问的活动及其生命周期调用 default: false
            .minTimeBetweenCrashesMs(2000) //应用程序崩溃之间必须经过的时间 default: 3000
            .restartActivity(MainActivity::class.java) // 重启的activity
//            .errorActivity(ErrorActivity::class.java) //发生错误跳转的activity
            .apply()

        LogUtils.e("performInit end:" + System.currentTimeMillis())
    }

    private fun initQbSdk() {
        val cb = object : QbSdk.PreInitCallback {

            override fun onViewInitFinished(arg0: Boolean) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                LogUtils.e("app", " onViewInitFinished is $arg0")
            }

            override fun onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        }
        //x5内核初始化接口
        QbSdk.initX5Environment(applicationContext, cb)
    }

    private fun initShare() {
        //        UMConfigure.init(this, Constants.ShareID, "Umeng", UMConfigure.DEVICE_TYPE_PHONE,null);
        //        PlatformConfig.setWeixin(Constants.WX_APPID, Constants.WX_SECRER);
        //        PlatformConfig.setQQZone(Constants.QQ_APPID, Constants.QQ_SECRET);
        //        PlatformConfig.setSinaWeibo(Constants.WB_APPID, Constants.WB_SECRET, "https://api.weibo.com/oauth5/default.html");
        //        //设置LOG开关，默认为false
        //        UMConfigure.setLogEnabled(true);
        //        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
    }


    private fun initAutoSizeConfig() {
        /**
         * 以下是 AndroidAutoSize 可以自定义的参数, [AutoSizeConfig] 的每个方法的注释都写的很详细
         * 使用前请一定记得跳进源码，查看方法的注释, 下面的注释只是简单描述!!!
         */
        AutoSizeConfig.getInstance()

            //是否让框架支持自定义 Fragment 的适配参数, 由于这个需求是比较少见的, 所以须要使用者手动开启
            //如果没有这个需求建议不开启
            .setCustomFragment(true).onAdaptListener = object : onAdaptListener {
            override fun onAdaptBefore(target: Any, activity: Activity) {
                //使用以下代码, 可支持 Android 的分屏或缩放模式, 但前提是在分屏或缩放模式下当用户改变您 App 的窗口大小时
                //系统会重绘当前的页面, 经测试在某些机型, 某些情况下系统不会重绘当前页面, ScreenUtils.getScreenSize(activity) 的参数一定要不要传 Application!!!
                //                        AutoSizeConfig.getInstance().setScreenWidth(ScreenUtils.getScreenSize(activity)[0]);
                //                        AutoSizeConfig.getInstance().setScreenHeight(ScreenUtils.getScreenSize(activity)[1]);
//                LogUtils.e(String.format(Locale.ENGLISH, "%s onAdaptBefore!", target.javaClass.name))

            }

            override fun onAdaptAfter(target: Any, activity: Activity) {
//                LogUtils.e(String.format(Locale.ENGLISH,"%s onAdaptAfter!",target.javaClass.name))
            }
        }
        //是否打印 AutoSize 的内部日志, 默认为 true, 如果您不想 AutoSize 打印日志, 则请设置为 false
        //                .setLog(false)
        //是否使用设备的实际尺寸做适配, 默认为 false, 如果设置为 false, 在以屏幕高度为基准进行适配时
        //AutoSize 会将屏幕总高度减去状态栏高度来做适配
        //设置为 true 则使用设备的实际屏幕高度, 不会减去状态栏高度
        //                .setUseDeviceSize(true)
        //是否全局按照宽度进行等比例适配, 默认为 true, 如果设置为 false, AutoSize 会全局按照高度进行适配
        //                .setBaseOnWidth(false)
        //设置屏幕适配逻辑策略类, 一般不用设置, 使用框架默认的就好
        //                .setAutoAdaptStrategy(new AutoAdaptStrategy())
        //当 App 中出现多进程, 并且您需要适配所有的进程, 就需要在 App 初始化时调用 initCompatMultiProcess()
        //在 Demo 中跳转的三方库中的 DefaultErrorActivity 就是在另外一个进程中, 所以要想适配这个 Activity 就需要调用 initCompatMultiProcess()
        AutoSize.initCompatMultiProcess(this)
    }

}

