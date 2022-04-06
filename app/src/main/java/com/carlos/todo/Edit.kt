package com.carlos.todo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.carlos.todo.model.CardData
import com.carlos.todo.model.SqLiteHelper
import com.carlos.todo.view.CardAdapter
import com.google.android.material.textfield.TextInputLayout

class Edit : AppCompatActivity() {

    private lateinit var field_title: TextInputLayout
    private lateinit var field_description: TextInputLayout
    private lateinit var bundle: Bundle
    private lateinit var bdd: SqLiteHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        bdd = SqLiteHelper.instance

        bundle = intent.extras!!
        val editTitle = bundle.getString("titulo")
        val editDescription = bundle.getString("descripcion")

        field_title = findViewById<TextInputLayout>(R.id.edittitle)
        field_description = findViewById<TextInputLayout>(R.id.editDescripcion)
        field_title.editText?.setText(editTitle)
        field_description.editText?.setText(editDescription)

        val update =findViewById<Button>(R.id.edit_save)
        val exit =findViewById<Button>(R.id.btn_salir)

        exit.setOnClickListener {
            finish()
        }

        update.setOnClickListener {
            updateToDo()
        }
    }

    private fun updateToDo() {
        val title = field_title.editText?.text.toString()
        val desc = field_description.editText?.text.toString()
        if (title.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "Campos vacios", Toast.LENGTH_SHORT).show()
        }else {
            if (title == bundle.getString("titulo") && desc == bundle.getString("descripcion")){
                Toast.makeText(this, "No se han ingresado cambios", Toast.LENGTH_SHORT).show()
            }else {
                val id = bundle.getInt("id")
                val card = CardData(id,title,desc)
                bdd.updateToDo(card)
                CardAdapter.instance.addItems(bdd.getAll())
                finish()
            }
        }
    }
}