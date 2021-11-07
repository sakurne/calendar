package com.example.calendar

import android.content.Context
import android.os.Environment
import android.util.JsonReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.IllegalArgumentException
import java.lang.reflect.GenericArrayType
import java.nio.file.Paths
import java.security.AccessControlContext
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime

class TasksReader(private val context: Context) {

    fun getTasksFromJson(): ArrayList<Task>{
        val dataStream = context.resources.openRawResource(R.raw.tasks_data)
        val tasksReader = JsonReader(InputStreamReader(dataStream, "UTF-8"))
        tasksReader.use {
            return getTasks(it)
        }
    }

    private fun getTasks(reader: JsonReader): ArrayList<Task>
    {
        val tasks = ArrayList<Task>()
        reader.beginArray()
        while (reader.hasNext()){
            tasks.add(readTask(reader))
        }
        reader.endArray()
        return tasks
    }

    private fun readTask(reader: JsonReader):Task{
        var id:Long? = null
        var dateStart:LocalDateTime? = null
        var dateFinish:LocalDateTime? = null
        var taskName:String? = null
        var description:String? = null

        reader.beginObject()
        while (reader.hasNext()){
            when(reader.nextName()){
                "id" -> id=reader.nextLong()
                "date_start" -> dateStart=LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(reader.nextLong()), OffsetDateTime.now().offset
                )
                "date_finish" -> dateFinish=LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(reader.nextLong()), OffsetDateTime.now().offset
                )
                "name" -> taskName=reader.nextString()
                "description" -> description=reader.nextString()
            }
        }
        reader.endObject()

        if (id==null || dateStart==null || dateFinish==null || taskName==null || description==null){
            throw IllegalArgumentException("There is not enough data in JSON element")
        }
        else {
            return Task(id, dateStart, dateFinish, taskName, description)
        }
    }




}