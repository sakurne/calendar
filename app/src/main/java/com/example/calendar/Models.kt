package com.example.calendar

import java.io.Serializable
import java.time.LocalDateTime

data class Task(
    val id: Long,
    val dateStart: LocalDateTime,
    val dateFinish: LocalDateTime,
    val name: String,
    val description:String
    ): Serializable
