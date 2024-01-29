package com.yyc.ams.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.yyc.ams.R
import com.yyc.ams.SingleLiveEvent
import com.yyc.ams.bean.AppRoomDataBase
import com.yyc.ams.bean.dao.UploadOrderDao
import com.yyc.ams.bean.dao.UploadOrderListDao
import com.yyc.ams.bean.db.UploadOrderBean
import com.yyc.ams.bean.db.UploadOrderListBean
import com.yyc.ams.network.REQUEST_SUCCESS
import com.yyc.ams.network.apiService
import com.yyc.ams.network.stateCallback.ListDataUiState
import com.yyc.ams.util.CacheUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.hgj.jetpackmvvm.base.appContext
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.requestNoCheck
import java.util.ArrayList

/**
 * @Author nike
 * @Date 2023/8/7 16:01
 * @Description
 */
class UploadModel: BaseViewModel(){

    var listBean: MutableLiveData<ListDataUiState<UploadOrderBean>> = MutableLiveData()

    var uploadBean: MutableLiveData<UploadOrderBean> = MutableLiveData()

    val loginID = CacheUtil.getUser()?.LoginID

    var isShowDialog: SingleLiveEvent<Boolean> = SingleLiveEvent()

    val companyId = CacheUtil.getUser()?.companyId

    val RoNo = CacheUtil.getUser()?.RoNo
    
    //分包上传 条数
    var uploadNum = 1000

    fun onRequest() {
        val uploadOrderDao = AppRoomDataBase.get().getUploadOrderDao()
        viewModelScope.launch(Dispatchers.IO) {
            val list = uploadOrderDao.findAll(RoNo, companyId)
            listBean.postValue(
                ListDataUiState(
                isSuccess = if (list.size == 0) false else true,
                listData = list as ArrayList<UploadOrderBean>
            ))
        }
    }

     fun UploadStockTake(bean: UploadOrderBean) {
         val uploadOrderListDao = AppRoomDataBase.get().getUploadOrderListDao()
         val uploadOrderDao = AppRoomDataBase.get().getUploadOrderDao()

         viewModelScope.launch(Dispatchers.IO) {
             val findAll = uploadOrderListDao.findByIdAll(RoNo, companyId, bean.orderId)
             val batchSize = uploadNum
             val batchedList = findAll.chunked(batchSize)
             isShowDialog.postValue(true)
             for ((index, batch) in batchedList.withIndex()) {
                 FileToByte(batch, bean, uploadOrderDao, uploadOrderListDao, index == batchedList.lastIndex)
             }
         }
    }

    private fun FileToByte(
        findAll: List<UploadOrderListBean>,
        bean: UploadOrderBean,
        uploadOrderDao: UploadOrderDao,
        uploadOrderListDao: UploadOrderListDao,
        b: Boolean
    ) {
        val toJson = Gson().toJson(findAll)
        requestNoCheck({ apiService.FileToByte(companyId, toJson) }, {
            if (it.code == REQUEST_SUCCESS) {
                viewModelScope.launch(Dispatchers.IO) {
                    bean.status = 1
                    uploadOrderDao.update(bean)
                    uploadBean.postValue(bean)

                    if (b){
                        isShowDialog.postValue(false)
                    }

                    uploadOrderListDao.deleteById(RoNo, companyId, bean.orderId)
                }
                ToastUtils.showShort(appContext.getText(R.string.text4))
            }else{
                ToastUtils.showShort(appContext.getText(R.string.text9))
            }
        }, {
            //请求失败 网络异常回调在这里
            loadingChange.dismissDialog
            ToastUtils.showShort(it.throwable!!.message)
            LogUtils.e(it.throwable)
        }, false)
    }

}