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

class ProfileFragment : Fragment() {

    private lateinit var profileImageView: ImageView
    private lateinit var etdocId: EditText
    private lateinit var etdocName: EditText
    private lateinit var etdocAge: EditText
    private lateinit var etdocPhone: EditText
    private lateinit var etdocEmail: EditText
    private lateinit var etdocSpecial: EditText

    private lateinit var etUsername: EditText

    private lateinit var saveButton: Button
    private lateinit var imageUri: Uri

    private lateinit var storageReference: FirebaseStorage
    private lateinit var databaseReference: FirebaseDatabase

    private lateinit var radioGroupGender: RadioGroup
    private lateinit var radioMale: RadioButton
    private lateinit var radioFemale: RadioButton



    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        profileImageView = view.findViewById(R.id.profileImageView)
        etdocId = view.findViewById(R.id.etdocId)
        etdocName = view.findViewById(R.id.etdocName)
        etdocAge = view.findViewById(R.id.etdocAge)
        etdocPhone = view.findViewById(R.id.etdocPhone)
        etdocEmail = view.findViewById(R.id.etdocEmail)
        etdocSpecial = view.findViewById(R.id.etdocSpecial)


        saveButton = view.findViewById(R.id.saveButton)

        radioGroupGender = view.findViewById(R.id.radioGroupGender)
        radioMale = view.findViewById(R.id.radioMale)
        radioFemale = view.findViewById(R.id.radioFemale)

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

    private fun saveProfile() {
        val docId = etdocId.text.toString().trim()
        val docName = etdocName.text.toString().trim()
        val docAge = etdocAge.text.toString().trim()
        val docPhone = etdocPhone.text.toString().trim()
        val docEmail = etdocEmail.text.toString().trim()
        val docSpecial = etdocSpecial.text.toString().trim()
        val gender = if (radioMale.isChecked) "Male" else "Female"

        if (!::imageUri.isInitialized) {
            Toast.makeText(requireContext(), "Please add your Profile Picture", Toast.LENGTH_SHORT).show()
            return
        }
        if (docId.isEmpty()) {
            etdocId.error = "Doctor Id is required"
            etdocId.requestFocus()
            return
        }
        if (docName.isEmpty()) {
            etdocName.error = "Doctor Name is required"
            etdocName.requestFocus()
            return
        }
        if (docAge.isEmpty()) {
            etdocAge.error = "Doctor Age is required"
            etdocAge.requestFocus()
            return
        }
        if (docPhone.isEmpty()) {
            etdocPhone.error = "Doctor Phone no is required"
            etdocPhone.requestFocus()
            return
        }
        if (docEmail.isEmpty()) {
            etdocEmail.error = "Doctor Email is required"
            etdocEmail.requestFocus()
            return
        }
        if (docSpecial.isEmpty()) {
            etdocSpecial.error = "Doctor Age is required"
            etdocSpecial.requestFocus()
            return
        }

        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid

        userId?.let { uid ->
            val storageRef = storageReference.reference.child("profile_images/$uid.jpg")
            val databaseRef = databaseReference.getReference("doctor/doctorsProfileDetails/$uid")

//        profileKey?.let { key ->
//            val storageRef = storageReference.reference.child("profile_images/$key.jpg")
//            val databaseRef = databaseReference.getReference("doctor/doctorsProfileDetails/$key")


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
                        // Create a JSON object to hold profile data
                        val profileData = hashMapOf(
                            "docId" to docId,
                            "DocUniId" to userId,
                            "docName" to docName,
                            "docAge" to docAge,
                            "docPhone" to docPhone,
                            "docEmail" to docEmail,
                            "docSpecial" to docSpecial,
                            "gender" to gender,
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