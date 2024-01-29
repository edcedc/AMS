package com.yyc.ams.viewmodel

import android.os.Handler
import androidx.annotation.Keep
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ThreadUtils.runOnUiThread
import com.yyc.ams.bean.AppRoomDataBase
import com.yyc.ams.keyctrl.Common
import com.yyc.ams.keyctrl.MyPlaySound
import com.yyc.ams.keyctrl.entity.InventoryInfo
import com.yyc.ams.keyctrl.entity.TagFindParam
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.databind.BooleanObservableField
import rfid.uhfapi_y2007.Rs232Port
import rfid.uhfapi_y2007.core.Util
import rfid.uhfapi_y2007.entities.ConnectResponse
import rfid.uhfapi_y2007.entities.Flag
import rfid.uhfapi_y2007.entities.FrequencyArea
import rfid.uhfapi_y2007.entities.RxdTagData
import rfid.uhfapi_y2007.entities.Session
import rfid.uhfapi_y2007.entities.SessionInfo
import rfid.uhfapi_y2007.protocol.vrp.Msg6CTagFieldConfig
import rfid.uhfapi_y2007.protocol.vrp.MsgFilteringTimeConfig
import rfid.uhfapi_y2007.protocol.vrp.MsgPowerConfig
import rfid.uhfapi_y2007.protocol.vrp.MsgPowerOff
import rfid.uhfapi_y2007.protocol.vrp.MsgQValueConfig
import rfid.uhfapi_y2007.protocol.vrp.MsgSessionConfig
import rfid.uhfapi_y2007.protocol.vrp.MsgUhfBandConfig
import rfid.uhfapi_y2007.protocol.vrp.Reader
import rfid.uhfapi_y2007.utils.Event
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Timer
import java.util.TimerTask

/**
 * @Author nike
 * @Date 2023/7/24 15:06
 * @Description
 */
class RfidModel : BaseViewModel() {

    var rfidData: MutableLiveData<InventoryInfo> = MutableLiveData()

    var isOpen: MutableLiveData<Boolean> = MutableLiveData()

    var isSearchOpen: MutableLiveData<String> = MutableLiveData()

    var language: MutableLiveData<Int> = MutableLiveData()

    val configDao = AppRoomDataBase.get().getConfigDao()

    var mTotalCount = 0
    private var mStartTime: Date? = null
    private val r = Runnable {
        val nowTime = Date()
        val diff = nowTime.time - mStartTime!!.time
        if (diff > 0) {
            val h = diff / 3600000
            val m = diff % 3600000 / 60000
            val s = diff % 60000 / 1000
            LogUtils.e(
                "总次数：" + Common.padLeft(' ', 5, "" + mTotalCount),
                "扫描时间：" + (if (h < 10) "0$h" else h) + ":" + (if (m < 10) "0$m" else m) + ":" + if (s < 10) "0$s" else s
            )
        }
        //mStatHandler.postDelayed(r, 1000);//一秒以后，再次回调此方法
    }

    private val ps: MyPlaySound = MyPlaySound(220)
    private var isBeep = BooleanObservableField(false)
    var isScan = BooleanObservableField(false)
    var threadBeep = MyThreadBeep()

    inner class MyThreadBeep : Thread() {
        override fun run() {
            while (isScan.get()) {
                if (isBeep.get()) {
                    isBeep.set(false)
                    ps.play()
                    mySleep(20)
                    ps.stop()
                    mySleep(5)
                } else {
                    mySleep(20)
                }
            }
        }
    }

    private fun mySleep(ms: Int) {
        try {
            Thread.sleep(ms.toLong())
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    //全局变量,判断前后台
    var isActive = BooleanObservableField(false)

    fun initStop() {
        if (Common.reader != null && Common.reader.isConnected) {
            if (Common.reader.Send(MsgPowerOff())) {
                LogUtils.e("停止读卡")
            }
        }

    }

    private var mTimer: Timer? = null
    fun startStat() {
        if (mTimer != null) {
            stopStat()
            mTimer = null
        }
        mTimer = Timer()
        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                runOnUiThread { mStatHandler.post(r) }
            }
        }
        mTimer!!.schedule(timerTask, 1000, 1000)
        mStartTime = Date()
        mTotalCount = 0
    }

    private var mStatHandler = Handler()
    fun stopStat() {
        if (mTimer == null) return
        mTimer!!.cancel()
        mTimer = null
        mStatHandler.post(r)
        //mStatHandler.removeCallbacks(r);//移除回调
    }

    //endregion
    //region RFID模块控制
    private var msgNotify = Event(this, "reader_OnInventoryReceived")
    private val connBrokenNotify = Event(this, "reader_OnBrokenNetwork")
    var tagFindParam: TagFindParam? = null
    private var timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS") // 日期的输出格式

