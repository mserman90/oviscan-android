package com.oviscan.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.oviscan.app.data.local.dao.MeasurementDao
import com.oviscan.app.data.local.dao.SheepDao
import com.oviscan.app.data.local.entity.MeasurementEntity
import com.oviscan.app.data.local.entity.SheepEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [SheepEntity::class, MeasurementEntity::class],
    version = 1,
    exportSchema = false
)
abstract class OviScanDatabase : RoomDatabase() {

    abstract fun sheepDao(): SheepDao
    abstract fun measurementDao(): MeasurementDao

    companion object {
        @Volatile
        private var INSTANCE: OviScanDatabase? = null

        fun getDatabase(context: Context): OviScanDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OviScanDatabase::class.java,
                    "oviscan_database"
                )
                .addCallback(DatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Prepopulate with realistic sheep flock data
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        prepopulateHerd(database.sheepDao())
                    }
                }
            }

            suspend fun prepopulateHerd(dao: SheepDao) {
                val now = System.currentTimeMillis()
                val oneDay = 24 * 60 * 60 * 1000L

                val demoSheep = listOf(
                    SheepEntity("TR06-0842", "Akkaraman", 3, now - (28 * oneDay), "İlk tohumlama suni, genel durum iyi."),
                    SheepEntity("TR06-0843", "Akkaraman", 2, now - (34 * oneDay), "İkiz doğum geçmişi var."),
                    SheepEntity("TR06-0915", "Morkaraman", 4, now - (26 * oneDay), "Koç katımı dönemi."),
                    SheepEntity("TR06-1022", "İvesi", 2, now - (45 * oneDay), "Süt verimi yüksek anaç."),
                    SheepEntity("TR06-1140", "Merinos Melezi", 3, now - (19 * oneDay), "Kızgınlık takibi yapılıyor.")
                )
                demoSheep.forEach { dao.insertSheep(it) }
            }
        }
    }
}
