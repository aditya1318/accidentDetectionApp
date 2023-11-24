package com.example.accidentdetectionapp.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.example.accidentdetectionapp.domain.entity.AnalysisResult
import com.example.accidentdetectionapp.domain.repository.ITensorFlowRepository
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.label.TensorLabel
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import javax.inject.Inject

class TensorFlowRepositoryImpl @Inject constructor(private val context: Context) :
    ITensorFlowRepository  {

    private val model by lazy {
        Interpreter(loadModelFile("model.tflite"), Interpreter.Options())
    }

    private fun loadModelFile(modelName: String): ByteBuffer {
        val fileDescriptor = context.assets.openFd(modelName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    override suspend fun analyzeImage(bitmap: Bitmap): AnalysisResult {
        // Resize the bitmap to the required input size of 224x224
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)

        // Convert the resized bitmap to TensorImage
        val image = TensorImage(DataType.FLOAT32)
        image.load(resizedBitmap)

        // Create a buffer for the model's output
        val probabilityBuffer = TensorBuffer.createFixedSize(model.getOutputTensor(0).shape(), DataType.FLOAT32)

        // Run the model
        model.run(image.buffer, probabilityBuffer.buffer.rewind())

        // Process the output
        val labels = loadLabels()
        val labeledProbability = TensorLabel(labels, probabilityBuffer).mapWithFloatValue

        // Find the highest probability and its corresponding label
        val (label, confidence) = findHighestProbability(labeledProbability)

        // Return the result
        return AnalysisResult(label, confidence)
    }




    private fun loadLabels(): List<String> {
        return context.assets.open("labels.txt").bufferedReader().useLines { lines ->
            lines.toList()
        }
    }

    private fun findHighestProbability(labeledProbability: Map<String, Float>): Pair<String, Float> {
        var maxLabel = ""
        var maxConfidence = 0f
        for ((label, confidence) in labeledProbability) {
            if (confidence > maxConfidence) {
                maxLabel = label
                maxConfidence = confidence
            }
        }
        return Pair(maxLabel, maxConfidence)
    }
}