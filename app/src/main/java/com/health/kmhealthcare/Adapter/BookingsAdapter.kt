package com.health.kmhealthcare.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.health.kmhealthcare.Models.Booking
import com.health.kmhealthcare.R

class BookingsAdapter(private val bookings: List<Booking>) :
    RecyclerView.Adapter<BookingsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textBookingID: TextView = view.findViewById(R.id.textBookingID)
        val textViewDate: TextView = view.findViewById(R.id.textViewDate)
        val textViewReason: TextView = view.findViewById(R.id.textViewReason)
        val textViewStatus: TextView = view.findViewById(R.id.textViewStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_paviewbooking, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val booking = bookings[position]
        holder.textBookingID.text = booking.bookingId
        holder.textViewDate.text = booking.date
        holder.textViewReason.text = booking.reason
        holder.textViewStatus.text = booking.status
//        when (booking.status) {
//            "pending" -> holder.textViewStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.status_pending))
//            "canceled" -> holder.textViewStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.status_canceled))
//            "confirmed" -> holder.textViewStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.status_confirmed))
//        }

        when (booking.status) {
            "pending" -> {
                holder.textViewStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.status_pending))
                holder.textViewStatus.text = booking.status
            }
            "canceled" -> {
                holder.textViewStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.status_canceled))
                // Custom message for canceled status
                holder.textViewStatus.text = "Canceled - Your selected date and time isn't available. Please choose another time slot."
            }
            "confirmed" -> {
                holder.textViewStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.status_confirmed))
                holder.textViewStatus.text = booking.status
            }
            else -> holder.textViewStatus.text = booking.status // Default case
        }
    }

    override fun getItemCount() = bookings.size
}
