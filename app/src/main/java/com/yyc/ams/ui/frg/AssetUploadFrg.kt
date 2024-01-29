package com.yyc.ams.ui.frg

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.TimeUtils
import com.google.gson.Gson
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.decoration.GridSpacingItemDecoration
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnExternalPreviewEventListener
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.utils.DensityUtil
import com.lxj.xpopup.XPopup
import com.yyc.ams.R
import com.yyc.ams.adapter.GridImageAdapter
import com.yyc.ams.base.BaseFragment
import com.yyc.ams.bean.AppRoomDataBase
import com.yyc.ams.bean.db.AssetBean
import com.yyc.ams.databinding.FAssetUploadBinding
import com.yyc.ams.ext.INVENTORY_STOCK
import com.yyc.ams.ext.SCAN_STATUS_MANUALLY
import com.yyc.ams.ext.UPLOAD_IMAGE_SPLIT
import com.yyc.ams.ext.showToast
import com.yyc.ams.viewmodel.AssetModel
import com.yyc.ams.viewmodel.AssetUploadModel
import com.yyc.ams.weight.FullyGridLayoutManager
import com.yyc.ams.weight.GlideEngine
import com.yyc.ams.weight.ImageFileCompressEngine
import kotlinx.coroutines.launch

/**
 * @Author nike
 * @Date 2023/8/2 14:18
 * @Description
 */
class AssetUploadFrg: BaseFragment<AssetUploadModel, FAssetUploadBinding>(){

    private val assetModel: AssetModel by activityViewModels()

    val imageAdapter by lazy { activity?.let { GridImageAdapter(it, localMediaList,) } }

    var localMediaList = ArrayList<LocalMedia?>()

    //原来状态
    var default_state: Int = -1

    var asstBean: AssetBean? = null

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.viewmodel = mViewModel
        mDatabind.click = ProxyClick()
        arguments?.let {
            asstBean = Gson().fromJson(it.getString("bean"), AssetBean::class.java)
        }

        default_state = asstBean!!.InventoryStatus
        mViewModel.status.set((if (default_state == INVENTORY_STOCK) getText(R.string.found) else getText(R.string.missing)).toString())
        mViewModel.remarks.set(asstBean?.Remarks)

        val iamgeList = asstBean!!.imageList
        if (!StringUtils.isEmpty(iamgeList)){
            val split = iamgeList!!.split(UPLOAD_IMAGE_SPLIT)
            split.forEachIndexed(){index, path ->
                if (!StringUtils.isEmpty(path)){
                    localMediaList.add(LocalMedia.generateLocalMedia(activity, path))
                }
            }
        }

        val manager = FullyGridLayoutManager(activity,3, GridLayoutManager.VERTICAL, false)
        mDatabind.recyclerView.setLayoutManager(manager)
        val itemAnimator: RecyclerView.ItemAnimator? = mDatabind.recyclerView.getItemAnimator()
        if (itemAnimator != null) {
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
        mDatabind.recyclerView.addItemDecoration(
            GridSpacingItemDecoration( 3, DensityUtil.dip2px(activity, 8f), false)
        )
        mDatabind.recyclerView.adapter = imageAdapter
        imageAdapter!!.selectMax = 3
        imageAdapter!!.setOnItemClickListener(object : GridImageAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                PictureSelector.create(activity)
                    .openPreview()
                    .setImageEngine(GlideEngine.createGlideEngine())
                    .setExternalPreviewEventListener(object : OnExternalPreviewEventListener {
                        override fun onPreviewDelete(position: Int) {
                            imageAdapter!!.delete(position)
                        }
                        override fun onLongPressDownload(context: Context?, media: LocalMedia?): Boolean {
                            return false
                        }
                    })
                    .startActivityPreview(position, true, imageAdapter!!.data)
            }

            override fun openPicture() {
                PictureSelector.create(activity)
                    .openGallery(SelectMimeType.ofImage())
                    .setMaxSelectNum(3)
                    .setCompressEngine(ImageFileCompressEngine())
                    .setImageEngine(GlideEngine.createGlideEngine())
                    .setSelectedData(imageAdapter!!.data)
                    .forResult(object : OnResultCallbackListener<LocalMedia?> {
                        override fun onResult(result: ArrayList<LocalMedia?>) {
                            localMediaList.clear()
                            localMediaList.addAll(result)
                            imageAdapter!!.getData().clear()
                            imageAdapter!!.getData().addAll(result)
                            imageAdapter!!.notifyDataSetChanged()
                        }
                        override fun onCancel() {}
                    })
            }

        })
    }

    inner class ProxyClick(){

         fun save(){
            val sb = StringBuffer()
            if (imageAdapter!!.data.size != 0){
                imageAdapter!!.data.forEachIndexed(){i, bean ->
                    if (bean!!.compressPath == null){
                        sb.append(bean!!.path).append(UPLOAD_IMAGE_SPLIT)
                    }else{
                        sb.append(bean!!.compressPath).append(UPLOAD_IMAGE_SPLIT)
                    }
                }
            }
            //保存是否在库
            if (default_state != asstBean?.InventoryStatus){
                asstBean!!.InventoryStatus = default_state
            }
            //存图片
            if (sb != null){
                asstBean!!.imageList = sb.toString()
            }
            asstBean!!.Remarks = mViewModel.remarks.get()
            //判断时间在不在
            var scanTime = asstBean!!.scanTime
            if (StringUtils.isEmpty(scanTime)){
                asstBean!!.scanTime = TimeUtils.getNowString()
            }
            if (asstBean!!.scanStatus == 0){
                asstBean!!.scanStatus = SCAN_STATUS_MANUALLY
            }
             //JSONObject 会赋值 null
            if (StringUtils.isEmpty(asstBean!!.LabelTag) || asstBean!!.LabelTag.equals("null")){
                asstBean!!.LabelTag = ""
            }
            val assetDao = AppRoomDataBase.get().getAssetDao()
            mViewModel.viewModelScope.launch {
                assetDao.update(asstBean!!)
            }
            assetModel.epcUploadData.value = asstBean!!
             showToast(getString(R.string.release_success))
        }

        fun status(){
            if (asstBean!!.InventoryStatus == INVENTORY_STOCK)return
            XPopup.Builder(context)
                .asCenterList(
                    getText(R.string.status), arrayOf(getString(R.string.found), getString(R.string.missing)),
                    null, default_state - 1
                ) { position, text ->
                    default_state = position + 1
                    mViewModel.status.set((if (default_state == INVENTORY_STOCK) getText(R.string.found) else getText(R.string.missing)).toString())
                }.show()
        }
    }

}