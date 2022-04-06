package com.carlos.todo.view

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.carlos.todo.Edit
import com.carlos.todo.MainActivity
import com.carlos.todo.R
import com.carlos.todo.model.CardData
import com.carlos.todo.model.SqLiteHelper

class CardAdapter(val c: Context):RecyclerView.Adapter<CardAdapter.CardViewHolder>() {
    private var userList:ArrayList<CardData> = ArrayList()
    private var onDelete: ((CardData) -> Unit)? = null

    companion object {
        lateinit var instance: CardAdapter

    }

    init {
        instance = this
    }

    inner class CardViewHolder(val v:View): RecyclerView.ViewHolder(v) {
        val myTitle: TextView
        var id: Int = 0
        val myDescription:TextView
        var myEdit:Button
        var myDelete:Button

        init {
            this.myTitle = v.findViewById<TextView>(R.id.card_title)
            this.myDescription = v.findViewById<TextView>(R.id.card_description)
            this.myEdit = v.findViewById<Button>(R.id.card_edit)
            this.myDelete = v.findViewById<Button>(R.id.card_delete)
            myEdit.setOnClickListener {
                val act = Intent(c,Edit::class.java)
                act.putExtra("id",this.id)
                act.putExtra("titulo", myTitle?.text.toString())
                act.putExtra("descripcion",myDescription?.text.toString())
                startActivity(c,act,null)
            }
        }
    }

    fun setOnDelete(callback:(CardData) -> Unit){
        this.onDelete = callback
    }

    fun addItems(items: ArrayList<CardData>) {
        userList = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v  = inflater.inflate(R.layout.list_item,parent,false)
        return CardViewHolder(v)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val newList = userList[position]
        holder.id = newList.id
        holder.myTitle.text = newList.title
        holder.myDescription.text = newList.description
        holder.myDelete.setOnClickListener { onDelete?.invoke(newList)}
    }

    override fun getItemCount(): Int {
        return userList.size
    }

}