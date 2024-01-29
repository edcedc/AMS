package com.yyc.ams.weight

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatEditText
import com.blankj.utilcode.util.ToastUtils
import com.flyco.roundview.RoundTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.lxj.xpopup.core.CenterPopupView
import com.yyc.ams.R

/**
 * User: Nike
 *  2023/12/18 12:55
 */
class PopDeleteRfidView(context: Context) : CenterPopupView(context) {


    override fun getImplLayoutId(): Int = R.layout.p_de_rifd

    override fun onCreate() {
        super.onCreate()

        findViewById<ImageButton>(R.id.iv_cancel).setOnClickListener {
            dismiss()
        }

        findViewById<RoundTextView>(R.id.tv_confirm).setOnClickListener {
            val optional = findViewById<TextInputEditText>(R.id.et_optional).text.toString()
            if (optional.equals("delete", ignoreCase = true)) {
                mItemClickListener?.onItemClick(optional)
                dismiss()
            } else {
                val tlDelete = findViewById<TextInputLayout>(R.id.tl_delete)
                tlDelete.error = context.getString(R.string.input_error)
                return@setOnClickListener
            }
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
        fun onItemClick(text1: String)
    }

}