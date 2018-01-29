package com.mathieu.sauvau.todolist
/**
 * Created by eldh on 29/01/2018.
 */

data class Todo(val name: String, val message: String, val date: String) {
    fun toFirebaseObject(): HashMap<String, String> {
        val todo = HashMap<String, String>()
        todo["name"] = name
        todo["message"] = message
        todo["date"] = date
        return todo
    }
}