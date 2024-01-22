package com.health.kmhealthcare

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.health.kmhealthcare.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PaymentFragment : Fragment() {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_payment, container, false)

        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid

        if (uid != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("PatientProfileDetails/$uid")
            checkUserProfile(uid, view)
        } else {
            // Handle user not logged in or UID is null
        }

        return view
    }

    private fun checkUserProfile(uid: String, view: View) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Assuming 'type' is a direct child in the user's profile data
                    val userType = dataSnapshot.child("type").getValue(String::class.java)
                    displayPaymentInfo(userType, view)
                } else {
                    // Profile does not exist, prompt user to fill profile details
                    Toast.makeText(context, "Please fill your profile details After that you can Pay.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors
            }
        })
    }

    private fun displayPaymentInfo(userType: String?, view: View) {
        val paymentInfoTextView: TextView = view.findViewById(R.id.paymentInfoTextView) // Make sure you have this TextView in your layout

        when (userType) {
            "Normal" -> paymentInfoTextView.text = "Your Yearly payment is 300£"
            "VIP" -> paymentInfoTextView.text = "Your Yearly payment is 600£"
            else -> paymentInfoTextView.text = "Unable to determine your payment amount Please add your Profile Detail first"
        }
    }
}