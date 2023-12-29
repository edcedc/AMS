package com.yyc.ams.ui.newfrg

import android.os.Bundle
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

    var eaf: Double = 1.0

    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            LabelTag = it.getString("epc")
        }
        mDatabind.viewmodel = mViewModel
        mDatabind.click = ProxyClick()
        mViewModel.openStatus.set(getString(R.string.rfid_on))
        mViewModel.epc.set(if (StringUtils.isEmpty(LabelTag)) "" else LabelTag)

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
                rfidModel.isSearchOpen.value = null
                assetSearchmModel.epcData.value = null
            }
        })
        mViewModel.onRequest()



        mViewModel.viewModelScope.launch(Dispatchers.IO) {
            var configBean = configDao.findFirst()
            if (configBean == null){
                configBean = ConfigBean()
                eaf = configBean.eaf
            }
            withContext(Dispatchers.Main) {

            }
        }

        mDatabind.etStrength.setText("70")
    }

    override fun createObserver() {
        super.createObserver()
        assetSearchmModel.epcData.observe(viewLifecycleOwner, Observer {
            val s = mViewModel.epc.get()
            if (it == null || !mViewModel.epc.get().trim().equals(it.tagId))return@Observer
            var rssi = it.rssi?.replace("-", "")!!.toDouble()
            mDatabind.circularProgressBar.progress = rssi!!.toFloat()
            ThreadUtils.runOnUiThread {
                mDatabind.circularProgressBar.setProgressWithAnimation(rssi!!.toFloat(), 1000)

                val A = mDatabind.etStrength.text.toString().toDouble()
                val n = eaf
                val distance = Math.pow(10.0, (Math.abs(rssi) - A) / (10 * n))
                mViewModel.distance.set(getString(R.string.distance) + "：" + String.format("%.2f", distance) + "m")
            }
        })
        mViewModel.listBean.observe(viewLifecycleOwner, Observer {
            //获取字符串数组
            val adpter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, it)
            mDatabind.etText.setTokenizer(ShellTokenizer(requireContext(), ' ', true))
            mDatabind.etText.setAdapter(adpter)
        })
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


