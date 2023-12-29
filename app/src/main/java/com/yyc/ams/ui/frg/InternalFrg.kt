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
import com.yyc.ams.databinding.FInternalBinding
import com.yyc.ams.ext.INTERNAL_ARCHIVES_TYPE
import com.yyc.ams.ext.INTERNAL_BOOK_TYPE
import com.yyc.ams.ext.bindViewPager2
import com.yyc.ams.ext.init
import com.yyc.ams.ext.initClose
import com.yyc.ams.viewmodel.InternalModel
import me.hgj.jetpackmvvm.ext.nav

/**
 * @Author nike
 * @Date 2023/8/8 14:46
 * @Description 内部借出
 */
class InternalFrg: BaseFragment<InternalModel, FInternalBinding>() {

    private val internalModel: InternalModel by activityViewModels()

    //fragment集合
    var fragments: ArrayList<Fragment> = arrayListOf()

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.includeToolbar.toolbar.initClose(getString(R.string.internal_borrow)) {nav().navigateUp()}

        mDatabind.viewmodel = mViewModel
        mDatabind.click = ProxyClick()
        mViewModel.state.set(getString(R.string.qRCode))
        mViewModel.save.set(getString(R.string.submit))

        //初始化viewpager2
        var bundle = Bundle()

        val bookFrg = InternalBookFrg()
        bookFrg.arguments = bundle
        fragments.add(bookFrg)

        val archivesFrg = InternalArchivesFrg()
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
                internalModel.searchText.value = p0.toString()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                internalModel.submitType.value = -1
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
            UIHelper.startZxingAct(if (mDatabind.includeViewpager.viewPager.currentItem == 0) INTERNAL_BOOK_TYPE else INTERNAL_ARCHIVES_TYPE)
        }

        fun save(){
            internalModel.submitType.value = if (mDatabind.includeViewpager.viewPager.currentItem == 0) INTERNAL_BOOK_TYPE else INTERNAL_ARCHIVES_TYPE
        }

    }

}