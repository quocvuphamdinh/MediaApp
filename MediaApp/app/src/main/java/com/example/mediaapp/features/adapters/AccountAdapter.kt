package com.example.mediaapp.features.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaapp.R
import com.example.mediaapp.models.User

class AccountAdapter(private val clickAccountItem: ClickAccountItem): RecyclerView.Adapter<AccountAdapter.AccountHolder>() {

    interface ClickAccountItem{
        fun clickItem(user: User)
    }
    private val differCallBack = object : DiffUtil.ItemCallback<User>(){
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differCallBack)
    fun submitList(list: List<User>) = differ.submitList(list)

    inner class AccountHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val txtName: TextView = itemView.findViewById(R.id.textViewFullnameAccountItemRow)
        val txtEmail: TextView = itemView.findViewById(R.id.textViewEmailAccountItemRow)
        val layout: RelativeLayout = itemView.findViewById(R.id.layoutAccountItemRow)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountHolder {
        return AccountHolder(LayoutInflater.from(parent.context).inflate(R.layout.account_item_row, parent, false))
    }

    override fun onBindViewHolder(holder: AccountHolder, position: Int) {
        val user = differ.currentList[position]
        holder.txtName.text = "${user.firstName} ${user.lastName}"
        holder.txtEmail.text = user.email
        holder.layout.setOnClickListener {
            clickAccountItem.clickItem(user)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}