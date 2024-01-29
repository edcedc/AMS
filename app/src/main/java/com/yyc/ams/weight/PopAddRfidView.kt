package com.yyc.ams.weight

import android.content.Context
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatEditText
import com.flyco.roundview.RoundTextView
import com.lxj.xpopup.core.CenterPopupView
import com.yyc.ams.R

/**
 * User: Nike
 *  2023/12/18 12:55
 */
class PopAddRfidView(context: Context) : CenterPopupView(context) {


    override fun getImplLayoutId(): Int = R.layout.p_add_rfid_file

    override fun onCreate() {
        super.onCreate()

        findViewById<ImageButton>(R.id.iv_cancel).setOnClickListener {
            dismiss()
        }

        findViewById<RoundTextView>(R.id.tv_confirm).setOnClickListener {
            var optional = findViewById<AppCompatEditText>(R.id.et_optional).text.toString()
            var location = findViewById<AppCompatEditText>(R.id.et_location).text.toString()
            /*if (location.isEmpty()){
                ToastUtils.showShort(context.getText(R.string.text11))
                return@setOnClickListener
            }*/
            mItemClickListener?.onItemClick(optional, location)
            dismiss()
        }
    }

    override fun onShow() {
        super.onShow()
    }

    override fun onDismiss() {
        super.onDismiss()
    }


    private var mItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(l: OnItemClickListener?) {
        mItemClickListener = l
    }

    interface OnItemClickListener {
        fun onItemClick(text1: String, text2: String)
    }

}