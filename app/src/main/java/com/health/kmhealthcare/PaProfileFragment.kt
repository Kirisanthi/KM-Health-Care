package com.health.kmhealthcare

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.*
import com.health.kmhealthcare.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class PaProfileFragment : Fragment() {

    private lateinit var profileImageView: ImageView
    private lateinit var etpaId: EditText
    private lateinit var etpaName: EditText
    private lateinit var etpaAge: EditText
    private lateinit var etpaPhone: EditText
    private lateinit var etpaEmail: EditText

    private lateinit var etpaUsername: EditText

    private lateinit var saveButton: Button
    private lateinit var imageUri: Uri

    private lateinit var storageReference: FirebaseStorage
    private lateinit var databaseReference: FirebaseDatabase

    private lateinit var radioGroupGender: RadioGroup
    private lateinit var radioMale: RadioButton
    private lateinit var radioFemale: RadioButton

    private lateinit var radioGroupType: RadioGroup
    private lateinit var radioVIP: RadioButton
    private lateinit var radioNormal: RadioButton



    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pa_profile, container, false)

        profileImageView = view.findViewById(R.id.profileImageView)
        etpaId = view.findViewById(R.id.etpaId)
        etpaName = view.findViewById(R.id.etpaName)
        etpaAge = view.findViewById(R.id.etpaAge)
        etpaPhone = view.findViewById(R.id.etpaPhone)
        etpaEmail = view.findViewById(R.id.etpaEmail)


        saveButton = view.findViewById(R.id.saveButton)

        radioGroupGender = view.findViewById(R.id.radioGroupGender)
        radioMale = view.findViewById(R.id.radioMale)
        radioFemale = view.findViewById(R.id.radioFemale)



        radioGroupType = view.findViewById(R.id.radioGroupType)
        radioVIP = view.findViewById(R.id.radioVIP)
        radioNormal = view.findViewById(R.id.radioNormal)

        storageReference = FirebaseStorage.getInstance()
        databaseReference = FirebaseDatabase.getInstance()



        profileImageView.setOnClickListener {
            chooseImage()
        }

        saveButton.setOnClickListener {
            saveProfile()
        }

        return view
    }


    // Launches the image picker
    private fun chooseImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    // Handles the result of the image picker
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data!!
            profileImageView.setImageURI(imageUri)
        }
    }

    // Saves the profile data and image to Firebase
    private fun saveProfile() {
        val paId = etpaId.text.toString().trim()
        val paName = etpaName.text.toString().trim()
        val paAge = etpaAge.text.toString().trim()
        val paPhone = etpaPhone.text.toString().trim()
        val paEmail = etpaEmail.text.toString().trim()
        val gender = if (radioMale.isChecked) "Male" else "Female"
        val type = if (radioVIP.isChecked) "VIP" else "Normal"

        if (!::imageUri.isInitialized) {
            Toast.makeText(requireContext(), "Please add your Profile Picture", Toast.LENGTH_SHORT).show()
            return
        }
        if (paId.isEmpty()) {
            etpaId.error = "patient Id is required"
            etpaId.requestFocus()
            return
        }
        if (paName.isEmpty()) {
            etpaName.error = "patient Name is required"
            etpaName.requestFocus()
            return
        }
        if (paAge.isEmpty()) {
            etpaAge.error = "patient Age is required"
            etpaAge.requestFocus()
            return
        }
        if (paPhone.isEmpty()) {
            etpaPhone.error = "patient Phone no is required"
            etpaPhone.requestFocus()
            return
        }
        if (paEmail.isEmpty()) {
            etpaEmail.error = "patient Email is required"
            etpaEmail.requestFocus()
            return
        }

        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid

        userId?.let { uid ->
            val storageRef = storageReference.reference.child("Patientprofile_images/$uid.jpg")
            val databaseRef = databaseReference.getReference("PatientProfileDetails/$uid")

            // Upload image to Firebase Storage
            val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageUri)
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val imageData = baos.toByteArray()

            val uploadTask = storageRef.putBytes(imageData)

            uploadTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Get the download URL from Firebase Storage
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        // Save profile data to Firebase Realtime Database
                        val profileData = hashMapOf(
                            "PaUniId" to userId,
                            "paId" to paId,
                            "paName" to paName,
                            "paAge" to paAge,
                            "paPhone" to paPhone,
                            "paEmail" to paEmail,

                            "gender" to gender,
                            "type" to type,
                            "profileImage" to downloadUri.toString()
                        )

                        databaseRef.setValue(profileData).addOnCompleteListener { dbTask ->
                            if (dbTask.isSuccessful) {
                                // Profile data saved successfully
                                Toast.makeText(requireContext(), "Profile saved successfully", Toast.LENGTH_SHORT).show()
                            } else {
                                // Handle database save failure
                                Toast.makeText(requireContext(), "Failed to save profile", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    // Handle image upload failure
                    Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}
