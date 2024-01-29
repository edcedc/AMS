package com.yyc.ams.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.yyc.ams.R
import com.yyc.ams.SingleLiveEvent
import com.yyc.ams.bean.AppRoomDataBase
import com.yyc.ams.bean.RfidStateBean
import com.yyc.ams.bean.db.AssetBean
import com.yyc.ams.bean.db.UploadOrderBean
import com.yyc.ams.bean.db.UploadOrderListBean
import com.yyc.ams.ext.INVENTORY_FAIL
import com.yyc.ams.ext.UPLOAD_IMAGE_SPLIT
import com.yyc.ams.ui.frg.AssetFrg
import com.yyc.ams.util.CacheUtil
import com.yyc.ams.util.ImageUtils
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import me.hgj.jetpackmvvm.base.appContext
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.databind.StringObservableField
import org.json.JSONArray
import org.json.JSONObject

/**
 * @Author nike
 * @Date 2023/7/27 11:45
 * @Description
 */
open class AssetModel : BaseViewModel() {

    var assetTitle: MutableLiveData<String> = MutableLiveData()

    var assetSerch: MutableLiveData<String> = MutableLiveData()

    var epcData: MutableLiveData<RfidStateBean> = MutableLiveData()

    var epcUploadData: MutableLiveData<AssetBean> = MutableLiveData()
//    val epcUploadData = SingleLiveEvent<AssetBean>()
//    val epcUploadData:MutableLiveData<AssetBean> = SingleLiveEvent()
//    val epcUploadData:MutableLiveData<AssetBean> = LockedLiveEvent()
//    val epcUploadData = MutableStateFlow<AssetBean>()

    var listBean: MutableLiveData<ArrayList<AssetBean>> = MutableLiveData()

    var listJsonArray: MutableLiveData<JSONArray> = MutableLiveData()

    var state = StringObservableField()

    var save = StringObservableField()

    var isShowDialog: MutableLiveData<Boolean> = MutableLiveData()

    val companyId = CacheUtil.getCompanyID()

    val LoginId = CacheUtil.getUser()?.LoginID

    val RoNo = CacheUtil.getUser()?.RoNo

    fun onRequest(orderId: String?, status: Int) {
        val assetDao = AppRoomDataBase.get().getAssetDao()
        var list: ArrayList<AssetBean>
        viewModelScope.launch(Dispatchers.IO) {
            if (status == -1){
                list = assetDao.findByIdNoFail(RoNo, orderId, companyId) as ArrayList<AssetBean>
            }else{
                list = assetDao.findById(RoNo, orderId, status, companyId) as ArrayList<AssetBean>
            }
            listBean.postValue(list)
        }
    }

    fun onRequestText(bean: String?) {
        var asstBean = Gson().fromJson(bean, AssetBean::class.java)

        val bean = JSONObject(bean)
        Flowable.fromCallable {
            val list = JSONArray()
            val oneArray = JSONArray()
            val oneData = JSONObject()
//            oneData.put("title", appContext.resources.getString(R.string.detailed))
            val headerkeys: Iterator<String> = bean.keys()
            while (headerkeys.hasNext()) {
                val headerkey = headerkeys.next()
                val headerValue: String = bean.getString(headerkey)
                var tagObt = JSONObject()
                tagObt.put("title", headerkey)
                tagObt.put("text", headerValue)
                if (headerkey.equals("data")) {
                    val data = JSONObject(bean.optString("data"))
                    val headerkeys: Iterator<String> = data.keys()
                    while (headerkeys.hasNext()) {
                        val headerkey = headerkeys.next()
                        val headerValue: String = data.getString(headerkey)
                        var twoData = JSONObject()
                        twoData.put("title", headerkey)
                        var twoArray = JSONArray()
                        var threeData = JSONObject(headerValue)
                        val headerkeys: Iterator<String> = threeData.keys()
                        while (headerkeys.hasNext()) {
                            val headerkey = headerkeys.next()
                            val headerValue: String = threeData.getString(headerkey)
                            var threeData = JSONObject()
                            threeData.put("title", headerkey)
                            threeData.put("text", headerValue)

                            if (headerkey.equals("Remarks")){
                                threeData.put("text", asstBean.Remarks)
                            }

                            //扫描状态
                            if (headerkey.equals("ScanDate")){
                                threeData.put("text", asstBean.scanTime)
                            }
                            if (headerkey.equals("ScanUser")){
                                threeData.put("text", if (!StringUtils.isEmpty(asstBean.scanTime)) LoginId else "")
                            }
                            if (headerkey.equals("FoundStatus")){
                                threeData.put("text", asstBean.scanStatus)
                            }

                            if (headerkey.equals("RoNo") || headerkey.equals("AssetNo")){

                            }else{
                                twoArray.put(threeData)
                            }
                            twoData.put("list", twoArray)
                        }
                        list.put(twoData)
                    }
                } else {
                    oneArray.put(tagObt)
                }
            }
            oneData.put("list", oneArray)
//            list.put(oneData)
            list
        }
            .subscribeOn(Schedulers.io()) //给上面分配了异步线程
            .observeOn(AndroidSchedulers.mainThread()) //给下面分配了UI线程
            .subscribe({ list ->
                listJsonArray.value = list
            }, { error ->
                // 处理错误
                LogUtils.e(error)
            })
    }

