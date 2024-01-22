package com.health.kmhealthcare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.health.kmhealthcare.databinding.ActivityGetStartedBinding

class activity_get_started : AppCompatActivity() {
    private var binding:ActivityGetStartedBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGetStartedBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.cvGetStarted?.setOnClickListener{
            startActivity(Intent(this, DoctorLogin::class.java))
            finish()
        }

        binding?.cvGetStarted2?.setOnClickListener{
            startActivity(Intent(this, PatientLogin::class.java))
            finish()
        }

    }
}