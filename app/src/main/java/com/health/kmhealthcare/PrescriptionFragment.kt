package com.health.kmhealthcare

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.health.kmhealthcare.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PrescriptionFragment : Fragment() {
    private lateinit var bookingIdEditText: EditText
    private lateinit var prescriptionEditText: EditText
    private lateinit var submitPrescriptionButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_prescription, container, false)

        val bookingIdEditText: EditText = view.findViewById(R.id.bookingIdEditText)
        val prescriptionEditText: EditText = view.findViewById(R.id.prescriptionEditText)
        val submitButton: Button = view.findViewById(R.id.submitPrescriptionButton)
        submitButton.setOnClickListener {
            val partialBookingId = bookingIdEditText.text.toString()
            val prescription = prescriptionEditText.text.toString()

            if (partialBookingId.isNotEmpty() && prescription.isNotEmpty()) {
                findAndUpdateBooking(partialBookingId, prescription)
            } else {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun findAndUpdateBooking(partialBookingId: String, prescription: String) {
        val bookingsRef = FirebaseDatabase.getInstance().getReference("doctor/bookings")
        bookingsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var bookingFound = false
                for (docSnapshot in dataSnapshot.children) {
                    for (bookingSnapshot in docSnapshot.children) {
                        val bookingId = bookingSnapshot.key
                        if (bookingId?.endsWith(partialBookingId) == true) {
                            updatePrescription(docSnapshot.key, bookingId, prescription)
                            bookingFound = true
                            break
                        }
                    }
                    if (bookingFound) break
                }
                if (!bookingFound) {
                    Toast.makeText(context, "Booking ID not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(context, "Error fetching data: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updatePrescription(docId: String?, bookingId: String, prescription: String) {
        val prescriptionRef = FirebaseDatabase.getInstance().getReference("doctor/bookings/$docId/$bookingId/prescription")
        prescriptionRef.setValue(prescription)
            .addOnSuccessListener {
                Toast.makeText(context, "Prescription added successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to add prescription", Toast.LENGTH_SHORT).show()
            }
    }


}
