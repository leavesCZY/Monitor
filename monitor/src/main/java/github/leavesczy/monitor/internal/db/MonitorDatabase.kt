package github.leavesczy.monitor.internal.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import github.leavesczy.monitor.internal.ContextProvider
import java.util.concurrent.TimeUnit

/**
 * @Author: leavesCZY
 * @Date: 2020/11/8 14:43
 * @Desc:
 */
@Database(
    entities = [Monitor::class],
    version = 42
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
                    val cache = monitorDatabase
                    if (cache != null) {
                        return@synchronized cache
                    }
                    val db = createDb(context = ContextProvider.context)
                    monitorDatabase = db
                    return@synchronized db
                }
            }

        private fun createDb(context: Context): MonitorDatabase {
            return Room.databaseBuilder(
                context,
                MonitorDatabase::class.java,
                MONITOR_DATABASE_NAME
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