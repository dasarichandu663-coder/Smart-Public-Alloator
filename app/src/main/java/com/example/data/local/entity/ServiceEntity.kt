package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey
    val serviceId: String,
    val nameEn: String,
    val nameTe: String,
    val altNamesCsv: String = "",
    val category: String,
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
    val lastVerifiedDate: String,
    val isVerified: Boolean = true,
    val officialVerificationSource: String,
    val warningNote: String = "Requirements may change. Confirm the latest details on the official portal or with an authorized MeeSeva centre."
)
