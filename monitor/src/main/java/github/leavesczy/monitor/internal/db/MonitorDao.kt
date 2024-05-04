package github.leavesczy.monitor.internal.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * @Author: leavesCZY
 * @Date: 2020/11/14 16:14
 * @Desc:
 */
@Dao
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