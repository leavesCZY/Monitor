package github.leavesczy.monitor.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * @Author: leavesCZY
 * @Date: 2020/11/14 16:14
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Dao
internal interface MonitorDao {

    @Insert
    fun insert(model: MonitorHttp): Long

    @Update
    fun update(model: MonitorHttp)

    @Query("select * from ${MonitorDatabase.MonitorTableName} where id =:id")
    fun queryRecord(id: Long): Flow<MonitorHttp>

    @Query("select * from ${MonitorDatabase.MonitorTableName} order by id desc limit :limit")
    fun queryRecord(limit: Int): Flow<List<MonitorHttp>>

    @Query("delete from ${MonitorDatabase.MonitorTableName}")
    suspend fun deleteAll()

}