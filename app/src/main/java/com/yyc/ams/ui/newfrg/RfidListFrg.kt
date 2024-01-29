package com.yyc.ams.ui.newfrg

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.TimeUtils
import com.kingja.loadsir.core.LoadService
import com.yyc.ams.R
import com.yyc.ams.adapter.RfidListAdapter
import com.yyc.ams.api.UIHelper
import com.yyc.ams.base.BaseFragment
import com.yyc.ams.bean.AppRoomDataBase
import com.yyc.ams.bean.DataBean
import com.yyc.ams.bean.RfidStateBean
import com.yyc.ams.bean.db.RfidBean
import com.yyc.ams.databinding.FRfidListBinding
import com.yyc.ams.ext.ASSET_TYPE
import com.yyc.ams.ext.SCAN_STATUS_QRCODE
import com.yyc.ams.ext.ZKING_ADD_TYPE
import com.yyc.ams.ext.ZKING_TYPE
import com.yyc.ams.ext.init
import com.yyc.ams.ext.loadServiceInit
import com.yyc.ams.ext.setNbOnItemClickListener
import com.yyc.ams.ext.showLoading
import com.yyc.ams.ext.showToast
import com.yyc.ams.mar.eventViewModel
import com.yyc.ams.util.MusicUtils
import com.yyc.ams.viewmodel.AssetModel
import com.yyc.ams.viewmodel.RfidListModel
import com.yyc.ams.viewmodel.RfidModel
import com.yyc.ams.weight.recyclerview.SpaceItemDecoration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.hgj.jetpackmvvm.ext.nav
import java.text.SimpleDateFormat

/**
 * User: Nike
 *  2023/12/15 15:12
 *     rfid列表
 */
class RfidListFrg() : BaseFragment<RfidListModel, FRfidListBinding>(){

    val adapter: RfidListAdapter by lazy { RfidListAdapter(arrayListOf()) }

    lateinit var loadsir: LoadService<Any>

    var parentId: Long = 0

    var parentIdString: String = ""

    var title: String = ""

    var location: String = ""

    val assetModel: AssetModel by activityViewModels()

    val rfidModel: RfidModel by activityViewModels()

    val rfidDao = AppRoomDataBase.get().getRfidDao()

    var position: Int = 0

    var positionUid: Int = 0

