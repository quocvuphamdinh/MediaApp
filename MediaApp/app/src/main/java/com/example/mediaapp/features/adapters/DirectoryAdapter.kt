package com.example.mediaapp.features.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaapp.R
import com.example.mediaapp.models.Directory

class DirectoryAdapter(private val cLickItemDirectory: CLickItemDirectory, private val layout:Int,private val isShowDetail:Boolean) :
    RecyclerView.Adapter<DirectoryAdapter.DirectoryHolder>() {

    interface CLickItemDirectory {
        fun clickItem(directory: Directory?, isHaveOptions: Boolean)
    }

    inner class DirectoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var imgFolder: ImageView
        val txtName: TextView = itemView.findViewById(R.id.textViewItemDirectory)
        lateinit var layout: RelativeLayout
        lateinit var imgOptions: ImageView
        init {
            if(isShowDetail){
                imgOptions = itemView.findViewById(R.id.imageViewOptionItemDirectory)
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
        val directory = differ.currentList[position]
        holder.txtName.text = directory.name
        if(isShowDetail){
            holder.imgOptions.setOnClickListener {
                cLickItemDirectory.clickItem(directory, true)
            }
            holder.layout.setOnClickListener {
                cLickItemDirectory.clickItem(directory, false)
            }
        }else{
            holder.imgFolder.setOnClickListener {
                cLickItemDirectory.clickItem(directory, false)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}