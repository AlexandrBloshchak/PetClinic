package com.example.petclinic.util

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.petclinic.data.AppDatabase
import com.example.petclinic.databinding.ActivityAddAppointmentBinding
import com.example.petclinic.model.Appointment
import com.example.petclinic.model.Patient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class AddAppointmentActivity : AppCompatActivity() {

    private lateinit var b: ActivityAddAppointmentBinding
    private lateinit var db: AppDatabase

    private var pets: List<Patient> = emptyList()
    private var selectedPetId: Long? = null

    private var selectedAddress: String? = null
    private var selectedCabinet: String? = null

    private val serviceTitles = listOf(
        "Диагностика: УЗИ",
        "Диагностика: Рентгенография",
        "Диагностика: Лабораторные анализы",
        "Диагностика: ЭХОКГ",
        "Лечение: Стационар",
        "Профилактика: Вакцинация",
        "Уход: Гигиена"
    )

    // Адреса (константы)
    private val addresses = listOf(
        "ул. Ленина, 10",
        "пр-т Мира, 25"
    )

    // Кабинеты зависят от адреса
    private val cabinetsByAddress = mapOf(
        "ул. Ленина, 10" to listOf("101", "102"),
        "пр-т Мира, 25" to listOf("201")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityAddAppointmentBinding.inflate(layoutInflater)
        setContentView(b.root)

        db = AppDatabase.getDatabase(this)

        b.btnBack.setOnClickListener { finish() }

        // 1) Спиннер услуг
        b.spTitle.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            serviceTitles
        )

        // 2) Дата/время через пикеры
        b.etDate.setOnClickListener { showDatePicker() }
        b.etTime.setOnClickListener { showTimePicker() }

        // 3) Адреса + кабинеты
        setupAddressAndCabinetSpinners()

        // 4) Питомцы
        loadPets()

        // 5) Сохранение
        b.btnSaveAppointment.setOnClickListener { save() }
    }

    private fun loadPets() {
        lifecycleScope.launch {
            pets = withContext(Dispatchers.IO) { db.patientDao().getAll() }

            if (pets.isEmpty()) {
                // если у тебя в xml есть emptyState + btnGoAddPet
                // можно показать пустой экран вместо finish()
                Toast.makeText(this@AddAppointmentActivity, "Сначала добавь питомца", Toast.LENGTH_SHORT).show()
                finish()
                return@launch
            }

            val names = pets.map { "${it.name} (${it.animalType})" }
            b.spPets.adapter = ArrayAdapter(
                this@AddAppointmentActivity,
                android.R.layout.simple_spinner_dropdown_item,
                names
            )

            selectedPetId = pets.first().id.toLong()

            b.spPets.setOnItemSelectedListener { pos ->
                selectedPetId = pets[pos].id.toLong()
            }
        }
    }

    private fun setupAddressAndCabinetSpinners() {
        b.spAddress.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            addresses
        )

        // значение по умолчанию
        selectedAddress = addresses.firstOrNull()
        updateCabinets() // заполнит кабинеты под выбранный адрес

        b.spAddress.setOnItemSelectedListener { pos ->
            selectedAddress = addresses[pos]
            updateCabinets()
        }

        b.spCabinet.setOnItemSelectedListener { pos ->
            val addr = selectedAddress
            val cabinets = cabinetsByAddress[addr].orEmpty()
            selectedCabinet = if (cabinets.isNotEmpty()) cabinets[pos] else null
        }
    }

    private fun updateCabinets() {
        val addr = selectedAddress ?: return
        val cabinets = cabinetsByAddress[addr].orEmpty()

        b.spCabinet.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            cabinets
        )

        val visible = cabinets.isNotEmpty()
        b.spCabinet.visibility = if (visible) View.VISIBLE else View.GONE
        b.tvCabinetLabel.visibility = if (visible) View.VISIBLE else View.GONE

        selectedCabinet = cabinets.firstOrNull()
    }

    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val dd = "%02d".format(dayOfMonth)
                val mm = "%02d".format(month + 1)
                b.etDate.setText("$dd.$mm.$year")
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker() {
        val cal = Calendar.getInstance()
        TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                val hh = "%02d".format(hourOfDay)
                val mm = "%02d".format(minute)
                b.etTime.setText("$hh:$mm")
            },
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun save() {
        // ✅ ВНИМАНИЕ: Spinner не имеет .text, берём selectedItem
        val title = (b.spTitle.selectedItem as? String).orEmpty().trim()

        val date = b.etDate.text.toString().trim()
        val time = b.etTime.text.toString().trim()

        val addr = selectedAddress
        val cab = selectedCabinet
        val place = when {
            addr.isNullOrBlank() -> ""
            cab.isNullOrBlank() -> addr
            else -> "$addr, кабинет $cab"
        }

        val petId = selectedPetId

        if (title.isEmpty() || date.isEmpty() || time.isEmpty() || place.isEmpty() || petId == null) {
            Toast.makeText(this, "Заполни все поля", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                db.appointmentDao().insert(
                    Appointment(
                        title = title,
                        date = date,
                        time = time,
                        place = place,
                        patientId = petId
                    )
                )
            }
            Toast.makeText(this@AddAppointmentActivity, "Запись сохранена", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    // helper чтобы быстро ловить выбор в Spinner
    private fun Spinner.setOnItemSelectedListener(onSelected: (pos: Int) -> Unit) {
        this.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: android.widget.AdapterView<*>?,
                view: android.view.View?,
                position: Int,
                id: Long
            ) = onSelected(position)

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }
}
