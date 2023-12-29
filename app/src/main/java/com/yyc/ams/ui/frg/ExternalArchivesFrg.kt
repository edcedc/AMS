package com.yyc.ams.ui.frg

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.TimeUtils
import com.kingja.loadsir.core.LoadService
import com.yyc.ams.R
import com.yyc.ams.adapter.DisposalListAdapter
import com.yyc.ams.api.UIHelper
import com.yyc.ams.base.BaseFragment
import com.yyc.ams.bean.DataBean
import com.yyc.ams.databinding.BNotTitleRecyclerBinding
import com.yyc.ams.ext.EXTERNAL_ARCHIVES_TYPE
import com.yyc.ams.ext.RFID_ARCHIVES
import com.yyc.ams.ext.init
import com.yyc.ams.ext.loadListData
import com.yyc.ams.ext.loadServiceInit
import com.yyc.ams.ext.setNbOnItemClickListener
import com.yyc.ams.ext.showLoading
import com.yyc.ams.ext.showToast
import com.yyc.ams.mar.eventViewModel
import com.yyc.ams.util.CacheUtil
import com.yyc.ams.viewmodel.ExternalModel
import com.yyc.ams.weight.recyclerview.SpaceItemDecoration
import me.hgj.jetpackmvvm.ext.nav
import org.json.JSONArray
import org.json.JSONObject

/**
 * @Author nike
 * @Date 2023/9/8 10:57
 * @Description 图书
 */
class ExternalArchivesFrg: BaseFragment<ExternalModel, BNotTitleRecyclerBinding>() {

    private val externalModel: ExternalModel by activityViewModels()

    val adapter: DisposalListAdapter by lazy { DisposalListAdapter(
        arrayListOf(),
        EXTERNAL_ARCHIVES_TYPE
    ) }

    var orderId: String? = null

    var title: String? = null

    var searchText: String? = null

    var isVisibility: Boolean = false

    //界面状态管理者
    lateinit var loadsir: LoadService<Any>

    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            orderId = it.getString("orderId")
            title = it.getString("title")
        }

        //初始化recyclerView
        mDatabind.recyclerView.init(LinearLayoutManager(context), adapter).let {
            it.addItemDecoration(SpaceItemDecoration(ConvertUtils.dp2px(10f), ConvertUtils.dp2px(10f), true))
        }
        adapter.run {
            setNbOnItemClickListener{adapter, view, position ->
                val bean = adapter.data[position] as DataBean
                UIHelper.startDisposalDetailsFrg(nav(), bean, title)
            }
        }

        //状态页配置
        loadsir = loadServiceInit(mDatabind.swipeRefresh) {
            //点击重试时触发的操作
            loadsir.showLoading()
            mViewModel.onRequestDetails(orderId, RFID_ARCHIVES)
        }

        //初始化 SwipeRefreshLayout  刷新
        mDatabind.swipeRefresh.init {
            mViewModel.onRequestDetails(orderId, RFID_ARCHIVES)
        }

        lifecycle.addObserver(object : DefaultLifecycleObserver {

            override fun onPause(owner: LifecycleOwner) {
                super.onPause(owner)
                isVisibility = false
            }

            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                isVisibility = true
            }
        })
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.listBooKArchivesData.observe(viewLifecycleOwner, {
            loadListData(it, adapter, loadsir, mDatabind.recyclerView, mDatabind.swipeRefresh, it.pageSize)
            adapter.appendList(it.listData)
        })
        //搜索
        externalModel.searchText.observe(viewLifecycleOwner, {
            if (isVisibility){
                searchText = it
                adapter!!.filter.filter(searchText)
            }
        })
        //扫码
        eventViewModel.zkingType.observeInFragment(this, Observer {
            if (it.type == EXTERNAL_ARCHIVES_TYPE) {
                val indexList = mutableListOf<Int>()
                adapter.data.filterIndexed { index, bean ->
                    val shouldBeIncluded = (!StringUtils.isEmpty(bean.LabelTag) && bean.LabelTag.equals(it.text)) || bean.AssetNo.equals(it.text)
                    // 将满足条件的索引添加到 indexList
                    if (shouldBeIncluded) {
                        indexList.add(index)
                    }
                    shouldBeIncluded
                }
                if (indexList.size == 0) {
                    showToast(getString(R.string.text5))
                    return@Observer
                }
                //更新item状态
                indexList.forEachIndexed() { index, i ->
                    val bean = adapter.data[i]
                    bean.type = if (bean.type == 1) 0 else 1
                    adapter.setData(i, bean)
                }
            }
        })
        //提交
        externalModel.submitType.observe(viewLifecycleOwner, {
            if (it == EXTERNAL_ARCHIVES_TYPE){
                if (adapter.data.size == 0){
                    showToast(getString(R.string.text6))
                    return@observe
                }
                val predicate = adapter.data.all { it.type == 0}
                if (predicate){
                    showToast(getString(R.string.text5))
                    return@observe
                }
                var ja = JSONArray()
                adapter.data.filterIndexed { index, bean ->
                    val shouldBeIncluded = (bean.type == 1)
                    if (shouldBeIncluded){
                        var jo = JSONObject()
                        jo.put("RoNo", bean.RoNo)
                        jo.put("userRoNo", CacheUtil.getUser()?.RoNo)
                        jo.put("scandate", TimeUtils.getNowString())
                        ja.put(jo)
                    }
                    shouldBeIncluded
                }
                mViewModel.UpdateDisposalAPP(orderId, ja)
            }
        })

        mViewModel.listClearArray.observe(viewLifecycleOwner, {
            for (i in 0 until it.length()) {
                val obj = it.optJSONObject(i)
                val index= adapter.data.indexOfFirst  { it.RoNo.equals(obj.optString("RoNo")) }
                if (index != -1)adapter.removeAt(index)
            }
            if (!StringUtils.isEmpty(searchText)){
                adapter!!.filter.filter(searchText)
            }
        })
    }

    override fun lazyLoadData() {
        //设置界面 加载中
        loadsir.showLoading()
        mViewModel.onRequestDetails(orderId, RFID_ARCHIVES)
    }
}