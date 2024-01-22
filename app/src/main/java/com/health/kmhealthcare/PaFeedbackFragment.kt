package com.health.kmhealthcare


    import android.os.Bundle
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.Button
    import android.widget.EditText
    import android.widget.RatingBar
    import androidx.fragment.app.Fragment
    import com.health.kmhealthcare.Models.UserData2
    import com.google.firebase.database.DatabaseReference
    import com.google.firebase.database.FirebaseDatabase
    import java.text.SimpleDateFormat
    import java.util.*



    import android.widget.Toast
    import com.health.kmhealthcare.R


class PaFeedbackFragment : Fragment() {

    private lateinit var textFieldEditText: EditText
    private lateinit var ratingBar: RatingBar
    private lateinit var saveButton: Button

    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pa_feedback, container, false)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().getReference("RecievedFeedback")

        // Initialize UI elements
        textFieldEditText = view.findViewById(R.id.etpaOpinion)
        ratingBar = view.findViewById(R.id.ratingBar)
        saveButton = view.findViewById(R.id.saveButton)

        // Set onClickListener for the Save button
        saveButton.setOnClickListener {
            saveUserData()
        }

        return view
    }

    private fun saveUserData() {
        // Get data from UI elements
        val textFieldData = textFieldEditText.text.toString().trim()
        val rating = ratingBar.rating

        // Get the current date as a default value
        val defaultDate = getDefaultDate()

        // Create a UserData object
        val userData = UserData2(textFieldData, rating, defaultDate)

        // Save data to Firebase Database
        val userId = database.push().key
        userId?.let {
            database.child(it).setValue(userData).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Feedback sent successfully
                    showToast("Feedback sent successfully")
                } else {
                    // Feedback sending failed
                    showToast("Failed to send feedback. Please try again.")
                }
            }
        }

        // Clear the input fields
        textFieldEditText.text.clear()
        ratingBar.rating = 0f
    }

    private fun getDefaultDate(): String {
        // Get the current date
        val currentDate = Calendar.getInstance().time

        // Format the date
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(currentDate)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
