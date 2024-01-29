package com.yyc.ams.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.TimeUtils
import com.hj.excellibrary.Excel
import com.hj.excellibrary.service.IWriteListener
import com.yyc.ams.bean.AppRoomDataBase
import com.yyc.ams.bean.DataBean
import com.yyc.ams.bean.ExcelBean
import com.yyc.ams.bean.db.ConfigBean
import com.yyc.ams.bean.db.RfidBean
import com.yyc.ams.ext.Externalpath
import com.yyc.ams.mar.eventViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.databind.StringObservableField
import org.apache.poi.ss.formula.functions.T
import java.io.File

/**
 * User: Nike
 *  2023/12/15 15:13
 */
class RfidListModel : BaseViewModel() {

    var listBean: MutableLiveData<ArrayList<RfidBean>> = MutableLiveData()

    val rfidDao = AppRoomDataBase.get().getRfidDao()

    var parentId = StringObservableField()

    var backTitle = StringObservableField()

    val configDao = AppRoomDataBase.get().getConfigDao()

    val rfidFileDao = AppRoomDataBase.get().getRfidFlieDao()

    val isLoaddingBean: MutableLiveData<DataBean> = MutableLiveData()

    fun onRequest(parentId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = rfidDao.findAll(parentId)
            withContext(Dispatchers.Main) {
                listBean.value = list as ArrayList<RfidBean>
            }
        }
    }

    fun setSave1(data: MutableList<RfidBean>, parentId: String, position: Int) {
        Flowable.fromCallable {
            val existsDir = FileUtils.createOrExistsDir(Externalpath)
            if (existsDir) {
                val excelList = ArrayList<ExcelBean>()
                data.forEachIndexed { index, rfidBean ->
                    val bean = ExcelBean()
                    bean.uId = String.format("%06d", rfidBean.uid)
                    bean.epc = rfidBean.epc
                    bean.crearDate = rfidBean.crearDate
                    excelList.add(bean)
                }
//                isLoaddingBean.postValue(DataBean(isLoading = true))
                excelList
            }else{
                emptyList()
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .doOnNext {
                var configBean = configDao.findFirst()
                var nowString = TimeUtils.getNowString()
                nowString = nowString.replace(" ", "_")
                var path = Externalpath + "/${configBean.rfidCode}" + "_${parentId}" + "_${nowString}"

                val batchedList = it.chunked(10000)
                for ((index, batch) in batchedList.withIndex()) {
                    setExcel(index, batch, path)
                }
            }
            .observeOn(AndroidSchedulers.mainThread()) //给下面分配了UI线程
            .subscribe({
                var bean = DataBean()
                bean.position = position
                bean.fileName = "path"
                eventViewModel.updateRfidFileData.postValue(bean)
            }, { error ->
                // 处理错误
                LogUtils.e(error)
            })
    }

    fun setExcel(index: Int, batch: List<ExcelBean>, path1: String) {
        var path = path1 + "_${index}"  + ".xlsx"
        Excel.get()
            .writeWith<ExcelBean>(File(path))
            .doWrite(
                object : IWriteListener {
                    override fun onStartWrite() {
                        LogUtils.e("onStartWrite")
                    }

                    override fun onWriteError(e: Exception) {
                        LogUtils.e(e.toString())
                    }

                    override fun onEndWrite() {
//                        var bean = DataBean()
//                        bean.position = position
//                        bean.fileName = path
//                        eventViewModel.updateRfidFileData.postValue(bean)
//
//                        isLoaddingBean.postValue(DataBean(isLoading = false))
                        LogUtils.e("onEndWrite")
                    }
                }, batch
            )
    }

    fun setSave(
        data: MutableList<RfidBean>,
        parentId: String,
        position: Int,
        location: String,
        title: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            var configBean = configDao.findFirst()
            var nowString = TimeUtils.getNowString()
            nowString = nowString.replace(" ", "_")
            var path = Externalpath + "/${configBean.rfidCode}" + "_${parentId}" + "_${nowString}" + ".xlsx"

            val existsDir = FileUtils.createOrExistsDir(Externalpath)
            if (existsDir){
                val excelList = ArrayList<ExcelBean>()
                data.forEachIndexed { index, rfidBean ->
                    val bean = ExcelBean()
                    bean.uId = "${rfidBean.position}"
                    bean.epc = rfidBean.epc
                    bean.crearDate = rfidBean.crearDate
                    bean.location = location
                    bean.title = title
                    excelList.add(bean)
                }
                Excel.get()
                    .writeWith<Any>(File(path))
                    .doWrite(
                        object : IWriteListener {
                            override fun onStartWrite() {
                                isLoaddingBean.postValue(DataBean(isLoading = true))
                                LogUtils.e("onStartWrite")
                            }

                            override fun onWriteError(e: Exception) {
                                LogUtils.e(e)
                            }

                            override fun onEndWrite() {
                                var bean = DataBean()
                                bean.position = position
                                bean.fileName = path
                                eventViewModel.updateRfidFileData.postValue(bean)

                                isLoaddingBean.postValue(DataBean(isLoading = false))
                                LogUtils.e("onEndWrite")
                            }
                        }, excelList
                    )

            }
            withContext(Dispatchers.Main) {
            }
        }
    }

}