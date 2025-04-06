package com.example.gaim.ui.utility

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gaim.R
import com.example.gaim.search.SearchResult

class CustomAdapter(private val dataSet: Array<SearchResult>) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    // ViewHolder for two TextViews
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView1: TextView = view.findViewById(R.id.textView1)
        val textView2: TextView = view.findViewById(R.id.textView2)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.text_row_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = dataSet[position]
        viewHolder.textView1.text = item.name
        viewHolder.textView2.text = item.accuracy.toString()
    }

    override fun getItemCount() = dataSet.size
}
