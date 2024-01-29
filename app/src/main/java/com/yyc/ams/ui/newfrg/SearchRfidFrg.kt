package com.yyc.ams.ui.newfrg

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.ThreadUtils
import com.yyc.ams.R
import com.yyc.ams.base.BaseFragment
import com.yyc.ams.bean.AppRoomDataBase
import com.yyc.ams.bean.db.ConfigBean
import com.yyc.ams.databinding.FSearchRfidBinding
import com.yyc.ams.ext.showToast
import com.yyc.ams.util.MusicUtils
import com.yyc.ams.viewmodel.AssetSearchmModel
import com.yyc.ams.viewmodel.RfidModel
import com.yyc.ams.weight.ShellTokenizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.hgj.jetpackmvvm.ext.nav


/**
 * User: Nike
 *  2023/12/19 16:14
 */
class SearchRfidFrg : BaseFragment<AssetSearchmModel, FSearchRfidBinding>() {

    var LabelTag: String? = null

    val assetSearchmModel: AssetSearchmModel by activityViewModels()

    val rfidModel: RfidModel by activityViewModels()

    val configDao = AppRoomDataBase.get().getConfigDao()

    var eaf = 1.0

    var strength = 0

    val mHandler = Handler(Looper.getMainLooper())

    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            LabelTag = it.getString("epc")
        }
        mDatabind.viewmodel = mViewModel
        mDatabind.click = ProxyClick()
        mViewModel.openStatus.set(getString(R.string.rfid_on))
        mViewModel.epc.set(if (StringUtils.isEmpty(LabelTag)) "" else LabelTag)
        MusicUtils.init(activity)

        mDatabind.circularProgressBar.onProgressChangeListener = { progress ->
            ThreadUtils.runOnUiThread {
                mViewModel.rssi.set("${progress}")
            }
        }

        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onPause(owner: LifecycleOwner) {
                super.onPause(owner)
                stop()
            }

            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                MusicUtils.clear()
                rfidModel.isSearchOpen.value = null
                assetSearchmModel.epcData.value = null
                mHandler.removeCallbacksAndMessages(null)
            }
        })
        mViewModel.onRequest()

        mViewModel.viewModelScope.launch(Dispatchers.IO) {
            var configBean = configDao.findFirst()
            eaf = configBean.eaf
            strength = configBean.strength
            withContext(Dispatchers.Main) {

            }
        }

    }

    override fun createObserver() {
        super.createObserver()
        assetSearchmModel.epcData.observe(viewLifecycleOwner, Observer {
            val s = mViewModel.epc.get()
            if (it == null || !mViewModel.epc.get().trim().equals(it.tagId))return@Observer
            var rssi = it.rssi?.replace("-", "")!!.toDouble()
            mDatabind.circularProgressBar.progress = rssi!!.toFloat()
            /*ThreadUtils.runOnUiThread {
                mDatabind.circularProgressBar.setProgressWithAnimation(rssi!!.toFloat(), 1000)

                val n = eaf
                val distance = Math.pow(10.0, (Math.abs(rssi) - strength) / (10 * n))
                mViewModel.distance.set(getString(R.string.distance) + "：" + String.format("%.2f", distance) + "m")
                LogUtils.e(n, strength)
            }*/

            mHandler.removeCallbacks(delayTask)
            ThreadUtils.runOnUiThread {
                mDatabind.circularProgressBar.setProgressWithAnimation(rssi!!.toFloat(), 1000)
                mDatabind.scSignal.setRssi(rssi.toInt())
                MusicUtils.setPlayCount(3)
                MusicUtils.setSpeechRate(2.0f)
                MusicUtils.play()
                mHandler.postDelayed(delayTask, 2000)
            }
        })
        mViewModel.listBean.observe(viewLifecycleOwner, Observer {
            //获取字符串数组
            val adpter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, it)
            mDatabind.etText.setTokenizer(ShellTokenizer(requireContext(), ' ', true))
            mDatabind.etText.setAdapter(adpter)
        })
    }

    // 定义一个延迟任务的 Runnable
    val delayTask = Runnable {
        mDatabind.scSignal.setRssi(0)
    }

    inner class ProxyClick() {

        fun onClose(){
            nav().navigateUp()
        }

        fun onIsOpen(){
            if (StringUtils.isEmpty(mViewModel.epc.get())){
                showToast("No EPC")
                return
            }
            mDatabind.circularProgressBar.progress = 0f
            mViewModel.distance.set(getString(R.string.distance) + "：" + "0m")

            if (mViewModel.openStatus.get().equals(getString(R.string.rfid_on))){
                rfidModel.isSearchOpen.value = mViewModel.epc.get()!!.trim()
                mViewModel.openStatus.set(getString(R.string.rfid_off))
                mDatabind.etText.setEnabled(false);
            }else{
                stop()
            }
        }
    }

    private fun stop() {
        rfidModel.isSearchOpen.value = null
        mViewModel.openStatus.set(getString(R.string.rfid_on))
        mDatabind.etText.setEnabled(true);
    }

}


