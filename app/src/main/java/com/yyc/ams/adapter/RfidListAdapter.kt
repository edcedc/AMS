package com.yyc.ams.adapter

import android.icu.text.Transliterator.Position
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
    BaseQuickAdapter<RfidBean, BaseViewHolder>(R.layout.i_rfid_list, data) {

    init {
        setAdapterAnimation(SettingUtil.getListMode())
    }

    override fun convert(holder: BaseViewHolder, item: RfidBean) {
        item.run {
            holder.setText(R.id.tv_id, String.format("%06d", position))
            holder.setText(R.id.tv_epc, epc)
            holder.setText(R.id.tv_date, crearDate)
        }
    }

}