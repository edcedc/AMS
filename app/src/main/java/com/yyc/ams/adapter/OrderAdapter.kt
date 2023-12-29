package com.yyc.ams.adapter

import android.view.View
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.widget.AppCompatTextView
import com.blankj.utilcode.util.StringUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.yyc.ams.R
import com.yyc.ams.bean.db.OrderBean
import com.yyc.ams.ext.setAdapterAnimation
import com.yyc.ams.util.SettingUtil

/**
 * @Author nike
 * @Date 2023/7/7 17:05
 * @Description
 */
class OrderAdapter (data: ArrayList<OrderBean>) :
    BaseQuickAdapter<OrderBean, BaseViewHolder>(
        R.layout.i_order, data), Filterable {


    init {
        setAdapterAnimation(SettingUtil.getListMode())
    }

    override fun convert(viewHolder: BaseViewHolder, bean: OrderBean) {
        //赋值
        bean.run {
            val bean = mFilterList[viewHolder.layoutPosition]
            viewHolder.setText(R.id.tv_title, bean.stocktakeno + " | " + bean.name)
            viewHolder.setText(R.id.tv_start_date, context.getString(R.string.start_date) + "：" + bean.startDate)
            viewHolder.setText(R.id.tv_end_date, context.getString(R.string.end_date) + "：" + bean.endDate)
            viewHolder.setText(R.id.tv_progress, context.getString(R.string.progress) + "：" + bean.progress + "/" + bean.total)
//            viewHolder.setText(R.id.tv_update_data, context.getString(R.string.update_date) + "：" + bean.lastUpdate)
            viewHolder.setText(R.id.tv_remarks, context.getString(R.string.remarks) + "：" + bean.remarks)
            viewHolder.getView<AppCompatTextView>(R.id.tv_remarks).visibility = if (StringUtils.isEmpty(bean.remarks)) View.GONE else View.VISIBLE
        }
    }

    var mFilterList = ArrayList<OrderBean>()

    fun appendList(list: List<OrderBean>) {
        data = list as MutableList<OrderBean>
        //这里需要初始化filterList
        mFilterList = list as ArrayList<OrderBean>
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            //执行过滤操作
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    //没有过滤的内容，则使用源数据
                    mFilterList = data as ArrayList<OrderBean>
                } else {
                    val filteredList: MutableList<OrderBean> = ArrayList()
                    for (i in data.indices) {
                        val bean = data[i]
                        val labelTag = bean.stocktakeno
                        if (!StringUtils.isEmpty(labelTag)) {
                            if (labelTag!!.contains(charString)) {
                                filteredList.add(bean)
                            }
                        }
                    }
                    mFilterList = filteredList as ArrayList<OrderBean>
                }
                val filterResults = FilterResults()
                filterResults.values = mFilterList
                return filterResults
            }

            //把过滤后的值返回出来
            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                mFilterList = filterResults.values as ArrayList<OrderBean>
                notifyDataSetChanged()
            }
        }
    }


    override fun getItemCount(): Int {
        return mFilterList.size
    }

    override fun hashCode(): Int {
        return mFilterList.hashCode()
    }

    fun  getFilterList(): List<OrderBean>{
        return mFilterList
    }

}