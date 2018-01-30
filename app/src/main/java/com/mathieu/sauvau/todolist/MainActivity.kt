package com.mathieu.sauvau.todolist

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.todolist_view.view.*
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import android.util.Log
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.view.*
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.helper.ItemTouchHelper


class MainActivity : AppCompatActivity() {

    private val TAG = "TodoApp"
    private val mAuth = FirebaseAuth.getInstance()
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val todoList = ArrayList<Todo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, options)
        main_recyclerView.layoutManager = LinearLayoutManager(this)
        main_recyclerView.adapter = TodoListAdapter(todoList)
        main_recyclerView.adapter.notifyDataSetChanged()

        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
              //  val adapter = main_recyclerView.adapter as TodoListAdapter
                deleteTodo(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(main_recyclerView)

     //   addItemBtn.setOnClickListener(this)

    }

    fun addTodo(view: View) {
        val intent = Intent(this, ToDoActivity::class.java)
        startActivity(intent)
    }

    fun deleteTodo(position: Int)
    {
        val database = FirebaseDatabase.getInstance()
        database.getReference(FirebaseAuth.getInstance().currentUser?.uid).child(todoList[position].id).removeValue()
        todoList.removeAt(position)
        main_recyclerView.adapter.notifyItemRemoved(position)
    }

    fun logOut(view: View) {
        mAuth.signOut()
        mGoogleSignInClient.signOut().addOnCompleteListener(this) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        val database = FirebaseDatabase.getInstance()
        database.getReference(FirebaseAuth.getInstance().currentUser?.uid).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        todoList.clear()
                        Log.w(TAG, "getUser:onCancelled " + dataSnapshot.toString())
                        Log.w(TAG, "count = " + dataSnapshot.childrenCount.toString() + " values " + dataSnapshot.key)
                        dataSnapshot.children
                                .map { it.getValue(Todo::class.java) }
                                .mapTo(todoList) { it!! }
                        val comparator = StringDateComparator()
                        Collections.sort(todoList, comparator)
                        main_recyclerView.adapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.w("TodoApp", "getUser:onCancelled", databaseError.toException())
                    }
                })
    }

    private class TodoListAdapter(val todoList: ArrayList<Todo>) : RecyclerView.Adapter<TodoListAdapter.TodoViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TodoViewHolder {
            val layoutInflater = LayoutInflater.from(parent?.context)
            val todoListView = layoutInflater.inflate(R.layout.todolist_view, parent, false)
            return TodoViewHolder(todoListView)
        }

        override fun getItemCount(): Int {
            return todoList.count()
        }

        override fun onBindViewHolder(holder: TodoViewHolder?, position: Int) {
            holder?.bindItems(todoList[position])
        }

        internal class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

            private lateinit var todo: Todo

            override fun onClick(view: View?) {
                val intent = Intent(itemView?.context, ToDoActivity::class.java)
                intent.putExtra("todo", todo)
                itemView.context.startActivity(intent)
            }

            fun bindItems(todo: Todo) {
                this.todo = todo
                itemView.todolistView_ImgBackground.setOnClickListener(this)
                itemView.todolistview_floatingBtnDone.setOnClickListener { todoDone(it) }
                itemView.todolistview_nameText.text = todo.name
                itemView.todolistview_messageText.text = todo.message
                val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE)
                val calendar = Calendar.getInstance()
                calendar.time = format.parse(todo.date)
                itemView.todolistview_dayText.text = calendar.get(Calendar.DAY_OF_MONTH).toString()
                itemView.todolistview_monthText.text = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.FRANCE)
            }

            fun todoDone(view: View) {
                val activity: MainActivity = itemView.context as MainActivity
                activity.deleteTodo(adapterPosition)
            }
        }
    }
}


