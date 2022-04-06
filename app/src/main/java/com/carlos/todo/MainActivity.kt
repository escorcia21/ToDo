package com.carlos.todo

import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.carlos.todo.model.CardData
import com.carlos.todo.model.SqLiteHelper
import com.carlos.todo.view.CardAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {

    private lateinit var floatBtn: FloatingActionButton
    private lateinit var recv: RecyclerView
    //private lateinit var userList:ArrayList<CardData>
    private lateinit var cardAdapter:CardAdapter
    private lateinit var admin: SqLiteHelper
    private lateinit var bdd: SQLiteDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        admin = SqLiteHelper(this,"ToDos", null, 1)
        bdd = admin.writableDatabase

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setContentView(R.layout.activity_main)
        //userList = ArrayList()
        floatBtn = findViewById(R.id.floatbtn)
        recv = findViewById(R.id.listView)
        cardAdapter = CardAdapter(this)
        recv.layoutManager = LinearLayoutManager(this)
        recv.adapter = cardAdapter
        getAllToDo()
        floatBtn.setOnClickListener { addToDo() }

        cardAdapter.setOnDelete {
            deleteToDo(it.id)
        }
    }

    private fun getAllToDo() {
        val lista = admin.getAll()
        Log.e("ToDo get","${lista.size}")
        cardAdapter.addItems(lista)
        Log.e("ToDo get-add","${lista.size}")
    }

    private fun deleteToDo(id: Int){
        //if (id == null) return
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Desea eliminar el siguiente ToDo?")
        builder.setCancelable(true)
        builder.setPositiveButton("Si"){dialog, _->
            admin.deleteToDo(id)
            getAllToDo()
            dialog.dismiss()
        }
        builder.setNegativeButton("No"){dialog, _->
            dialog.dismiss()
        }
        val alert = builder.create()
        alert.show()
    }

    private fun addToDo() {
        val inflter = LayoutInflater.from(this)
        val v = inflter.inflate(R.layout.add_todo,null)
        val userName = v.findViewById<TextInputLayout>(R.id.modal_title)
        val userNo = v.findViewById<TextInputLayout>(R.id.modal_Descripcion)

        val addDialog = AlertDialog.Builder(this)

        addDialog.setView(v)
        addDialog.setPositiveButton("Agregar"){
                dialog,_->
            val title = userName.editText?.text.toString()
            val desc = userNo.editText?.text.toString()
            cardAdapter.notifyDataSetChanged()
            if (title.isEmpty() || desc.isEmpty()){
                Toast.makeText(this,"Campos vacios",Toast.LENGTH_SHORT).show()
            }else {
                //userList.add(CardData(null,title,desc))
                var todo = CardData(1,title,desc)
                val status = admin.insertToDo(todo)

                if (status > -1) {
                    Toast.makeText(this,"Se ha Agregado la tarea",Toast.LENGTH_SHORT).show()
                }else {
                    Toast.makeText(this,"No se ha Agregado la tarea",Toast.LENGTH_SHORT).show()
                }
                getAllToDo()
            }
            dialog.dismiss()
        }
        addDialog.setNegativeButton("Cancelar"){
                dialog,_->
            dialog.dismiss()
            Toast.makeText(this,"Cancel",Toast.LENGTH_SHORT).show()

        }
        addDialog.create()
        addDialog.show()
    }
}