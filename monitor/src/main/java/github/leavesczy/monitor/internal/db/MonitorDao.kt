package github.leavesczy.monitor.internal.db

import androidx.paging.PagingSource
import androidx.room3.Dao
import androidx.room3.DaoReturnTypeConverters
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Update
import androidx.room3.paging.PagingSourceDaoReturnTypeConverter
import kotlinx.coroutines.flow.Flow

@Dao
@DaoReturnTypeConverters(PagingSourceDaoReturnTypeConverter::class)
internal interface MonitorDao {

    @Insert
    fun insertRecord(record: MonitorRecord): Long

    @Update
    fun updateRecord(record: MonitorRecord)

    @Insert
    fun insertPayload(payload: MonitorPayload)

    @Update
    fun updatePayload(payload: MonitorPayload)

    @Transaction
    fun insertPending(record: MonitorRecord, payload: MonitorPayload): Long {
        val id = insertRecord(record = record)
        insertPayload(payload = payload.copy(recordId = id))
        return id
    }

    @Transaction
    fun completeRecord(record: MonitorRecord, payload: MonitorPayload) {
        updateRecord(record = record)
        updatePayload(payload = payload)
    }

    @Transaction
    @Query("select * from ${MonitorDatabase.MONITOR_TABLE_NAME} where id = :id")
    suspend fun queryRecordWithPayload(id: Long): MonitorRecordWithPayload

    @Transaction
    @Query("select * from ${MonitorDatabase.MONITOR_TABLE_NAME} where id = :id")
    fun queryRecordWithPayloadAsFlow(id: Long): Flow<MonitorRecordWithPayload>

    @Query(
        """
        select * from ${MonitorDatabase.MONITOR_TABLE_NAME}
        order by id desc
        limit :limit
        """
    )
    fun queryRecords(limit: Int): Flow<List<MonitorRecord>>

    @Query("select * from ${MonitorDatabase.MONITOR_TABLE_NAME} order by id desc")
    fun queryRecords(): PagingSource<Int, MonitorRecord>

    @Query("delete from ${MonitorDatabase.MONITOR_TABLE_NAME}")
    suspend fun deleteAll()

}