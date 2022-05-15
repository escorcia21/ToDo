package com.carlos.todo.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.*
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.carlos.todo.Edit
import com.carlos.todo.MainActivity
import com.carlos.todo.R
import com.carlos.todo.model.CardData
import com.google.android.material.card.MaterialCardView

class CardAdapter(val c: Context):RecyclerView.Adapter<CardAdapter.CardViewHolder>(),  Filterable{
    private var userList:ArrayList<CardData> = ArrayList()
    private lateinit var userListBackup:ArrayList<CardData>
    private val mainActivity: MainActivity
    private var onDelete: ((CardData) -> Unit)? = null

    companion object {
        lateinit var instance: CardAdapter
    }

    init {
        instance = this
        this.mainActivity = c as MainActivity
    }

    inner class CardViewHolder(v:View,mainActivity: MainActivity): RecyclerView.ViewHolder(v) {
        var card: MaterialCardView
        val myTitle: TextView
        var id: Int = 0
        val myDescription:TextView
        var myEdit:Button
        var myDelete:Button
        var arrow: ImageButton
        var copy: ImageButton
        var cardDate:TextView
        var hiddenView: LinearLayout

        init {
            // init variables
            this.myTitle = v.findViewById(R.id.card_title)
            this.myDescription = v.findViewById(R.id.card_description)
            this.myEdit = v.findViewById(R.id.card_edit)
            this.myDelete = v.findViewById(R.id.card_delete)

            //init card
            this.card = v.findViewById(R.id.card)
            this.arrow = v.findViewById(R.id.arrow_button)
            this.copy = v.findViewById(R.id.copy)
            this.hiddenView = v.findViewById(R.id.hide)
            this.cardDate = v.findViewById(R.id.card_date)

            // init listeners
            myEdit.setOnClickListener {
                val act = Intent(c,Edit::class.java)
                act.putExtra("id",this.id)
                act.putExtra("titulo", myTitle?.text.toString())
                act.putExtra("descripcion",myDescription?.text.toString())
                act.putExtra("fecha",cardDate?.text.toString())
                startActivity(c,act,null)
            }
        }
    }

    fun setOnDelete(callback:(CardData) -> Unit){
        this.onDelete = callback
    }

    fun addItems(items: ArrayList<CardData>) {
        userList = items
        userListBackup = ArrayList<CardData>(userList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v  = inflater.inflate(R.layout.list_item,parent,false)
        return CardViewHolder(v,mainActivity)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val newList = userList[position]
        holder.id = newList.id
        holder.myTitle.text = newList.title
        holder.myDescription.text = newList.description
        holder.myDelete.setOnClickListener { onDelete?.invoke(newList)}
        holder.cardDate.text = newList.date

        holder.arrow.setOnClickListener {
            if (holder.hiddenView.visibility == View.GONE) {
                holder.hiddenView.visibility = View.VISIBLE
                holder.arrow.setImageResource(R.drawable.ic_baseline_expand_less_24)
            } else {
                holder.hiddenView.visibility = View.GONE
                holder.arrow.setImageResource(R.drawable.ic_baseline_expand_more_24)
            }
        }

        holder.copy.setOnClickListener{
            copyToCipBoard(holder.myDescription.text.toString())
        }

        if(mainActivity.isContextualMenuEnable){

        }
    }

    private fun copyToCipBoard(textToCopy: String) {
        val clipboardManager = c.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", textToCopy)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(c, "Text copied to clipboard", Toast.LENGTH_LONG).show()
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun getFilter(): Filter {
        return filterData;
    }

    private val filterData = object: Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = ArrayList<CardData>()

            if (constraint == null || constraint.isEmpty()){
                filteredList.addAll(userListBackup)
                Log.e("Tam","${filteredList.size}")
            } else {
                val pattern = constraint.toString().lowercase().trim()

                for (item in userListBackup) {
                    if (item.title.lowercase().contains(pattern)) {
                        filteredList.add(item)
                    }
                }
            }

            val results = FilterResults()
            results.values = filteredList

            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            userList.clear()
            userList.addAll(results?.values as Collection<CardData>)
            notifyDataSetChanged()
        }

    }
}