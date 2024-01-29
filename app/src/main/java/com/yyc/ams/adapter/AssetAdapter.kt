package com.yyc.ams.adapter

import android.widget.Filter
import android.widget.Filterable
import android.widget.ListView
import com.blankj.utilcode.util.StringUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.yyc.ams.R
import com.yyc.ams.bean.db.AssetBean
import com.yyc.ams.ext.INVENTORY_FAIL
import com.yyc.ams.ext.INVENTORY_STOCK
import com.yyc.ams.ext.setAdapterAnimation
import com.yyc.ams.util.SettingUtil
import org.json.JSONObject

/**
 * @Author nike
 * @Date 2023/7/7 17:05
 * @Description
 */
class AssetAdapter (data: ArrayList<AssetBean>) :BaseQuickAdapter<AssetBean, BaseViewHolder>(R.layout.i_order, data), Filterable {


    init {
        setAdapterAnimation(SettingUtil.getListMode())
    }

    override fun convert(viewHolder: BaseViewHolder, bean: AssetBean) {
        //赋值
        bean.run {
            val bean = mFilterList[viewHolder.layoutPosition]
            viewHolder.setText(R.id.tv_title, bean.AssetNo)
            viewHolder.setText(R.id.tv_end_date, context.getString(R.string.label_tag) + "：" + bean.LabelTag + " " + if (StringUtils.isEmpty(bean.AssetStatus)) "" else bean.AssetStatus)

            if (bean.InventoryStatus != INVENTORY_FAIL){
                viewHolder.setGone(R.id.iv_state, false)
                viewHolder.setImageResource(R.id.iv_state, if (bean.InventoryStatus == INVENTORY_STOCK) R.mipmap.icon_30 else R.mipmap.icon_31)
                viewHolder.setGone(R.id.tv_title, false)
            }else{
                viewHolder.setGone(R.id.iv_state, true)
                viewHolder.setGone(R.id.tv_title, true)
            }

            val listStr = ArrayList<String>()
            if (!StringUtils.isEmpty(bean.data)){
                val dataObj = JSONObject(bean.data)
                val jsonObject = dataObj.optJSONObject("Tag")
                if (jsonObject != null){
                    val headerkeys: Iterator<String> = jsonObject.keys()
                    while (headerkeys.hasNext()) {
                        val headerkey = headerkeys.next()
                        val headerValue: String = jsonObject.getString(headerkey)
                        if (headerkey.equals("Remarks") || headerkey.equals("LabelTag")
                            || headerkey.equals("InventoryStatus") || headerkey.equals("AssetNo")
                            || headerkey.equals("RoNo")){

                        }else{
                            listStr.add(headerkey + "：" + headerValue)
                        }
                    }
                }
            }

            viewHolder.getView<ListView>(R.id.listview).setClickable(false);
            viewHolder.getView<ListView>(R.id.listview).setPressed(false);
            viewHolder.getView<ListView>(R.id.listview).setEnabled(false);
            viewHolder.getView<ListView>(R.id.listview).adapter = OrderTextAdapter(context, listStr)

//            viewHolder.getView<AppCompatTextView>(R.id.tv_start_date).visibility = View.GONE

            val data = bean.data
            if (!StringUtils.isEmpty(data)){
                var dataObj = JSONObject(data)
                val tag = dataObj.optJSONObject("Tag")
                if (tag != null){
                    tag.put("InventoryStatus", bean.type.toString())
                }
                val inventory = dataObj.optJSONObject("Inventory")
                if (inventory != null){
                    inventory.put("FoundStatus", bean.scanStatus.toString())
                }
                dataObj.put("Tag", tag)
                dataObj.put("Inventory", inventory)
                bean.data = dataObj.toString()
            }
        }
    }

    var mFilterList = ArrayList<AssetBean>()

    fun appendList(list: List<AssetBean>) {
        data = list as MutableList<AssetBean>
        //这里需要初始化filterList
        mFilterList = list as ArrayList<AssetBean>
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            //执行过滤操作
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    //没有过滤的内容，则使用源数据
                    mFilterList = data as ArrayList<AssetBean>
                } else {
                    val filteredList: MutableList<AssetBean> = ArrayList()
                    for (i in data.indices) {
                        val bean = data[i]
                        val assetNo = bean.AssetNo
                        val labelTag = bean.LabelTag
                        if (assetNo.contains(charString) || labelTag.contains(charString)) {
                            filteredList.add(bean)
                        }
                    }
                    mFilterList = filteredList as ArrayList<AssetBean>
                }
                val filterResults = FilterResults()
                filterResults.values = mFilterList
                return filterResults
            }

            //把过滤后的值返回出来
            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                mFilterList = filterResults.values as ArrayList<AssetBean>
                notifyDataSetChanged()
            }
        }
    }


    override fun getItemCount(): Int {
        return mFilterList.size
    }

}