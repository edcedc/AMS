package com.yyc.ams.bean.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @Author nike
 * @Date 2023/9/6 17:40
 * @Description
 */
@Entity
open class UploadOrderBean {

    @PrimaryKey(autoGenerate = true)//自增长
    var uid: Int = 0

    @ColumnInfo(name = "upload_title")
    var title: String = ""

    @ColumnInfo(name = "upload_status")//是否保存 0、1
    var status: Int = 0

    @ColumnInfo(name = "upload_companyId")
    var companyId: String = ""

    @ColumnInfo(name = "upload_roNo")
    var RoNo: String = ""

    @ColumnInfo(name = "upload_data")
    var data: String = ""

    @ColumnInfo(name = "upload_orderId")
    var orderId: String = ""

    override fun toString(): String {
        return "UploadOrderBean(uid=$uid, title='$title', status=$status, companyId='$companyId', RoNo='$RoNo', data='$data')"
    }

}