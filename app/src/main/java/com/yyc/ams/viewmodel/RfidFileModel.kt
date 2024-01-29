package com.yyc.ams.viewmodel

import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.ToastUtils
import com.luck.picture.lib.utils.ToastUtils.showToast
import com.yyc.ams.R
import com.yyc.ams.adapter.RfidFileAdapter
import com.yyc.ams.bean.AppRoomDataBase
import com.yyc.ams.bean.db.ConfigBean
import com.yyc.ams.bean.db.RfidFileBean
import com.yyc.ams.ext.showEditDialog
import com.yyc.ams.ext.showToast
import com.yyc.ams.weight.PopDeleteRfidView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import java.io.File

/**
 * User: Nike
 *  2023/12/15 15:13
 */
class RfidFileModel : BaseViewModel() {

    var listBean: MutableLiveData<ArrayList<RfidFileBean>> = MutableLiveData()

    val rfidFlieDao = AppRoomDataBase.get().getRfidFlieDao()

    val rfidDao = AppRoomDataBase.get().getRfidDao()

    val deleteSuccess: MutableLiveData<Boolean> = MutableLiveData()

    val configDao = AppRoomDataBase.get().getConfigDao()

    fun onRequest() {
        viewModelScope.launch(Dispatchers.IO) {
            val findAll = rfidFlieDao.findAll()
            findAll.forEachIndexed { index, rfidFlieBean ->
                val list = rfidDao.findAll(rfidFlieBean.uid.toLong())
                rfidFlieBean.number = list.size
            }
            withContext(Dispatchers.Main) {
                listBean.value = findAll as ArrayList<RfidFileBean>
            }
        }
    }

    fun onDelete(
        list: ArrayList<Int>,
        adapter: RfidFileAdapter,
        requireActivity: FragmentActivity
    ) {
        val popDeleteRfidView = PopDeleteRfidView(requireActivity)
        showEditDialog(requireActivity)
            .asCustom(popDeleteRfidView)
            .show()
        popDeleteRfidView.setOnItemClickListener(object : PopDeleteRfidView.OnItemClickListener {
            override fun onItemClick(text1: String) {
                viewModelScope.launch(Dispatchers.IO) {
                    val reversedList = list.reversed()
                    reversedList.forEachIndexed { index, i ->
                        val bean = adapter.data[i]
                        rfidFlieDao.deleteById(bean)
                        rfidDao.deleteParentId(bean.uid.toLong())
                        withContext(Dispatchers.Main) {
                            adapter.removeAt(i)
                        }
                    }
                    withContext(Dispatchers.Main) {
                        deleteSuccess.value = true
                    }
                }
            }
        })
    }

    fun onMail(
        list: java.util.ArrayList<Int>,
        adapter: RfidFileAdapter,
        requireActivity: FragmentActivity
    ) {
        if (list.size == 0){
            ToastUtils.showShort(requireActivity.getString(R.string.no_excel))
            return
        }
        list.forEachIndexed { index, i ->
            val bean = adapter.data[i]
            val flieName = bean.flieName
            if (StringUtils.isEmpty(flieName)) {
                ToastUtils.showShort("${bean.uid}" + requireActivity.getString(R.string.no_excel))
                return
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            var configBean = configDao.findFirst()
            val recipient = configBean.recipient
            if (StringUtils.isEmpty(recipient)) {
                ToastUtils.showShort(requireActivity.getString(R.string.text16))
                return@launch
            }
            withContext(Dispatchers.Main) {
                val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
                // 邮件发送类型：带附件的邮件
                intent.type = "application/octet-stream"
                intent.putExtra(
                    Intent.EXTRA_EMAIL,
                    arrayOf(configBean.recipient)
                )
                intent.putExtra(Intent.EXTRA_SUBJECT, "EPC 掃描資料")
                intent.putExtra(Intent.EXTRA_TEXT, "請查收")

                // 准备附件的 URI 列表
                val fileUris = ArrayList<Uri>()
                list.forEachIndexed { index, i ->
                    val bean = adapter.data[i]
                    val flieName = bean.flieName
                    fileUris.add(
                        FileProvider.getUriForFile(
                            requireActivity,
                            "com.yyc.ams.fileprovider",
                            File(flieName)
                        )
                    )
                }
                for (uri in fileUris) {
                    requireActivity.grantUriPermission(
                        requireActivity.packageName,
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    requireActivity.grantUriPermission(
                        requireActivity.packageName,
                        uri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                }
                intent.putParcelableArrayListExtra(
                    Intent.EXTRA_STREAM,
                    fileUris
                )
                requireActivity.startActivity(Intent.createChooser(intent, "請選擇郵件並發送"))
            }
        }
        deleteSuccess.value = true
    }

}