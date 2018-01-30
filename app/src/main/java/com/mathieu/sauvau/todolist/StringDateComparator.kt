package com.mathieu.sauvau.todolist

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by eldh on 30/01/2018.
 */
class StringDateComparator : Comparator<Todo> {
    var dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE)
    override fun compare(lhs: Todo, rhs: Todo): Int {
        return dateFormat.parse(lhs.date).compareTo(dateFormat.parse(rhs.date))
    }
}