package com.example.myapplication

import android.app.Application
import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.Student
import kotlinx.coroutines.launch

class StudentViewModel(app: Application) : AndroidViewModel(app) {

    private val dao = AppDatabase.get(app).studentDao()

    val students = dao.getAll().asLiveData()

    fun save(student: Student, onDuplicate: () -> Unit, onDone: () -> Unit) {
        viewModelScope.launch {
            try {
                if (student.id == 0L) dao.insert(student) else dao.update(student)
                onDone()
            } catch (e: SQLiteConstraintException) {
                onDuplicate()   // trùng mã số SV
            }
        }
    }

    fun delete(student: Student) {
        viewModelScope.launch { dao.delete(student) }
    }
}