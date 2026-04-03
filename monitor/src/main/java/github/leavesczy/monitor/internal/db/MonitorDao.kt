package github.leavesczy.monitor.internal.db

import androidx.paging.PagingSource
import androidx.room3.Dao
import androidx.room3.DaoReturnTypeConverters
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import androidx.room3.paging.PagingSourceDaoReturnTypeConverter
import kotlinx.coroutines.flow.Flow

/**
 * @Author: leavesCZY
 * @Date: 2020/11/14 16:14
 * @Desc:
 */
@Dao
@DaoReturnTypeConverters(PagingSourceDaoReturnTypeConverter::class)
internal interface MonitorDao {

    @Insert
    fun insertMonitor(monitor: Monitor): Long

    @Update
    fun updateMonitor(monitor: Monitor)

    @Query("select * from ${MonitorDatabase.MONITOR_TABLE_NAME} where id =:id")
    suspend fun queryMonitor(id: Long): Monitor

    @Query("select * from ${MonitorDatabase.MONITOR_TABLE_NAME} where id =:id")
    fun queryMonitorAsFlow(id: Long): Flow<Monitor>

    @Query("select * from ${MonitorDatabase.MONITOR_TABLE_NAME} order by id desc limit :limit")
    fun queryMonitors(limit: Int): Flow<List<Monitor>>

    @Query("select * from ${MonitorDatabase.MONITOR_TABLE_NAME} order by id desc")
    fun queryMonitors(): PagingSource<Int, Monitor>

    @Query("delete from ${MonitorDatabase.MONITOR_TABLE_NAME}")
    suspend fun deleteAll()

}