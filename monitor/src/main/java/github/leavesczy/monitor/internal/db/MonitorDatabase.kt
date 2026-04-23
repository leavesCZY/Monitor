package github.leavesczy.monitor.internal.db

import android.content.Context
import androidx.room3.Database
import androidx.room3.ExperimentalRoomApi
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.room3.TypeConverters
import github.leavesczy.monitor.internal.ContextProvider
import java.util.concurrent.TimeUnit

/**
 * @Author: leavesCZY
 * @Date: 2020/11/8 14:43
 * @Desc:
 */
@Database(
    entities = [Monitor::class],
    version = 44
)
@TypeConverters(value = [MonitorTypeConverter::class])
internal abstract class MonitorDatabase : RoomDatabase() {

    companion object {

        private const val MONITOR_DATABASE_NAME = "Monitor"

        const val MONITOR_TABLE_NAME = "Monitor"

        private var monitorDatabase: MonitorDatabase? = null

        val instance: MonitorDatabase
            get() {
                return monitorDatabase ?: synchronized(lock = MonitorDatabase::class.java) {
                    val cachedDatabase = monitorDatabase
                    if (cachedDatabase != null) {
                        cachedDatabase
                    } else {
                        val database = createDb(context = ContextProvider.context)
                        monitorDatabase = database
                        database
                    }
                }
            }

        @OptIn(ExperimentalRoomApi::class)
        private fun createDb(context: Context): MonitorDatabase {
            return Room.databaseBuilder(
                context = context,
                klass = MonitorDatabase::class.java,
                name = MONITOR_DATABASE_NAME
            ).fallbackToDestructiveMigration(dropAllTables = true)
                .setAutoCloseTimeout(
                    autoCloseTimeout = 2,
                    autoCloseTimeUnit = TimeUnit.MINUTES
                )
                .build()
        }

    }

    abstract val monitorDao: MonitorDao

}