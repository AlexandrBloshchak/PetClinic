package com.example.petclinic.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "appointments",
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
data class Appointment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val title: String,      // название записи
    val date: String,       // дата (строкой для простоты)
    val time: String,       // время
    val place: String,      // место
    val patientId: Long?     // ссылка на питомца
)
