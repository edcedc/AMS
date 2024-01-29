package com.yyc.ams

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import com.yyc.ams.base.BaseActivity
import com.yyc.ams.bean.RfidStateBean
import com.yyc.ams.databinding.AMainBinding
import com.yyc.ams.ext.RFID_TAG
import com.yyc.ams.ext.SCAN_STATUS_SCAN
import com.yyc.ams.ext.showToast
import com.yyc.ams.keyctrl.Common
import com.yyc.ams.keyctrl.IKeyRecv
import com.yyc.ams.keyctrl.KeyRecerver
import com.yyc.ams.keyctrl.ScanType
import com.yyc.ams.keyctrl.ThreadPoolManager
import com.yyc.ams.keyctrl.entity.TagFindParam
import com.yyc.ams.util.CacheUtil
import com.yyc.ams.viewmodel.AssetModel
import com.yyc.ams.viewmodel.AssetSearchmModel
import com.yyc.ams.viewmodel.RfidModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rfid.uhfapi_y2007.core.Util
import rfid.uhfapi_y2007.entities.MemoryBank
import rfid.uhfapi_y2007.entities.TagParameter
import rfid.uhfapi_y2007.protocol.vrp.MsgPowerOff
import rfid.uhfapi_y2007.protocol.vrp.MsgTagInventory
import java.util.Date
import java.util.Locale

class MainActivity : BaseActivity<RfidModel, AMainBinding>(), IKeyRecv {

    var shortPress = false

    private val assetModel: AssetModel by viewModels()

    private val assetSearchmModel: AssetSearchmModel by viewModels()

    override fun initView(savedInstanceState: Bundle?) {
        BarUtils.setStatusBarLightMode(this, true)
        BarUtils.setStatusBarColor(this, getColor(R.color.w_f4f9fd))

        KeyRecerver.setKeyRecvCallback(this)
        //注册广播接收
        registerReceiver()

        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onPause(owner: LifecycleOwner) {
                super.onPause(owner)
                mViewModel.isOpen.value = false
            }

            override fun onStop(owner: LifecycleOwner) {
                super.onStop(owner)
                if (!AppUtils.isAppForeground()) {
                    //app 进入后台
                    mViewModel.isActive.set(false) //记录当前已经进入后台
                    mViewModel.isOpen.value = false
                }
            }

            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                unregisterReceiver(keyReceiver)
            }
        })
    }

    override fun createObserver() {
        super.createObserver()
        // rfid数据接收回调
        mViewModel.rfidData.observe(this, {
            mViewModel.mTotalCount += 1
            var tagId = mViewModel.rfidData.value!!.TagId
            if (tagId.contains(" ")) {
                tagId = tagId.replace(" ", "")
            }
            LogUtils.e(tagId, it.Rssi)
            //搜索/识别回调
            if (mViewModel.tagFindParam == null) {
                assetModel.epcData.value = RfidStateBean(
                    tagId = tagId,
                    scanStatus = SCAN_STATUS_SCAN,
                    rssi = it.Rssi
                )
            } else {
                assetSearchmModel.epcData.value = RfidStateBean(
                    tagId = tagId,
                    rssi = it.Rssi
                )
            }
        })


        //是否启动rdif
        mViewModel.isOpen.observe(this@MainActivity, { resultState ->
            if (resultState && mViewModel.initConn.get()) {
                mViewModel.onClean()
                mViewModel.startStat()
                if (Common.reader != null && Common.reader.isConnected) {
                    Common.reader.Send(MsgPowerOff()) // 先停止

                    //EPC
                    Common.selectEpcParam = TagParameter()
                    Common.scanType = ScanType.ScanEpc
                    Common.reader.Send(MsgTagInventory())
                    Common.selectEpcParam.MemoryBank = MemoryBank.EPCMemory

                    //TID
//                    Common.selectParam = TagParameter()
//                    Common.scanType = ScanType.ScanTid
//                    Common.reader.Send(MsgTagRead())
//                    Common.selectParam.MemoryBank = MemoryBank.TIDMemory
                    // 蜂鸣线程
                    mViewModel.isScan.set(true)
                    if (Common.isEnableBeep) {
                        ThreadPoolManager.getInstance().execute(mViewModel.threadBeep)
                        LogUtils.e("// 蜂鸣线程")
                    }
                } else {
                    showToast("Connection failed")
                }
            } else {
                if (mViewModel.isScan.get()){
                    mViewModel.isScan.set(false)
                    mViewModel.onClean()
                    mViewModel.disconn()
//                mViewModel.initStop()
                    mViewModel.stopStat()
                    LogUtils.e("读卡断开")
                }
            }
        })

        //指定搜索RFID
        mViewModel.isSearchOpen.observe(this, {
            if (StringUtils.isEmpty(it)) {
                mViewModel.tagFindParam = null
                mViewModel.isOpen.value = false
            } else {
                mViewModel.tagFindParam = TagFindParam()
                mViewModel.tagFindParam?.ScanMB = RFID_TAG
                mViewModel.tagFindParam?.Data = Util.ConvertHexStringToByteArray(it)
                mViewModel.tagFindParam?.Mask = Util.ConvertHexStringToByteArray(it)
                mViewModel.isOpen.value = true
            }
        })

        mViewModel.language.observe(this, {languageSelected->
            if (languageSelected < 0)return@observe
            when (languageSelected) {
                0 -> {
                    changeLanguage(this, Locale.SIMPLIFIED_CHINESE)
                }

                1 -> {
                    changeLanguage(this, Locale.TRADITIONAL_CHINESE)
                }

                2 -> {
                    changeLanguage(this, Locale.ENGLISH)
                }
            }
            CacheUtil.setLanguage(languageSelected)
        })
    }

    //region 按键广播
    private var keyReceiver: KeyReceiver? = null
    fun registerReceiver() {
        keyReceiver = KeyReceiver()
        val filter = IntentFilter()
        filter.addAction("android.rfid.FUN_KEY")
        filter.addAction("android.intent.action.FUN_KEY")
        registerReceiver(keyReceiver, filter)
    }

    private inner class KeyReceiver : BroadcastReceiver() {

        var clickTime: Date? = null

        override fun onReceive(context: Context, intent: Intent) {
            var keyCode = intent.getIntExtra("keyCode", 0)
            if (keyCode == 0) {
                keyCode = intent.getIntExtra("keycode", 0)
            }
            if (keyCode != KeyEvent.KEYCODE_F4) return
            val keyDown = intent.getBooleanExtra("keydown", true)
            if (keyDown) {
                if (clickTime != null) {
                    val t = Date().time - clickTime!!.time
                    if (t < 1000) return
                }
                clickTime = Date()
            } else {
                OnKeyUp(keyCode)
            }
            LogUtils.e("按键" + keyCode)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_F5
            || keyCode == KeyEvent.KEYCODE_F7
        ) {
            if (event!!.action == KeyEvent.ACTION_DOWN) {
                event!!.startTracking() //只有执行了这行代码才会调用onKeyLongPress的；
                if (event!!.repeatCount == 0) {
                    shortPress = true
                }
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun OnKeyDown(keycode: Int) {
        LogUtils.e(keycode)
    }

    override fun OnKeyUp(keycode: Int) {
        LogUtils.e(keycode)
    }

}