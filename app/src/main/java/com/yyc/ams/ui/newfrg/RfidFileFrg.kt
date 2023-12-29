package com.yyc.ams.ui.newfrg

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.TimeUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemLongClickListener
import com.kingja.loadsir.core.LoadService
import com.yyc.ams.R
import com.yyc.ams.adapter.RfidFileAdapter
import com.yyc.ams.api.UIHelper
import com.yyc.ams.base.BaseFragment
import com.yyc.ams.bean.AppRoomDataBase
import com.yyc.ams.bean.db.RfidFileBean
import com.yyc.ams.databinding.FRfidFileBinding
import com.yyc.ams.ext.init
import com.yyc.ams.ext.loadServiceInit
import com.yyc.ams.ext.showEditDialog
import com.yyc.ams.ext.showEmpty
import com.yyc.ams.ext.showLoading
import com.yyc.ams.ext.showToast
import com.yyc.ams.mar.eventViewModel
import com.yyc.ams.viewmodel.RfidFileModel
import com.yyc.ams.weight.PopAddRfidView
import com.yyc.ams.weight.PopRfidFileSetView
import com.yyc.ams.weight.recyclerview.SpaceItemDecoration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.hgj.jetpackmvvm.ext.nav
import java.text.SimpleDateFormat


/**
 * User: Nike
 *  2023/12/15 15:12
 *     rfid 文件列表
 */
class RfidFileFrg : BaseFragment<RfidFileModel, FRfidFileBinding>(), View.OnClickListener {

    val adapter: RfidFileAdapter by lazy { RfidFileAdapter(arrayListOf()) }

    lateinit var loadsir: LoadService<Any>

    val rfidFlieDao = AppRoomDataBase.get().getRfidFlieDao()

    var isAllSelect: Boolean = false

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.viewmodel = mViewModel
        mDatabind.click = ProxyClick()
        mDatabind.includeSel.tvSel.setOnClickListener(this)
        mDatabind.includeSel.lySend.setOnClickListener(this)
        mDatabind.includeSel.lyDelete.setOnClickListener(this)
        mDatabind.includeSel.fyDel.setOnClickListener(this)

        //状态页配置
        loadsir = loadServiceInit(mDatabind.swipeRefresh) {
            loadsir.showLoading()
            mViewModel.onRequest()
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
            setOnItemLongClickListener(object : OnItemLongClickListener {
                override fun onItemLongClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int): Boolean {
                    if (!isAllSelect) {
                        updateSelectStatus(position)
                    }
                    mDatabind.recyclerView?.scrollToPosition(position)
                    return true
                }
            })
            setOnItemClickListener { adapter, view, position ->
                if (isAllSelect) {
                    updateSelectStatus(position)
                } else {
                    val bean = data[position]
                    UIHelper.startRfidListFrg(
                        nav(),
                        bean.uid.toLong(),
                        position,
                        bean.title,
                        bean.location
                    )
                }
            }
            setOnItemClickListener(object : RfidFileAdapter.OnItemClickListener {
                override fun onItemClick(position: Int, item: RfidFileBean, view: View) {
                    val popRfidFileSetView = PopRfidFileSetView(requireContext())
                    showEditDialog(requireActivity())
                        .autoOpenSoftInput(false)
                        .offsetY(150)
                        .atView(view)
                        .asCustom(popRfidFileSetView)
                        .show()
                    popRfidFileSetView.setOnItemClickListener(object :
                        PopRfidFileSetView.OnItemClickListener {
                        override fun onItemDeleteClick() {
                            var list = arrayListOf<Int>()
                            list.add(position)
                            mViewModel.onDelete(list, adapter, requireActivity())
                        }

                        override fun onItemMailClick() {
                            var list = arrayListOf<Int>()
                            list.add(position)
                            mViewModel.onMail(list, adapter, requireActivity())
                        }
                    })
                }
            })

