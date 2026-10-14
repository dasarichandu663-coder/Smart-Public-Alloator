package com.example.data.model

enum class Language(val code: String, val displayName: String) {
    EN("en", "English"),
    TE("te", "తెలుగు")
}

enum class ServiceCategory(val displayNameEn: String, val displayNameTe: String) {
    REVENUE("Revenue Department", "రెవెన్యూ శాఖ"),
    CIVIL_SUPPLIES("Civil Supplies & Food", "పౌర సరఫరాలు మరియు ఆహార శాఖ"),
    MUNICIPAL("Municipal & Panchayat", "మున్సిపల్ & పంచాయతీ రాజ్"),
    TRANSPORT("Transport & Driving", "రవాణా శాఖ"),
    SOCIAL_WELFARE("Social Welfare", "సాంఘిక సంక్షేమ శాఖ"),
    GENERAL("General Public Services", "సాధారణ పౌర సేవలు")
}

data class DocumentItem(
    val id: String,
    val nameEn: String,
    val nameTe: String,
    val explanationEn: String,
    val explanationTe: String,
    val whereToObtainEn: String,
    val whereToObtainTe: String,
    val sourceUrl: String? = null,
    val isMandatory: Boolean = true
)

data class ServiceRecord(
    val id: String,
    val nameEn: String,
    val nameTe: String,
    val altNames: List<String> = emptyList(),
    val category: ServiceCategory,
    val state: String = "Andhra Pradesh",
    val districtRestriction: String = "All Districts",
    val departmentEn: String,
    val departmentTe: String,
    val issuingAuthorityEn: String,
    val issuingAuthorityTe: String,
    val portalName: String,
    val officialPortalUrl: String,
    val officialInfoUrl: String,
    val statusUrl: String? = null,
    val fee: String,
    val processingTime: String,
    val requiredDocuments: List<DocumentItem>,
    val applicationStepsEn: List<String>,
    val applicationStepsTe: List<String>,
    val lastVerifiedDate: String,
    val isVerified: Boolean = true,
    val officialVerificationSource: String,
    val warningNote: String = "Requirements may change. Confirm the latest details on the official portal or with an authorized MeeSeva centre."
)

enum class TaskPriority(val displayName: String, val weight: Int) {
    URGENT("Urgent", 4),
    HIGH("High", 3),
    MEDIUM("Medium", 2),
    LOW("Low", 1)
}

enum class TaskStatus {
    PENDING,
    ASSIGNED,
    IN_PROGRESS,
    COMPLETED
}

data class TaskRecord(
    val id: String,
    val title: String,
    val citizenName: String,
    val category: ServiceCategory,
    val priority: TaskPriority,
    val effortHours: Double,
    val status: TaskStatus = TaskStatus.PENDING,
    val assignedStaffId: String? = null,
    val assignedStaffName: String? = null,
    val assignmentReason: String? = null,
    val createdAt: String = "2026-10-14"
)

data class StaffMember(
    val id: String,
    val name: String,
    val designation: String,
    val eligibleCategories: List<ServiceCategory>,
    val maxDailyCapacityHours: Double = 8.0,
    val initialWorkloadHours: Double = 0.0,
    var currentWorkloadHours: Double = 0.0,
    val isAvailable: Boolean = true
)

data class AllocationResult(
    val algorithmName: String,
    val assignedTasks: List<TaskRecord>,
    val unassignedTasks: List<TaskRecord>,
    val staffWorkloadsBefore: Map<String, Double>,
    val staffWorkloadsAfter: Map<String, Double>,
    val stdDevBefore: Double,
    val stdDevAfter: Double,
    val maxMinSpreadBefore: Double,
    val maxMinSpreadAfter: Double,
    val ruleViolationsCount: Int,
    val ruleViolationDetails: List<String>,
    val executionTimeMs: Long,
    val isApprovedByAdmin: Boolean = false
)

data class DemoAppointment(
    val id: String,
    val tokenNumber: String,
    val citizenName: String,
    val phone: String,
    val serviceName: String,
    val centerName: String,
    val appointmentDate: String,
    val slotTime: String,
    val status: String = "CONFIRMED (DEMO)",
    val createdAt: String = "2026-10-14"
)

data class ReportedIssue(
    val id: String,
    val serviceName: String,
    val issueType: String,
    val description: String,
    val contactEmail: String,
    val date: String
)

data class BenchmarkedMetric(
    val scenarioNumber: Int,
    val title: String,
    val metricLabel: String,
    var measuredValue: String,
    val targetGoal: String,
    var isPassed: Boolean = true,
    var notes: String = ""
)
