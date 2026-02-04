package com.example.petclinic.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.petclinic.model.Visit

@Dao
interface VisitDao {

    @Query("SELECT * FROM visits WHERE patientId = :patientId ORDER BY id DESC")
    suspend fun getByPatient(patientId: Int): List<Visit>

    @Insert
    suspend fun insert(v: Visit)

    @Query("DELETE FROM visits WHERE id = :id")
    suspend fun deleteById(id: Long)
}