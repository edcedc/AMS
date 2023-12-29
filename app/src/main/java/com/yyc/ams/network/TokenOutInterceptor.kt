package com.yyc.ams.network

import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.ToastUtils
import com.yyc.ams.bean.BaseResponseBean
import com.google.gson.Gson
import com.yyc.ams.api.UIHelper
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.IOException

/**
 * 作者　: hegaojian
 * 时间　: 2022/1/13
 * 描述　: token过期拦截器
 */
class TokenOutInterceptor : Interceptor {

    val gson: Gson by lazy { Gson() }

    @kotlin.jvm.Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        return if (response.body() != null && response.body()!!.contentType() != null) {
            val mediaType = response.body()!!.contentType()
            val string = response.body()!!.string()
            val responseBody = ResponseBody.create(mediaType, string)
            val apiResponse = gson.fromJson(string, BaseResponseBean::class.java)
            //判断逻辑 模拟一下
            if (apiResponse.code == 99999) {
                //如果是普通的activity话 可以直接跳转，如果是navigation中的fragment，可以发送通知跳转
//                appContext.startActivity(Intent(appContext, TestActivity::class.java).apply {
//                    flags = Intent.FLAG_ACTIVITY_NEW_TASK})
                UIHelper.startLoginAct()
                ActivityUtils.finishAllActivities()
            }
            val errorMessage = apiResponse.ErrorMessage
            if (!StringUtils.isEmpty(errorMessage) && errorMessage.contains("请退出重新登陆")){
                UIHelper.startLoginAct()
                ActivityUtils.finishAllActivities()
                ToastUtils.showLong(errorMessage)
            }

            response.newBuilder().body(responseBody).build()
        } else {
            response
        }
    }
}