package com.health.kmhealthcare.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.health.kmhealthcare.Models.Booking
import com.health.kmhealthcare.R

class BookingAdapter(private val bookings: List<Booking>,
                     private val onConfirm: (String) -> Unit,
                     private val onCancel: (String) -> Unit,
                     private val context: Context): RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    class BookingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewBooking: TextView = view.findViewById(R.id.textBookingID)
        val textViewDate: TextView = view.findViewById(R.id.textViewDate)
        val textViewReason: TextView = view.findViewById(R.id.textViewReason)
        val textViewStatus: TextView = view.findViewById(R.id.textViewStatus)
        val buttonConfirm: Button = view.findViewById(R.id.buttonConfirm)
        val buttonCancel: Button = view.findViewById(R.id.buttonCancel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booking, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]

        holder.textViewBooking.text = "Booking ID: ${booking.bookingId}"
        holder.textViewDate.text = "Date: ${booking.date}"
        holder.textViewReason.text = "Reason: ${booking.reason}"
        holder.textViewStatus.text = "Status: ${booking.status}"
        when (booking.status) {
            "pending" -> holder.textViewStatus.setTextColor(ContextCompat.getColor(context, R.color.status_pending))
            "confirmed" -> holder.textViewStatus.setTextColor(ContextCompat.getColor(context, R.color.status_confirmed))
            "canceled" -> holder.textViewStatus.setTextColor(ContextCompat.getColor(context, R.color.status_canceled))
        }
        // Bind other data to the views
        holder.buttonConfirm.setOnClickListener { onConfirm(booking.bookingId) }
        holder.buttonCancel.setOnClickListener { onCancel(booking.bookingId) }
    }

    override fun getItemCount() = bookings.size
}
