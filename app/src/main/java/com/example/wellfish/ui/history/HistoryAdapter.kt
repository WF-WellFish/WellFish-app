package com.example.wellfish.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wellfish.R
import com.example.wellfish.data.response.DataItem

class HistoryAdapter(
    private val historyList: List<DataItem>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val fishNameTextView: TextView = itemView.findViewById(R.id.tv_fish_name)
        private val dateTextView: TextView = itemView.findViewById(R.id.tv_date)
        private val imageView: ImageView = itemView.findViewById(R.id.img_item_photo)

        fun bind(dataItem: DataItem, clickListener: OnItemClickListener) {
            fishNameTextView.text = dataItem.name
            dateTextView.text = dataItem.createdAt
            Glide.with(itemView.context)
                .load(dataItem.picture)
                .into(imageView)

            itemView.setOnClickListener {
                clickListener.onItemClick(dataItem.id?.toString() ?: "")
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(historyId: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val dataItem = historyList[position]
        holder.bind(dataItem, onItemClickListener)
    }

    override fun getItemCount(): Int = historyList.size
}
