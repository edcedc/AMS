package com.yyc.ams.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.yyc.ams.bean.AppRoomDataBase
import com.yyc.ams.bean.db.AssetBean
import com.yyc.ams.bean.db.OrderBean
import com.yyc.ams.network.REQUEST_SUCCESS
import com.yyc.ams.network.apiService
import com.yyc.ams.network.stateCallback.ListDataUiState
import com.yyc.ams.util.CacheUtil
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.databind.FloatObservableField
import me.hgj.jetpackmvvm.callback.databind.StringObservableField
import me.hgj.jetpackmvvm.ext.requestNoCheck
import org.json.JSONObject

/**
 * @Author nike
 * @Date 2023/7/7 16:51
 * @Description
 */
class DownloadModel: BaseViewModel() {

    var progressText = StringObservableField("0%")

    var progress = FloatObservableField()

    var listBean: MutableLiveData<ListDataUiState<OrderBean>> = MutableLiveData()
//    var listBean: MutableLiveData<BaseListBean<ArrayList<DataBean>>> = MutableLiveData()

    private var compositeDisposable: Disposable? = null

    fun onRequest() {

        val roNo = CacheUtil.getUser()!!.RoNo
        val companyID = CacheUtil.getCompanyID()

        requestNoCheck({ apiService.stockTakeList() }, {

            if (it.code == REQUEST_SUCCESS){
                val data = it.data
                if (data != null && data.size != 0){
                    //删除当前用户的数据库
                    val appRoomDataBase = AppRoomDataBase.get()
                    val orderDao = appRoomDataBase.getOrderDao()
                    val assetDao = appRoomDataBase.getAssetDao()
                    val uploadOrderDao = appRoomDataBase.getUploadOrderDao()
                    val uploadOrderListDao = appRoomDataBase.getUploadOrderListDao()
                    viewModelScope.launch(Dispatchers.IO) {
                        orderDao.deleteById(roNo, companyID)
                        assetDao.deleteById(roNo, companyID)
                        uploadOrderDao.deleteById(roNo, companyID)
                        uploadOrderListDao.deleteById(roNo, companyID)

                        data.forEachIndexed(){index, bean ->
                            bean.RoNo = roNo
                            bean.companyId = companyID
                            //存储数据库
                            orderDao.add(bean)
                            stockTakeListAsset(bean, roNo, companyID)
                        }
                        withContext(Dispatchers.Main) {
                            listBean.value = ListDataUiState(
                                isSuccess = true,
                                listData = it.data,
                                pageSize = it.count
                            )
                        }
                    }
                }else{
                    listBean.value = ListDataUiState(
                        isSuccess = false
                    )
                }
            }

        }, {
            //请求失败
            listBean.value = ListDataUiState(
                isSuccess = false,
                errMessage = it.errorMsg,
                listData = arrayListOf()
            )
        })
    }

    private fun stockTakeListAsset(bean: OrderBean, roNo: String, companyID: String) {
        var stocktakeno = bean.stocktakeno
        val assetDao = AppRoomDataBase.get().getAssetDao()

        requestNoCheck({ apiService.stockTakeListAsset(stocktakeno) }, {
            val json = JSONObject(it.string())
            if (json.optInt("code") == REQUEST_SUCCESS) {
                val data = json.optJSONArray("data")
                if (data != null) {
                    compositeDisposable = Flowable.range(0, data.length())
                        .onBackpressureBuffer()
                        .flatMap({ index ->
                            val jsonObject = data.optJSONObject(index)
                            val just = Flowable.just(jsonObject)
                            just.flatMap({ jsonObject ->
                                val tag = jsonObject.optJSONObject("Tag")
                                val inventory = jsonObject.optJSONObject("Inventory")
                                val bean = AssetBean()

                                val pair = Pair(jsonObject, bean)
                                Flowable.fromCallable {
                                    val jsonObject = pair.first as JSONObject
                                    val bean = pair.second as AssetBean
                                    bean.ids = tag.optString("RoNo")
                                    bean.OrderRoNo = stocktakeno
                                    bean.AssetNo = tag.optString("AssetNo")
                                    bean.LabelTag = tag.optString("LabelTag")
                                    bean.Remarks = tag.optString("remarks")
                                    bean.InventoryStatus = tag.optInt("InventoryStatus")
                                    if (inventory != null){
                                        bean.scanStatus = inventory.optInt("FoundStatus")
                                    }
                                    bean.RoNo = roNo
                                    bean.companyId = companyID

                                    bean.data = jsonObject.toString()

                                    bean
                                }.subscribeOn(Schedulers.io())
                            }, true, 1)
                        })
                        .subscribe { bean ->
                            viewModelScope.launch {
                                assetDao.add(bean)
                            }
                        }
                }
            }
        }, {
            //请求失败
            listBean.value = ListDataUiState(
                isSuccess = false,
                errMessage = it.errorMsg,
                listData = arrayListOf()
            )
            ToastUtils.showShort(it.message)
            LogUtils.e(it.throwable)
        })
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable?.dispose()
    }

}