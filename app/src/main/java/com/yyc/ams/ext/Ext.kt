package com.yyc.ams.ext

import com.blankj.utilcode.util.PathUtils

/**
 * 作者　: hegaojian
 * 时间　: 2020/4/16
 * 描述　:
 */
//文件路径
var Externalpath =  PathUtils.getExternalAppDataPath() + "/RfidFile"

/**
 *  搜索epc还是tid
 */
val RFID_TAG = "EPC"
/**
 *  判断rfid订单是什么类型
 */
val RFID_BOOK = 0
val RFID_ARCHIVES = 1

/**
 * 进入订单详情item的不同页面判断值
 */
var ORDER_DESC = 1
var ORDER_CREATE = 0


/**
 *  盘点清单二级状态
 */
val INVENTORY_ALL : Int = 0//全部
val INVENTORY_STOCK : Int = 1//在库
val INVENTORY_NOT : Int = 0//不在库
val INVENTORY_FAIL : Int = 2//异常

/**
 *  订单状态值
 */
val STATE_0 : Int = 0
val STATE_1 : Int = 1
val STATE_2 : Int = 2

/**
 *  连接状态
 */
val CONNECTING: Int = 0
val CONNECTION_SUCCEEDED: Int = 1
val CONNECTION_FAILED: Int = 2

/**
 *  跟后台协议上传图片用stringbuffer ? 隔开
 */
val UPLOAD_IMAGE_SPLIT: String = "?"

/**
 *  扫描类型
 *  117QRCode  116 Barcode 118 手工
 */
val SCAN_STATUS_SCAN: Int = 116
val SCAN_STATUS_QRCODE: Int = 117
val SCAN_STATUS_MANUALLY: Int = 118

/**
 *  区分跳转扫码页面回调参数
 */
val ORDER_TYPE: Int = 1
val ASSET_TYPE: Int = 2
val DISPOSAL_TYPE: Int = 3
val DISPOSAL_BOOK_TYPE: Int = 4
val DISPOSAL_ARCHIVES_TYPE: Int = 5
val EXTERNAL_BOOK_TYPE: Int = 6
val EXTERNAL_ARCHIVES_TYPE: Int = 7
val EXTERNAL_BAORROW_TYPE: Int = 8
val INTERNAL_ARCHIVES_TYPE: Int = 10
val INTERNAL_BOOK_TYPE: Int = 11
val ZKING_TYPE: Int = 12
val ZKING_ADD_TYPE: Int = 13


