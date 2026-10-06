package com.example.myapplication

import android.os.Bundle
import android.util.Patterns
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.data.Student
import com.example.myapplication.databinding.ActivityStudentFormBinding
import com.example.myapplication.util.confirm
import com.example.myapplication.util.loadAvatar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class StudentFormActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_STUDENT = "extra_student"
    }

    private lateinit var binding: ActivityStudentFormBinding
    private val viewModel: StudentViewModel by viewModels()

    private var editingStudent: Student? = null
    private var avatarPath: String? = null   // ảnh chọn từ thiết bị (đã copy vào app)

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri == null) return@registerForActivityResult
            lifecycleScope.launch {
                val file = File(filesDir, "avatar_${System.currentTimeMillis()}.jpg")
                withContext(Dispatchers.IO) {
                    contentResolver.openInputStream(uri)?.use { input ->
                        file.outputStream().use { output -> input.copyTo(output) }
                    }
                }
                avatarPath = file.absolutePath
                binding.edtLink.setText("")          // đã chọn ảnh thì bỏ link
                binding.imgAvatar.loadAvatar(avatarPath)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        editingStudent = IntentCompat.getSerializableExtra(intent, EXTRA_STUDENT, Student::class.java)

        val s = editingStudent
        if (s != null) {
            binding.tvTitle.setText(R.string.title_edit)
            binding.edtName.setText(s.name)
            binding.edtCode.setText(s.studentCode)
            binding.edtEmail.setText(s.email)
            if (s.avatar?.startsWith("http", ignoreCase = true) == true) {
                binding.edtLink.setText(s.avatar)
            } else {
                avatarPath = s.avatar
            }
            binding.imgAvatar.loadAvatar(s.avatar)
        } else {
            binding.tvTitle.setText(R.string.title_add)
            binding.imgAvatar.loadAvatar(null)
        }

        binding.btnPick.setOnClickListener {
            pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        // Xem trước ảnh khi nhập xong link
        binding.edtLink.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val link = binding.edtLink.text.toString().trim()
                if (link.isNotEmpty()) binding.imgAvatar.loadAvatar(link)
            }
        }

        binding.btnSave.setOnClickListener { onSaveClicked() }
    }

    private fun onSaveClicked() {
        val name = binding.edtName.text.toString().trim()
        val code = binding.edtCode.text.toString().trim()
        val email = binding.edtEmail.text.toString().trim()
        val link = binding.edtLink.text.toString().trim()

        if (!validate(name, code, email)) return

        val student = Student(
            id = editingStudent?.id ?: 0,
            name = name,
            studentCode = code,
            email = email,
            avatar = if (link.isNotEmpty()) link else avatarPath
        )

        if (editingStudent != null) {
            // SỬA: phải xác nhận
            confirm(R.string.confirm_edit) { doSave(student) }
        } else {
            // THÊM MỚI: lưu luôn
            doSave(student)
        }
    }

    private fun doSave(student: Student) {
        viewModel.save(
            student,
            onDuplicate = { binding.tilCode.error = getString(R.string.err_duplicate_code) },
            onDone = { finish() }
        )
    }

    private fun validate(name: String, code: String, email: String): Boolean {
        binding.tilName.error = if (name.isEmpty()) getString(R.string.err_required) else null
        binding.tilCode.error = if (code.isEmpty()) getString(R.string.err_required) else null
        binding.tilEmail.error = when {
            email.isEmpty() -> getString(R.string.err_required)
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> getString(R.string.err_email)
            else -> null
        }
        return binding.tilName.error == null &&
                binding.tilCode.error == null &&
                binding.tilEmail.error == null
    }
}