package com.example.accidentdetectionapp.data.model.accidentResponse

data class Accident(
    val __v: Int,
    val _id: String,
    val description: String,
    val emergencyServicesNotified: List<EmergencyServicesNotified>,
    val location: Location,
    val photos: List<String>,
    val reportedBy: String,
    val status: String,
    val timestamp: String
)