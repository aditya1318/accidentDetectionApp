package com.example.accidentdetectionapp.data.repository

import android.util.Log
import com.cloudinary.Cloudinary
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.cloudinary.utils.ObjectUtils
import com.example.accidentdetectionapp.domain.repository.ImageUploadRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CloudinaryImageUploadRepository : ImageUploadRepository {

    override suspend fun uploadImage(filePath: String) :String{
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                try {
                    MediaManager.get().upload(filePath).callback(object : UploadCallback {
                        override fun onStart(requestId: String?) {
                            // Log or handle start
                        }

                        override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                            // Log or handle progress
                        }

                        override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                            val url = resultData["url"].toString()
                            Log.d("uploaded","URL: $url")
                            continuation.resume(url)
                        }

                        override fun onError(requestId: String?, error: ErrorInfo?) {
                            continuation.resumeWithException(Exception(error?.description))
                        }

                        override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                            // Log or handle reschedule
                        }
                    }).dispatch()
                } catch (e: Exception) {
                    Log.e("ImageUpload", "Upload failed: ${e.message}")
                    continuation.resumeWithException(e)
                }
            }
        }
    }
}
