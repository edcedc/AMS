package com.yyc.ams.api

import com.yyc.ams.bean.BaseListBean
import com.yyc.ams.bean.BaseResponseBean
import com.yyc.ams.bean.DataBean
import com.yyc.ams.bean.db.OrderBean
import com.yyc.ams.util.CacheUtil
import okhttp3.ResponseBody
import retrofit2.http.*


/**
 * Created by xuhao on 2017/11/16.
 * Api 接口
 */

interface ApiService{

    companion object {

        private val url =
//            "192.168.2.18"
            "47.243.120.137"

        var SERVLET_URL = "http://" +
                url + "/RFIDInventoryWebService/MobileWebService.asmx/"

    }

    //登录
    @FormUrlEncoded
    @POST("CheckLogin")
    suspend fun CheckLogin(
        @Field("companyID") companyID: String,
        @Field("loginID") loginID: String,
        @Field("userPwd") userPwd: String
    ): BaseResponseBean<DataBean>

    //盘点列表
    @FormUrlEncoded
    @POST("stockTakeList")
    suspend fun stockTakeList(
        @Field("userid") userid: String = CacheUtil.getUser()!!.RoNo,
        @Field("companyid") companyid: String  = CacheUtil.getCompanyID()
    ): BaseListBean<ArrayList<OrderBean>>

    // Rfid列表
    @FormUrlEncoded
    @POST("stockTakeListAsset")
    suspend fun stockTakeListAsset(
        @Field("orderno") orderno: String,
        @Field("userid") userid: String = CacheUtil.getUser()!!.RoNo,
        @Field("companyid") companyid: String  = CacheUtil.getCompanyID()
    ): ResponseBody

    // 外部借用订单列表
    @FormUrlEncoded
    @POST("GetExteriOrderBorrowingList")
    suspend fun GetExteriOrderBorrowingList(
        @Field("All") All: String,
        @Field("UnitCode") companyid: String  = CacheUtil.getCompanyID()
    ): BaseListBean<ArrayList<DataBean>>

    // 外部借出订单详情
    @FormUrlEncoded
    @POST("GetExteriOrderBorrowingDetails")
    suspend fun GetExteriOrderBorrowingDetails(
        @Field("type") type: Int,
        @Field("order") order: String?,
        @Field("All") All: String,
        @Field("UnitCode") companyid: String  = CacheUtil.getCompanyID()
    ): BaseListBean<ArrayList<DataBean>>

    // 内部借出订单
    @FormUrlEncoded
    @POST("GetInsideOrderBorrowingDetailsAPP")
    suspend fun GetInsideOrderBorrowingDetailsAPP(
        @Field("type") type: Int,
        @Field("All") All: String,
        @Field("UnitCode") companyid: String  = CacheUtil.getCompanyID()
    ): BaseListBean<ArrayList<DataBean>>

    // 注销订单列表
    @FormUrlEncoded
    @POST("GetDisposalorder")
    suspend fun GetDisposalorder(
        @Field("All") All: String,
        @Field("UnitCode") companyid: String  = CacheUtil.getCompanyID()
    ): BaseListBean<ArrayList<DataBean>>

    // 注销订单详情
    @FormUrlEncoded
    @POST("GetDisposalorderDetails")
    suspend fun GetDisposalorderDetails(
        @Field("type") type: Int,
        @Field("order") order: String?,
        @Field("All") All: String,
        @Field("UnitCode") companyid: String  = CacheUtil.getCompanyID()
    ): BaseListBean<ArrayList<DataBean>>

    //上传图片
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("UploadStockTake")
    suspend fun FileToByte(
        @Field("companyID") UnitCode: String?,
        @Field("strJson") strJson: String
    ): BaseResponseBean<DataBean>

    //上传图片
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("FileToByte")
    suspend fun FileToByte(
        @Field("Suffix") Suffix: String,
        @Field("iCode") iCode: String,
        @Field("fileLoc") fileLoc: Int,
        @Field("loginID") loginID: String?,
        @Field("order") order: String,
        @Field("UnitCode") UnitCode: String?,
        @Field("str1") str1: String
        ): BaseResponseBean<DataBean>

    //上传数据
    @POST("UploadStockTake")
    @FormUrlEncoded
    suspend fun UploadStockTake(
        @Field("UnitCode") unitCode: String?,
        @Field("strJson") strJson: String

    ): BaseResponseBean<DataBean>

    //提交在架上
    @POST("GetwhetherBorrowingExistApp")
    @FormUrlEncoded
    suspend fun GetwhetherBorrowingExistApp(
        @Field("str") strJson: String,
        @Field("UnitCode") companyid: String  = CacheUtil.getCompanyID()
    ): BaseResponseBean<DataBean>

    //注销确认上传
    @POST("UpdateDisposalAPP")
    @FormUrlEncoded
    suspend fun UpdateDisposalAPP(
        @Field("Order") Order: String,
        @Field("str") strJson: String,
        @Field("Disposal_Reason") text: String,
        @Field("userid") userid: String  = CacheUtil.getUser()!!.RoNo,
        @Field("UnitCode") companyid: String  = CacheUtil.getCompanyID()
    ): BaseResponseBean<DataBean>

    //外部借用确认上传
    @POST("GetUpdateExteriorBorrowingApp")
    @FormUrlEncoded
    suspend fun GetUpdateExteriorBorrowingApp(
        @Field("Order") orderId: String,
        @Field("strJson") strJson: String,
        @Field("UnitCode") companyid: String  = CacheUtil.getCompanyID()
    ): BaseResponseBean<DataBean>

    //内部借用确认上传
    @POST("GetupdateInsideBorrowingApp")
    @FormUrlEncoded
    suspend fun GetupdateInsideBorrowingApp(
        @Field("strJson") strJson: String,
        @Field("UnitCode") companyid: String  = CacheUtil.getCompanyID()
    ): BaseResponseBean<DataBean>

}