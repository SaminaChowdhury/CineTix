package com.example.cinetix.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cinetix.R
import com.example.cinetix.databinding.ItemTimeBinding

class TimeAdapter(private val timeSlots: List<String>):
    RecyclerView.Adapter<TimeAdapter.TimeViewHolder>() {

    private var selectedPosition = -1
    private var lastSelectedPosition = -1

    inner class TimeViewHolder(private val binding : ItemTimeBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(time:String){
            binding.TextViewTime.text = time
            if(selectedPosition == adapterPosition){
                binding.TextViewTime.setBackgroundResource(R.drawable.white_bg)
                binding.TextViewTime.setTextColor(binding.root.context.getColor(R.color.black))
            }else{
                binding.TextViewTime.setBackgroundResource(R.drawable.grey_bg)
                binding.TextViewTime.setTextColor(binding.root.context.getColor(R.color.white3))
            }
            binding.root.setOnClickListener {
                val pos = bindingAdapterPosition
                if(pos != RecyclerView.NO_POSITION){
                    lastSelectedPosition = selectedPosition
                    selectedPosition = pos
                    notifyItemChanged(lastSelectedPosition)
                    notifyItemChanged(selectedPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeViewHolder {
        return TimeViewHolder(ItemTimeBinding.inflate(LayoutInflater.from(parent.context),parent,false) )
    }

    override fun getItemCount(): Int = timeSlots.size

    override fun onBindViewHolder(holder: TimeViewHolder, position: Int) {
        holder.bind(timeSlots[position])
    }

    fun getSelectedTime(): String? {
        return if (selectedPosition != -1) timeSlots[selectedPosition] else null
    }
}