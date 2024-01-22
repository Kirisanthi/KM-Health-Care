package com.health.kmhealthcare.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.health.kmhealthcare.Models.Prescription
import com.health.kmhealthcare.R

class PrescriptionAdapter(private val prescriptions: List<Prescription>) :
    RecyclerView.Adapter<PrescriptionAdapter.PrescriptionViewHolder>() {

    class PrescriptionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val prescriptionTextView: TextView = view.findViewById(R.id.prescription_text_view)
        val dateTextView: TextView = view.findViewById(R.id.date_text_view) // TextView for the date
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrescriptionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.prescription_item, parent, false)
        return PrescriptionViewHolder(view)
    }

    override fun onBindViewHolder(holder: PrescriptionViewHolder, position: Int) {
        val prescription = prescriptions[position]
        holder.dateTextView.text = "Appointment Date: ${prescription.date}"
        holder.prescriptionTextView.text = "Prescription: ${prescription.prescriptionText}"
    }

    override fun getItemCount() = prescriptions.size
}
