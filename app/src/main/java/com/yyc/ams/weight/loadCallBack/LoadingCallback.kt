package com.yyc.ams.weight.loadCallBack

import android.content.Context
import android.view.View
import com.yyc.ams.R
import com.kingja.loadsir.callback.Callback


class LoadingCallback : Callback() {

    override fun onCreateView(): Int {
        return R.layout.layout_loading
    }

    override fun onReloadEvent(context: Context?, view: View?): Boolean {
        return true
    }
}