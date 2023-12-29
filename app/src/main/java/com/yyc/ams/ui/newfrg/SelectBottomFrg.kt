package com.yyc.ams.ui.newfrg

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yyc.ams.R
import com.yyc.ams.base.BaseFullBottomSheetFragment

/**
 * User: Nike
 *  2023/12/21 17:45
 */
class SelectBottomFrg : BaseFullBottomSheetFragment(), View.OnClickListener {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.p_rfid_sel, container, false)
        dialog!!.setCanceledOnTouchOutside(false);//点击屏幕不消失

        return rootView
    }


    override fun onClick(p0: View?) {

    }


}