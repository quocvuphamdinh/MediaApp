package com.example.mediaapp.features.myspace.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaapp.R
import com.example.mediaapp.models.Directory

class MySpaceMusicAdapter : RecyclerView.Adapter<MySpaceMusicAdapter.MySpaceMusicHolder>(){

    inner class MySpaceMusicHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var imgFolder:ImageView = itemView.findViewById(R.id.image_item)
    }

    val differCallback = object : DiffUtil.ItemCallback<Directory>(){
        override fun areItemsTheSame(oldItem: Directory, newItem: Directory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Directory, newItem: Directory): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }
    private val differ = AsyncListDiffer(this, differCallback)

    fun submitList(list:List<Directory>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MySpaceMusicHolder {
        return MySpaceMusicHolder(LayoutInflater.from(parent.context).inflate(R.layout.my_space_music_item_row, parent, false))
    }

    override fun onBindViewHolder(holder: MySpaceMusicHolder, position: Int) {
        if(position==differ.currentList.size){
            holder.imgFolder.setImageResource(R.drawable.ic_add_directory)
        }else{
            val directory = differ.currentList[position]
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size+1
    }
}