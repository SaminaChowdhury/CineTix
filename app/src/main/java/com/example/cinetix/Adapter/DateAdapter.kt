package com.example.cinetix.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.persistableBundleOf
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
            if(selectedPosition == position){
                binding.TextViewTime.setBackgroundResource(R.drawable.white_bg)
                binding.TextViewTime.setTextColor(binding.root.context.getColor(R.color.black))
            }else{
                binding.TextViewTime.setBackgroundResource(R.drawable.grey_bg)
                binding.TextViewTime.setTextColor(binding.root.context.getColor(R.color.white3))
            }
            binding.root.setOnClickListener {
                val position = position
                if(position!=RecyclerView.NO_POSITION){
                    lastSelectedPosition = selectedPosition
                    selectedPosition = position
                    notifyItemChanged(lastSelectedPosition)
                    notifyItemChanged(selectedPosition)
                }
    }

}}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeAdapter.TimeViewHolder {
        return TimeViewHolder(ItemTimeBinding.inflate(LayoutInflater.from(parent.context),parent,false) )
    }

    override fun getItemCount(): Int = timeSlots.size

    override fun onBindViewHolder(holder: TimeAdapter.TimeViewHolder, position: Int) {
        holder.bind(timeSlots[position])
    }
}