package com.example.petclinic.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.petclinic.model.Vaccination

@Dao
interface VaccinationDao {

    @Query("SELECT * FROM vaccinations WHERE patientId = :patientId ORDER BY id DESC")
    suspend fun getByPatient(patientId: Int): List<Vaccination>

    @Insert
    suspend fun insert(v: Vaccination)

    @Query("DELETE FROM vaccinations WHERE id = :id")
    suspend fun deleteById(id: Long)
}