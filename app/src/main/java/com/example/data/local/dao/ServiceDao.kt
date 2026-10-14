package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.DocumentChecklistItemEntity
import com.example.data.local.entity.ServiceEntity
import com.example.data.local.entity.ServiceWithChecklist
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceDao {

    @Transaction
    @Query("SELECT * FROM services ORDER BY isVerified DESC, nameEn ASC")
    fun getAllServicesWithChecklists(): Flow<List<ServiceWithChecklist>>

    @Transaction
    @Query("SELECT * FROM services WHERE serviceId = :serviceId LIMIT 1")
    fun getServiceWithChecklistById(serviceId: String): Flow<ServiceWithChecklist?>

    @Transaction
    @Query("""
        SELECT * FROM services 
        WHERE nameEn LIKE '%' || :query || '%' 
           OR nameTe LIKE '%' || :query || '%' 
           OR departmentEn LIKE '%' || :query || '%' 
           OR departmentTe LIKE '%' || :query || '%'
           OR altNamesCsv LIKE '%' || :query || '%'
        ORDER BY isVerified DESC, nameEn ASC
    """)
    fun searchServices(query: String): Flow<List<ServiceWithChecklist>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: ServiceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocuments(documents: List<DocumentChecklistItemEntity>)

    @Query("DELETE FROM service_documents WHERE serviceId = :serviceId")
    suspend fun deleteDocumentsForService(serviceId: String)

    @Transaction
    suspend fun upsertFullService(service: ServiceEntity, documents: List<DocumentChecklistItemEntity>) {
        insertService(service)
        deleteDocumentsForService(service.serviceId)
        insertDocuments(documents)
    }

    @Query("UPDATE services SET isVerified = :isVerified, lastVerifiedDate = :lastVerifiedDate WHERE serviceId = :serviceId")
    suspend fun updateVerificationStatus(serviceId: String, isVerified: Boolean, lastVerifiedDate: String)

    @Query("DELETE FROM services WHERE serviceId = :serviceId")
    suspend fun deleteService(serviceId: String)

    @Query("SELECT COUNT(*) FROM services")
    suspend fun getServicesCount(): Int
}
