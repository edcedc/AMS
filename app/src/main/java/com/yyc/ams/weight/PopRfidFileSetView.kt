package com.yyc.ams.weight

import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.core.HorizontalAttachPopupView
import com.yyc.ams.R

/**
 * User: Nike
 *  2023/12/20 15:24
 */
class PopRfidFileSetView(context: Context) : HorizontalAttachPopupView(context) {

    override fun getImplLayoutId(): Int = R.layout.p_rfid_file_set

    override fun onCreate() {
        super.onCreate()
        findViewById<AppCompatTextView>(R.id.tv_delete).setOnClickListener {
            mItemClickListener?.onItemDeleteClick()
            dismiss()
        }
        findViewById<AppCompatTextView>(R.id.tv_mail).setOnClickListener {
            mItemClickListener?.onItemMailClick()
            dismiss()
        }
    }

    var mItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(l: OnItemClickListener?) {
        mItemClickListener = l
    }

    interface OnItemClickListener {
        fun onItemDeleteClick()
        fun onItemMailClick()
    }


    override fun show(): BasePopupView {
        return super.show()
    }

    override fun onDismiss() {
        super.onDismiss()
    }

}