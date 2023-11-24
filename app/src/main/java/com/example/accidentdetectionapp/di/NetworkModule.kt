package com.example.accidentdetectionapp.di

import android.content.Context
import com.cloudinary.Cloudinary
import com.example.accidentdetectionapp.data.datasource.ApiService
import com.example.accidentdetectionapp.data.repository.AccidentRepositoryImpl
import com.example.accidentdetectionapp.data.repository.CloudinaryImageUploadRepository
import com.example.accidentdetectionapp.data.repository.TensorFlowRepositoryImpl
import com.example.accidentdetectionapp.data.repository.UserAuthenticationRepositoryImpl
import com.example.accidentdetectionapp.domain.repository.AccidentRepository
import com.example.accidentdetectionapp.domain.repository.ITensorFlowRepository
import com.example.accidentdetectionapp.domain.repository.ImageUploadRepository
import com.example.accidentdetectionapp.domain.repository.UserAuthenticationRepository
import com.example.accidentdetectionapp.utils.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://clumsy-belt-crow.cyclic.app")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }



    @Provides
    @Singleton
    fun provideImageUploadRepository(): ImageUploadRepository = CloudinaryImageUploadRepository()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserAuthenticationRepository(apiService: ApiService): UserAuthenticationRepository {
        return UserAuthenticationRepositoryImpl(apiService)
    }

    @Singleton
    @Provides
    fun provideSessionManager(@ApplicationContext context: Context): SessionManager {
        return SessionManager(context)
    }

        @Provides
        @Singleton
        fun provideTensorFlowRepository(@ApplicationContext context: Context): ITensorFlowRepository = TensorFlowRepositoryImpl(context)

    @Provides
    @Singleton
    fun provideAccidentRepository(apiService: ApiService): AccidentRepository = AccidentRepositoryImpl(apiService)


}
