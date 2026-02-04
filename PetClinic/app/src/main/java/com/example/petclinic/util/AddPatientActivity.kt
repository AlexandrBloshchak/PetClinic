package com.example.petclinic.util

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import java.io.File
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.petclinic.R
import com.example.petclinic.model.Patient
import java.util.Calendar

class AddPatientActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etType: EditText
    private lateinit var etBreed: EditText
    private var selectedPhotoPath: String? = null

    private lateinit var etBirthDate: EditText
    private lateinit var tvAgeAuto: TextView

    private lateinit var ivPetPhoto: ImageView
    private lateinit var btnPickPhoto: Button

    private lateinit var btnSave: Button

    private var selectedBirthMillis: Long? = null
    private var selectedPhotoUri: Uri? = null

    private lateinit var viewModel: AddPatientViewModel

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val path = copyToInternalStorage(uri)
            selectedPhotoPath = path
            findViewById<ImageView>(R.id.ivPetPhoto).setImageURI(Uri.fromFile(File(path)))
        }
    }

    private fun copyToInternalStorage(uri: Uri): String {
        val fileName = "pet_${System.currentTimeMillis()}.jpg"
        val file = File(filesDir, fileName)

        contentResolver.openInputStream(uri).use { input ->
            file.outputStream().use { output ->
                input?.copyTo(output)
            }
        }
        return file.absolutePath
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_patient)

        // findViewById
        etName = findViewById(R.id.etName)
        etType = findViewById(R.id.etType)
        etBreed = findViewById(R.id.etBreed)

        etBirthDate = findViewById(R.id.etBirthDate)
        tvAgeAuto = findViewById(R.id.tvAgeAuto)

        ivPetPhoto = findViewById(R.id.ivPetPhoto)
        btnPickPhoto = findViewById(R.id.btnPickPhoto)

        btnSave = findViewById(R.id.btnSave)

        viewModel = ViewModelProvider(this)[AddPatientViewModel::class.java]

        btnPickPhoto.setOnClickListener {
            pickImage.launch("image/*")
        }

        etBirthDate.setOnClickListener {
            showBirthDatePicker()
        }

        btnSave.setOnClickListener {
            savePatient()
        }
    }

    private fun showBirthDatePicker() {
        val cal = Calendar.getInstance()
        val dialog = DatePickerDialog(
            this,
            { _, year, month, day ->
                val c = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, day)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                selectedBirthMillis = c.timeInMillis
                etBirthDate.setText("%02d.%02d.%04d".format(day, month + 1, year))

                val age = calculateAgeYears(selectedBirthMillis!!)
                tvAgeAuto.text = "Возраст: $age лет"
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
        dialog.datePicker.maxDate = System.currentTimeMillis()
        dialog.show()
    }

    private fun calculateAgeYears(birthMillis: Long): Int {
        val birth = Calendar.getInstance().apply { timeInMillis = birthMillis }
        val today = Calendar.getInstance()

        var age = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR)

        val mToday = today.get(Calendar.MONTH)
        val mBirth = birth.get(Calendar.MONTH)

        if (mToday < mBirth || (mToday == mBirth && today.get(Calendar.DAY_OF_MONTH) < birth.get(Calendar.DAY_OF_MONTH))) {
            age--
        }
        return age.coerceAtLeast(0)
    }

    private fun savePatient() {
        val name = etName.text.toString().trim()
        val animalType = etType.text.toString().trim()
        val breed = etBreed.text.toString().trim()

        val ownerName = getSharedPreferences("auth", MODE_PRIVATE)
            .getString("fio", "")
            ?.trim()
            .orEmpty()

        if (ownerName.isEmpty()) {
            Toast.makeText(this, "Не найден владелец. Перезайди в аккаунт.", Toast.LENGTH_SHORT).show()
            return
        }

        if (name.isEmpty() || animalType.isEmpty()) {
            Toast.makeText(this, "Заполни имя и тип питомца", Toast.LENGTH_SHORT).show()
            return
        }

        val birthMillis = selectedBirthMillis
        if (birthMillis == null) {
            Toast.makeText(this, "Выбери дату рождения питомца", Toast.LENGTH_SHORT).show()
            return
        }

        val ageAuto = calculateAgeYears(birthMillis)

        val patient = Patient(
            name = name,
            animalType = animalType,
            breed = breed,
            age = ageAuto,
            ownerName = ownerName,
            birthDateMillis = birthMillis,
            photoPath = selectedPhotoPath
        )

        viewModel.insert(patient)
        finish()
    }
}
