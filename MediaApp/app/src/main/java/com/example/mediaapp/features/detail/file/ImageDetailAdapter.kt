package com.example.mediaapp.features.detail.file

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaapp.R
import com.example.mediaapp.models.File
import com.squareup.picasso.Picasso

class ImageDetailAdapter : RecyclerView.Adapter<ImageDetailAdapter.ImageDetailHolder>() {
    private val differCallBack = object : DiffUtil.ItemCallback<File>(){
        override fun areItemsTheSame(oldItem: File, newItem: File): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: File, newItem: File): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }
    private val differ = AsyncListDiffer(this, differCallBack)
    fun submitList(list : List<File>) = differ.submitList(list)

    inner class ImageDetailHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var img : ImageView = itemView.findViewById(R.id.imageViewImageDetailItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageDetailHolder {
        return ImageDetailHolder(LayoutInflater.from(parent.context).inflate(R.layout.image_detail_item_row, parent, false))
    }

    override fun onBindViewHolder(holder: ImageDetailHolder, position: Int) {
        val file = differ.currentList[position]
        Picasso.get().load(file.content).into(holder.img)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}