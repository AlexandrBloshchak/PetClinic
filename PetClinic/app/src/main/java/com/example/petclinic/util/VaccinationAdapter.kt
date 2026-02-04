package com.example.petclinic.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.petclinic.R
import com.example.petclinic.model.Vaccination

class VaccinationAdapter : ListAdapter<Vaccination, VaccinationAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<Vaccination>() {
        override fun areItemsTheSame(a: Vaccination, b: Vaccination) = a.id == b.id
        override fun areContentsTheSame(a: Vaccination, b: Vaccination) = a == b
    }

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvVaccine: TextView = v.findViewById(R.id.tvVaccine)
        val tvDates: TextView = v.findViewById(R.id.tvDates)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_vaccination, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        holder.tvVaccine.text = item.vaccineName
        holder.tvDates.text = if (item.nextDate.isNullOrBlank())
            "Сделано: ${item.doneDate}"
        else
            "Сделано: ${item.doneDate} • Следующая: ${item.nextDate}"
    }
}
