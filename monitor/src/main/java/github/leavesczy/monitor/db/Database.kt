package github.leavesczy.monitor.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import github.leavesczy.monitor.holder.ContextHolder

/**
 * @Author: leavesCZY
 * @Date: 2020/11/8 14:43
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Database(entities = [HttpInformation::class], version = 7, exportSchema = false)
@TypeConverters(Converters::class)
internal abstract class MonitorHttpInformationDatabase : RoomDatabase() {

    abstract val httpInformationDao: MonitorHttpInformationDao

    companion object {

        private const val DB_NAME = "MonitorHttpInformation.db"

        val INSTANCE: MonitorHttpInformationDatabase by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            create(ContextHolder.context)
        }

        private fun create(context: Context): MonitorHttpInformationDatabase {
            return Room.databaseBuilder(
                context,
                MonitorHttpInformationDatabase::class.java,
                DB_NAME
            ).fallbackToDestructiveMigration()
                .build()
        }
    }

}