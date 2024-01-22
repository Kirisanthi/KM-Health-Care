package com.health.kmhealthcare

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.health.kmhealthcare.Adapter.BookingsAdapter
import com.health.kmhealthcare.Models.Booking
import com.health.kmhealthcare.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ViewBookingStatuesFragment : Fragment() {
    private lateinit var bookingsRecyclerView: RecyclerView
    private lateinit var bookingsAdapter: BookingsAdapter
    private var bookingsList = mutableListOf<Booking>()
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.i("ViewBookings", "hiii")
        val view = inflater.inflate(R.layout.fragment_view_booking_statues, container, false)

        // Initialize RecyclerView and Adapter
        bookingsRecyclerView = view.findViewById(R.id.bookingsRecyclerView)
        bookingsRecyclerView.layoutManager = LinearLayoutManager(context)
        bookingsAdapter = BookingsAdapter(bookingsList)
        bookingsRecyclerView.adapter = bookingsAdapter
        Log.i("ViewBookings", "Firebase initialized")
        loadBookings()

        return view
    }
    private fun loadBookings() {
        try {
            auth = FirebaseAuth.getInstance()
            val patientId = auth.currentUser?.uid
            Log.i("ViewBookings", "Patient ID: $patientId")
            val database = FirebaseDatabase.getInstance()
            val doctorsRef = database.getReference("doctor/bookings")
            doctorsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        bookingsList.clear()
                        for (docSnapshot in dataSnapshot.children) {
                            for (bookingSnapshot in docSnapshot.children) {
                                val booking = bookingSnapshot.getValue(Booking::class.java)
                                if (booking?.patientId == patientId) {
                                    booking?.let { bookingsList.add(it) }
                                }
                            }
                        }
                        bookingsAdapter.notifyDataSetChanged()
                    } else {
                        Log.i("ViewBookings", "No bookings found")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("ViewBookings", "Error fetching data", databaseError.toException())
                }
            })
        } catch (exception: Exception) {
            Log.e("ViewBookings", "Error in loadBookings", exception)
        }
    }



//    private fun loadBookings() {
//        auth = FirebaseAuth.getInstance()
//        val patientId = auth.currentUser?.uid
//
//        Log.i("ViewBookings", "Patient ID: $patientId")
//
//        val database = FirebaseDatabase.getInstance()
//        val doctorsRef = database.getReference("doctor/bookings")
//
//        doctorsRef.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    bookingsList.clear()
//                    for (docSnapshot in dataSnapshot.children) {
//                        for (bookingSnapshot in docSnapshot.children) {
//                            val booking = bookingSnapshot.getValue(Booking::class.java)
//                            if (booking?.patientId == patientId) {
//                                booking?.let { bookingsList.add(it) }
//                            }
//                        }
//                    }
//                    bookingsAdapter.notifyDataSetChanged()
//                } else {
//                    Log.i("ViewBookings", "No bookings found")
//                    // Handle the case where there are no bookings
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                Log.e("ViewBookings", "Error fetching data", databaseError.toException())
//            }
//        })
//    }
}