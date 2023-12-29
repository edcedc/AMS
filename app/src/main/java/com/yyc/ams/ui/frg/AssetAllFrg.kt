package com.yyc.ams.ui.frg

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.StringUtils
import com.yyc.ams.R
import com.yyc.ams.adapter.AssetAdapter
import com.yyc.ams.api.UIHelper
import com.yyc.ams.base.BaseFragment
import com.yyc.ams.databinding.BNotTitleRecyclerBinding
import com.yyc.ams.ext.INVENTORY_FAIL
import com.yyc.ams.ext.init
import com.yyc.ams.ext.setNbOnItemClickListener
import com.yyc.ams.viewmodel.AssetModel
import com.yyc.ams.viewmodel.RfidModel
import com.yyc.ams.weight.recyclerview.SpaceItemDecoration
import me.hgj.jetpackmvvm.ext.nav

/**
 * @Author nike
 * @Date 2023/7/27 16:18
 * @Description 全部
 */
class AssetAllFrg: BaseFragment<AssetModel, BNotTitleRecyclerBinding>(){

    private val assetModel: AssetModel by activityViewModels()

    private val reidModel: RfidModel by activityViewModels()

    val adapter: AssetAdapter by lazy { AssetAdapter(arrayListOf()) }

    var orderId: String? = null

    var searchText: String? = null

    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            orderId = it.getString("orderId")
        }
        mDatabind.swipeRefresh.isEnabled = false
        //初始化recyclerView
        mDatabind.recyclerView.init(LinearLayoutManager(context), adapter).let {
            it.addItemDecoration(SpaceItemDecoration(ConvertUtils.dp2px(10f), ConvertUtils.dp2px(10f), true))
        }
        adapter.run {
            setNbOnItemClickListener{adapter, view, position ->
                val bean = mFilterList[position]
                UIHelper.startAssetDetailsFrg(nav(), bean)
//                LogUtils.e(bean.LabelTag)
            }
        }

        mViewModel.onRequest(orderId, -1)

        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                assetModel.assetTitle.value = getString(R.string.full_list) + "(" + adapter.data.size + ")"
            }
        })
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.listBean.observe(viewLifecycleOwner, Observer {
            adapter.setList(it)
            adapter!!.appendList(it)
            assetModel.assetTitle.value = getString(R.string.full_list) + "(" + it.size + ")"
        })
        //搜索
        assetModel.assetSerch.observe(viewLifecycleOwner, {
            searchText = it
            adapter!!.filter.filter(searchText)
        })
        //识别RFID
        assetModel.epcUploadData.observe(viewLifecycleOwner, {
            if (it == null || it.InventoryStatus == INVENTORY_FAIL)return@observe
            val index = adapter.data.indexOfFirst {bean ->
                (!StringUtils.isEmpty(bean.LabelTag) && bean.LabelTag.equals(it.LabelTag)) || bean.AssetNo.equals(it.AssetNo)
            }
            if (index != -1){
                val bean = adapter.data[index]
                bean.InventoryStatus = it.InventoryStatus
//                bean.status = if (bean.status == INVENTORY_STOCK) INVENTORY_NOT else INVENTORY_STOCK
                bean.scanStatus = it.scanStatus
                adapter.setData(index, bean)
            }
            //更新搜索页面item状态
            if (!StringUtils.isEmpty(searchText)){
                adapter!!.filter.filter(searchText)
            }
        })
    }

}