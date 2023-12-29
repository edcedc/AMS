package com.yyc.ams.util

import android.text.TextUtils
import com.blankj.utilcode.util.StringUtils
import com.yyc.ams.bean.DataBean
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tencent.mmkv.MMKV
import com.yyc.ams.api.ApiService

object CacheUtil {
    /**
     * 获取保存的账户信息
     */
    fun getUser(): DataBean? {
        val kv = MMKV.mmkvWithID("app")
        val userStr = kv.decodeString("user")
        return if (TextUtils.isEmpty(userStr)) {
           null
        } else {
            Gson().fromJson(userStr, DataBean::class.java)
        }
    }

    /**
     * 设置账户信息
     */
    fun setUser(userResponse: DataBean?) {
        val kv = MMKV.mmkvWithID("app")
        if (userResponse == null) {
            kv.encode("user", "")
            setIsLogin(false)
        } else {
            kv.encode("user", Gson().toJson(userResponse))
            setIsLogin(true)
        }

    }

    /**
     *  设置url
     */
    fun setUrl(url: String) {
        val kv = MMKV.mmkvWithID("app")
        kv.encode("url", url)
    }

    /**
     *  获取url
     */
    fun getUrl(): String{
        val kv = MMKV.mmkvWithID("app")
        val s = kv.decodeString("url")
        if (StringUtils.isEmpty(s)){
            return ApiService.SERVLET_URL
        }else{
            return s!!
        }
    }

    /**
     *  设置companyID
     */
    fun setCompanyID(companyID: String) {
        val kv = MMKV.mmkvWithID("app")
        kv.encode("companyID", companyID)
    }

    /**
     *  获取companyID
     */
    fun getCompanyID(): String{
        val kv = MMKV.mmkvWithID("app")
        val s = kv.decodeString("companyID")
        if (StringUtils.isEmpty(s)){
            return "iml"
        }else{
            return s!!
        }
    }

    /**
     *  获取设置语言参数
     */
    fun getLanguage(): Int{
        val kv = MMKV.mmkvWithID("app")
        val s = kv.decodeInt("Language")
        return s
    }

    /**
     *  设置语言参数
     */
    fun setLanguage(language: Int) {
        val kv = MMKV.mmkvWithID("app")
        kv.encode("Language", language)
    }

    /**
     * 是否已经登录
     */
    fun isLogin(): Boolean {
        val kv = MMKV.mmkvWithID("app")
        return kv.decodeBool("login", false)
    }

    /**
     * 设置是否已经登录
     */
    fun setIsLogin(isLogin: Boolean) {
        val kv = MMKV.mmkvWithID("app")
        kv.encode("login", isLogin)
    }

    /**
     * 是否是第一次登陆
     */
    fun isFirst(): Boolean {
        val kv = MMKV.mmkvWithID("app")
        return kv.decodeBool("first", true)
    }
    /**
     * 是否是第一次登陆
     */
    fun setFirst(first:Boolean): Boolean {
        val kv = MMKV.mmkvWithID("app")
        return kv.encode("first", first)
    }


    /**
     * 获取搜索历史缓存数据
     */
    fun getSearchHistoryData(): ArrayList<String> {
        val kv = MMKV.mmkvWithID("cache")
        val searchCacheStr =  kv.decodeString("history")
        if (!TextUtils.isEmpty(searchCacheStr)) {
            return Gson().fromJson(searchCacheStr
                , object : TypeToken<ArrayList<String>>() {}.type)
        }
        return arrayListOf()
    }

    fun setSearchHistoryData(searchResponseStr: String) {
        val kv = MMKV.mmkvWithID("cache")
        kv.encode("history",searchResponseStr)
    }
}