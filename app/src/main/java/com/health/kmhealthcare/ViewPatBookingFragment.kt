package com.health.kmhealthcare

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.health.kmhealthcare.Adapter.BookingAdapter
import com.health.kmhealthcare.Models.Booking
import com.health.kmhealthcare.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ViewPatBookingFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bookingAdapter: BookingAdapter
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_view_pat_booking, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewBookingDetails)
        recyclerView.layoutManager = LinearLayoutManager(context)

        auth = FirebaseAuth.getInstance()
        val doctorId = auth.currentUser?.uid

        doctorId?.let { docId ->
            databaseReference = FirebaseDatabase.getInstance().getReference("doctor/bookings/$docId")
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val bookings = snapshot.children.mapNotNull { it.getValue(Booking::class.java) }
                        bookingAdapter = BookingAdapter(bookings, { bookingId ->
                            val selectedBooking = bookings.find { it.bookingId == bookingId }
                            selectedBooking?.let { booking ->
                                updateBookingStatus(docId, booking.bookingId, "confirmed", booking.patientId)
                            }
                        }, { bookingId ->
                            val selectedBooking = bookings.find { it.bookingId == bookingId }
                            selectedBooking?.let { booking ->
                                updateBookingStatus(docId, booking.bookingId, "canceled", booking.patientId)
                            }
                        }, requireContext())
                        recyclerView.adapter = bookingAdapter
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        } ?: run {
            // Handle the scenario when doctorId is null
        }

        return view
    }

    private fun updateBookingStatus(doctorId: String, bookingId: String, newStatus: String, patientId: String) {
        val bookingStatusRef = FirebaseDatabase.getInstance().getReference("doctor/bookings/$doctorId/$bookingId/status")
        bookingStatusRef.setValue(newStatus)
            .addOnSuccessListener {
                Toast.makeText(context, "Booking status updated to $newStatus", Toast.LENGTH_SHORT).show()
                sendNotificationToPatient(patientId, "Your booking status has been updated to $newStatus")
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to update booking status", Toast.LENGTH_SHORT).show()
            }
    }

    private fun sendNotificationToPatient(patientId: String, message: String) {
        // Retrieve FCM token for the patient and send a notification
        val tokenRef = FirebaseDatabase.getInstance().getReference("patients/$patientId/token")
        tokenRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val token = snapshot.getValue(String::class.java)
                    token?.let {
                        // Code to send notification using FCM
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}
