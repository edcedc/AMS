package com.yyc.ams.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.yyc.ams.bean.AppRoomDataBase
import com.yyc.ams.bean.db.OrderBean
import com.yyc.ams.util.CacheUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.databind.StringObservableField

/**
 * @Author nike
 * @Date 2023/7/7 12:00
 * @Description
 */
class OrderModel: BaseViewModel() {

    var triggerLocation = StringObservableField()

    var username = StringObservableField()

    var time = StringObservableField()

    var listBean: MutableLiveData<ArrayList<OrderBean>> = MutableLiveData()

    fun onRequest() {
        val RoNo = CacheUtil.getUser()?.RoNo
        val companyId = CacheUtil.getUser()?.companyId
        val orderDao = AppRoomDataBase.get().getOrderDao()
        /*viewModelScope.launch(Dispatchers.IO) {
            if (RoNo != null) {
                val list = orderDao.findById(RoNo)
                withContext(Dispatchers.Main) {
                    listBean.value = list as ArrayList<OrderBean>
                }
            }
        }*/
        viewModelScope.launch(Dispatchers.IO) {
            if (RoNo != null) {
                val list = orderDao.findById(RoNo, companyId)
                listBean.postValue(list as ArrayList<OrderBean>)
            }
        }
    }

}