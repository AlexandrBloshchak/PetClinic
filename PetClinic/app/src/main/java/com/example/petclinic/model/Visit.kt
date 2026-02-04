package com.example.petclinic.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "visits",
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
data class Visit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val patientId: Int,
    val date: String,         // "03.02.2026"
    val doctor: String,       // "Иванов И.И."
    val diagnosis: String,    // "Отит"
    val treatment: String?    // "Капли 7 дней"
)