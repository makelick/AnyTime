package com.makelick.anytime.model

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.makelick.anytime.model.entity.Task
import kotlinx.coroutines.tasks.await
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

    suspend fun getUncompletedTasksByDate(date: String) = performFirestoreOperation {
        tasksCollectionRef.whereEqualTo("date", date).whereEqualTo("completed", false)
            .get().await().toObjects(Task::class.java)
    }

    suspend fun getCompletedTasksCount() = performFirestoreOperation {
        tasksCollectionRef.whereEqualTo("completed", true).get().await().size()
    }

    suspend fun getUncompletedTasksCount() = performFirestoreOperation {
        tasksCollectionRef.whereEqualTo("completed", false).get().await().size()
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun getCategories() = performFirestoreOperation {
        userDocRef.get().await().get("categories") as List<String>
    }

    suspend fun updateCategories(categories: List<String>) = performFirestoreOperation {
        userDocRef.update("categories", categories).await()
    }

    private inline fun <T> performFirestoreOperation(operation: () -> T): Result<T> {
        return try {
            Result.success(operation())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}