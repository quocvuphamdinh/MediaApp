package com.example.mediaapp.features.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaapp.R
import com.example.mediaapp.models.Directory

class DirectoryAdapter(private val cLickItemDirectory: CLickItemDirectory) :
    RecyclerView.Adapter<DirectoryAdapter.DirectoryHolder>() {

    interface CLickItemDirectory {
        fun clickItem(directory: Directory)
    }

    inner class DirectoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgFolder: ImageView = itemView.findViewById(R.id.image_item)
        val txtName: TextView = itemView.findViewById(R.id.textViewItemDirectory)
    }

    private val differCallback = object : DiffUtil.ItemCallback<Directory>() {
        override fun areItemsTheSame(oldItem: Directory, newItem: Directory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Directory, newItem: Directory): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }
    private val differ = AsyncListDiffer(this, differCallback)

    fun submitList(list: List<Directory>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DirectoryHolder {
        return DirectoryHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.directory_item_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: DirectoryHolder, position: Int) {
        val directory = differ.currentList[position]
        holder.txtName.text = directory.name
        holder.imgFolder.setOnClickListener {
            cLickItemDirectory.clickItem(directory)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}