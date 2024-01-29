package com.yyc.ams.bean.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.yyc.ams.bean.db.RfidBean
import com.yyc.ams.bean.db.RfidFileBean

/**
 * @Author nike
 * @Date 2023/9/6 17:42
 * @Description
 */
@Dao
interface RfidDao {

    /**
     * 增加
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(vararg bean: RfidBean)

    @Insert
    fun add(bean: RfidBean): Long

    /**
     * 查询父类id查询
     */
    @Query("SELECT * FROM RfidBean WHERE rfid_parentId = :parentId")
    fun findAll(parentId: Long): List<RfidBean>

    @Query("SELECT * FROM RfidBean WHERE rfid_parentId = :parentId LIMIT :limit")
    suspend fun findAll(parentId: Long, limit: Int): List<RfidBean>

    /**
     * 获取全部
     */
    @Query("SELECT * FROM RfidBean")
    fun findAll(): List<RfidBean>

    @Query("SELECT * FROM RfidBean GROUP BY rfid_epc")
    fun findAllDistinct(): List<RfidBean>

    /**
     *  查询是否重复
     */
    @Query("SELECT * FROM RfidBean WHERE rfid_parentId = :parentId AND rfid_epc = :epc")
    fun findParentIdBean(parentId: Long, epc: String): RfidBean

    /**
     * 根据用户ID删除全部
     */
    @Query("DELETE FROM RfidBean WHERE rfid_parentId = :parentId")
    suspend fun deleteParentId(parentId: Long)

    /**
     * 更新
     */
    @Update
     fun update(bean: RfidBean)

    /**
     * 根据主键删除全部
     */
    @Delete
    suspend fun deleteById(bean: RfidBean)
}