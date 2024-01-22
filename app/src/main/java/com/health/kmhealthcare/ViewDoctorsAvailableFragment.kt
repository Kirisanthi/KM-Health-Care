package com.health.kmhealthcare

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.health.kmhealthcare.R
import com.health.kmhealthcare.Models.DoctorAvailability
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ViewDoctorsAvailableFragment : Fragment() {

    private lateinit var docDurationTextView: TextView
    private lateinit var docAvailableDaysCountTextView: TextView
    private lateinit var docAvailableDaysTextView: TextView
    private lateinit var bookButton: Button
    private lateinit var cancelButton: Button

    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_doctors_available, container, false)

        val docId = arguments?.getString("docId")
        Log.i("ViewDoctorsAvailable", "Received Doc ID: $docId")

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference
        // Retrieve data from Firebase
        val userId = auth.currentUser?.uid

        // Initialize views
        docDurationTextView = view.findViewById(R.id.docduration)
        docAvailableDaysCountTextView = view.findViewById(R.id.docavaidayscount)
        docAvailableDaysTextView = view.findViewById(R.id.docavaidays)
        bookButton = view.findViewById(R.id.bookButton)
//        cancelButton = view.findViewById(R.id.cancelButton)



        bookButton.setOnClickListener {
            // Create a new instance of BookDoctorFragment
            val bookDoctorFragment = BookDoctorFragment()

            val bundle = Bundle()
            bundle.putString("docId", docId)
            bundle.putString("patientId", userId)
            bookDoctorFragment.arguments = bundle
            // Assuming you are in a Fragment, use fragmentManager for transactions
            // If you are in an Activity, use supportFragmentManager instead
            val transaction = requireActivity().supportFragmentManager.beginTransaction()

            // Replace the current fragment with the new one
            transaction.replace(R.id.fragment_container, bookDoctorFragment)

            // Add the transaction to the back stack if you want to navigate back
            transaction.addToBackStack(null)

            // Commit the transaction
            transaction.commit()
        }



        Log.i("ViewDoctorsAvailable", "Firebase initialized")

        // Initialize Firebase


        Log.i("ViewDoctorsAvailable", "Current user ID: $userId")
        userId?.let {
            val availabilityRef = databaseReference.child("doctor/doctorsProfileDetails/$docId/availability")

            availabilityRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()) {

                        val availabilityData = snapshot.getValue(DoctorAvailability::class.java)

                        // Check if availabilityData is not null
                        availabilityData?.let {
                            val duration = it.duration
                            val numberOfDays = it.numberOfDays
                            val availableDays = it.selectedDays?.joinToString(", ")

                            // Log the retrieved data for debugging
                            Log.d("ViewDoctorsAvailable", "Duration: $duration, No of Days: $numberOfDays, Available Days: $availableDays")

                            // Update TextViews with retrieved data
                            docDurationTextView.text = "Duration: $duration hours"
                            docAvailableDaysCountTextView.text = "No of Days: $numberOfDays"
                            docAvailableDaysTextView.text = "Available Days: $availableDays"
                        } ?: run {
                            Log.e("ViewDoctorsAvailable", "Availability data is null")
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        requireContext(),
                        "Failed to retrieve data: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }

        return view
    }
}