package com.health.kmhealthcare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import android.widget.RadioButton
import android.widget.RadioGroup

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.health.kmhealthcare.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream


class DoctorPrescription : AppCompatActivity() {
    private lateinit var profileImageView: ImageView
    private lateinit var etdocId: EditText
    private lateinit var etdocName: EditText
    private lateinit var etdocAge: EditText
    private lateinit var etdocPhone: EditText
    private lateinit var etdocEmail: EditText
    private lateinit var etdocSpecial: EditText

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_prescription)

        profileImageView = findViewById(R.id.profileImageView)
        etdocId = findViewById(R.id.etdocId)
        etdocName = findViewById(R.id.etdocName)
        etdocAge = findViewById(R.id.etdocAge)
        etdocPhone = findViewById(R.id.etdocPhone)
        etdocEmail = findViewById(R.id.etdocEmail)
        etdocSpecial = findViewById(R.id.etdocSpecial)


        saveButton = findViewById(R.id.saveButton)

        radioGroupGender = findViewById(R.id.radioGroupGender)
        radioMale = findViewById(R.id.radioMale)
        radioFemale = findViewById(R.id.radioFemale)

        storageReference = FirebaseStorage.getInstance()
        databaseReference = FirebaseDatabase.getInstance()

        profileImageView.setOnClickListener {
            chooseImage()
        }

        saveButton.setOnClickListener {
            saveProfile()
        }
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
        val docId = etdocId.text.toString().trim()
        val docName = etdocName.text.toString().trim()
        val docAge = etdocAge.text.toString().trim()
        val docPhone = etdocPhone.text.toString().trim()
        val docEmail = etdocEmail.text.toString().trim()
        val docSpecial = etdocSpecial.text.toString().trim()
        val gender = if (radioMale.isChecked) "Male" else "Female"

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
            val databaseRef = databaseReference.getReference("doctorslist/$uid")

            // Upload image to Firebase Storage
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
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
                            "docId" to docId,
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
                                Toast.makeText(this,"Saved success",Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(this,"Fail",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this,"Image upload fail",Toast.LENGTH_SHORT).show()                }
            }
        }
    }
}
