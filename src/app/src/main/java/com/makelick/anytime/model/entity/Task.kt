package com.makelick.anytime.model.entity

import com.google.firebase.firestore.DocumentId
import java.io.Serializable


data class Task(
    @DocumentId val id: String? = null,
    var isCompleted: Boolean = false,
    val title: String? = null,
    val category: String? = null,
    val priority: Int? = null,
    val date: String? = null,
    val description: String? = null
) : Serializable
