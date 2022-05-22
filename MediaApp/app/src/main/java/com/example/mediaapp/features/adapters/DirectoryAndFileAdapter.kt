package com.example.mediaapp.features.adapters

import android.annotation.SuppressLint
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
import com.example.mediaapp.models.File
import com.example.mediaapp.util.Constants

class DirectoryAndFileAdapter(private val clickItemDirectoryAndFile: ClickItemDirectoryAndFile): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface ClickItemDirectoryAndFile{
        fun clickItem(item: Any?, isHaveOptions: Boolean)
    }
    inner class DirectoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val txtNameDirectory: TextView = itemView.findViewById(R.id.textViewItemDirectoryDetail)
        val layoutDirectory: RelativeLayout = itemView.findViewById(R.id.layoutItemDirectory)
        val imgOptions: ImageView = itemView.findViewById(R.id.imageViewOptionItemDirectory)
    }
    inner class FileHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val txtNameFile: TextView = itemView.findViewById(R.id.textViewItemFileDetail)
        val layoutFile: RelativeLayout = itemView.findViewById(R.id.layoutFileItem)
        val imgOptions: ImageView = itemView.findViewById(R.id.imageViewOptionItemFile)
    }

    private val differCallBack = object : DiffUtil.ItemCallback<Any>(){
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when{
                oldItem is Directory && newItem is Directory -> oldItem.id == newItem.id
                oldItem is File && newItem is File -> oldItem.id == newItem.id
                else -> false
            }
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when{
                oldItem is Directory && newItem is Directory -> oldItem.hashCode() == newItem.hashCode()
                oldItem is File && newItem is File -> oldItem.hashCode() == newItem.hashCode()
                else -> false
            }
        }
    }
    private val differ = AsyncListDiffer(this, differCallBack)
    fun submitList(list: List<Any>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            Constants.DIRECTORY_TYPE -> DirectoryHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.directory_detail_item_row, parent, false))
            Constants.FILE_TYPE -> FileHolder( LayoutInflater.from(parent.context)
                .inflate(R.layout.file_detail_item_row, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = differ.currentList[position]
        when(item){
            is Directory ->{
                (holder as DirectoryHolder).txtNameDirectory.text = item.name
                holder.layoutDirectory.setOnClickListener {
                    clickItemDirectoryAndFile.clickItem(item, false)
                }
                holder.imgOptions.setOnClickListener {
                    clickItemDirectoryAndFile.clickItem(item, true)
                }
            }
            is File ->{
                (holder as FileHolder).txtNameFile.text = item.name
                holder.layoutFile.setOnClickListener {
                    clickItemDirectoryAndFile.clickItem(item, false)
                }
                holder.imgOptions.setOnClickListener {
                    clickItemDirectoryAndFile.clickItem(item, true)
                }
            }
            else -> throw IllegalArgumentException("Invalid binding")
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when(differ.currentList[position]){
            is Directory -> Constants.DIRECTORY_TYPE
            is File -> Constants.FILE_TYPE
            else -> throw IllegalArgumentException("Invalid type of data {$position}")
        }
    }
}