    fun onUpload(orderId: String?, assetFrg: AssetFrg) {
        val assetDao = AppRoomDataBase.get().getAssetDao()
        val uploadOrderDao = AppRoomDataBase.get().getUploadOrderDao()
        val uploadOrderListDao = AppRoomDataBase.get().getUploadOrderListDao()
        val time = TimeUtils.getNowString()

        var ja = JSONArray()
        viewModelScope.launch(Dispatchers.IO) {
            val list = assetDao.findById(orderId, RoNo, companyId)
            if (list == null || list.size == 0){
                ToastUtils.showShort(appContext.getString(R.string.no_found1))
                return@launch
            }
            isShowDialog.postValue(true)


            var isAdd = false
            var uploadOrderBean = uploadOrderDao.findOrderId(orderId, RoNo, companyId)
            if (uploadOrderBean == null){
                uploadOrderBean = UploadOrderBean()
                isAdd = true
            }
            uploadOrderBean.status = 0
            uploadOrderBean.title = "Stock Take：" + orderId + " | " + time
            uploadOrderBean.RoNo = RoNo!!
            uploadOrderBean.companyId = companyId!!
            uploadOrderBean.orderId = orderId!!
            if (isAdd){
                uploadOrderDao.add(uploadOrderBean)
            }else{
                uploadOrderDao.update(uploadOrderBean)
            }
            list.forEachIndexed { index, assetBean ->
                val bean = UploadOrderListBean()
                bean.loginID = RoNo
                bean.orderNo = orderId
                bean.AssetNo = assetBean.AssetNo
                bean.ScanDate = assetBean.scanTime
                bean.EPC = assetBean.LabelTag
                bean.Remarks = assetBean.Remarks
                bean.statusID = if (assetBean.InventoryStatus == INVENTORY_FAIL) 2 else assetBean.InventoryStatus
                bean.FoundStatus = assetBean.scanStatus
                bean.Last_ScanTime = time
                bean.companyId = companyId

                val imageList = assetBean.imageList
                val split = imageList.split(UPLOAD_IMAGE_SPLIT)
                val sb = StringBuffer()
                split.forEachIndexed(){index, s ->
                    if (!StringUtils.isEmpty(s)){
                        val imageToBase64 = ImageUtils.imageToBase64(s)
                        sb.append(imageToBase64).append(UPLOAD_IMAGE_SPLIT)
                    }
                }
                if (!StringUtils.isEmpty(sb)){
//                        FileToByte(bean, URLEncoder.encode(sb.toString(), "UTF-8"))
                }
                bean.img = sb.toString()

                uploadOrderListDao.add(bean)
            }
            withContext(Dispatchers.Main) {
                runBlocking {
                    delay(1000) // 延迟1秒钟
                    isShowDialog.value = false
                }
                ToastUtils.showShort(appContext.getText(R.string.release_success))
            }
        }
    }

}