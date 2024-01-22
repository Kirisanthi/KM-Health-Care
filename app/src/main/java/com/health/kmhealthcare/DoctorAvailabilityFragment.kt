package com.health.kmhealthcare

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import com.health.kmhealthcare.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class DoctorAvailabilityFragment : Fragment() {

    private lateinit var etDoctorId: EditText
    private lateinit var etDuration: EditText
    private lateinit var etNumOfDays: EditText
    private lateinit var checkboxMonday: CheckBox
    private lateinit var checkboxTuesday: CheckBox
    private lateinit var checkboxWednesday: CheckBox
    private lateinit var checkboxThursday: CheckBox
    private lateinit var checkboxFriday: CheckBox


    // Add CheckBox variables for other days as needed
    private lateinit var btnSaveAvailability: Button

    private lateinit var databaseReference: FirebaseDatabase


        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.fragment_doctor_availability, container, false)

            etDoctorId = view.findViewById(R.id.etDoctorIdText)
            etDuration = view.findViewById(R.id.etDurationText)
            etNumOfDays = view.findViewById(R.id.etNumOfDaysText)
            checkboxMonday = view.findViewById(R.id.checkBoxMonday)
            checkboxTuesday = view.findViewById(R.id.checkBoxTuesday)
            checkboxWednesday = view.findViewById(R.id.checkBoxWednesday)
            checkboxThursday = view.findViewById(R.id.checkBoxThursday)
            checkboxFriday = view.findViewById(R.id.checkBoxFriday)

            // Initialize other checkboxes as needed
            btnSaveAvailability = view.findViewById(R.id.saveAvailabilityButton)

            databaseReference = FirebaseDatabase.getInstance()

            btnSaveAvailability.setOnClickListener {
                saveAvailability()
            }

            return view
        }
    private fun saveAvailability() {
        val doctorId = etDoctorId.text.toString().trim()
        val duration = etDuration.text.toString().trim()
        val numberOfDays = etNumOfDays.text.toString().trim()

        if (doctorId.isEmpty() || duration.isEmpty() || numberOfDays.isEmpty()) {
            // Handle empty fields
            return
        }

        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid

        userId?.let { uid ->
            val availabilityRef = databaseReference.getReference("doctor/doctorsProfileDetails/$uid/availability")
//            val availabilityRef = databaseReference.getReference("doctorsProfileDetails/doctoravailability/$uid").push()

            val selectedDays = mutableListOf<String>()
            if (checkboxMonday.isChecked) selectedDays.add("Monday")
            if (checkboxTuesday.isChecked) selectedDays.add("Tuesday")
            if (checkboxWednesday.isChecked) selectedDays.add("Wednesday")
            if (checkboxThursday.isChecked) selectedDays.add("Thursday")
            if (checkboxFriday.isChecked) selectedDays.add("Friday")

            // Add other days as needed

            val availabilityData = hashMapOf(
                "doctorId" to doctorId,
                "duration" to duration,
                "numberOfDays" to numberOfDays,
                "selectedDays" to selectedDays
            )

            availabilityRef.setValue(availabilityData)
                .addOnCompleteListener { dbTask ->
                    if (dbTask.isSuccessful) {
                        Toast.makeText(requireContext(), "Doctor Available Time saved successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Your data not save, Please try again", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}