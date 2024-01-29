package com.yyc.ams.bean.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.yyc.ams.bean.db.RfidFileBean


/**
 * @Author nike
 * @Date 2023/9/6 17:42
 * @Description
 */
@Dao
interface RfidFileDao {

    /**
     * 增加
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(vararg bean: RfidFileBean)


    @Insert
    fun add(bean: RfidFileBean): Long

    /**
     * 查询所有
     */
    @Query("SELECT * FROM RfidFileBean ORDER BY uid DESC")
    fun findAll(): List<RfidFileBean>

    /**
     * 根据父类Id查询
     */
    @Query("SELECT * FROM RfidFileBean WHERE uid= :parentId")
    fun findParentIdBean(parentId: Long): RfidFileBean

    /**
     * 更新
     */
    @Update
     fun update(bean: RfidFileBean)

    /**
     * 根据主键删除全部
     */
    @Delete
    suspend fun deleteById(bean: RfidFileBean)

}