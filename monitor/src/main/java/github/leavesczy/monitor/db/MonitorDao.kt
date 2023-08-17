package github.leavesczy.monitor.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

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

    @Query("SELECT * FROM ${MonitorDatabase.MonitorTableName} WHERE id =:id")
    fun queryRecord(id: Long): LiveData<MonitorHttp>

    @Query("SELECT * FROM ${MonitorDatabase.MonitorTableName} order by id desc limit :limit")
    fun queryRecord(limit: Int): LiveData<List<MonitorHttp>>

    @Query("DELETE FROM ${MonitorDatabase.MonitorTableName}")
    fun deleteAll()

}