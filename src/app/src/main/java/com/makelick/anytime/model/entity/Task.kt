package com.makelick.anytime.model.entity

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class Task(
    @DocumentId val id: String,
    val isCompleted: Boolean,
    val title: String,
    val category: String,
    val priority: Int,
    val date: Date,
    val description: String
)
