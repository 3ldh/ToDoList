package com.mathieu.sauvau.todolist

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_to_do.*
import java.text.SimpleDateFormat
import java.util.*

class ToDoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_do)
    }

    fun saveTodo(view: View)
    {
        val calendar: Calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, datePicker.year)
        calendar.set(Calendar.MONTH, datePicker.month)
        calendar.set(Calendar.DAY_OF_MONTH, datePicker.dayOfMonth)

        val format = SimpleDateFormat("dd/MM/yyyy HH:mm")
        val dateString = format.format(calendar.time)
        val database = FirebaseDatabase.getInstance()
        val user = FirebaseAuth.getInstance().currentUser
        val key = database.getReference(user?.uid).push().key
        val todo = Todo(todoNameEditText.text.toString(), todoMessageEditText.text.toString(), dateString)

        val childUpdates = HashMap<String, Any>()
        childUpdates[key] = todo.toFirebaseObject()
        database.getReference(user?.uid).updateChildren(childUpdates){ databaseError, databaseReference ->
            if (databaseError == null) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}
