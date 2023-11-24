package com.example.accidentdetectionapp.domain.repository

interface ImageUploadRepository {
    suspend fun uploadImage(filePath: String):String
}
