package com.yyc.ams.bean


import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by yc on 2017/8/17.
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class DataBean(
    var id: String = "",
    var LoginID: String = "",
    var RoNo: String = "",
    var type: Int = 0,
    var number: Int = 0,
    var Status: Int = 0,
    var position: Int = 0,
    var pImgfile: String = "",
    var updateDate: String = "",
    var text: String? = null,
    var companyId: String? = null,
    var OrderNo: String? = null,
    var Disposal_speed: String? = null,
    var AssetNo: String? = null,
    var LevelType: String? = null,
    var ArchivesType: String? = null,
    var QRCode: String? = null,
    var DisposalModel: String? = null,
    var Location: String? = null,
    var Title: String? = null,
    var LibraryCallNo: String? = null,
    var ArchivesNo: String? = null,
    var LabelTag: String? = null,
    var OrderName: String? = null,
    var org: String? = null,
    var us: String? = null,
    var BorrowDate: String? = null,
    var Phone: String? = null,
    var Progress: String? = null,
    var Password: String? = null,
    var isLoading: Boolean = false,
    var fileName: String? = null
) : Parcelable