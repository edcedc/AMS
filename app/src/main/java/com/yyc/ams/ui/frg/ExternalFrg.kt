package com.yyc.ams.ui.frg

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ConvertUtils
import com.kingja.loadsir.core.LoadService
import com.yyc.ams.R
import com.yyc.ams.adapter.ExternalAdapter
import com.yyc.ams.api.UIHelper
import com.yyc.ams.base.BaseFragment
import com.yyc.ams.bean.DataBean
import com.yyc.ams.databinding.FDisposalBinding
import com.yyc.ams.ext.EXTERNAL_BAORROW_TYPE
import com.yyc.ams.ext.init
import com.yyc.ams.ext.initClose
import com.yyc.ams.ext.loadListData
import com.yyc.ams.ext.loadServiceInit
import com.yyc.ams.ext.setNbOnItemClickListener
import com.yyc.ams.ext.showLoading
import com.yyc.ams.mar.eventViewModel
import com.yyc.ams.viewmodel.ExternalModel
import com.yyc.ams.weight.recyclerview.SpaceItemDecoration
import me.hgj.jetpackmvvm.ext.nav

/**
 * @Author nike
 * @Date 2023/8/8 14:46
 * @Description 外部借出
 */
class ExternalFrg: BaseFragment<ExternalModel, FDisposalBinding>() {

    val adapter: ExternalAdapter by lazy { ExternalAdapter(arrayListOf()) }

    //界面状态管理者
    lateinit var loadsir: LoadService<Any>

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.includeToolbar.toolbar.initClose(getString(R.string.external_borrow)) {nav().navigateUp()}

        //初始化recyclerView
        mDatabind.recyclerView.init(LinearLayoutManager(context), adapter).let {
            it.addItemDecoration(SpaceItemDecoration(ConvertUtils.dp2px(10f), ConvertUtils.dp2px(10f), true))
        }

        adapter.run {
            setNbOnItemClickListener{adapter, view, position ->
                val bean = adapter.data[position] as DataBean
                UIHelper.starExternalFrg2(nav(), bean.OrderNo, bean.Title!!)
            }
        }

        //状态页配置
        loadsir = loadServiceInit(mDatabind.swipeRefresh) {
            //点击重试时触发的操作
            loadsir.showLoading()
            mViewModel.onRequest()
        }

        //初始化 SwipeRefreshLayout  刷新
        mDatabind.swipeRefresh.init {
            mViewModel.onRequest()
        }

        mDatabind.includeSearch.etText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                adapter!!.filter.filter(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        mDatabind.includeSearch.ivQr.setOnClickListener {
            UIHelper.startZxingAct(EXTERNAL_BAORROW_TYPE)
        }
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.listData.observe(viewLifecycleOwner, {
            loadListData(it, adapter, loadsir, mDatabind.recyclerView, mDatabind.swipeRefresh, it.pageSize)
            adapter.appendList(it.listData)
        })
        //扫码
        eventViewModel.zkingType.observeInFragment(this, Observer {
            if (it.type == EXTERNAL_BAORROW_TYPE){
                val filteredList = adapter.data.filterIndexed()  { index, bean ->
                    it.text.equals(bean.OrderNo)
                }
                if (filteredList.size != 0){
                    UIHelper.starExternalFrg2(nav(), filteredList.get(0).OrderNo, filteredList.get(0).Title!!)
                }
            }
        })
    }

    override fun lazyLoadData() {
        //设置界面 加载中
        loadsir.showLoading()
        mViewModel.onRequest()
    }

}