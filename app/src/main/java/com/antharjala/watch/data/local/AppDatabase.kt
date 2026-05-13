package com.antharjala.watch.data.local

import android.content.Context
import androidx.room.*
import com.antharjala.watch.data.model.AlertItem
import com.antharjala.watch.data.model.AlertSeverity
import com.antharjala.watch.data.model.BorewellEntry

class Converters {
    @TypeConverter
    fun fromSeverity(value: AlertSeverity): String = value.name

    @TypeConverter
    fun toSeverity(value: String): AlertSeverity = AlertSeverity.valueOf(value)
}

@Database(
    entities = [BorewellEntry::class, AlertItem::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun borewellDao(): BorewellDao
    abstract fun alertDao(): AlertDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "anthar_jala_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
