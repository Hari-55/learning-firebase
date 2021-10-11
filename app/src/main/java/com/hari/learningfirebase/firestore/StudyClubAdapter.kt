package com.hari.learningfirebase.firestore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hari.learningfirebase.databinding.ItemDashBoardScBinding

class StudyClubAdapter(private val data: ArrayList<ParticipantModel>, private val listener: OnClickDashBoard) :
    RecyclerView.Adapter<StudyClubAdapter.ViewHolder>() {

    fun setData(data: List<ParticipantModel>) {
        this.data.clear()
        this.data.addAll(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDashBoardScBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.renderDashBoard(data[position], listener)
    }

    override fun getItemCount() = data.size

    class ViewHolder(val binding: ItemDashBoardScBinding) : RecyclerView.ViewHolder(binding.root) {
        fun renderDashBoard(data: ParticipantModel, listener: OnClickDashBoard) {
            with(binding) {
                tvName.text = data.name
                tvNim.text = data.nim
                tvClass.text = "${data._class} - ${data.mentor}"
                tvStatus.text = if (data.statusAcc == true) "Sudah di Acc" else "Pending"
                itemView.setOnClickListener {
                    listener.click(data, StatusUpdateEnum.UPDATE)
                }
                ivDelete.setOnClickListener {
                    listener.click(data, StatusUpdateEnum.DELETE)
                }
            }
        }
    }
}