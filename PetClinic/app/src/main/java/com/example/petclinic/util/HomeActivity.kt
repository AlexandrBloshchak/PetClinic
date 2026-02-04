package com.example.petclinic.util

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.petclinic.data.AppDatabase
import com.example.petclinic.databinding.ActivityHomeBinding
import com.example.petclinic.util.PatientAdapter
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var b: ActivityHomeBinding
    private lateinit var db: AppDatabase
    private val adapter = PatientAdapter { patient ->
        val i = Intent(this, PetMedicalCardActivity::class.java)
        i.putExtra("patientId", patient.id)
        startActivity(i)
    }

    private val appointmentAdapter = AppointmentAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.rvAppointments.layoutManager = LinearLayoutManager(this)
        b.rvAppointments.adapter = appointmentAdapter

        b.btnMakeAppointment.setOnClickListener {
            startActivity(Intent(this, AddAppointmentActivity::class.java))
        }
        db = AppDatabase.getDatabase(this)

        val sp = getSharedPreferences("auth", MODE_PRIVATE)
        b.tvOwnerName.text = sp.getString("fio", "Хозяин") ?: "Хозяин"

        b.rvMyPets.layoutManager = LinearLayoutManager(this)
        b.rvMyPets.adapter = adapter

        b.btnAddPet.setOnClickListener {
            startActivity(Intent(this, com.example.petclinic.util.AddPatientActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            val pets = db.patientDao().getAll()
            adapter.submitList(pets)

            val apps = db.appointmentDao().getAllWithPets()
            appointmentAdapter.submitList(apps)
        }
    }
}
