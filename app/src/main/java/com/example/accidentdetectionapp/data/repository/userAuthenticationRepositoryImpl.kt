package com.example.accidentdetectionapp.data.repository

import android.util.Log
import com.example.accidentdetectionapp.data.datasource.ApiService
import com.example.accidentdetectionapp.domain.entity.LoginRequest
import com.example.accidentdetectionapp.domain.entity.SignUpRequest
import com.example.accidentdetectionapp.domain.entity.UserEntity
import com.example.accidentdetectionapp.domain.repository.UserAuthenticationRepository
import javax.inject.Inject

class UserAuthenticationRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : UserAuthenticationRepository {
    override suspend fun login(loginRequest: LoginRequest): Result<Pair<UserEntity, String>> {
        Log.d("UserAuthRepository", "Attempting to log in user: $loginRequest")
        try {
            val response = apiService.userLogin(loginRequest)
            if (response.isSuccessful && response.body() != null) {
                val userDto = response.body()!!.data.user
                val token = response.body()!!.token

                val userEntity = UserEntity(
                    __v = userDto.__v,
                    _id = userDto._id,
                    email = userDto.email,
                    role = userDto.role,
                    username = userDto.username
                )

                Log.d("UserAuthRepository", "Login successful for user: ${userEntity.username}")
                return Result.success(Pair(userEntity, token))
            } else {
                val errorMessage = "Error during login: Response was unsuccessful or null. Response code: ${response.code()}"
                Log.e("UserAuthRepository", errorMessage)
                return Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("UserAuthRepository", "Error during login: ${e.message}")
            return Result.failure(e)
        }
    }


    override suspend fun register(
        signuprequest: SignUpRequest
    ): Result<UserEntity> {
        Log.d("UserAuthRepository", "Attempting to register user: $signuprequest")
        try {
            val response = apiService.userSignUp(signuprequest)
            if (response.isSuccessful && response.body() != null) {
                // Assuming the successful response contains the user data you want to return
                val userDto = response.body()!!.data.user
                // Map the User DTO to UserEntity
                val userEntity = UserEntity(
                    __v = userDto.__v,
                    _id = userDto._id,
                    email = userDto.email,

                    role = userDto.role,
                    username = userDto.username
                )
                Log.d("UserAuthRepository", "Registration successful for user: $signuprequest")
                return Result.success(userEntity)
            } else {
                // Handle error cases, such as HTTP errors or the body being null
                val errorMessage = "Error during registration: Response was unsuccessful or null. Response code: ${response.code()}"
                Log.e("UserAuthRepository", errorMessage)
                return Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("UserAuthRepository", "Error during registration: ${e.message}")
            return Result.failure(e)
        }
    }

}
