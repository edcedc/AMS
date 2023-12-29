package com.yyc.ams.ui.frg

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ConvertUtils
import com.yyc.ams.adapter.AssetDetailsAdapter
import com.yyc.ams.base.BaseFragment
import com.yyc.ams.databinding.BTitleRecyclerBinding
import com.yyc.ams.ext.init
import com.yyc.ams.ext.initClose
import com.yyc.ams.viewmodel.DisposalModel
import com.yyc.ams.weight.recyclerview.SpaceItemDecoration
import me.hgj.jetpackmvvm.ext.nav
import org.json.JSONArray

/**
 * @Author nike
 * @Date 2023/9/11 11:20
 * @Description 注销订单详情
 */
class DisposalDetailsFrg : BaseFragment<DisposalModel, BTitleRecyclerBinding>() {

    var jsonArray = JSONArray()

    val adapter by lazy { activity?.let { AssetDetailsAdapter(it, jsonArray) } }

    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
           val bean = it.getString("bean")
           val title = it.getString("title")
            mDatabind.includeToolbar.toolbar.initClose(title!!) {nav().navigateUp()}
            mViewModel.onRequestText(title, bean)
        }
        mDatabind.swipeRefresh.isEnabled = false
        //初始化recyclerView
        mDatabind.recyclerView.init(LinearLayoutManager(context), adapter!!).let {
            it.addItemDecoration(SpaceItemDecoration(ConvertUtils.dp2px(0f), ConvertUtils.dp2px(0f), true))
        }
        adapter.run {

        }
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.listJsonArray.observe(viewLifecycleOwner, {
            var list = it as JSONArray
            for (i in list.length() - 1 downTo 0) {
                val obj = list.optJSONObject(i)
                jsonArray.put(obj)
            }
            adapter!!.notifyDataSetChanged()
        })
    }

}