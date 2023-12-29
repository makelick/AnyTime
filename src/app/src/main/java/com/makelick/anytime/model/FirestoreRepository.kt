package com.makelick.anytime.model

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.makelick.anytime.model.entity.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreRepository @Inject constructor(
    userId: String
) {

    private val userDocRef = Firebase.firestore.document("users/$userId")
    private val tasksCollectionRef = userDocRef.collection("tasks")

    val allTasks = MutableStateFlow<List<Task>>(emptyList())
    val categories = MutableStateFlow<List<String>>(emptyList())

    init {
        tasksCollectionRef.addSnapshotListener { value, error ->
            if (error != null) {
                throw error
            }
            allTasks.value = value?.toObjects(Task::class.java) ?: emptyList()
        }

        userDocRef.addSnapshotListener { value, error ->
            if (error != null) {
                throw error
            }

            categories.value =
                (value?.get("categories") as? List<*>)?.map { it.toString() } ?: emptyList()
        }
    }

    suspend fun addTask(task: Task): Result<Unit> {
        return try {
            task.id = tasksCollectionRef.document().id
            updateTask(task)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTask(task: Task): Result<Unit> {
        return try {
            tasksCollectionRef.document(task.id.toString()).set(task).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCategories(newCategories: List<String>): Result<Unit> {
        return try {
            userDocRef.update("categories", newCategories).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTask(taskId: String) {
        tasksCollectionRef.document(taskId).delete().await()
    }
}