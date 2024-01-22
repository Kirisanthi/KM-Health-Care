package com.health.kmhealthcare.Models

data class Prescription(
    val bookingId: String,
    val prescriptionText: String,
    val date: String // Added date field
)