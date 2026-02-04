package com.example.petclinic.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patients")
data class Patient(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,
    val animalType: String,
    val breed: String,
    val age: Int,
    val ownerName: String,
    val birthDateMillis: Long = 0L,
    val photoUri: String? = null,
    val photoPath: String? = null,

    val isSterilized: Boolean = false,
    val sterilizationDate: String? = null,
    val sterilizationNotes: String? = null
)
