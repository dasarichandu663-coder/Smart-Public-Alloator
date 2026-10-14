package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.ServiceDao
import com.example.data.local.entity.DocumentChecklistItemEntity
import com.example.data.local.entity.ServiceEntity
import com.example.data.local.entity.ServiceWithChecklist
import com.example.data.repository.ServiceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [ServiceEntity::class, DocumentChecklistItemEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ServiceDatabase : RoomDatabase() {

    abstract fun serviceDao(): ServiceDao

    companion object {
        @Volatile
        private var INSTANCE: ServiceDatabase? = null

        fun getInstance(context: Context): ServiceDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ServiceDatabase::class.java,
                    "service_knowledge_base.db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Prepopulate with verified civic services
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    prepopulateDatabase(database.serviceDao())
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun prepopulateDatabase(dao: ServiceDao) {
            val initialServices = ServiceRepository.getInitialServices()
            initialServices.forEach { serviceRecord ->
                val (serviceEntity, docEntities) = ServiceWithChecklist.fromDomainModel(serviceRecord)
                dao.upsertFullService(serviceEntity, docEntities)
            }
        }
    }
}
