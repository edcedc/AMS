package com.yyc.ams.ui.frg

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.yyc.ams.R
import com.yyc.ams.api.UIHelper
import com.yyc.ams.base.BaseFragment
import com.yyc.ams.databinding.FDisposal2Binding
import com.yyc.ams.ext.DISPOSAL_ARCHIVES_TYPE
import com.yyc.ams.ext.DISPOSAL_BOOK_TYPE
import com.yyc.ams.ext.bindViewPager2
import com.yyc.ams.ext.init
import com.yyc.ams.ext.initClose
import com.yyc.ams.viewmodel.DisposalModel
import me.hgj.jetpackmvvm.ext.nav

/**
 * @Author nike
 * @Date 2023/7/27 11:43
 * @Description RFID 列表
 */
class DisposalFrg2: BaseFragment<DisposalModel, FDisposal2Binding>() {

    private val disposalModel: DisposalModel by activityViewModels()

    var orderId: String? = ""

    var title: String? = ""

    //fragment集合
    var fragments: ArrayList<Fragment> = arrayListOf()

    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            orderId = it.getString("orderId")
            title = it.getString("title")
            mDatabind.includeToolbar.toolbar.initClose(title!!) {nav().navigateUp()}
        }
        mDatabind.viewmodel = mViewModel
        mDatabind.click = ProxyClick()
        mViewModel.state.set(getString(R.string.qRCode))
        mViewModel.save.set(getString(R.string.submit))

        //初始化viewpager2
        var bundle = Bundle()
        bundle.putString("orderId", orderId)
        bundle.putString("title", title)

        val bookFrg = DisposalBookFrg()
        bookFrg.arguments = bundle
        fragments.add(bookFrg)

        val archivesFrg = DisposalArchivesFrg()
        archivesFrg.arguments = bundle
        fragments.add(archivesFrg)

        mDatabind.includeViewpager.viewPager.init(this, fragments)
        var mTitles =
            arrayListOf(
            getString(R.string.book),
            getString(R.string.archives),
        )

        mDatabind.includeViewpager.magicIndicator.bindViewPager2(mDatabind.includeViewpager.viewPager, mTitles)
        mDatabind.includeViewpager.viewPager.offscreenPageLimit = mTitles.size


        mDatabind.includeSearch.ivQr.visibility = View.GONE

        mDatabind.includeSearch.etText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                disposalModel.searchText.value = p0.toString()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        lifecycle.addObserver(object : DefaultLifecycleObserver {

            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                disposalModel.submitType.value = -1
            }
        })
    }

    override fun lazyLoadData() {
        //设置界面 加载中
//        loadsir.showLoading()
    }

    override fun createObserver() {
        super.createObserver()

    }

    inner class ProxyClick() {

        fun state(){
            UIHelper.startZxingAct(if (mDatabind.includeViewpager.viewPager.currentItem == 0) DISPOSAL_BOOK_TYPE else DISPOSAL_ARCHIVES_TYPE)
        }

        fun save(){
            disposalModel.submitType.value = if (mDatabind.includeViewpager.viewPager.currentItem == 0) DISPOSAL_BOOK_TYPE else DISPOSAL_ARCHIVES_TYPE
        }

    }

}