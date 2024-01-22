package com.health.kmhealthcare

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.health.kmhealthcare.R
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*

class BookDoctorFragment : Fragment() {

    private lateinit var datePickerButton: Button
    private lateinit var reasonForBookingEditText: EditText
    private lateinit var bookButton: Button
    private var docId: String? = null
    private var patientId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_book_doctor, container, false)

        // Initialize views
        datePickerButton = view.findViewById(R.id.datePickerButton)
        reasonForBookingEditText = view.findViewById(R.id.reasonForBooking)
        bookButton = view.findViewById(R.id.bookButton)
//        cancelButton = view.findViewById(R.id.cancelButton)

        docId = arguments?.getString("docId")
        patientId = arguments?.getString("patientId")

        // Set up DatePickerDialog and TimePickerDialog
        datePickerButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val timePickerDialog = TimePickerDialog(
                        requireContext(),
                        { _, hourOfDay, minute ->
                            val selectedDateTime = "${year}-${month + 1}-${dayOfMonth} ${hourOfDay}:${minute}"
                            datePickerButton.text = selectedDateTime
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    )
                    timePickerDialog.show()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        // Handle Book Now button click
        bookButton.setOnClickListener {
            val selectedDateTime = datePickerButton.text.toString()
            val reasonForBooking = reasonForBookingEditText.text.toString()
            val bookingRef = FirebaseDatabase.getInstance().getReference("doctor/bookings/$docId")

            bookingRef.orderByChild("date").equalTo(selectedDateTime)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            Toast.makeText(
                                context,
                                "Your selected time already booked, please choose another time slot or another day",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            val timeStamp = System.currentTimeMillis()
                            val bookingId = "$timeStamp-${patientId?.takeLast(4)}"

                            val bookingDetails = mapOf(
                                "bookingId" to bookingId,
                                "patientId" to patientId,
                                "date" to selectedDateTime,
                                "reason" to reasonForBooking,
                                "status" to "pending"
                            )

                            bookingRef.child(bookingId).setValue(bookingDetails)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(
                                            context,
                                            "Booking successful",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        reasonForBookingEditText.text.clear()
                                        datePickerButton.text = "Select Date"
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Failed to make booking",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Toast.makeText(
                            context,
                            "Database error: ${databaseError.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
        }


        return view
    }
}