            mDatabind.includeSel.cbSelect.setOnCheckedChangeListener(object :
                OnCheckedChangeListener {
                override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                    adapter.data.forEachIndexed { index, rfidFileBean ->
                        if (p1) {
                            rfidFileBean.status = 1
                        } else {
                            rfidFileBean.status = 0
                        }
                    }
                    adapter.notifyDataSetChanged()
                    setSelectAllNumber()
                }
            })
        }
    }

    private fun RfidFileAdapter.updateSelectStatus(position: Int) {
        val bean = data[position]
        bean.status = if (bean.status == 1) 0 else 1
        setData(position, bean)
        onAllSelect(bean.status)
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.listBean.observe(viewLifecycleOwner, Observer {
            adapter.setList(it)
            if (it.size == 0) loadsir.showEmpty() else loadsir.showSuccess()
        })
        //更新数据
        eventViewModel.updateRfidFileData.observeInFragment(this, Observer {
            val bean = adapter.data[it.position]
            bean.number = it.number
            bean.updateDate = it.updateDate

            val fileName = it.fileName
            if (!StringUtils.isEmpty(fileName)) {
                bean.flieName = fileName.toString()
            }
            adapter.setData(it.position, bean)

            mViewModel.viewModelScope.launch(Dispatchers.IO) {
                rfidFlieDao.update(bean)
            }
        })
        mViewModel.deleteSuccess.observe(viewLifecycleOwner, Observer {
            mDatabind.includeSel.fyDel.performClick()
            if (adapter.data.size == 0){
                loadsir.showEmpty()
            }
        })
    }

    override fun lazyLoadData() {
        super.lazyLoadData()
        //设置界面 加载中
        loadsir.showLoading()
        mViewModel.onRequest()
    }

    override fun onDestroy() {
        super.onDestroy()
        mActivity.setSupportActionBar(null)
    }

    inner class ProxyClick() {

        fun toSearch() {
            UIHelper.startSearchRfidFrg(nav(), null)
        }

        fun onConfig() {
            val dialog = ConfigBottomFrg()
            dialog.show(childFragmentManager, "a")
        }

        fun addRfid() {
            mDatabind.includeSel.fyDel.performClick()
            val popAddRfidView = PopAddRfidView(requireContext())
            showEditDialog(requireActivity())
                .asCustom(popAddRfidView)
                .show()
            popAddRfidView.setOnItemClickListener(object : PopAddRfidView.OnItemClickListener {
                override fun onItemClick(text1: String, text2: String) {
                    mViewModel.viewModelScope.launch(Dispatchers.IO) {
                        val bean = RfidFileBean()
                        bean.title = text1
                        bean.location = text2
                        bean.crearDate = TimeUtils.getNowString(SimpleDateFormat("yyyy-MM-dd"))
                        val insertedId = rfidFlieDao.add(bean)
                        bean.uid = insertedId.toInt()
                        adapter.addData(0, bean)
                        withContext(Dispatchers.Main) {
                            loadsir.showSuccess()
                            mDatabind.recyclerView.scrollToPosition(0)
                            UIHelper.startRfidListFrg(nav(), insertedId, 0, bean.title, bean.location)
                        }
                    }
                }
            })
        }
    }

    //好他妈乱
    fun onAllSelect(status: Int) {
        if (status == 0) {
            if (adapter.data.all { rfidFileBean -> rfidFileBean.status == 0 }) {
                mDatabind.includeSel.layout.visibility = View.GONE
            } else {
                mDatabind.includeSel.layout.visibility = View.VISIBLE
            }
        } else {
            if (mDatabind.includeSel.layout.visibility == View.GONE) {
                mDatabind.includeSel.layout.visibility = View.VISIBLE
            }
        }
        if (mDatabind.includeSel.layout.visibility == View.GONE) {
            isAllSelect = false
        } else {
            isAllSelect = true
        }
        setSelectAllNumber()
    }

    private fun setSelectAllNumber() {
        val countStatusOne = adapter.data.count { it.status == 1 }
        mDatabind.includeSel.tvSel.setText(getString(R.string.select_all) + "${countStatusOne}" + ")")
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ly_send -> {
                var list = arrayListOf<Int>()
                adapter.data.forEachIndexed { index, rfidFileBean ->
                    if (rfidFileBean.status == 1){
                        list.add(index)
                    }
                }
                mViewModel.onMail(list, adapter, requireActivity())
            }

            R.id.ly_delete -> {
                if (adapter.data.all { rfidFileBean -> rfidFileBean.status == 0 }) {
                    showToast(getString(R.string.no_select_file))
                    return
                }
                var list = arrayListOf<Int>()
                adapter.data.forEachIndexed { index, rfidFileBean ->
                    if (rfidFileBean.status == 1){
                        list.add(index)
                    }
                }
                mViewModel.onDelete(list, adapter, requireActivity())
            }

            R.id.fy_del -> {
                isAllSelect = false
                adapter.data.forEachIndexed { index, rfidFileBean ->
                    rfidFileBean.status = 0
                }
                adapter.notifyDataSetChanged()
                mDatabind.includeSel.layout.visibility = View.GONE
            }
        }
    }

}