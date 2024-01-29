package com.yyc.ams.bean.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.yyc.ams.bean.db.UploadOrderBean

/**
 * @Author nike
 * @Date 2023/9/6 17:42
 * @Description
 */
@Dao
interface UploadOrderDao {

    /**
     * 增加
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(vararg bean: UploadOrderBean)

    /**
     * 根据用户ID删除全部
     */
    @Query("DELETE FROM UploadOrderBean WHERE upload_roNo = :roNoId AND upload_companyId = :companyId")
    suspend fun deleteById(roNoId: String, companyId: String)

    /**
     * 根据用户查询所有
     */
    @Query("SELECT * FROM UploadOrderBean WHERE upload_roNo = :roNoId AND upload_companyId = :companyId")
    fun findAll(roNoId: String?, companyId: String?): List<UploadOrderBean>

    /**
     * 根据订单查询所有
     */
    @Query("SELECT * FROM UploadOrderBean WHERE upload_roNo = :roNoId AND upload_companyId = :companyId AND upload_orderId = :orderId")
    fun findOrderId(orderId: String?, roNoId: String?, companyId: String?): UploadOrderBean

    /**
     * 更新
     */
    @Update
     fun update(bean: UploadOrderBean)

}