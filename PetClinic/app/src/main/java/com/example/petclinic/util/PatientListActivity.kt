package com.example.petclinic.util

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petclinic.R
import com.example.petclinic.data.AppDatabase
import kotlinx.coroutines.launch

class PatientListActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var adapter: PatientAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_list)

        db = AppDatabase.getDatabase(this)

        val patientAdapter = PatientAdapter { patient ->
            val intent = Intent(this@PatientListActivity, PetMedicalCardActivity::class.java)
            intent.putExtra("PATIENT_ID", patient.id)
            startActivity(intent)
        }

        findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(this@PatientListActivity)
            adapter = patientAdapter
        }
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            val patients = db.patientDao().getAll()
            adapter.submitList(patients)
        }
    }
}
