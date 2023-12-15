package com.makelick.anytime.model

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.makelick.anytime.model.entity.Task
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class FirestoreRepository @Inject constructor() {

    private val firestore = Firebase.firestore

    suspend fun addTask(userId: String, task: Task): Result<Unit> {
        return try {
            firestore.collection("users/$userId/tasks/")
                .add(task)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTask(userId: String, task: Task): Result<Unit> {
        return try {
            firestore.document("users/$userId/tasks/${task.id}")
                .set(task)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTask(userId: String, taskId: String): Result<Unit> {
        return try {
            firestore.document("users/$userId/tasks/$taskId")
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllTasks(userId: String): Result<List<Task>> {
        return try {
            val tasks = firestore.collection("users/$userId/tasks/")
                .get()
                .await()
            val taskList = tasks.toObjects(Task::class.java)
            Result.success(taskList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTasksByCategory(userId: String, category: String): Result<List<Task>> {
        return try {
            val tasks = firestore.collection("users/$userId/tasks/")
                .whereEqualTo("category", category)
                .get()
                .await()
            val taskList = tasks.toObjects(Task::class.java)
            Result.success(taskList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTasksByPriority(userId: String, priority: Int): Result<List<Task>> {
        return try {
            val tasks = firestore.collection("users/$userId/tasks/")
                .whereEqualTo("priority", priority)
                .get()
                .await()
            val taskList = tasks.toObjects(Task::class.java)
            Result.success(taskList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTasksByDate(userId: String, date: Date): Result<List<Task>> {
        return try {
            val tasks = firestore.collection("users/$userId/tasks/").whereEqualTo("date", date).get().await()
            val taskList = tasks.toObjects(Task::class.java)
            Result.success(taskList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}