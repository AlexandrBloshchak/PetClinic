package com.example.petclinic.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.petclinic.model.Appointment

data class AppointmentWithPet(
    val id: Long,
    val title: String,
    val date: String,
    val time: String,
    val place: String,
    val patientId: Int,
    val petName: String,
    val petType: String,
    val petAge: Int,
    val petPhotoPath: String?,
    val petPhotoUri: String?
)

@Dao
interface AppointmentDao {

    @Insert
    suspend fun insert(appointment: Appointment)

    @Query("""
    SELECT a.id, a.title, a.date, a.time, a.place, a.patientId,
           p.name AS petName, p.animalType AS petType, p.age AS petAge,
           p.photoPath AS petPhotoPath, p.photoUri AS petPhotoUri
    FROM appointments a
    INNER JOIN patients p ON p.id = a.patientId
    ORDER BY a.id DESC
    """)
    suspend fun getAllWithPets(): List<AppointmentWithPet>

    @Query("DELETE FROM appointments WHERE id = :id")
    suspend fun deleteById(id: Long)
}
