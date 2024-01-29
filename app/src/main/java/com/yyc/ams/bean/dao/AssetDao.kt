package com.yyc.ams.bean.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.yyc.ams.bean.db.AssetBean
import kotlinx.coroutines.flow.Flow

/**
 * @Author nike
 * @Date 2023/7/28 10:34
 * @Description
 */
@Dao
interface AssetDao {

    /**
     * 增加
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(vararg bean: AssetBean)

    /**
     * 删除
     */
    @Delete
    suspend fun delete(bean: AssetBean)

    //删除数据库表全部数据
    @Query("delete from AssetBean")
    suspend fun deleteAll()

    //根据用户ID删除全部
    @Query("DELETE FROM AssetBean WHERE asset_roNo = :id AND asset_companyId = :companyId")
    suspend fun deleteById(id: String, companyId: String)

    /**
     * 查询所有
     */
    @Query("SELECT * FROM AssetBean")
    fun findAll(): List<AssetBean>

    /**
     * 根据用户查询所有
     */
    @Query("SELECT * FROM AssetBean WHERE asset_roNo =:roNo AND asset_companyId =:companyId")
    fun findAll(roNo: String, companyId: String): Flow<List<AssetBean>>

    /**
     * 查询所有 根据条件查询
     */
    @Query("SELECT * FROM AssetBean WHERE asset_roNo =:roNo AND asset_orderNo = :orderId AND asset_inventoryStatus = :status AND asset_companyId = :companyId")
    fun findById(roNo: String?, orderId: String?, status: Int, companyId: String?): List<AssetBean>

    /**
     *  查询全部，不包括异常
     */
    @Query("SELECT * FROM AssetBean WHERE asset_roNo =:roNo AND asset_orderNo = :orderId AND asset_inventoryStatus != 2 AND asset_companyId = :companyId")
    fun findByIdNoFail(roNo: String?, orderId: String?, companyId: String?): List<AssetBean>

    /**
     * 根据label跟订单id 查询
     */
    @Query("SELECT * FROM AssetBean WHERE asset_labelTag =:LabelTag AND asset_orderNo = :orderId AND asset_roNo = :userId AND asset_companyId = :companyId AND asset_inventoryStatus != 2")
    fun findLabelTagId(LabelTag: String?, orderId: String?, userId: String?, companyId: String?): AssetBean

     @Query("SELECT * FROM AssetBean WHERE asset_labelTag =:LabelTag AND asset_orderNo = :orderId AND asset_roNo = :userId AND asset_companyId = :companyId AND asset_inventoryStatus = 2")
    fun findFailLabelTagId(LabelTag: String?, orderId: String?, userId: String?, companyId: String?): AssetBean

    /**
     * 根据rfid id 查询
     */
    @Query("SELECT * FROM AssetBean WHERE asset_assetNo =:assetId AND asset_orderNo = :orderId AND asset_roNo = :userId AND asset_companyId = :companyId AND asset_inventoryStatus != 3")
    fun findAssetId(assetId: String?, orderId: String?, userId: String?, companyId: String?): AssetBean

    /**
     * 更新
     */
    @Update
    suspend fun update(bean: AssetBean)

    /**
     *  查询异常订单
     */
    @Query("SELECT * FROM AssetBean WHERE asset_orderNo = :orderId AND asset_inventoryStatus = 2 ")
    fun findFildId( orderId: String?): List<AssetBean>

    /**
     *  根据用户ID和订单查询全部
     */
    @Query("SELECT * FROM AssetBean WHERE asset_orderNo = :orderId AND asset_roNo = :roNoId AND asset_companyId = :companyId AND (asset_inventoryStatus = 1 OR asset_inventoryStatus = 2)")
    fun findById(orderId: String?, roNoId: String?, companyId: String?): List<AssetBean>

    @Query("SELECT * FROM AssetBean WHERE asset_orderNo = :orderId AND asset_roNo = :roNoId AND asset_companyId = :companyId AND asset_inventoryStatus = 2")
    fun findById1(orderId: String?, roNoId: String?, companyId: String?): List<AssetBean>

}