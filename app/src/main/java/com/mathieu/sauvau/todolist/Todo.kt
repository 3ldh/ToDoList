package com.mathieu.sauvau.todolist

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by eldh on 31/01/2018.
 */
data class Todo(val id: String = "", val name: String = "", val message: String = "", val date: String = "") : Serializable

class TodoCompare : Comparator<Todo> {
    var dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE)
    override fun compare(lhs: Todo, rhs: Todo): Int {
        return dateFormat.parse(lhs.date).compareTo(dateFormat.parse(rhs.date))
    }
}