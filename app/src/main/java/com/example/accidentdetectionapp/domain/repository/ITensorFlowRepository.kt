package com.example.accidentdetectionapp.domain.repository

import android.graphics.Bitmap
import com.example.accidentdetectionapp.domain.entity.AnalysisResult

interface ITensorFlowRepository {
    suspend fun analyzeImage(bitmap: Bitmap): AnalysisResult
}
