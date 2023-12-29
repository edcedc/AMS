package com.yyc.ams.adapter

import android.widget.Filter
import android.widget.Filterable
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.yyc.ams.R
import com.yyc.ams.bean.DataBean
import com.yyc.ams.ext.DISPOSAL_ARCHIVES_TYPE
import com.yyc.ams.ext.DISPOSAL_BOOK_TYPE
import com.yyc.ams.ext.EXTERNAL_ARCHIVES_TYPE
import com.yyc.ams.ext.EXTERNAL_BOOK_TYPE
import com.yyc.ams.ext.INTERNAL_ARCHIVES_TYPE
import com.yyc.ams.ext.INTERNAL_BOOK_TYPE
import com.yyc.ams.ext.setAdapterAnimation
import com.yyc.ams.util.SettingUtil

/**
 * @Author nike
 * @Date 2023/7/7 17:05
 * @Description
 */
class DisposalListAdapter(data: ArrayList<DataBean>, mType: Int) :BaseQuickAdapter<DataBean, BaseViewHolder>(R.layout.i_asset1, data), Filterable {

    var mType: Int = 0

    init {
        setAdapterAnimation(SettingUtil.getListMode())
        this.mType = mType
    }

    override fun convert(viewHolder: BaseViewHolder, bean: DataBean) {
        //赋值
        bean.run {
            val bean = mFilterList[viewHolder.layoutPosition]
            viewHolder.setText(R.id.tv_title1, "：" + bean.Title)
            viewHolder.setText(R.id.tv_location1, "：" + bean.Location)

            viewHolder.setImageResource(R.id.iv_image, if (bean.type == 0) R.mipmap.icon_31 else R.mipmap.icon_30)

            when(mType){
                EXTERNAL_BOOK_TYPE, INTERNAL_BOOK_TYPE, DISPOSAL_BOOK_TYPE ->{
                    viewHolder.setText(R.id.tv_text, bean.AssetNo + " | " + bean.LibraryCallNo)
                    viewHolder.setGone(R.id.tv_epc, true)
                    viewHolder.setGone(R.id.tv_epc1, true)
                    viewHolder.setGone(R.id.tv_type, true)
                    viewHolder.setGone(R.id.tv_type1, true)
                }
                EXTERNAL_ARCHIVES_TYPE, INTERNAL_ARCHIVES_TYPE, DISPOSAL_ARCHIVES_TYPE ->{
                    viewHolder.setText(R.id.tv_text, bean.AssetNo + " | " + bean.ArchivesType)
                    viewHolder.setText(R.id.tv_epc, context.getText(R.string.level))
                    viewHolder.setText(R.id.tv_epc1, "：" + bean.LevelType)
                    viewHolder.setText(R.id.tv_type, context.getText(R.string.type))
                    viewHolder.setText(R.id.tv_type1, "：" + bean.ArchivesNo)
                    viewHolder.setGone(R.id.tv_epc, false)
                    viewHolder.setGone(R.id.tv_epc1, false)
                    viewHolder.setGone(R.id.tv_type, false)
                    viewHolder.setGone(R.id.tv_type1, false)
                }

                else -> {

                }
            }
        }
    }

    var mFilterList = ArrayList<DataBean>()

    fun appendList(list: List<DataBean>) {
        data = list as MutableList<DataBean>
        //这里需要初始化filterList
        mFilterList = list as ArrayList<DataBean>
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            //执行过滤操作
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    //没有过滤的内容，则使用源数据
                    mFilterList = data as ArrayList<DataBean>
                } else {
                    val filteredList: MutableList<DataBean> = ArrayList()
                    for (i in data.indices) {
                        val bean = data[i]
                        val assetNo = bean.AssetNo
                        if (assetNo!!.contains(charString)) {
                            filteredList.add(bean)
                        }
                    }
                    mFilterList = filteredList as ArrayList<DataBean>
                }
                val filterResults = FilterResults()
                filterResults.values = mFilterList
                return filterResults
            }

            //把过滤后的值返回出来
            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                mFilterList = filterResults.values as ArrayList<DataBean>
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

    private fun  getFilterList(): List<DataBean>{
        return mFilterList
    }

}