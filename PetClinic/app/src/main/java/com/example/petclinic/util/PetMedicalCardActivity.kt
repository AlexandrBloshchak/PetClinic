package com.example.petclinic.util

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.petclinic.data.AppDatabase
import com.example.petclinic.databinding.ActivityPetMedicalCardBinding
import com.example.petclinic.model.Vaccination
import com.example.petclinic.model.Visit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Calendar

class PetMedicalCardActivity : AppCompatActivity() {

    private lateinit var b: ActivityPetMedicalCardBinding
    private lateinit var db: AppDatabase
    private val vAdapter = VaccinationAdapter()
    private val visitAdapter = VisitAdapter()

    private var patientId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityPetMedicalCardBinding.inflate(layoutInflater)
        setContentView(b.root)

        db = AppDatabase.getDatabase(this)
        patientId = intent.getIntExtra("patientId", -1)
        if (patientId == -1) {
            finish()
            return
        }

        b.rvVaccinations.layoutManager = LinearLayoutManager(this)
        b.rvVaccinations.adapter = vAdapter

        b.rvVisits.layoutManager = LinearLayoutManager(this)
        b.rvVisits.adapter = visitAdapter

        b.btnAddVaccination.setOnClickListener { showAddVaccinationDialog() }
        b.btnAddVisit.setOnClickListener { showAddVisitDialog() }
        b.btnSetSterilization.setOnClickListener { showSterilizationDialog() }
    }

    override fun onResume() {
        super.onResume()
        loadAll()
    }

    private fun loadAll() {
        lifecycleScope.launch {
            val patient = withContext(Dispatchers.IO) { db.patientDao().getById(patientId) }
            if (patient == null) {
                finish()
                return@launch
            }

            b.tvName.text = patient.name
            b.tvInfo.text = "${patient.animalType} • ${patient.age} лет • ${patient.breed}"

            when {
                !patient.photoPath.isNullOrBlank() -> {
                    runCatching {
                        b.ivPhoto.setImageURI(Uri.fromFile(File(patient.photoPath)))
                    }
                }
                !patient.photoUri.isNullOrBlank() -> {
                    runCatching { b.ivPhoto.setImageURI(Uri.parse(patient.photoUri)) }
                }
                else -> {
                    b.ivPhoto.setImageResource(android.R.drawable.ic_menu_report_image)
                }
            }

            b.tvSterilization.text = if (patient.isSterilized) {
                "Да • ${patient.sterilizationDate ?: "дата не указана"}\n${patient.sterilizationNotes ?: ""}".trim()
            } else {
                "Нет"
            }

            val vaccs = withContext(Dispatchers.IO) { db.vaccinationDao().getByPatient(patientId) }
            val visits = withContext(Dispatchers.IO) { db.visitDao().getByPatient(patientId) }

            vAdapter.submitList(vaccs)
            visitAdapter.submitList(visits)
        }
    }
    private fun calculateNextVaccinationDate(doneDate: String, vaccineName: String): String? {
        val parts = doneDate.split(".")
        if (parts.size != 3) return null

        val day = parts[0].toIntOrNull() ?: return null
        val month = parts[1].toIntOrNull() ?: return null
        val year = parts[2].toIntOrNull() ?: return null

        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, day)
        }

        cal.add(Calendar.YEAR, 1)

        val nd = cal.get(Calendar.DAY_OF_MONTH)
        val nm = cal.get(Calendar.MONTH) + 1
        val ny = cal.get(Calendar.YEAR)
        return "%02d.%02d.%04d".format(nd, nm, ny)
    }
    private fun showAddVaccinationDialog() {
        val vName = EditText(this).apply { hint = "Название прививки (например, Бешенство)" }
        val vDone = EditText(this).apply { hint = "Дата (например, 03.02.2026)" }

        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(48, 24, 48, 0)
            addView(vName); addView(vDone);
        }

        AlertDialog.Builder(this)
            .setTitle("Добавить прививку")
            .setView(layout)
            .setPositiveButton("Сохранить") { _, _ ->
                val name = vName.text.toString().trim()
                val done = vDone.text.toString().trim()

                if (name.isEmpty() || done.isEmpty()) {
                    Toast.makeText(this, "Заполни поля", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val next = calculateNextVaccinationDate(done, name)

                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        db.vaccinationDao().insert(
                            Vaccination(
                                patientId = patientId,
                                vaccineName = name,
                                doneDate = done,
                                nextDate = next
                            )
                        )
                    }
                    Toast.makeText(this@PetMedicalCardActivity, "Прививка добавлена", Toast.LENGTH_SHORT).show()
                }

                if (name.isEmpty() || done.isEmpty()) {
                    toast("Заполните название и дату")
                    return@setPositiveButton
                }

                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        db.vaccinationDao().insert(
                            Vaccination(patientId = patientId, vaccineName = name, doneDate = done, nextDate = next)
                        )
                    }
                    loadAll()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showAddVisitDialog() {
        val eDate = EditText(this).apply { hint = "Дата (например, 03.02.2026)" }
        val eDoctor = EditText(this).apply { hint = "Доктор (ФИО)" }
        val eDiagnosis = EditText(this).apply { hint = "Диагноз/болезнь" }
        val eTreat = EditText(this).apply { hint = "Лечение/заметки (необязательно)" }

        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(48, 24, 48, 0)
            addView(eDate); addView(eDoctor); addView(eDiagnosis); addView(eTreat)
        }

        AlertDialog.Builder(this)
            .setTitle("Добавить посещение")
            .setView(layout)
            .setPositiveButton("Сохранить") { _, _ ->
                val date = eDate.text.toString().trim()
                val doctor = eDoctor.text.toString().trim()
                val diagnosis = eDiagnosis.text.toString().trim()
                val treat = eTreat.text.toString().trim().ifBlank { null }

                if (date.isEmpty() || doctor.isEmpty() || diagnosis.isEmpty()) {
                    toast("Заполните дату, доктора и диагноз")
                    return@setPositiveButton
                }

                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        db.visitDao().insert(
                            Visit(patientId = patientId, date = date, doctor = doctor, diagnosis = diagnosis, treatment = treat)
                        )
                    }
                    loadAll()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showSterilizationDialog() {
        val eDate = EditText(this).apply { hint = "Дата (например, 03.02.2026)" }
        val eNotes = EditText(this).apply { hint = "Комментарий (необязательно)" }

        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(48, 24, 48, 0)
            addView(eDate); addView(eNotes)
        }

        AlertDialog.Builder(this)
            .setTitle("Стерилизация/кастрация")
            .setView(layout)
            .setPositiveButton("Сохранить") { _, _ ->
                val date = eDate.text.toString().trim()
                val notes = eNotes.text.toString().trim().ifBlank { null }

                if (date.isEmpty()) {
                    toast("Укажите дату")
                    return@setPositiveButton
                }

                lifecycleScope.launch {
                    val patient = withContext(Dispatchers.IO) { db.patientDao().getById(patientId) } ?: return@launch
                    withContext(Dispatchers.IO) {
                        db.patientDao().updatePatient(
                            patient.copy(
                                isSterilized = true,
                                sterilizationDate = date,
                                sterilizationNotes = notes
                            )
                        )
                    }
                    loadAll()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
