package com.example.myapplication.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "students",
    indices = [Index(value = ["studentCode"], unique = true)]
)
data class Student(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val studentCode: String,
    val email: String,
    val avatar: String? = null   // link http hoặc đường dẫn file trong bộ nhớ app
) : Serializable