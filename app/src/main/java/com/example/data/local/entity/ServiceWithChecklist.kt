package com.example.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.example.data.model.DocumentItem
import com.example.data.model.ServiceCategory
import com.example.data.model.ServiceRecord

data class ServiceWithChecklist(
    @Embedded
    val service: ServiceEntity,

    @Relation(
        parentColumn = "serviceId",
        entityColumn = "serviceId"
    )
    val requiredDocuments: List<DocumentChecklistItemEntity>
) {
    fun toDomainModel(): ServiceRecord {
        val categoryEnum = try {
            ServiceCategory.valueOf(service.category)
        } catch (e: Exception) {
            ServiceCategory.REVENUE
        }

        val altNamesList = if (service.altNamesCsv.isBlank()) {
            emptyList()
        } else {
            service.altNamesCsv.split("|").map { it.trim() }.filter { it.isNotEmpty() }
        }

        val domainDocs = requiredDocuments.sortedBy { it.displayOrder }.map { d ->
            DocumentItem(
                id = d.id,
                nameEn = d.nameEn,
                nameTe = d.nameTe,
                explanationEn = d.explanationEn,
                explanationTe = d.explanationTe,
                whereToObtainEn = d.whereToObtainEn,
                whereToObtainTe = d.whereToObtainTe,
                sourceUrl = d.sourceUrl,
                isMandatory = d.isMandatory
            )
        }

        return ServiceRecord(
            id = service.serviceId,
            nameEn = service.nameEn,
            nameTe = service.nameTe,
            altNames = altNamesList,
            category = categoryEnum,
            state = service.state,
            districtRestriction = service.districtRestriction,
            departmentEn = service.departmentEn,
            departmentTe = service.departmentTe,
            issuingAuthorityEn = service.issuingAuthorityEn,
            issuingAuthorityTe = service.issuingAuthorityTe,
            portalName = service.portalName,
            officialPortalUrl = service.officialPortalUrl,
            officialInfoUrl = service.officialInfoUrl,
            statusUrl = service.statusUrl,
            fee = service.fee,
            processingTime = service.processingTime,
            requiredDocuments = domainDocs,
            applicationStepsEn = listOf(
                "Step 1: Check document readiness checklist to ensure all prerequisites are gathered.",
                "Step 2: Visit ${service.portalName} or nearest Grama/Ward Sachivalayam.",
                "Step 3: Submit application with statutory fee of ${service.fee}.",
                "Step 4: Receive Application Acknowledgement Number for status tracking."
            ),
            applicationStepsTe = listOf(
                "దశ 1: అవసరమైన అన్ని ధృవీకరణ పత్రాలు సిద్ధంగా ఉన్నాయో లేదో చెక్‌లిస్ట్ ద్వారా సరిచూసుకోండి.",
                "దశ 2: ${service.portalName} లేదా గ్రామ/వార్డు సచివాలయాన్ని సందర్శించండి.",
                "దశ 3: ${service.fee} నిర్ణీత రుసుము చెల్లించి దరఖాస్తు సమర్పించండి.",
                "దశ 4: అప్లికేషన్ స్టేటస్ ట్రాకింగ్ కోసం రసీదు నంబర్ తీసుకోండి."
            ),
            lastVerifiedDate = service.lastVerifiedDate,
            isVerified = service.isVerified,
            officialVerificationSource = service.officialVerificationSource,
            warningNote = service.warningNote
        )
    }

    companion object {
        fun fromDomainModel(domain: ServiceRecord): Pair<ServiceEntity, List<DocumentChecklistItemEntity>> {
            val entity = ServiceEntity(
                serviceId = domain.id,
                nameEn = domain.nameEn,
                nameTe = domain.nameTe,
                altNamesCsv = domain.altNames.joinToString("|"),
                category = domain.category.name,
                state = domain.state,
                districtRestriction = domain.districtRestriction,
                departmentEn = domain.departmentEn,
                departmentTe = domain.departmentTe,
                issuingAuthorityEn = domain.issuingAuthorityEn,
                issuingAuthorityTe = domain.issuingAuthorityTe,
                portalName = domain.portalName,
                officialPortalUrl = domain.officialPortalUrl,
                officialInfoUrl = domain.officialInfoUrl,
                statusUrl = domain.statusUrl,
                fee = domain.fee,
                processingTime = domain.processingTime,
                lastVerifiedDate = domain.lastVerifiedDate,
                isVerified = domain.isVerified,
                officialVerificationSource = domain.officialVerificationSource,
                warningNote = domain.warningNote
            )

            val docEntities = domain.requiredDocuments.mapIndexed { index, doc ->
                DocumentChecklistItemEntity(
                    id = doc.id,
                    serviceId = domain.id,
                    nameEn = doc.nameEn,
                    nameTe = doc.nameTe,
                    explanationEn = doc.explanationEn,
                    explanationTe = doc.explanationTe,
                    whereToObtainEn = doc.whereToObtainEn,
                    whereToObtainTe = doc.whereToObtainTe,
                    sourceUrl = doc.sourceUrl,
                    isMandatory = doc.isMandatory,
                    displayOrder = index
                )
            }

            return entity to docEntities
        }
    }
}
