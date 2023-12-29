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
open class RfidFileBean {

    @PrimaryKey(autoGenerate = true)//自增长
    var uid: Int = 0

    @ColumnInfo(name = "rfid_file_title")
    var title: String = ""

    @ColumnInfo(name = "rfid_file_location")
    var location: String = ""

    @ColumnInfo(name = "rfid_file_number")
    var number: Int = 0

    @ColumnInfo(name = "rfid_file_crearDate")
    var crearDate: String = ""

    @ColumnInfo(name = "rfid_file_updateDate")
    var updateDate: String = ""

    @ColumnInfo(name = "rfid_file_flieName")
    var flieName: String = ""

    var status: Int = 0

    override fun toString(): String {
        return "RfidFileBean(uid=$uid, title='$title', location='$location', number=$number, crearDate='$crearDate', updateDate='$updateDate', flieName='$flieName', status=$status)"
    }

}