package com.example.petclinic.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.petclinic.R
import com.example.petclinic.model.Visit

class VisitAdapter : ListAdapter<Visit, VisitAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<Visit>() {
        override fun areItemsTheSame(a: Visit, b: Visit) = a.id == b.id
        override fun areContentsTheSame(a: Visit, b: Visit) = a == b
    }

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvVisitTop: TextView = v.findViewById(R.id.tvVisitTop)
        val tvDiagnosis: TextView = v.findViewById(R.id.tvDiagnosis)
        val tvTreatment: TextView = v.findViewById(R.id.tvTreatment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_visit, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        holder.tvVisitTop.text = "${item.date} • Доктор ${item.doctor}"
        holder.tvDiagnosis.text = "Диагноз: ${item.diagnosis}"
        holder.tvTreatment.text = "Лечение: ${item.treatment ?: "—"}"
    }
}
