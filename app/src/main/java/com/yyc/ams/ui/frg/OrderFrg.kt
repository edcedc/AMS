package com.yyc.ams.ui.frg

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ConvertUtils
import com.yyc.ams.R
import com.yyc.ams.adapter.OrderAdapter
import com.yyc.ams.ext.init
import com.yyc.ams.ext.loadServiceInit
import com.yyc.ams.ext.showLoading
import com.yyc.ams.viewmodel.OrderModel
import com.yyc.ams.weight.recyclerview.SpaceItemDecoration
import com.google.android.material.navigation.NavigationView
import com.kingja.loadsir.core.LoadService
import com.yyc.ams.api.UIHelper
import com.yyc.ams.base.BaseFragment
import com.yyc.ams.bean.db.OrderBean
import com.yyc.ams.databinding.FOrderBinding
import com.yyc.ams.ext.ORDER_TYPE
import com.yyc.ams.ext.setNbOnItemClickListener
import com.yyc.ams.mar.eventViewModel
import com.yyc.ams.util.CacheUtil
import com.yyc.ams.viewmodel.RfidModel
import me.hgj.jetpackmvvm.ext.nav

/**
 * @Author nike
 * @Date 2023/7/7 11:59
 * @Description
 */
class OrderFrg : BaseFragment<OrderModel, FOrderBinding>(), NavigationView.OnNavigationItemSelectedListener {

    val adapter: OrderAdapter by lazy { OrderAdapter(arrayListOf()) }

    private val rfidModel: RfidModel by activityViewModels()

    lateinit var loadsir: LoadService<Any>

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.viewmodel = mViewModel
        mDatabind.navView.setNavigationItemSelectedListener(this)
//        hideSoftKeyboard(activity)



        //状态页配置
        loadsir = loadServiceInit(mDatabind.swipeRefresh) {

        }


    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.listBean.observe(viewLifecycleOwner, Observer {
            loadsir.showSuccess()
            adapter.setList(it)
            adapter!!.appendList(it)
        })
        eventViewModel.mainListEvent.observeInFragment(this, Observer {
            mViewModel.onRequest()
        })
        eventViewModel.zkingType.observeInFragment(this, Observer {
            if (it.type == ORDER_TYPE){
                 val filteredList = adapter.data.filterIndexed()  { index, bean ->
                     it.text.equals(bean.stocktakeno)
                 }
                if (filteredList.size != 0){
                    UIHelper.startAssetFrg(nav(), filteredList.get(0).stocktakeno)
                }
            }
        })
    }

    override fun lazyLoadData() {
        super.lazyLoadData()
        //设置界面 加载中
        loadsir.showLoading()

    }

    override fun onDestroy() {
        super.onDestroy()
        mActivity.setSupportActionBar(null)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        mDatabind.drawerLayout.postDelayed({
            when (item.itemId) {
                R.id.nav_load -> {
                    UIHelper.startDownloadFrg(nav())
                }

                R.id.nav_upload -> {
                    UIHelper.starUploadFrg(nav())
                }

               /* R.id.nav_external_borrow -> {
                    UIHelper.starExternalBaorrowFrg(nav())
                }

                R.id.nav_internal_borrow -> {
                    UIHelper.starInternalBaorrowFrg(nav())
                }

                R.id.nav_disposal -> {
                    UIHelper.starDisposalFrg(nav())
                }
*/
                R.id.nav_login -> {
                    val user = CacheUtil.getUser()
                    user?.Password = null
                    CacheUtil.setUser(user)
                    UIHelper.startLoginAct()
                    ActivityUtils.finishAllActivities()
                }
            }
            mDatabind.drawerLayout.closeDrawer(GravityCompat.END)
        }, 300)
        return true
    }

    //region  抽屉布局
    fun onOpenDrawer() {
        if (!mDatabind.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            mDatabind.drawerLayout.openDrawer(GravityCompat.END)
        }
    }

}