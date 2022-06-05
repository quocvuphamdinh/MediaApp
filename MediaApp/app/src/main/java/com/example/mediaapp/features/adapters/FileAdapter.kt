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
import com.example.mediaapp.models.File

class FileAdapter(private val cLickItemDirectory: CLickItemDirectory) :
    RecyclerView.Adapter<FileAdapter.DirectoryHolder>() {

    interface CLickItemDirectory {
        fun clickItem(file: File)
        fun longClickItem(file: File)
    }

    inner class DirectoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgFolder: ImageView = itemView.findViewById(R.id.image_item_file)
        var txtName: TextView = itemView.findViewById(R.id.textViewItemFile)
    }

    private val differCallback = object : DiffUtil.ItemCallback<File>() {
        override fun areItemsTheSame(oldItem: File, newItem: File): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: File, newItem: File): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }
    private val differ = AsyncListDiffer(this, differCallback)

    fun submitList(list: List<File>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DirectoryHolder {
        return DirectoryHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.file_item_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: DirectoryHolder, position: Int) {
        val file = differ.currentList[position]
        holder.txtName.text = file.name
        holder.imgFolder.setOnClickListener {
            cLickItemDirectory.clickItem(file)
        }
        holder.imgFolder.setOnLongClickListener {
            cLickItemDirectory.longClickItem(file)
            true
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}