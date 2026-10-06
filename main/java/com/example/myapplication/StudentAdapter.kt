package com.example.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.Student
import com.example.myapplication.databinding.ItemStudentBinding
import com.example.myapplication.util.loadAvatar

class StudentAdapter(
    private val onItemClick: (Student) -> Unit
) : ListAdapter<Student, StudentAdapter.VH>(Diff) {

    class VH(val binding: ItemStudentBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemStudentBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val s = getItem(position)
        holder.binding.tvName.text = s.name
        holder.binding.tvCode.text = s.studentCode
        holder.binding.imgAvatar.loadAvatar(s.avatar)
        holder.binding.root.setOnClickListener { onItemClick(s) }
    }

    object Diff : DiffUtil.ItemCallback<Student>() {
        override fun areItemsTheSame(a: Student, b: Student) = a.id == b.id
        override fun areContentsTheSame(a: Student, b: Student) = a == b
    }
}