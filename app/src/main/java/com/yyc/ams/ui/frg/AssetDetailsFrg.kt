package com.yyc.ams.ui.frg

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.yyc.ams.R
import com.yyc.ams.base.BaseFragment
import com.yyc.ams.bean.AppRoomDataBase
import com.yyc.ams.bean.db.AssetBean
import com.yyc.ams.databinding.FAssetDetailsBinding
import com.yyc.ams.ext.bindViewPager2
import com.yyc.ams.ext.init
import com.yyc.ams.ext.initClose
import com.yyc.ams.viewmodel.AssetModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.hgj.jetpackmvvm.ext.nav

/**
 * @Author nike
 * @Date 2023/8/2 12:00
 * @Description rfid详情
 */
class AssetDetailsFrg: BaseFragment<AssetModel, FAssetDetailsBinding>() {

    var fragments: ArrayList<Fragment> = arrayListOf()

    var bean: String? = null

    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            var data = Gson().fromJson(it.getString("bean") , AssetBean::class.java)
            val assetDao = AppRoomDataBase.get().getAssetDao()
            mViewModel.viewModelScope.launch(Dispatchers.IO) {
                val assetBean = assetDao.findAssetId(data.AssetNo, data.OrderRoNo, data.RoNo, data.companyId)
                bean = Gson().toJson(assetBean)

                withContext(Dispatchers.Main) {
                    mDatabind.includeToolbar.toolbar.initClose(data.AssetNo) {nav().navigateUp()}

                    //初始化viewpager2
                    var bundle = Bundle()
                    bundle.putString("bean", bean)

                    val assetSearchFrg = AssetSearchFrg()
                    assetSearchFrg.arguments = bundle
                    fragments.add(assetSearchFrg)

                    val assetTextFrg = AssetTextFrg()
                    assetTextFrg.arguments = bundle
                    fragments.add(assetTextFrg)

                    val assetUpload = AssetUploadFrg()
                    assetUpload.arguments = bundle
                    fragments.add(assetUpload)

                    mDatabind.includeViewpager.viewPager.init(this@AssetDetailsFrg, fragments)

                    var mTitles =
                        arrayListOf(
                            getString(R.string.search_assets),
                            getString(R.string.detailed),
                            getString(R.string.remarks)
                        )
                    mDatabind.includeViewpager.magicIndicator.bindViewPager2(mDatabind.includeViewpager.viewPager, mTitles)
                    mDatabind.includeViewpager.viewPager.offscreenPageLimit = mTitles.size

                    mDatabind.includeViewpager.viewPager.setCurrentItem(1, false)
                }
            }

        }
    }
}