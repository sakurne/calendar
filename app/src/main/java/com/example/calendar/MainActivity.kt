package com.example.calendar

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import androidx.gridlayout.widget.GridLayout
import com.example.calendar.TasksReader
import com.google.android.material.button.MaterialButton
import java.io.File
import java.sql.Timestamp
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.ceil
import kotlin.math.floor

class MainActivity : AppCompatActivity() {

    private lateinit var tasks:ArrayList<Task>
    private lateinit var tasksGrid:GridLayout
    private lateinit var todayTasks:ArrayList<Task>

    private val hoursCount = 24
    private val rowsPerHour = 4
    private val hoursText = "%d:00 - %d:00"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tasks = TasksReader(this).getTasksFromJson()
        tasksGrid = findViewById(R.id.tasksGrid)
        val calendar = findViewById<CalendarView>(R.id.calendarView)
        calendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            fillTasksGrid(
                LocalDateTime.of(year, month+1, dayOfMonth, 0, 0, 0)
            )
        }
        val todayTime = LocalDateTime.now()
        calendar.date = todayTime.toEpochSecond(
            OffsetDateTime.now().offset
        )*1000
        fillTasksGrid(todayTime)
    }

    private fun fillTasksGrid(tasksDate: LocalDateTime){
        tasksGrid.removeAllViews()
        todayTasks = getTasksForToday(tasksDate)
        fillTimeColumn()
        fillTasksColumn(todayTasks, tasksDate)
    }

    private fun fillTimeColumn(){
        val columnsCount = if (todayTasks.count() > 0) todayTasks.count() else 1
        for (hour in 0 until hoursCount - 1){
            fillCell(
                rowsPerHour*hour,
                0,
                4,
                hoursText.format(hour, hour+1),
                false,
                0.5f
            )
            for (row in 0 until rowsPerHour){
                for (taskIndex in 1..columnsCount) {
                    fillCell(hour * 4 + row, taskIndex, 1, "")
                }
            }
        }
    }

    private fun fillTasksColumn(todayTasks:ArrayList<Task>, tasksDate: LocalDateTime){
        for ((taskIndex, task) in todayTasks.withIndex())
        {
            val dateStart = task.dateStart.toLocalDate() == tasksDate.toLocalDate()
            val dateFinish = task.dateFinish.toLocalDate() == tasksDate.toLocalDate()
            val startRow = if (dateStart) getRowIndexByTime(task.dateStart.toLocalTime()) else 0
            val endRow = if (dateFinish) getRowIndexByTime(
                task.dateFinish.toLocalTime(), true
            ) else rowsPerHour*hoursCount
            val durationDifference = endRow-startRow
            val duration = if (durationDifference == 0) 1 else durationDifference
            val taskButton = fillCell(
                startRow, taskIndex+1, duration, task.name, true
            )
            taskButton.setOnClickListener {
                val intent = Intent(this, TaskActivity::class.java).apply {
                    putExtra("task", task)
                }
                startActivity(intent)
            }
        }
    }

    private fun getRowIndexByTime(rowTime:LocalTime, ceiling:Boolean=false):Int {
        val rowtime = rowTime.toSecondOfDay().toDouble()
        val secondsForOneRow = (60.00/rowsPerHour)*60
        return floor(rowtime/secondsForOneRow).toInt() + (if (ceiling) 1 else 0)
    }

    private fun fillCell(
        row:Int,
        column:Int,
        rowSize:Int,
        text:String,
        clickable:Boolean=false,
        columnWeight:Float=1f
    ): Button{
        val button = MaterialButton(this, null, R.attr.materialButtonOutlinedStyle)
        val layoutParams = GridLayout.LayoutParams(
            GridLayout.spec(row, rowSize), GridLayout.spec(column, columnWeight)
        )
        layoutParams.setGravity(Gravity.FILL)
        button.text = text
        button.setStrokeColorResource(R.color.cardview_dark_background)
        button.setBackgroundColor(Color.WHITE)
        button.setTextColor(Color.BLACK)
        button.isClickable = clickable
        layoutParams.marginEnd = 0
        layoutParams.marginStart = 0
        layoutParams.setMargins(0)
        button.setPadding(0,0,0,0)
        layoutParams.height = 100
        button.height = 100
        tasksGrid.addView(button, layoutParams)
        return button
    }

    private fun getTasksForToday(tasksDateTime: LocalDateTime):ArrayList<Task> {
        val resultTasks:ArrayList<Task> = ArrayList()
        val tasksDate = tasksDateTime.toLocalDate()
        for (task in tasks){
            val taskStartDate = task.dateStart.toLocalDate()
            val taskFinishDate = task.dateFinish.toLocalDate()
            if (
                tasksDate in taskStartDate..taskFinishDate
            ){
                resultTasks.add(task)
            }
        }
        return resultTasks
    }
}