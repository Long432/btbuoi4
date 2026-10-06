package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.data.Student
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.databinding.DialogStudentDetailBinding
import com.example.myapplication.util.confirm
import com.example.myapplication.util.loadAvatar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: StudentViewModel by viewModels()
    private val adapter = StudentAdapter { showDetail(it) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvStudents.layoutManager = LinearLayoutManager(this)
        binding.rvStudents.adapter = adapter

        viewModel.students.observe(this) { list ->
            adapter.submitList(list)
            binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, StudentFormActivity::class.java))
        }
    }

    /** Trang 2: hộp thoại chi tiết. */
    private fun showDetail(student: Student) {
        val d = DialogStudentDetailBinding.inflate(layoutInflater)
        d.imgAvatar.loadAvatar(student.avatar)
        d.tvName.text = student.name
        d.tvCode.text = getString(R.string.detail_code, student.studentCode)
        d.tvEmail.text = getString(R.string.detail_email, student.email)

        AlertDialog.Builder(this)
            .setView(d.root)
            .setPositiveButton(R.string.edit) { _, _ ->
                startActivity(
                    Intent(this, StudentFormActivity::class.java)
                        .putExtra(StudentFormActivity.EXTRA_STUDENT, student)
                )
            }
            .setNegativeButton(R.string.delete) { _, _ ->
                confirm(R.string.confirm_delete) { viewModel.delete(student) }
            }
            .setNeutralButton(R.string.close, null)
            .show()
    }
}