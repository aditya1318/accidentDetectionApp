package com.example.accidentdetectionapp.data.datasource

import com.example.accidentdetectionapp.data.model.accidentResponse.accidentResponseDto
import com.example.accidentdetectionapp.data.model.loginReponse.UserLoginReponseDto
import com.example.accidentdetectionapp.data.model.signup.signupDto
import com.example.accidentdetectionapp.domain.entity.CreateAccidentRequest
import com.example.accidentdetectionapp.domain.entity.LoginRequest
import com.example.accidentdetectionapp.domain.entity.SignUpRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @POST("/auth/signup")
    suspend fun userSignUp(
     @Body signUpRequest: SignUpRequest
    ): Response<signupDto>

    @POST("/auth/login")
    suspend fun userLogin(
        @Body loginRequest: LoginRequest
    ): Response<UserLoginReponseDto>

    @POST("/accidents")
    suspend fun createAccident(
        @Header("Authorization") authToken: String,
        @Body createAccidentRequest: CreateAccidentRequest
    ): Response<accidentResponseDto>  // Change the response type based on your server's response
}
