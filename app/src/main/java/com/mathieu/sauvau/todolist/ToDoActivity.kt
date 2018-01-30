package com.mathieu.sauvau.todolist

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_to_do.*
import java.text.SimpleDateFormat
import java.util.*
import java.text.ParseException


class ToDoActivity : AppCompatActivity() {

    private val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE)
    private var todo: Todo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_do)
        if (intent.extras != null) {
            val extras = intent.extras
            todo = extras?.get("todo") as Todo?
            todoNameEditText.setText(todo?.name)
            todoMessageEditText.setText(todo?.message)

            try {
                val calendar = Calendar.getInstance()
                calendar.time = format.parse(todo?.date)
                datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            } catch (e: ParseException) {
                e.printStackTrace()
            }

        }
    }

    fun saveTodo(view: View) {
        val calendar: Calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, datePicker.year)
        calendar.set(Calendar.MONTH, datePicker.month)
        calendar.set(Calendar.DAY_OF_MONTH, datePicker.dayOfMonth)

        val dateString = format.format(calendar.time)
        val database = FirebaseDatabase.getInstance()
        val user = FirebaseAuth.getInstance().currentUser
        val userDatabaseRef = database.getReference(user?.uid)
        val key = if (todo == null) userDatabaseRef.push().key else todo!!.id
        todo = Todo(key, todoNameEditText.text.toString(), todoMessageEditText.text.toString(), dateString)
        userDatabaseRef.child(key).setValue(todo)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
