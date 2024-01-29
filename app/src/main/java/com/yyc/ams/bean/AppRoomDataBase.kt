package com.yyc.ams.bean

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.yyc.ams.bean.dao.AssetDao
import com.yyc.ams.bean.dao.ConfigDao
import com.yyc.ams.bean.dao.OrderDao
import com.yyc.ams.bean.dao.RfidDao
import com.yyc.ams.bean.dao.RfidFileDao
import com.yyc.ams.bean.dao.UploadOrderDao
import com.yyc.ams.bean.dao.UploadOrderListDao
import com.yyc.ams.bean.db.AssetBean
import com.yyc.ams.bean.db.ConfigBean
import com.yyc.ams.bean.db.OrderBean
import com.yyc.ams.bean.db.RfidBean
import com.yyc.ams.bean.db.RfidFileBean
import com.yyc.ams.bean.db.UploadOrderBean
import com.yyc.ams.bean.db.UploadOrderListBean
import com.yyc.ams.mar.MyApplication


/**
 * @Author nike
 * @Date 2023/7/11 18:00
 * @Description
 */
@Database(entities = [OrderBean::class,
                     AssetBean::class,
                     UploadOrderBean::class,
                     UploadOrderListBean::class,
                     RfidFileBean::class,
                     RfidBean::class,
                     ConfigBean::class,
                     ], version = 1, exportSchema = false)
abstract class AppRoomDataBase : RoomDatabase() {

    abstract fun getOrderDao(): OrderDao

    abstract fun getAssetDao(): AssetDao

    abstract fun getUploadOrderDao(): UploadOrderDao

    abstract fun getUploadOrderListDao(): UploadOrderListDao

    abstract fun getRfidFlieDao(): RfidFileDao

    abstract fun getRfidDao(): RfidDao

    abstract fun getConfigDao(): ConfigDao

    companion object {

        @Volatile
        private var databaseInstance: AppRoomDataBase? = null

        @Synchronized
        @JvmStatic
        fun get(): AppRoomDataBase {
            if (databaseInstance == null) {
                databaseInstance = Room.databaseBuilder(
                    MyApplication.get(),
                    AppRoomDataBase::class.java,
                    "AMS_APP.db"
                )
                    /*.addMigrations(
                        object : Migration(1, 2) {
                        override fun migrate(database: SupportSQLiteDatabase) {
                            // 执行从旧版本升级到新版本的 SQL 语句
//                            database.execSQL("CREATE TABLE IF NOT EXISTS 'OrderBean' (id INTEGER PRIMARY KEY, description TEXT)")
                        }
                    })*/
//                    .allowMainThreadQueries()//允许在主线程操作数据库，一般不推荐；设置这个后主线程调用增删改查不会报错，否则会报错
//                    .openHelperFactory(factory)
                    .build()
            }
            return databaseInstance!!
        }

    }

}