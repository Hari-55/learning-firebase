package com.hari.learningfirebase.realtime

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hari.learningfirebase.databinding.ItemLeaderBoardBinding

class LeaderBoardAdapter(private val data: ArrayList<LeaderBoardModel>, private val listener: OnClickLeaderBoard) :
    RecyclerView.Adapter<LeaderBoardAdapter.ViewHolder>() {

    fun setData(data: List<LeaderBoardModel>) {
        this.data.clear()
        this.data.addAll(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLeaderBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.renderLeaderBoard(data[position])
    }

    override fun getItemCount() = data.size

    inner class ViewHolder(private val binding: ItemLeaderBoardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun renderLeaderBoard(data: LeaderBoardModel) {
            with(binding) {
                tvName.text = data.name
                tvPoint.text = data.point
                itemView.setOnClickListener { listener.click(data) }
            }
        }
    }
}