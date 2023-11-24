package com.example.accidentdetectionapp.domain.entity

data class CreateAccidentRequest(
    val reportedBy: String, // User ID
    val location: UserLocation,
    val photo: String,
    val description: String,
    val emergencyServicesNotified: List<EmergencyService>,
    val token : String
    // Additional fields...
)

data class EmergencyService(
    val serviceType: ServiceType,

)

enum class ServiceType {
    police, ambulance, fire_brigade
}
data class UserLocation(
    val type: String = "Point",
    val coordinates: List<Double>
)
