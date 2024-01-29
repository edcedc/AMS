package com.yyc.ams.viewmodel

import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.databind.StringObservableField

/**
 * @Author nike
 * @Date 2023/7/6 18:07
 * @Description
 */
class SettingModel: BaseViewModel() {

    val language = StringObservableField()

    val host = StringObservableField()

    val companyId = StringObservableField()

}