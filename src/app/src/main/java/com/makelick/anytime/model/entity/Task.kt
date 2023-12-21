package com.makelick.anytime.model.entity

import com.google.firebase.firestore.DocumentId
import java.io.Serializable
import java.util.Date


data class Task(
    @DocumentId val id: String? = null,
    var isCompleted: Boolean = false,
    val title: String? = null,
    val category: String? = null,
    val priority: Int? = null,
    val date: Date? = null,
    val description: String? = null
) : Serializable
