package com.example.calendar

import android.os.Bundle
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.example.calendar.databinding.ActivityTaskBinding
import java.time.format.DateTimeFormatter

//import com.example.calendar.databinding.ActivityTaskBinding

class TaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskBinding
    private lateinit var task: Task

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        task = intent.getSerializableExtra("task") as Task
        binding.nameTextView.text = task.name
        val dateTimeFormatter  = DateTimeFormatter.ofPattern("HH:mm dd-MM-yy")
        binding.timeStartTextView.text = task.dateStart.format(dateTimeFormatter)
        binding.timeFinishTextView.text = task.dateFinish.format(dateTimeFormatter)
        binding.descriptionTextView.text = task.description
    }
}