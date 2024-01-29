package com.yyc.ams.bean.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yyc.ams.bean.db.UploadOrderListBean

/**
 * @Author nike
 * @Date 2023/9/13 17:49
 * @Description
 */
@Dao
interface UploadOrderListDao {

    /**
     * 增加
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(vararg bean: UploadOrderListBean)

    /**
     * 根据用户ID删除全部
     */
    @Query("DELETE FROM UploadOrderListBean WHERE ul_loginID = :roNo AND ul_companyId = :companyId AND ul_orderNo = :orderId")
    fun deleteById(roNo: String?, companyId: String?, orderId: String?)


    /**
     * 根据用户ID删除全部
     */
    @Query("DELETE FROM UploadOrderListBean WHERE ul_loginID = :roNo AND ul_companyId = :companyId")
    suspend fun deleteById(roNo: String?, companyId: String?)

    /**
     * 根据用户查询所有
     */
    @Query("SELECT * FROM UploadOrderListBean WHERE ul_loginID = :roNoId AND ul_companyId = :companyId AND ul_orderNo = :orderId")
    fun findByIdAll(roNoId: String?, companyId: String?, orderId: String?): List<UploadOrderListBean>

}