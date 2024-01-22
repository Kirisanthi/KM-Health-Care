package com.health.kmhealthcare

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import com.health.kmhealthcare.databinding.ActivityPatientSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class PatientSignUpActivity : BaseActivity() {
    private var binding: ActivityPatientSignUpBinding? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityPatientSignUpBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        auth = Firebase.auth

        binding?.tvLoginPagePa?.setOnClickListener{
            startActivity(Intent(this, PatientLogin::class.java))
            finish()
        }
        binding?.btnSignUpPa?.setOnClickListener { registerUserPa() }

    }

    private fun registerUserPa()
    {
        val name = binding?.etSinUpNamePa?.text.toString()
        val email = binding?.etSinUpEmailPa?.text.toString()
        val password = binding?.etSinUpPasswordPa?.text.toString()
        if (validateForm(name, email, password))
        {
            showProgressBar()
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this){task ->
                    if (task.isSuccessful)
                    {
                        showToast(this,"User Id Created Successfully")
                        hideProgressBar()
                        startActivity(Intent(this, PatientLogin::class.java))
                        finish()
                    }
                    else
                    {
                        showToast(this,"User Id not Created Successfully")
                        hideProgressBar()
//                        Toast.makeText(this,"Oops! Something went wrong", Toast.LENGTH_SHORT).show()
                    }

                }
        }
    }

    private fun validateForm(name: String, email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(name)->{
                binding?.tilNamePa?.error = "Enter name"
                false
            }
            TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()->{
                binding?.tilEmailPa?.error = "Enter valid email address"
                false
            }
            TextUtils.isEmpty(password)->{
                binding?.tilPasswordPa?.error = "Enter password"
                false
            }
            else -> { true }
        }
    }

}