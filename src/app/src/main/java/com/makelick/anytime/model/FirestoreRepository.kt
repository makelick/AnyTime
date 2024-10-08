package com.makelick.anytime.model

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.makelick.anytime.model.entity.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreRepository @Inject constructor(
    userId: String
) {

    private val userDocRef = Firebase.firestore.document("users/$userId")
    private val tasksCollectionRef = userDocRef.collection("tasks")

    val allTasks = MutableStateFlow<List<Task>>(emptyList())
    val categories = MutableStateFlow<List<String>>(emptyList())

    init {
        tasksCollectionRef.addSnapshotListener { value, error ->
            if (error != null) return@addSnapshotListener
            allTasks.value = value?.toObjects(Task::class.java) ?: emptyList()
        }

        userDocRef.addSnapshotListener { value, error ->
            if (error != null) return@addSnapshotListener

            if (value?.exists() == true) {
                categories.value =
                    (value.get("categories") as? List<*>)?.map { it.toString() } ?: emptyList()
            } else {
                userDocRef.set(mapOf("categories" to listOf("Personal", "Work")))
            }
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
