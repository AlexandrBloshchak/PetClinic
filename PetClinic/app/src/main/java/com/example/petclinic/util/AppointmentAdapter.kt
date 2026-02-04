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
import com.example.petclinic.dao.AppointmentWithPet
import com.google.android.material.imageview.ShapeableImageView
import java.io.File

class AppointmentAdapter :
    ListAdapter<AppointmentWithPet, AppointmentAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<AppointmentWithPet>() {
        override fun areItemsTheSame(a: AppointmentWithPet, b: AppointmentWithPet) = a.id == b.id
        override fun areContentsTheSame(a: AppointmentWithPet, b: AppointmentWithPet) = a == b
    }

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvTitle: TextView = v.findViewById(R.id.tvTitle)
        val tvDateTime: TextView = v.findViewById(R.id.tvDateTime)
        val tvPlace: TextView = v.findViewById(R.id.tvPlace)
        val tvPet: TextView = v.findViewById(R.id.tvPet)
        val ivPet: ShapeableImageView = v.findViewById(R.id.ivPetThumb)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        holder.tvTitle.text = item.title
        holder.tvDateTime.text = "${item.date} • ${item.time}"
        holder.tvPlace.text = item.place
        holder.tvPet.text = "${item.petName} (${item.petType}, ${item.petAge} лет)"
        when {
            !item.petPhotoPath.isNullOrBlank() -> {
                holder.ivPet.setImageURI(Uri.fromFile(File(item.petPhotoPath)))
            }
            !item.petPhotoUri.isNullOrBlank() -> {
                holder.ivPet.setImageURI(Uri.parse(item.petPhotoUri))
            }
            else -> {
                holder.ivPet.setImageResource(android.R.drawable.ic_menu_report_image)
            }
        }
    }
}