    var searchText: String? = null

    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            parentId = it.getLong("parentId")
            position = it.getInt("position")
            title = it.getString("title").toString()
            location = it.getString("location").toString()
            parentIdString = String.format("%06d", parentId)
        }
        mDatabind.viewmodel = mViewModel
        mDatabind.click = ProxyClick()
        mViewModel.parentId.set(parentIdString)
        mViewModel.backTitle.set(getString(R.string.back_list))

        //状态页配置
        loadsir = loadServiceInit(mDatabind.swipeRefresh) {
            loadsir.showLoading()
            mViewModel.onRequest(parentId)
        }
        mDatabind.swipeRefresh.isEnabled = false
        //初始化recyclerView
        mDatabind.recyclerView.init(LinearLayoutManager(context), adapter).let {
            it.addItemDecoration(
                SpaceItemDecoration(
                    ConvertUtils.dp2px(0f),
                    ConvertUtils.dp2px(10f),
                    true
                )
            )
        }
        adapter.run {
            setNbOnItemClickListener { adapter, view, position ->
                val bean = mFilterList[position]
                UIHelper.startSearchRfidFrg(nav(), bean.epc)
            }
        }

        lifecycle.addObserver(object : DefaultLifecycleObserver {

            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                rfidModel.isOpen.value = false
                assetModel.epcData.value = null

                var bean = DataBean()
                bean.position = position
                bean.updateDate = TimeUtils.getNowString(SimpleDateFormat("yyyy-MM-dd"))
                bean.number = adapter.data.size
                eventViewModel.updateRfidFileData.value = bean
            }

            override fun onPause(owner: LifecycleOwner) {
                super.onPause(owner)
                isOpenRfid(false)
                assetModel.epcData.value = null
            }
        })

        mDatabind.etText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchText = p0.toString()
                adapter!!.filter.filter(searchText)
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.listBean.observe(viewLifecycleOwner, Observer {
            adapter.setList(it)
            adapter!!.appendList(it)
            mViewModel.backTitle.set(getString(R.string.back_list) + "（${it.size}）")
            positionUid = adapter.data.size
            if (it.size == 0) {
                isOpenRfid(true)
            }
            loadsir.showSuccess()
        })
        assetModel.epcData.observe(viewLifecycleOwner, {
            if (it == null) return@observe

            mViewModel.viewModelScope.launch(Dispatchers.IO) {
                //判断重复
                var rfidBean = rfidDao.findParentIdBean(parentId, it.tagId!!)
                if (rfidBean == null){
                    rfidBean = RfidBean()
                    rfidBean.epc = it.tagId
                    rfidBean.crearDate = TimeUtils.getNowString()
                    rfidBean.parentId = parentId
                    positionUid += 1
                    rfidBean.position = positionUid
                    val uId = rfidDao.add(rfidBean)
                    withContext(Dispatchers.Main) {
                        adapter.addData(rfidBean)
                        mViewModel.backTitle.set(getString(R.string.back_list) + "（${adapter.data.size}）")

                        if (!StringUtils.isEmpty(searchText)){
                            adapter!!.filter.filter(searchText)
                        }

                        Handler().postDelayed({
                            if (mDatabind.recyclerView != null){
                                mDatabind.recyclerView?.scrollToPosition(adapter.data.size - 1)
                            }
                        }, 300)
                    }
                    MusicUtils.play()
                }
            }
        })
        mViewModel.isLoaddingBean.observe(viewLifecycleOwner, Observer{
            if (it.isLoading){
                showLoading(getString(R.string.text14))
            }else{
                Handler(Looper.getMainLooper()).postDelayed({
                    dismissLoading()
                }, 1000)
            }
        })

        /*eventViewModel.zkingType.observeInFragment(this, Observer {
            if (it.type == ZKING_TYPE){
                var index = adapter.data.indexOfFirst { bean ->
                    (bean.epc.equals(it.text, ignoreCase = true) == true)
                }
                if (index != -1){
                    val bean = adapter.data[index]
                    UIHelper.startSearchRfidFrg(nav(), bean.epc)
                }else{
                    showToast(requireActivity().getString(R.string.search_erroe))
                }
            }
        })*/

        eventViewModel.zkingType.observeInFragment(this, Observer {
            if (it.type == ZKING_ADD_TYPE){
                var index = adapter.data.indexOfFirst { bean ->
                    (bean.epc.equals(it.text, ignoreCase = true) == true)
                }
                if (index == -1){
                    assetModel.epcData.value = RfidStateBean(
                        tagId = it.text,
                        scanStatus = SCAN_STATUS_QRCODE,
                        rssi = "0"
                    )
                }else{
                    showToast(requireActivity().getString(R.string.tag_exists))
                }
            }
        })
    }

    override fun lazyLoadData() {
        super.lazyLoadData()
        //设置界面 加载中
        loadsir.showLoading()
        mViewModel.onRequest(parentId)
    }

    override fun onDestroy() {
        super.onDestroy()
        mActivity.setSupportActionBar(null)
    }

    inner class ProxyClick() {

        fun toRfid(){
            isOpenRfid(if (mDatabind.fbRfid.labelText.equals(getString(R.string.rfid_off)))false else true)
            mDatabind.fbMenu.close(false)
        }

        fun toSave(){
            isOpenRfid(false)
            mViewModel.setSave(adapter.data, parentIdString, position, location, title)
            mDatabind.fbMenu.close(true)
        }

        fun onClose(){
            nav().navigateUp()
        }

        fun onZking(){
            UIHelper.startZxingAct(ZKING_TYPE)
        }

        fun onAdd(){
            UIHelper.startZxingAct(ZKING_ADD_TYPE)
        }

    }

    fun isOpenRfid(isOpen: Boolean){
        if (isOpen){
            MusicUtils.init(activity)
            rfidModel.isOpen.postValue(true)
            mDatabind.fbRfid.labelText = getString(R.string.rfid_off)
        }else{
            MusicUtils.clear()
            rfidModel.isOpen.postValue(false)
            mDatabind.fbRfid.labelText = getString(R.string.rfid_on)
        }
    }

}