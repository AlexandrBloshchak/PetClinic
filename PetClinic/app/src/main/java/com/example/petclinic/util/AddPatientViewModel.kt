package com.example.petclinic.util

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.petclinic.PatientRepository.PatientRepository
import com.example.petclinic.data.AppDatabase
import com.example.petclinic.model.Patient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddPatientViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PatientRepository =
        PatientRepository(AppDatabase.getDatabase(application).patientDao())

    fun insert(patient: Patient) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(patient)
        }
    }
}