package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.ServiceDatabase
import com.example.data.local.dao.ServiceDao
import com.example.data.local.entity.DocumentChecklistItemEntity
import com.example.data.local.entity.ServiceEntity
import com.example.data.local.entity.ServiceWithChecklist
import com.example.data.repository.ServiceRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ServiceRoomDatabaseTest {

    private lateinit var db: ServiceDatabase
    private lateinit var dao: ServiceDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, ServiceDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.serviceDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndRetrieveServiceWithChecklist() = runBlocking {
        val initialServices = ServiceRepository.getInitialServices()
        val incomeCert = initialServices.first { it.id == "ap_income_cert" }

        val (serviceEntity, docEntities) = ServiceWithChecklist.fromDomainModel(incomeCert)
        dao.upsertFullService(serviceEntity, docEntities)

        val retrieved = dao.getServiceWithChecklistById("ap_income_cert").first()
        assertNotNull(retrieved)
        assertEquals("ap_income_cert", retrieved?.service?.serviceId)
        assertEquals("Income Certificate", retrieved?.service?.nameEn)
        assertEquals("Revenue Department", retrieved?.service?.departmentEn)
        assertEquals("https://onlineap.meeseva.gov.in/CitizenPortal/userinterface/citizen/Home.aspx", retrieved?.service?.officialPortalUrl)
        assertTrue(retrieved?.service?.isVerified == true)

        // Verify Document checklist items
        assertEquals(5, retrieved?.requiredDocuments?.size)
        val docNames = retrieved?.requiredDocuments?.map { it.nameEn } ?: emptyList()
        assertTrue(docNames.any { it.contains("Application Form") })
        assertTrue(docNames.any { it.contains("Aadhaar") })
        assertTrue(docNames.any { it.contains("Salary") })
    }

    @Test
    fun searchServicesInRoomDatabase() = runBlocking {
        ServiceDatabase.prepopulateDatabase(dao)

        val searchResult = dao.searchServices("Revenue").first()
        assertTrue(searchResult.isNotEmpty())
        assertTrue(searchResult.any { it.service.serviceId == "ap_income_cert" })

        val domainModel = searchResult.first { it.service.serviceId == "ap_income_cert" }.toDomainModel()
        assertEquals("Income Certificate", domainModel.nameEn)
        assertEquals("Revenue Department", domainModel.departmentEn)
    }

    @Test
    fun updateVerificationStatus() = runBlocking {
        val initialServices = ServiceRepository.getInitialServices()
        val (serviceEntity, docEntities) = ServiceWithChecklist.fromDomainModel(initialServices.first())
        dao.upsertFullService(serviceEntity, docEntities)

        dao.updateVerificationStatus(serviceEntity.serviceId, false, "Demo — verification required")

        val updated = dao.getServiceWithChecklistById(serviceEntity.serviceId).first()
        assertFalse(updated?.service?.isVerified ?: true)
        assertEquals("Demo — verification required", updated?.service?.lastVerifiedDate)
    }
}
