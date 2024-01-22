package com.health.kmhealthcare

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import com.health.kmhealthcare.databinding.ActivityPatientLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class PatientLogin : BaseActivity() {
    private var binding: ActivityPatientLoginBinding? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        auth = Firebase.auth

        binding?.tvRegisterPa?.setOnClickListener{
            startActivity(Intent(this, PatientSignUpActivity::class.java))
            finish()
        }

        binding?.tvForgotPassword?.setOnClickListener{
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
        binding?.btnSignInPa?.setOnClickListener{
            signInUserPa()
        }
    }
    private fun signInUserPa(){
        val email = binding?.etSinInEmailPa?.text.toString()
        val password = binding?.etSinInPasswordPa?.text.toString()
        if (validateForm(email, password))
        {
            showProgressBar()
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this){task->
                    if (task.isSuccessful)
                    {
                        startActivity(Intent(this, MainActivity2::class.java))
                        finish()
                        hideProgressBar()
                    }
                    else
                    {
                        showToast(this,"Your Email or Password Wrong Please check it")
                        hideProgressBar()
                    }
                }
        }
    }

    private fun validateForm(email:String,password:String):Boolean
    {
        return when {
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