package com.example.petclinic.util

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.petclinic.R
import com.example.petclinic.model.Patient
import com.google.android.material.imageview.ShapeableImageView
import java.io.File

class PatientAdapter(
    private val onClick: (Patient) -> Unit
) : ListAdapter<Patient, PatientAdapter.PatientViewHolder>(DiffCallback()) {

    class PatientViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvName)
        val ivThumb: ShapeableImageView = view.findViewById(R.id.ivPetThumb)
        val type: TextView = view.findViewById(R.id.tvType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_patient, parent, false)
        return PatientViewHolder(view)
    }

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        val patient = getItem(position)
        holder.name.text = patient.name
        holder.type.text = "${patient.animalType} • ${patient.age} лет"
        holder.itemView.setOnClickListener { onClick(patient) }
        when {
            !patient.photoPath.isNullOrBlank() -> {
                holder.ivThumb.setImageURI(Uri.fromFile(File(patient.photoPath)))
            }
            !patient.photoUri.isNullOrBlank() -> {
                holder.ivThumb.setImageURI(Uri.parse(patient.photoUri))
            }
            else -> {
                holder.ivThumb.setImageResource(android.R.drawable.ic_menu_report_image)
            }
        }

    }

    class DiffCallback : DiffUtil.ItemCallback<Patient>() {
        override fun areItemsTheSame(a: Patient, b: Patient) = a.id == b.id
        override fun areContentsTheSame(a: Patient, b: Patient) = a == b
    }
}
