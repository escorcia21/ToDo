package com.carlos.todo.model

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class SqLiteHelper(context: Context,name: String, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, name, factory, version) {


    companion object {
        lateinit var instance: SqLiteHelper
    }

    init {
        instance = this
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("create table ToDo(id INTEGER primary key autoIncrement, title text, description text, date text)")

    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }

    fun insertToDo(record: CardData): Long {
        val bdd = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("title", record.title)
        contentValues.put("description", record.description)
        contentValues.put("date", record.date)
        val result = bdd.insert("ToDo",null,contentValues)
        bdd.close()
        return result
    }

    fun getAll(): ArrayList<CardData> {
        var cardlist = ArrayList<CardData>()
        val query = "SELECT * FROM Todo"
        val bdd = this.readableDatabase
        val cursor: Cursor?
        try {
            cursor = bdd.rawQuery(query,null)
        } catch (e: Exception) {
            e.printStackTrace()
            bdd.execSQL(query)

            return ArrayList()
        }

        var id: Int
        var title:String
        var description: String
        var fecha: String

        if(cursor.moveToFirst()){
            do {
                id = cursor.getInt(cursor.getColumnIndex("id").toInt())
                title = cursor.getString(cursor.getColumnIndex("title").toInt())
                description = cursor.getString(cursor.getColumnIndex("description").toInt())
                fecha = cursor.getString(cursor.getColumnIndex("date").toInt())

                var rec = CardData(id,title,description,fecha)
                cardlist.add(rec)
            } while (cursor.moveToNext())
        }
        return cardlist
    }

    fun deleteToDo(id:Int): Int{
        val bdd = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("id",id)

        val status = bdd.delete("ToDo","id=$id",null)
        bdd.close()
        return status
    }

    fun updateToDo(todo: CardData): Int {
        val bdd = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("id",todo.id)
        contentValues.put("title",todo.title)
        contentValues.put("description",todo.description)
        contentValues.put("date",todo.date)
        val status = bdd.update("ToDo",contentValues,"id=${todo.id}",null)
        bdd.close()
        return status
    }
}