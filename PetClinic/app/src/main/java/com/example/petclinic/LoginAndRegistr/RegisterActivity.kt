package com.example.petclinic.LoginAndRegistr

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.petclinic.data.AppDatabase
import com.example.petclinic.databinding.ActivityRegisterBinding
import com.example.petclinic.model.Patient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.petclinic.util.HomeActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var b: ActivityRegisterBinding
    private lateinit var db: AppDatabase

    // здесь будем хранить ссылки на поля питомцев
    private data class PetFields(
        val etName: EditText,
        val etType: EditText,
        val etAge: EditText
    )

    private val petFields = mutableListOf<PetFields>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(b.root)

        db = AppDatabase.getDatabase(this)

        val counts = (1..5).toList()
        b.spPetCount.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            counts
        )

        // по умолчанию 1 питомец
        renderPetInputs(1)

        b.spPetCount.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                renderPetInputs(counts[pos])
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        b.btnDoRegister.setOnClickListener {
            doRegister()
        }
    }

    private fun renderPetInputs(count: Int) {
        b.containerPets.removeAllViews()
        petFields.clear()

        repeat(count) { index ->
            val block = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(0, 16, 0, 16)
            }

            val title = TextView(this).apply {
                text = "Питомец №${index + 1}"
                textSize = 16f
            }

            val etName = EditText(this).apply { hint = "Имя питомца" }
            val etType = EditText(this).apply { hint = "Тип (кот/собака/...)" }
            val etAge = EditText(this).apply {
                hint = "Возраст"
                inputType = android.text.InputType.TYPE_CLASS_NUMBER
            }

            block.addView(title)
            block.addView(etName)
            block.addView(etType)
            block.addView(etAge)

            b.containerPets.addView(block)
            petFields += PetFields(etName, etType, etAge)
        }
    }

    private fun doRegister() {
        val fio = b.etOwnerFio.text.toString().trim()
        val ageOwner = b.etOwnerAge.text.toString().trim()
        val phone = b.etOwnerPhone.text.toString().trim()
        val email = b.etOwnerEmail.text.toString().trim()
        val pass = b.etRegPassword.text.toString().trim()

        if (fio.isEmpty() || phone.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Заполни ФИО, телефон и пароль", Toast.LENGTH_SHORT).show()
            return
        }

        // Проверяем питомцев
        val pets = petFields.mapIndexedNotNull { i, f ->
            val name = f.etName.text.toString().trim()
            val type = f.etType.text.toString().trim()
            val age = f.etAge.text.toString().trim().toIntOrNull() ?: 0

            if (name.isEmpty() || type.isEmpty()) {
                Toast.makeText(this, "Заполни имя и тип у питомца №${i + 1}", Toast.LENGTH_SHORT).show()
                return
            }

            Patient(
                name = name,
                animalType = type,
                breed = "", // пока пусто
                age = age,
                ownerName = fio
            )
        }

        // сохраняем “аккаунт” локально (упрощённо)
        getSharedPreferences("auth", MODE_PRIVATE).edit()
            .putString("login", phone) // или email — как хочешь
            .putString("pass", pass)
            .putString("fio", fio)
            .putString("email", email)
            .putString("phone", phone)
            .putString("ageOwner", ageOwner)
            .apply()

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                pets.forEach { db.patientDao().insert(it) }
            }
            startActivity(Intent(this@RegisterActivity, HomeActivity::class.java))
            finish()
        }
    }
}
