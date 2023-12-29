package com.yyc.ams.viewmodel

import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.yyc.ams.R
import com.yyc.ams.bean.DataBean
import com.yyc.ams.network.REQUEST_SUCCESS
import com.yyc.ams.network.apiService
import com.yyc.ams.network.stateCallback.ListDataUiState
import me.hgj.jetpackmvvm.base.appContext
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.databind.StringObservableField
import me.hgj.jetpackmvvm.ext.request
import me.hgj.jetpackmvvm.ext.requestNoCheck
import org.json.JSONArray
import org.json.JSONObject

/**
 * @Author nike
 * @Date 2023/8/8 14:49
 * @Description
 */
class ExternalModel: BaseViewModel(){

    var state = StringObservableField()

    var save = StringObservableField()

    var searchText: MutableLiveData<String> = MutableLiveData()

    var listJsonArray: MutableLiveData<JSONArray> = MutableLiveData()

    var listClearArray: MutableLiveData<JSONArray> = MutableLiveData()

    var submitType: MutableLiveData<Int> = MutableLiveData()
//    val submitType: SingleLiveEvent<Int> = SingleLiveEvent()

    var listData: MutableLiveData<ListDataUiState<DataBean>> = MutableLiveData()

    var listBooKArchivesData: MutableLiveData<ListDataUiState<DataBean>> = MutableLiveData()

    val pagerNumber: Int = 1

    fun onRequest() {
        request({ apiService.GetExteriOrderBorrowingList("") }, {
            val isRefresh = true
            val listDataUiState =
                ListDataUiState(
                    isSuccess = true,
                    isEmpty = it!!.isEmpty(),
                    isRefresh = isRefresh,
                    isFirstEmpty = pagerNumber == 1 && it.isEmpty(),
                    listData = it
                )
            listData.value = listDataUiState
        }, {
            //请求失败
            val listDataUiState =
                ListDataUiState(
                    isSuccess = false,
                    errMessage = it.errorMsg,
                    isRefresh = true,
                    listData = arrayListOf<DataBean>()
                )
            listData.value = listDataUiState
        })
    }

    fun onRequestDetails(orderId: String?, i: Int) {
        request({ apiService.GetExteriOrderBorrowingDetails(i, orderId, "") }, {
            val isRefresh = true
            val listDataUiState =
                ListDataUiState(
                    isSuccess = true,
                    isEmpty = it!!.isEmpty(),
                    isRefresh = isRefresh,
                    isFirstEmpty = pagerNumber == 1 && it.isEmpty(),
                    listData = it
                )
            listBooKArchivesData.value = listDataUiState
        }, {
            //请求失败
            val listDataUiState =
                ListDataUiState(
                    isSuccess = false,
                    errMessage = it.errorMsg,
                    isRefresh = true,
                    listData = arrayListOf<DataBean>()
                )
            listBooKArchivesData.value = listDataUiState
        })
    }

    fun UpdateDisposalAPP(orderId: String?, str: JSONArray) {
        requestNoCheck({ apiService.GetUpdateExteriorBorrowingApp(orderId!!, str.toString())},{
            if (it.code == REQUEST_SUCCESS){
                listClearArray.value = str
                ToastUtils.showShort(appContext.getText(R.string.text4))
            }else{
                ToastUtils.showShort(appContext.getText(R.string.text9))
            }
        },{
            //请求失败 网络异常回调在这里
            loadingChange.dismissDialog
            ToastUtils.showShort(it.throwable!!.message)
            LogUtils.e(it.throwable, it.throwable!!.message)
        }, true, appContext.getString(R.string.loading))
    }

    fun onRequestText(title: String?, bean: String?) {
        val jo = JSONObject(bean)
        val ja = JSONArray()
        val headerkeys: Iterator<String> = jo.keys()
        while (headerkeys.hasNext()) {
            val headerkey = headerkeys.next()
            val headerValue: String = jo.getString(headerkey)
            var threeData = JSONObject()
            threeData.put("title", headerkey)
            threeData.put("text", headerValue)
            ja.put(threeData)
        }

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        jsonObject.put("title", title)
        jsonObject.put("list", ja)
        jsonArray.put(jsonObject)
        listJsonArray.value = jsonArray
    }

}