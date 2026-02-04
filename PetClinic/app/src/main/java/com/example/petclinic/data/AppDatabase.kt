package com.example.petclinic.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.petclinic.dao.AppointmentDao
import com.example.petclinic.dao.PatientDao
import com.example.petclinic.dao.VaccinationDao
import com.example.petclinic.dao.VisitDao
import com.example.petclinic.model.Appointment
import com.example.petclinic.model.Patient
import com.example.petclinic.model.Vaccination
import com.example.petclinic.model.Visit

@Database(
    entities = [Patient::class, Appointment::class, Vaccination::class, Visit::class],
    version = 5, // <-- увеличили версию (было 3)
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun patientDao(): PatientDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun vaccinationDao(): VaccinationDao
    abstract fun visitDao(): VisitDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "petclinic_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
