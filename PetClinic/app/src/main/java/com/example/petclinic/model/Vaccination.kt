package com.example.petclinic.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "vaccinations",
    foreignKeys = [
        ForeignKey(
            entity = Patient::class,
            parentColumns = ["id"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("patientId")]
)
data class Vaccination(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val patientId: Int,
    val vaccineName: String,
    val doneDate: String,     // "03.02.2026"
    val nextDate: String?     // "03.02.2027" или null
)