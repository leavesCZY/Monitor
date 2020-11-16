package github.leavesc.monitor.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

/**
 * @Author: leavesC
 * @Date: 2020/11/14 16:14
 * @Desc:
 */
@Dao
interface MonitorHttpInformationDao {

    @Insert
    fun insert(model: HttpInformation): Long

    @Update
    fun update(model: HttpInformation)

    @Query("SELECT * FROM monitor_httpInformation WHERE id =:id")
    fun queryRecordObservable(id: Long): LiveData<HttpInformation>

    @Query("SELECT * FROM monitor_httpInformation")
    fun queryAllRecord(): List<HttpInformation>

    @Query("SELECT * FROM monitor_httpInformation order by id desc limit :limit")
    fun queryAllRecordObservable(limit: Int): LiveData<List<HttpInformation>>

    @Query("SELECT * FROM monitor_httpInformation order by id desc")
    fun queryAllRecordObservable(): LiveData<List<HttpInformation>>

    @Query("DELETE FROM monitor_httpInformation")
    fun deleteAll()

}