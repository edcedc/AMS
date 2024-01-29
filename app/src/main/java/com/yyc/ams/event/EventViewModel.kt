package com.yyc.ams.event

import com.yyc.ams.bean.DataBean
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.livedata.event.EventLiveData

/**
 * 描述　:APP全局的ViewModel，可以在这里发送全局通知替代EventBus，LiveDataBus等
 */
class EventViewModel : BaseViewModel() {

    val mainListEvent = EventLiveData<Boolean>()

    val zkingType = EventLiveData<DataBean>()


    val updateRfidFileData = EventLiveData<DataBean>()

}