    private var isSuc = BooleanObservableField(false)
    private fun conn(): Boolean {
        Common.setReaderConnPort("Reader1", Rs232Port("COM13,115200"))
        //        Common.setReaderConnPort("Reader1", new TcpClientPort("192.168.95.166:9090"));
        Common.reader = Reader("Reader1")
        val response: ConnectResponse = Common.reader.Connect()
        if (response.IsSucessed) {
            Common.reader.Send(MsgPowerOff()) //关功放
            //queryScanMode();//查询读取模式
            Reader.OnBrokenNetwork.addEvent(connBrokenNotify)
            Common.reader.OnInventoryReceived.addEvent(msgNotify)
            isSuc.set(true)
        } else {
            LogUtils.e("连接失败" + response.ErrorInfo.errMsg)
            isSuc.set(false)
        }
        return isSuc.get()
    }

    /**
     * 配置
     */
    fun config() {
        //工作频段CN0 FCC1 CU2
        val bMsg = MsgUhfBandConfig(FrequencyArea.valueOf(0))
        if (!Common.reader.Send(bMsg, 3000)) {
            LogUtils.e("工作频段设置失败")
        }
        // 设置重复标签过滤时间  数值 *100ms
        var fMsg = MsgFilteringTimeConfig(10)
        if (!Common.reader.Send(fMsg, 3000)) {
            LogUtils.e("重复标签过滤时间设置失败")
        }
        // 设置读6C标签数据包含字段   true false
        val msg = Msg6CTagFieldConfig(false, true)
        if (!Common.reader.Send(msg, 3000)) {
            LogUtils.e("天线和RSSI设置失败")
        }




        /*if (tagFindParam == null){
            //多标签
            si.Session = Session.S1
            si.Flag = Flag.Flag_A
            q = 4
        }else{
            //单标签
            si.Session = Session.S0
            si.Flag = Flag.Flag_A_B
            q = 0
        }*/

        viewModelScope.launch(Dispatchers.IO) {
            var configBean = configDao.findFirst()
            withContext(Dispatchers.Main) {
                //频率0-32
                val pMsg = MsgPowerConfig(byteArrayOf(configBean.power.toByte()))
                if (!Common.reader.Send(pMsg, 3000)) {
                    LogUtils.e("天线功率设置失败")
                }

                //设置Q值 0-15
                var q: Byte

                val si = SessionInfo()
                if (configBean.session == 1){
                    si.Session = Session.S1
                    si.Flag = Flag.Flag_A
                    q = 4
                }else{
                    si.Session = Session.S0
                    si.Flag = Flag.Flag_A_B
                    q = 0
                }
                val msgS = MsgSessionConfig(si)
                if (!Common.reader.Send(msgS)) {
                    LogUtils.e("Session设置失败")
                }
                val msgQ = MsgQValueConfig(q)
                if (!Common.reader.Send(msgQ, 500)) {
                    LogUtils.e("Q值设置失败")
                }
            }
        }



        //设置声音
        Common.isEnableBeep = false
        //显示
        Common.tagEncoding = "HEX"
    }

    @Keep
    private fun reader_OnInventoryReceived(sender: Reader, tagData: RxdTagData?) {
        val epc =
            if (Common.tagEncoding.equals("HEX")) Util.ConvertByteArrayToHexWordString(tagData!!.epc) else Common.bytesToAscii(
                tagData!!.epc
            )
        val tid = Util.ConvertByteArrayToHexWordString(tagData!!.tid)
        val rssi = if (tagData!!.rssi.toInt() == 0) "" else "" + Common.getRSSI(
            tagData.rssi.toInt()
        )

        if (tagFindParam != null) {
            var isM = false
            isM =
                if (tagFindParam!!.ScanMB.equals("EPC")) tagFindParam!!.isMatching(tagData.epc) else tagFindParam!!.isMatching(
                    tagData.tid
                )
            if (!isM) return
        }

        val tag = InventoryInfo()
        tag.TagId = if (epc == null || epc === "") tid else epc
        tag.MB = if (epc == null || epc === "") "TID" else "EPC"
        tag.Rssi = rssi
        tag.ReadTime = timeFormat.format(Date())
        rfidData.postValue(tag)
        isBeep.set(true)
    }

    /**
     *  连接
     */
    var initConn = object : ObservableBoolean(){
        override fun get(): Boolean {
            disconn()
            return if(conn()){
                config()
                LogUtils.e(
                    "RFID模块连接成功",
                    Common.reader.getHardwareVersion(),
                    Common.reader.getSoftwareVersion()
                )
                true
            }else{
                LogUtils.e("RFID模块连接失败")
                false
            }
        }
    }

    fun disconn() {
        if (Common.reader != null) {
            Common.reader.Send(MsgPowerOff())
            Reader.OnBrokenNetwork.removeEvent(connBrokenNotify)
            Common.reader.OnInventoryReceived.removeEvent(msgNotify)
            Common.reader.Disconnect()
            Common.reader = null
        }
    }

    fun onClean() {
        Common.selectParam = null
        Common.selectEpcParam = null
    }
}