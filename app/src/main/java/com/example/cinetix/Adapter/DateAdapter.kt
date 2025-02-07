package com.example.cinetix.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cinetix.R
import com.example.cinetix.databinding.ItemDateBinding

class DateAdapter(private val dateSlots: List<String>) :
    RecyclerView.Adapter<DateAdapter.DateViewHolder>() {

    private var selectedPosition = -1
    private var lastSelectedPosition = -1

    inner class DateViewHolder(private val binding: ItemDateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(date: String) {
            val dateParts = date.split("/")
            if (dateParts.size == 3) {
                binding.dayTxt.text = dateParts[0]
                binding.datMonthTxt.text = "${dateParts[1]} ${dateParts[2]}"
            }


            if (selectedPosition == adapterPosition) {
                binding.mailLayout.setBackgroundResource(R.drawable.white_bg)
                binding.dayTxt.setTextColor(binding.root.context.getColor(R.color.black))
                binding.datMonthTxt.setTextColor(binding.root.context.getColor(R.color.black))
            } else {
                binding.mailLayout.setBackgroundResource(R.drawable.grey_bg)
                binding.dayTxt.setTextColor(binding.root.context.getColor(R.color.white3))
                binding.datMonthTxt.setTextColor(binding.root.context.getColor(R.color.white3))
            }

            // Click Listener for Selection
            binding.root.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    lastSelectedPosition = selectedPosition
                    selectedPosition = pos
                    notifyItemChanged(lastSelectedPosition)
                    notifyItemChanged(selectedPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val binding = ItemDateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DateViewHolder(binding)
    }

    override fun getItemCount(): Int = dateSlots.size

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        holder.bind(dateSlots[position])
    }

    fun getSelectedDate(): String? {
        return if (selectedPosition != -1) dateSlots[selectedPosition] else null
    }
}