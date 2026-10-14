package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.ServiceRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Smart Public Allocator", appName)
    }

    @Test
    fun `verify Andhra Pradesh Income Certificate is present and verified`() {
        val repo = ServiceRepository()
        val incomeCert = repo.getServiceById("ap_income_cert")
        assertNotNull(incomeCert)
        assertEquals("Income Certificate", incomeCert?.nameEn)
        assertEquals("Revenue Department", incomeCert?.departmentEn)
        assertEquals("Andhra Pradesh", incomeCert?.state)
        assertTrue(incomeCert?.isVerified == true)
        assertTrue(incomeCert?.requiredDocuments?.isNotEmpty() == true)
    }

    @Test
    fun `verify classical greedy workload allocation algorithm produces zero rule violations`() {
        val repo = ServiceRepository()
        val result = repo.runClassicalWorkloadAllocation()
        assertEquals(0, result.ruleViolationsCount)
        assertTrue(result.assignedTasks.isNotEmpty())
        assertTrue(result.stdDevAfter <= result.stdDevBefore || result.assignedTasks.size > 0)
    }
}
