package com.makelick.anytime.model

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageRepository @Inject constructor() {

    suspend fun uploadImage(userId: String, imageUri: Uri): Result<Uri> {
        val ref = Firebase.storage.reference.child("/profilePictures/$userId/picture.png")
        return try {
            ref.putFile(imageUri).await()
            Result.success(ref.downloadUrl.await())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}