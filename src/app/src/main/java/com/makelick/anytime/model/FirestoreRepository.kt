package com.makelick.anytime.model

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.makelick.anytime.model.entity.Task
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class FirestoreRepository @Inject constructor(
    userId: String
) {

    private val userDocRef = Firebase.firestore.document("users/$userId")
    private val tasksCollectionRef = userDocRef.collection("tasks")

    suspend fun addTask(task: Task) = performFirestoreOperation {
        tasksCollectionRef.add(task).await()
    }

    suspend fun updateTask(task: Task) = performFirestoreOperation {
        tasksCollectionRef.document(task.id.toString()).set(task).await()
    }

    suspend fun deleteTask(taskId: String) = performFirestoreOperation {
        tasksCollectionRef.document(taskId).delete().await()
    }

    suspend fun getAllTasks() = performFirestoreOperation {
        tasksCollectionRef.get().await().toObjects(Task::class.java)
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun getCategories() = performFirestoreOperation {
        userDocRef.get().await().get("categories") as List<String>
    }

    suspend fun getTasksByCategory(category: String) = performFirestoreOperation {
        tasksCollectionRef.whereEqualTo("category", category).get().await()
            .toObjects(Task::class.java)
    }

    suspend fun getTasksByPriority(priority: Int) = performFirestoreOperation {
        tasksCollectionRef.whereEqualTo("priority", priority).get().await()
            .toObjects(Task::class.java)
    }

    suspend fun getTasksByDate(date: Date) = performFirestoreOperation {
        tasksCollectionRef.whereEqualTo("date", date).get().await()
            .toObjects(Task::class.java)
    }

    private inline fun <T> performFirestoreOperation(operation: () -> T): Result<T> {
        return try {
            Result.success(operation())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}