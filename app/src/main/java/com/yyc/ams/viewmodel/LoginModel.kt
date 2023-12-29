package com.yyc.ams.viewmodel

import android.view.View
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.yyc.ams.R
import com.yyc.ams.api.UIHelper
import com.yyc.ams.bean.BaseResponseBean
import com.yyc.ams.bean.DataBean
import com.yyc.ams.mar.appViewModel
import com.yyc.ams.network.apiService
import com.yyc.ams.util.CacheUtil
import me.hgj.jetpackmvvm.base.appContext
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.databind.BooleanObservableField
import me.hgj.jetpackmvvm.callback.databind.StringObservableField
import me.hgj.jetpackmvvm.ext.requestNoCheck
import me.hgj.jetpackmvvm.state.ResultState

/**
 * @Author nike
 * @Date 2023/7/5 14:55
 * @Description
 */
class LoginModel: BaseViewModel() {

    //用户名
    var username = StringObservableField()

    //密码(登录注册界面)
    var password = StringObservableField()

    //是否显示明文密码（登录注册界面）
    var isShowPwd = BooleanObservableField()

    //用户名清除按钮是否显示   不要在 xml 中写逻辑 所以逻辑判断放在这里
    var clearVisible = object :ObservableInt(username){
        override fun get(): Int {
            return if(username.get().isEmpty()){
                View.GONE
            }else{
                View.VISIBLE
            }
        }
    }

    //密码显示按钮是否显示   不要在 xml 中写逻辑 所以逻辑判断放在这里
    var passwordVisible = object : ObservableInt(password){
        override fun get(): Int {
            return if(password.get().isEmpty()){
                View.GONE
            }else{
                View.VISIBLE
            }
        }
    }

    //方式1  自动脱壳过滤处理请求结果，判断结果是否成功
    var loginResult = MutableLiveData<ResultState<BaseResponseBean<DataBean?>>>()
//
    //方式2  不用框架帮脱壳，判断结果是否成功
//    var loginResult = MutableLiveData<ResultState<ApiResponse<UserInfo>>>()

    fun login(username: String, password: String) {
        requestNoCheck({apiService.CheckLogin(CacheUtil.getCompanyID(), username, password)},{
            //请求成功 自己拿到数据做业务需求操作
            if(it.isSucces()){
                val data = it.data
                if (data != null){
                    //登录成功 通知账户数据发生改变鸟
                    data.Password = password
                    data.companyId = CacheUtil.getCompanyID()
                    CacheUtil.setUser(data)
                    CacheUtil.setIsLogin(true)
                    appViewModel.userInfo.value = data!!
                    UIHelper.startMainAct()
                    ActivityUtils.finishAllActivities()
                }
                if (it.msg.contains("103")){
                    ToastUtils.showShort("No Account")
                }else if (it.msg.contains("104")){
                    ToastUtils.showShort("The account or password is incorrect")
                }
            }
        },{
            //请求失败 网络异常回调在这里
            loadingChange.dismissDialog
            ToastUtils.showShort(it.throwable!!.message)
            LogUtils.e(it.throwable, it.throwable!!.message)
        }, true, appContext.getString(R.string.loading))
    }

}