package com.health.kmhealthcare

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import com.health.kmhealthcare.databinding.ActivityDoctorSignUp2Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class DoctorSignUpActivity : BaseActivity() {
    private var binding: ActivityDoctorSignUp2Binding? = null
    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityDoctorSignUp2Binding.inflate(layoutInflater)
        setContentView(binding?.root)

        auth = Firebase.auth

        binding?.tvLoginPage?.setOnClickListener{
            startActivity(Intent(this, DoctorLogin::class.java))
            finish()
        }
        binding?.btnSignUp?.setOnClickListener { registerUser() }

    }

    private fun registerUser()
    {
        val name = binding?.etSinUpName?.text.toString()
        val email = binding?.etSinUpEmail?.text.toString()
        val password = binding?.etSinUpPassword?.text.toString()
        if (validateForm(name, email, password))
        {
            showProgressBar()
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this){task ->
                    if (task.isSuccessful)
                    {
                        showToast(this,"User Id Created Successfully")
                        hideProgressBar()
                        startActivity(Intent(this, DoctorLogin::class.java))
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
    private fun validateForm(name:String, email:String,password:String):Boolean
    {
        return when {
            TextUtils.isEmpty(name)->{
                binding?.tilName?.error = "Enter name"
                false
            }
            TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()->{
                binding?.tilEmail?.error = "Enter valid email address"
                false
            }
            TextUtils.isEmpty(password)->{
                binding?.tilPassword?.error = "Enter password"
                false
            }
            else -> { true }
        }
    }
}