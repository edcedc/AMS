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
open class RfidBean {

    @PrimaryKey(autoGenerate = true)//自增长
    var uid: Int = 0

    @ColumnInfo(name = "rfid_epc")
    var epc: String = ""

    @ColumnInfo(name = "rfid_crearDate")
    var crearDate: String = ""

    @ColumnInfo(name = "rfid_parentId")
    var parentId: Long = 0

    @ColumnInfo(name = "rfid_status")
    var status: Int = 0

    @ColumnInfo(name = "rfid_position")
    var position: Int = 0

    override fun toString(): String {
        return "RfidBean(uid=$uid, epc='$epc', crearDate='$crearDate', parentId=$parentId, status=$status, position=$position)"
    }

}