package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "service_documents",
    foreignKeys = [
        ForeignKey(
            entity = ServiceEntity::class,
            parentColumns = ["serviceId"],
            childColumns = ["serviceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["serviceId"])]
)
data class DocumentChecklistItemEntity(
    @PrimaryKey
    val id: String,
    val serviceId: String,
    val nameEn: String,
    val nameTe: String,
    val explanationEn: String,
    val explanationTe: String,
    val whereToObtainEn: String,
    val whereToObtainTe: String,
    val sourceUrl: String? = null,
    val isMandatory: Boolean = true,
    val displayOrder: Int = 0
)
