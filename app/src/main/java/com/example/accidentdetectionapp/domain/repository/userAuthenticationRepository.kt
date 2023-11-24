package com.example.accidentdetectionapp.domain.repository

import com.example.accidentdetectionapp.domain.entity.LoginRequest
import com.example.accidentdetectionapp.domain.entity.SignUpRequest
import com.example.accidentdetectionapp.domain.entity.UserEntity


interface UserAuthenticationRepository {
    suspend fun login(loginRequest: LoginRequest):Result<Pair<UserEntity, String>>
    suspend fun register(signuprequest: SignUpRequest): Result<UserEntity>
}
