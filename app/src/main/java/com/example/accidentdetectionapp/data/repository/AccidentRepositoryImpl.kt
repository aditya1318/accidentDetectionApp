package com.example.accidentdetectionapp.data.repository

import com.example.accidentdetectionapp.data.datasource.ApiService
import com.example.accidentdetectionapp.data.model.accidentResponse.accidentResponseDto
import com.example.accidentdetectionapp.domain.entity.CreateAccidentRequest
import com.example.accidentdetectionapp.domain.repository.AccidentRepository
import retrofit2.Response
import javax.inject.Inject

class AccidentRepositoryImpl @Inject constructor(  private val apiService: ApiService) :
    AccidentRepository {
    override suspend fun createAccident(
  
        createAccidentRequest: CreateAccidentRequest
    ): Response<accidentResponseDto> {
        val token = createAccidentRequest.token
        return apiService.createAccident("Bearer $token", createAccidentRequest)
    }
}