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
    fun insert(model: Monitor): Long

    @Update
    fun update(model: Monitor)

    @Query("select * from ${MonitorDatabase.MonitorTableName} where id =:id")
    suspend fun query(id: Long): Monitor

    @Query("select * from ${MonitorDatabase.MonitorTableName} where id =:id")
    fun queryFlow(id: Long): Flow<Monitor>

    @Query("select * from ${MonitorDatabase.MonitorTableName} order by id desc limit :limit")
    fun queryFlow(limit: Int): Flow<List<Monitor>>

    @Query("delete from ${MonitorDatabase.MonitorTableName}")
    suspend fun deleteAll()

}