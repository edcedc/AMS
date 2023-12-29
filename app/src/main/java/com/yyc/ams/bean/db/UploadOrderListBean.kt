package com.yyc.ams.bean.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @Author nike
 * @Date 2023/9/13 17:46
 * @Description
 */
@Entity
open class UploadOrderListBean {

    @PrimaryKey(autoGenerate = true)//自增长
    var uid: Int = 0

    @ColumnInfo(name = "ul_loginID")
    var loginID: String = ""

    @ColumnInfo(name = "ul_orderNo")
    var orderNo: String = ""

    @ColumnInfo(name = "ul_assetNo")
    var AssetNo: String = ""

    @ColumnInfo(name = "ul_scanDate")
    var ScanDate: String = ""

    @ColumnInfo(name = "ul_epc")
    var EPC: String = ""

    @ColumnInfo(name = "ul_remarks")
    var Remarks: String = ""

    @ColumnInfo(name = "ul_statusID")
    var statusID: Int = 0

    @ColumnInfo(name = "ul_foundStatus")
    var FoundStatus: Int = 0

    @ColumnInfo(name = "ul_last_ScanTime")
    var Last_ScanTime: String = ""

    @ColumnInfo(name = "ul_img")
    var img: String = ""

    @ColumnInfo(name = "ul_companyId")
    var companyId: String = ""

    override fun toString(): String {
        return "UploadOrderListBean(uid=$uid, loginID='$loginID', orderNo='$orderNo', AssetNo='$AssetNo', ScanDate='$ScanDate', EPC='$EPC', Remarks='$Remarks', statusID=$statusID, FoundStatus=$FoundStatus, Last_ScanTime='$Last_ScanTime', img='$img', companyId='$companyId')"
    }

}