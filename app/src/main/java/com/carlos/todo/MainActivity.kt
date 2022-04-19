package com.carlos.todo

import android.app.*
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.carlos.todo.databinding.ActivityMainBinding
import com.carlos.todo.model.*
import com.carlos.todo.view.CardAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var floatBtn: FloatingActionButton
    private lateinit var recv: RecyclerView
    //private lateinit var userList:ArrayList<CardData>
    private lateinit var cardAdapter:CardAdapter
    private lateinit var admin: SqLiteHelper
    private lateinit var bdd: SQLiteDatabase
    private lateinit var bar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        //setContentView(binding.root)
        createNotificationChannel()

        admin = SqLiteHelper(this,"ToDos", null, 1)
        bdd = admin.writableDatabase

        //userList = ArrayList()
        floatBtn = findViewById(R.id.floatbtn)
        recv = findViewById(R.id.listView)
        recv.setHasFixedSize(true)
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

    private fun createNotificationChannel()
    {
        val name = "Notif Channel"
        val desc = "A Description of the Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun scheduleNotification(title: String, message:String, date: Long){
        val intent = Intent(applicationContext, NotificationHelper::class.java)
        intent.putExtra(titleExtra, title)
        intent.putExtra(smsExtra, message)

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notiId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            date,
            pendingIntent
        )
        showAlert(date, title, message)
    }

    private fun showAlert(time: Long, title: String, message: String)
    {
        val date = Date(time)
        val dateFormat = android.text.format.DateFormat.getLongDateFormat(applicationContext)
        val timeFormat = android.text.format.DateFormat.getTimeFormat(applicationContext)

        AlertDialog.Builder(this)
            .setTitle("Notification Scheduled")
            .setMessage(
                "Title: " + title +
                        "\nMessage: " + message +
                        "\nAt: " + dateFormat.format(date) + " " + timeFormat.format(date))
            .setPositiveButton("Okay"){_,_ ->}
            .show()
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
        Log.e("ToDo get","izquierda-Eliminar")
        val builder = MaterialAlertDialogBuilder(this)
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
        val date = v.findViewById<TextInputEditText>(R.id.modal_date)
        val time = v.findViewById<TextInputEditText>(R.id.modal_time)
        var alarma = Calendar.getInstance()

        time.setShowSoftInputOnFocus(false)
        date.setShowSoftInputOnFocus(false)


        val materialTimePicker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(10)
                .setTitleText("Select Appointment time")
                .build()


        time.setOnFocusChangeListener { view, b -> if (b == true){ materialTimePicker.show(getSupportFragmentManager(),materialTimePicker.toString())} }
        time.setOnClickListener{

            materialTimePicker.show(supportFragmentManager,materialTimePicker.toString())
        }

        materialTimePicker.addOnPositiveButtonClickListener {
            val formattedTime: String = "${materialTimePicker.hour}:${materialTimePicker.minute}"
            time.setText(formattedTime)
        }

        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())

        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(constraintsBuilder.build())
                .build()

        date.setOnFocusChangeListener { view, b -> if (b == true){ datePicker.show(getSupportFragmentManager(),datePicker.toString())} }
        date.setOnClickListener{
            datePicker.show(supportFragmentManager,datePicker.toString())
        }

        datePicker.addOnPositiveButtonClickListener {
            val timeZoneUTC = TimeZone.getDefault()
            val offsetFromUTC = timeZoneUTC.getOffset(Date().time) * -1
            val simpleFormat = SimpleDateFormat("yyyy/MM/dd", Locale.US)
            val fecha = Date(it + offsetFromUTC)
            date.setText(simpleFormat.format(fecha))
        }
        val addDialog = MaterialAlertDialogBuilder(this)

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




            val formato = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US)
            var txt = date.text.toString() + " " + time.text.toString().replace("pm","").replace("am","")
            var date = formato.parse(txt)
            //Toast.makeText(this, date.toString(), Toast.LENGTH_SHORT).show()
            scheduleNotification(title,"Mensaje", date.time)


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

