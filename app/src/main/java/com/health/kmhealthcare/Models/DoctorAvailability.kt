package com.health.kmhealthcare.Models

data class DoctorAvailability(
    val doctorId: String? = null,
    val duration: String? = null,
    val numberOfDays: String? = null,
    val selectedDays: List<String>? = null
)