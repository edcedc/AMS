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
import com.yyc.ams.ext.INVENTORY_STOCK
import com.yyc.ams.ext.init
import com.yyc.ams.ext.setNbOnItemClickListener
import com.yyc.ams.viewmodel.AssetModel
import com.yyc.ams.weight.recyclerview.SpaceItemDecoration
import me.hgj.jetpackmvvm.ext.nav

/**
 * @Author nike
 * @Date 2023/7/27 16:18
 * @Description 在库
 */
class AssetInStockFrg: BaseFragment<AssetModel, BNotTitleRecyclerBinding>(){

    private val assetModel: AssetModel by activityViewModels()

    val adapter: AssetAdapter by lazy { AssetAdapter(arrayListOf()) }

    var orderId: String? = null

    var searchText: String? = null

    var fmIsVisible = false

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

        mViewModel.onRequest(orderId, INVENTORY_STOCK)

        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                fmIsVisible = true
                assetModel.assetTitle.value = getString(R.string.found) + "(" + adapter.data.size + ")"
            }

            override fun onPause(owner: LifecycleOwner) {
                super.onPause(owner)
                fmIsVisible = false
            }
        })
    }


    override fun createObserver() {
        super.createObserver()
        mViewModel.listBean.observe(viewLifecycleOwner, Observer {
            adapter.setList(it)
            adapter!!.appendList(it)
        })
        //搜索
        assetModel.assetSerch.observe(viewLifecycleOwner, {
            searchText = it
            adapter!!.filter.filter(searchText)
        })
        //识别 / 更新 RFID
        assetModel.epcUploadData.observe(viewLifecycleOwner, {
            if (it == null || it.InventoryStatus == INVENTORY_FAIL)return@observe
            adapter.data.filterIndexed { index, bean ->
                val shouldBeIncluded = (!StringUtils.isEmpty(bean.LabelTag) && bean.LabelTag.equals(it.LabelTag)) || bean.AssetNo.equals(it.AssetNo)
                if (shouldBeIncluded) {
                    return@observe
                }
                shouldBeIncluded
            }
            adapter.addData(0, it)
            //过滤重复在库的操作 /增加在库
            /*if (it.InventoryStatus == INVENTORY_STOCK){
                adapter.data.filterIndexed { index, bean ->
                    val shouldBeIncluded = (!StringUtils.isEmpty(bean.LabelTag) && bean.LabelTag.equals(it.LabelTag)) || bean.AssetNo.equals(it.AssetNo)
                    if (shouldBeIncluded) {
                        return@observe
                    }
                    shouldBeIncluded
                }
                adapter.addData(0, it)
            }else{
                //在库变不在库
                val indexList = mutableListOf<Int>()
                adapter.data.filterIndexed { index, bean ->
                    val shouldBeIncluded = (!StringUtils.isEmpty(bean.LabelTag) && bean.LabelTag.equals(it.LabelTag)) || bean.AssetNo.equals(it.AssetNo)
                    // 将满足条件的索引添加到 indexList
                    if (shouldBeIncluded) {
                        indexList.add(index)
                    }
                    shouldBeIncluded
                }
                //更新item状态
                indexList.forEachIndexed() { index, i ->
                    adapter.removeAt(i)
                }
            }*/
//            mDatabind.recyclerView.scrollToPosition(0)
            //更新搜索页面item状态
            if (!StringUtils.isEmpty(searchText)){
                adapter!!.filter.filter(searchText)
            }
            if (fmIsVisible)assetModel.assetTitle.value = getString(R.string.found) + "(" + adapter.data.size + ")"
        })
    }

}