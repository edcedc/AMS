package com.yyc.ams.weight.loadCallBack


import com.yyc.ams.R
import com.kingja.loadsir.callback.Callback


class EmptyCallback : Callback() {

    override fun onCreateView(): Int {
        return R.layout.layout_empty
    }

}