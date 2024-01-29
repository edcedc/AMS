package com.yyc.ams.adapter

import android.icu.text.Transliterator.Position
import android.widget.Filter
import android.widget.Filterable
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.yyc.ams.R
import com.yyc.ams.bean.DataBean
import com.yyc.ams.bean.db.AssetBean
import com.yyc.ams.bean.db.RfidBean
import com.yyc.ams.ext.setAdapterAnimation
import com.yyc.ams.util.SettingUtil

/**
 * User: Nike
 *  2023/12/15 16:12
 */
class RfidListAdapter (data: ArrayList<RfidBean>) :
    BaseQuickAdapter<RfidBean, BaseViewHolder>(R.layout.i_rfid_list, data), Filterable {

    init {
        setAdapterAnimation(SettingUtil.getListMode())
    }

    override fun convert(holder: BaseViewHolder, item: RfidBean) {
        val bean = mFilterList[holder.layoutPosition]
        holder.setText(R.id.tv_id, String.format("%06d", bean.position))
        holder.setText(R.id.tv_epc, bean.epc)
        holder.setText(R.id.tv_date, bean.crearDate)
    }

    var mFilterList = ArrayList<RfidBean>()

    fun appendList(list: List<RfidBean>) {
        data = list as MutableList<RfidBean>
        //这里需要初始化filterList
        mFilterList = list as ArrayList<RfidBean>
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            //执行过滤操作
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    //没有过滤的内容，则使用源数据
                    mFilterList = data as ArrayList<RfidBean>
                } else {
                    val filteredList: MutableList<RfidBean> = ArrayList()
                    for (i in data.indices) {
                        val bean = data[i]
                        val assetNo = bean.epc
                        if (assetNo.contains(charString)) {
                            filteredList.add(bean)
                        }
                    }
                    mFilterList = filteredList as ArrayList<RfidBean>
                }
                val filterResults = FilterResults()
                filterResults.values = mFilterList
                return filterResults
            }

            //把过滤后的值返回出来
            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                mFilterList = filterResults.values as ArrayList<RfidBean>
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return mFilterList.size
    }

}