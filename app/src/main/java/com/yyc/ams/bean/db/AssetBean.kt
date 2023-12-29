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
open class AssetBean {

    @PrimaryKey(autoGenerate = true)//自增长
    var uid: Int = 0

    @ColumnInfo(name = "asset_id")
    var ids: String = ""

    @ColumnInfo(name = "asset_orderNo")
    var OrderRoNo: String = ""

    @ColumnInfo(name = "asset_assetNo")
    var AssetNo: String = ""

    @ColumnInfo(name = "asset_libraryCallNo")
    var LibraryCallNo: String = ""

    @ColumnInfo(name = "asset_archivesNo")
    var ArchivesNo: String = ""

    @ColumnInfo(name = "asset_remarks")
    var Remarks: String = ""

    @ColumnInfo(name = "asset_labelTag")
    var LabelTag: String = ""

    @ColumnInfo(name = "asset_title")
    var Title: String = ""

    @ColumnInfo(name = "asset_language")
    var Language: String = ""

    @ColumnInfo(name = "asset_image_list")
    var imageList: String = ""

    @ColumnInfo(name = "asset_location")
    var Location: String = ""

    @ColumnInfo(name = "asset_foundStatus")
    var FoundStatus: Int = 0

    @ColumnInfo(name = "asset_inventoryStatus")
    var InventoryStatus: Int = 0

    @ColumnInfo(name = "asset_type")
    var type: Int = 0

    @ColumnInfo(name = "asset_status")
    var status: Int = 0

    @ColumnInfo(name = "asset_roNo")
    var RoNo: String = ""

    @ColumnInfo(name = "asset_scanTime")
    var scanTime: String = ""

    @ColumnInfo(name = "asset_scanStatus")
    var scanStatus: Int = 0

    @ColumnInfo(name = "asset_companyId")
    var companyId: String = ""

    @ColumnInfo(name = "asset_data")
    var data: String = ""

    @ColumnInfo(name = "asset_AssetStatus")
    var AssetStatus: String = ""

    override fun toString(): String {
        return "AssetBean(uid=$uid, ids='$ids', OrderRoNo='$OrderRoNo', AssetNo='$AssetNo', LibraryCallNo='$LibraryCallNo', ArchivesNo='$ArchivesNo', Remarks='$Remarks', LabelTag='$LabelTag', Title='$Title', Language='$Language', imageList='$imageList', Location='$Location', FoundStatus=$FoundStatus, InventoryStatus=$InventoryStatus, type=$type, status=$status, RoNo='$RoNo', scanTime='$scanTime', scanStatus=$scanStatus, companyId='$companyId', data='$data', AssetStatus='$AssetStatus')"
    }

}