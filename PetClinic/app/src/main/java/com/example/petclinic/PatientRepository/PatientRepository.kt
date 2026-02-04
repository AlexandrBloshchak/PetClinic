package com.example.petclinic.PatientRepository

import com.example.petclinic.dao.PatientDao
import com.example.petclinic.model.Patient

class PatientRepository(private val dao: PatientDao) {


    suspend fun insert(patient: Patient) {
        dao.insert(patient)
    }

    suspend fun getAll(): List<Patient> {
        return dao.getAll()
    }

    suspend fun update(patient: Patient) {
        dao.updatePatient(patient)
    }

    suspend fun delete(patient: Patient) {
        dao.delete(patient)
    }
}
