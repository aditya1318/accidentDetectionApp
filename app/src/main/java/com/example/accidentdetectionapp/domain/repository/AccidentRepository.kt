package com.example.accidentdetectionapp.domain.repository

import com.example.accidentdetectionapp.data.model.accidentResponse.accidentResponseDto
import com.example.accidentdetectionapp.domain.entity.CreateAccidentRequest
import retrofit2.Response

interface AccidentRepository {
    suspend fun createAccident(

        createAccidentRequest: CreateAccidentRequest
    ): Response<accidentResponseDto>
}