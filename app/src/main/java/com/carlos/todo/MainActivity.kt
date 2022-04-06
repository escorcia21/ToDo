package com.carlos.todo

import android.content.ClipData
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.carlos.todo.model.CardData
import com.carlos.todo.model.SharedPrefHelper
import com.carlos.todo.model.SqLiteHelper
import com.carlos.todo.view.CardAdapter
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {

    private lateinit var floatBtn: FloatingActionButton
    private lateinit var recv: RecyclerView
    //private lateinit var userList:ArrayList<CardData>
    private lateinit var cardAdapter:CardAdapter
    private lateinit var admin: SqLiteHelper
    private lateinit var bdd: SQLiteDatabase
    private lateinit var bar: MaterialToolbar

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
        bar = findViewById<MaterialToolbar>(R.id.topAppBar)

        bar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.favorite -> {
                    var theme = SharedPrefHelper(this)

                    if (theme.darkMode == 0){
                        theme.darkMode = 1
                    }else{
                        theme.darkMode = 0
                    }
                    setDayNigth()
                    true
                }
                else -> false
            }
        }

        cardAdapter.setOnDelete {
            deleteToDo(it.id)
        }
        setDayNigth()
    }

    private fun setDayNigth(){
        var theme = SharedPrefHelper(this)
        var mode = theme.darkMode
        if (mode == 0){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            delegate.applyDayNight()
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            delegate.applyDayNight()
        }
    }

    private fun getAllToDo() {
        val lista = admin.getAll()
        //Log.e("ToDo get","${lista.size}")
        cardAdapter.addItems(lista)
        //Log.e("ToDo get-add","${lista.size}")
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