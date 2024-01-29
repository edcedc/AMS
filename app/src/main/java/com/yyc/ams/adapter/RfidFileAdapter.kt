package com.yyc.ams.adapter

import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageButton
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.flyco.roundview.RoundFrameLayout
import com.yyc.ams.R
import com.yyc.ams.bean.db.RfidFileBean
import com.yyc.ams.ext.setAdapterAnimation
import com.yyc.ams.util.SettingUtil

/**
 * User: Nike
 *  2023/12/15 16:12
 */
class RfidFileAdapter (data: ArrayList<RfidFileBean>) :
    BaseQuickAdapter<RfidFileBean, BaseViewHolder>(R.layout.i_rfid_file, data) {

    init {
        setAdapterAnimation(SettingUtil.getListMode())
    }

    override fun convert(holder: BaseViewHolder, item: RfidFileBean) {
        item.run {
            holder.setText(R.id.tv_id, String.format("%06d", uid))
            holder.setText(R.id.tv_title, title)
            holder.setText(R.id.tv_location, location)
            holder.setText(R.id.tv_create, crearDate)
            holder.setText(R.id.tv_update, updateDate)
            holder.setText(R.id.tv_epc, number.toString())

            holder.setVisible(R.id.fy_sel, if (status == 1) true else false)
        }
        holder.getView<FrameLayout>(R.id.iv_set).setOnClickListener {
            mItemClickListener?.onItemClick(holder.adapterPosition, item, it)
        }
    }

    private var mItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(l: OnItemClickListener?) {
        mItemClickListener = l
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, item: RfidFileBean, view: View)
    }

}