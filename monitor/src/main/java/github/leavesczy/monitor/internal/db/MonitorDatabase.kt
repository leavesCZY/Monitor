package github.leavesczy.monitor.internal.db

import android.content.Context
import androidx.room3.ColumnTypeConverters
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import github.leavesczy.monitor.internal.core.MonitorContextProvider
import java.util.concurrent.TimeUnit

@Database(
    entities = [MonitorRecord::class],
    version = 1
)
@ColumnTypeConverters(value = [MonitorTypeConverter::class])
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
                        val database =
                            createDatabase(context = MonitorContextProvider.requireApplication())
                        monitorDatabase = database
                        database
                    }
                }
            }

        private fun createDatabase(context: Context): MonitorDatabase {
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