package com.yyc.ams.bean.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.yyc.ams.bean.db.ConfigBean
import com.yyc.ams.bean.db.UploadOrderBean

/**
 * @Author nike
 * @Date 2023/9/6 17:42
 * @Description
 */
@Dao
interface ConfigDao {

    /**
     * 增加
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(vararg bean: ConfigBean)

    /**
     * 查询所有
     */
    @Query("SELECT * FROM ConfigBean LIMIT 1")
    fun findFirst(): ConfigBean

    /**
     * 更新
     */
    @Update
     fun update(bean: ConfigBean)

}