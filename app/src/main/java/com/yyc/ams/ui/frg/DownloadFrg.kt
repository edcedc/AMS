package com.yyc.ams.ui.frg

import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ThreadUtils.runOnUiThread
import com.kingja.loadsir.core.LoadService
import com.yyc.ams.R
import com.yyc.ams.base.BaseFragment
import com.yyc.ams.bean.AppRoomDataBase
import com.yyc.ams.databinding.FDownloadBinding
import com.yyc.ams.ext.initClose
import com.yyc.ams.ext.loadServiceInit
import com.yyc.ams.ext.showEmpty
import com.yyc.ams.ext.showLoading
import com.yyc.ams.mar.eventViewModel
import com.yyc.ams.util.CacheUtil
import com.yyc.ams.viewmodel.DownloadModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

import me.hgj.jetpackmvvm.ext.nav
import java.text.NumberFormat
import java.util.concurrent.TimeUnit


/**
 * @Author nike
 * @Date 2023/7/7 16:51
 * @Description  警报日记
 */
class DownloadFrg : BaseFragment<DownloadModel, FDownloadBinding>() {

    //界面状态管理者
    lateinit var loadsir: LoadService<Any>

    var compositeDisposable: Disposable? = null

    val numberFormat = NumberFormat.getInstance()

    var number: Int = 1

    val roNo = CacheUtil.getUser()!!.RoNo

    val companyID = CacheUtil.getCompanyID()

    override fun initView(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        mDatabind.viewmodel = mViewModel
        mDatabind.includeToolbar.toolbar.initClose(getString(R.string.download)) {
            nav().navigateUp()
        }
        //状态页配置
        loadsir = loadServiceInit(mDatabind.layout) {
            //点击重试时触发的操作
            loadsir.showLoading()
            mViewModel.onRequest()
        }

        numberFormat.setMaximumFractionDigits(2)

//        mDatabind.progressView.setOnProgressChangeListener{
//            mDatabind.progressView.labelText = "Download ${it.toInt()}%"
//        }
        mDatabind.circularProgressBar.onProgressChangeListener = { progress ->
            mViewModel.progressText.set("${progress.toInt()}%")
        }

        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                compositeDisposable?.dispose()
                mActivity.setSupportActionBar(null)
            }
        })
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.listBean.observe(viewLifecycleOwner, Observer {
            if (it.isSuccess) {
                eventViewModel.mainListEvent.postValue(true)
                if (it.pageSize != 0) {
                    val assetDao = AppRoomDataBase.get().getAssetDao()
                    mViewModel.viewModelScope.launch {
                        assetDao.findAll(roNo, companyID)
                            .flowOn(Dispatchers.IO)
                            .collect { data ->
                                number = data.size
                            }
                    }

                    compositeDisposable = Observable.interval(1, TimeUnit.SECONDS)
                        .observeOn(Schedulers.io()) // 切换到IO线程执行操作
                        .subscribe { tick ->
                            var result = numberFormat.format(number.toFloat() / it.pageSize.toFloat() * 100).toFloat()
//                            mDatabind.progressView.progress = result

                            mDatabind.circularProgressBar.progress = result
                            runOnUiThread {
                                mDatabind.circularProgressBar.setProgressWithAnimation(result, 1000)
                            }
                            LogUtils.i(number, result)

                            if (number >= it.pageSize){
                                compositeDisposable?.dispose()
                                runOnUiThread {
                                    Handler().postDelayed({
                                        mDatabind.tvText.text = "Sync Success"
                                    }, 1200) // 延迟1秒（即1000毫秒）
                                }
                            }
                        }
                }
            } else {
                loadsir.showEmpty()
            }
        })
    }

    override fun lazyLoadData() {
        //设置界面 加载中
        mViewModel.onRequest()
    }


}