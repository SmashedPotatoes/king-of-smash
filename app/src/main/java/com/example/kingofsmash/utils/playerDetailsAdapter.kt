package com.example.kingofsmash.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kingofsmash.databinding.FragmentPlayerCardItemBinding
import com.example.kingofsmash.models.Card

class playerDetailsAdapter(private val cardList : List<Card>) : RecyclerView.Adapter<playerDetailsAdapter.MyViewHolder>() {

    class MyViewHolder(var itemBinding: FragmentPlayerCardItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(card: Card){
            itemBinding.fragmentPlayerCardItemDescription.text = card.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = FragmentPlayerCardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItem(cardList[position])
    }

    override fun getItemCount(): Int {
        return cardList.size
    }
}