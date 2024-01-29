package com.yyc.ams.bean.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @Author nike
 * @Date 2023/7/28 10:22
 * @Description
 */
@Entity
open class ConfigBean {

    @PrimaryKey(autoGenerate = true)//自增长
    var uid: Int = 0

    @ColumnInfo(name = "cofig_power")
    var power: Int = 32

    @ColumnInfo(name = "cofig_session")
    var session: Int = 1

    @ColumnInfo(name = "cofig_flag")
    var flag: Int = 0

    @ColumnInfo(name = "cofig_rssi")
    var rssi: Int = 50

    @ColumnInfo(name = "cofig_strength")//信号强度
    var strength: Int = 70

    @ColumnInfo(name = "cofig_eaf")//环境衰退因子
    var eaf: Double = 3.3

    @ColumnInfo(name = "cofig_rfidCode")
    var rfidCode: String = ""

    @ColumnInfo(name = "cofig_recipient")
    var recipient: String = ""

    override fun toString(): String {
        return "ConfigBean(uid=$uid, power=$power, session=$session, flag=$flag, rssi=$rssi, strength=$strength, eaf=$eaf, rfidCode='$rfidCode', recipient='$recipient')"
    }

}