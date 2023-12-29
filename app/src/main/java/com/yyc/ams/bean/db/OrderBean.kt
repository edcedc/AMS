package com.yyc.ams.bean.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @Author nike
 * @Date 2023/7/25 15:31
 * @Description
 */
@Entity
open class OrderBean {

    @PrimaryKey(autoGenerate = true)//自增长
    var uid: Int = 0

    @ColumnInfo(name = "order_id")
    var id: String = ""

    @ColumnInfo(name = "order_stocktakeno")
    var stocktakeno: String = ""

    @ColumnInfo(name = "order_name")
    var name: String = ""

    @ColumnInfo(name = "order_startDate")
    var startDate: String = ""

    @ColumnInfo(name = "order_lastUpdate")
    var lastUpdate: String = ""

    @ColumnInfo(name = "order_endDate")
    var endDate: String = ""

    @ColumnInfo(name = "order_progress")
    var progress: Int = 0

    @ColumnInfo(name = "order_remarks")
    var remarks: String = ""

    @ColumnInfo(name = "order_roNo")
    var RoNo: String = ""

    @ColumnInfo(name = "order_total")
    var total: Int = 0

    @ColumnInfo(name = "order_companyId")
    var companyId: String = ""

    override fun toString(): String {
        return "OrderBean(uid=$uid, id='$id', stocktakeno='$stocktakeno', name='$name', startDate='$startDate', lastUpdate='$lastUpdate', endDate='$endDate', progress=$progress, remarks='$remarks', RoNo='$RoNo', total=$total, companyId='$companyId')"
    }

}