package com.yyc.ams.viewmodel

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.yyc.ams.R
import com.yyc.ams.bean.AppRoomDataBase
import com.yyc.ams.bean.RfidStateBean
import com.yyc.ams.bean.db.AssetBean
import com.yyc.ams.bean.db.RfidBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.hgj.jetpackmvvm.base.appContext
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.databind.StringObservableField

/**
 * @Author nike
 * @Date 2023/8/23 11:11
 * @Description
 */
class AssetSearchmModel: BaseViewModel() {

    var rssi = StringObservableField("0.0")

    val isOpen = ObservableBoolean()

    val openStatus = StringObservableField(appContext.getString(R.string.start))

    val text13 = StringObservableField(appContext.getString(R.string.distance) + "：")

    val epc = StringObservableField()

    val distance = StringObservableField(appContext.getString(R.string.distance) + "：0m")

    var epcData: MutableLiveData<RfidStateBean> = MutableLiveData()

    val rfidDao = AppRoomDataBase.get().getRfidDao()

    var listBean: MutableLiveData<ArrayList<String>> = MutableLiveData()

    fun onRequest() {
        viewModelScope.launch(Dispatchers.IO) {
            val findAll = rfidDao.findAllDistinct()
            withContext(Dispatchers.Main) {
                if (findAll.size != 0){
                    val list = ArrayList<String>()
                    findAll.forEach {bean->
                        list.add(bean.epc)
                    }
                    listBean.value = list
                }
            }
        }
    }

}