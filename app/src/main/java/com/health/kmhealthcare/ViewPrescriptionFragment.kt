package com.health.kmhealthcare

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.health.kmhealthcare.R
import com.health.kmhealthcare.Adapter.PrescriptionAdapter
import com.health.kmhealthcare.Models.Prescription
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ViewPrescriptionFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PrescriptionAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_view_prescription, container, false)
        recyclerView = view.findViewById(R.id.prescriptions_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        retrievePrescriptions()

        return view
    }




    private fun retrievePrescriptions() {
        val prescriptions = mutableListOf<Prescription>()
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid // User ID

        val ref = FirebaseDatabase.getInstance().getReference("doctor/bookings")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Log.d("ViewPrescriptionFragment", "No data found")
                    return
                }

                dataSnapshot.children.forEach { doctorSnapshot -> // Iterate through each doctor
                    doctorSnapshot.children.forEach { bookingSnapshot -> // Iterate through each booking
                        if (bookingSnapshot.child("patientId").getValue(String::class.java) == userId) {
                            val prescriptionText = bookingSnapshot.child("prescription").getValue(String::class.java) ?: ""
                            val bookingId = bookingSnapshot.key ?: ""
                            val date = bookingSnapshot.child("date").getValue(String::class.java) ?: ""
                            prescriptions.add(Prescription(bookingId, prescriptionText, date))
                        }
                    }
                }
                adapter = PrescriptionAdapter(prescriptions)
                recyclerView.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })
    }

}