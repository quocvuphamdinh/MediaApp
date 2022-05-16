package com.example.mediaapp.features.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaapp.R
import com.example.mediaapp.models.Directory

class DirectoryAdapter(private val cLickItemDirectory: CLickItemDirectory, private val layout:Int,private val isShowDetail:Boolean) :
    RecyclerView.Adapter<DirectoryAdapter.DirectoryHolder>() {

    interface CLickItemDirectory {
        fun clickItem(directory: Directory?)
    }

    inner class DirectoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var imgFolder: ImageView
        val txtName: TextView = itemView.findViewById(R.id.textViewItemDirectory)
        lateinit var layout: LinearLayout
        init {
            if(isShowDetail){
                layout = itemView.findViewById(R.id.layoutItemDirectory)
            }else{
                imgFolder = itemView.findViewById(R.id.image_item)
            }
        }
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
                .inflate(layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: DirectoryHolder, position: Int) {
        if(isShowDetail){
            val directory = differ.currentList[position]
            holder.txtName.text = directory.name
            holder.layout.setOnClickListener {
                cLickItemDirectory.clickItem(directory)
            }
        }else{
            if (position == differ.currentList.size) {
                holder.imgFolder.setImageResource(R.drawable.ic_add_directory)
                holder.txtName.visibility = View.GONE
                holder.imgFolder.setOnClickListener {
                    cLickItemDirectory.clickItem(null)
                }
            } else {
                val directory = differ.currentList[position]
                holder.txtName.text = directory.name
                holder.imgFolder.setOnClickListener {
                    cLickItemDirectory.clickItem(directory)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        if(isShowDetail){
            return differ.currentList.size
        }
        return differ.currentList.size + 1
    }
}