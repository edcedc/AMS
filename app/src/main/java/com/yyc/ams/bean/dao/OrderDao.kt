package com.yyc.ams.bean.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.yyc.ams.bean.db.OrderBean
import kotlinx.coroutines.flow.Flow

/**
 * @Author nike
 * @Date 2023/7/25 15:41
 * @Description
 */
@Dao
interface OrderDao {

    /**
     * 增加
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(vararg bean: OrderBean)

    /**
     * 删除
     */
    @Delete
    suspend fun delete(bean: OrderBean)

    //删除数据库表全部数据
    @Query("delete from OrderBean")
    suspend fun deleteAll()

    //根据用户ID删除全部
    @Query("DELETE FROM OrderBean WHERE order_roNo = :id AND order_companyId = :companyId")
    suspend fun deleteById(id: String, companyId: String)

    /**
     * 查询所有
     */
    @Query("SELECT * FROM OrderBean")
    fun findAll(): Flow<List<OrderBean>>

    //根据用户ID查询全部
    @Query("SELECT * FROM OrderBean WHERE order_roNo = :id AND order_companyId = :companyId")
    fun findById(id: String, companyId: String?): List<OrderBean>

    //根据用户ID和订单查询全部
    @Query("SELECT * FROM OrderBean WHERE order_stocktakeno = :orderId AND order_roNo = :roNoId AND order_companyId = :companyId")
    fun findById(orderId: String?, roNoId: String?, companyId: String?): List<OrderBean>

    /**
     * 修改
     */
    @Update
    suspend fun update(bean: OrderBean)
}