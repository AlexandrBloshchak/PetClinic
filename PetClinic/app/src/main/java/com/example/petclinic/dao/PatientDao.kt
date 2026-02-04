package com.example.petclinic.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.petclinic.model.Patient

@Dao
interface PatientDao {

    @Query("SELECT * FROM patients")
    suspend fun getAll(): List<Patient>

    @Query("SELECT * FROM patients WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Patient?

    @Insert
    suspend fun insert(patient: Patient)

    @Update
    suspend fun updatePatient(patient: Patient)

    @Delete
    suspend fun delete(patient: Patient)
}
