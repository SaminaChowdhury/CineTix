package com.example.cinetix.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cinetix.Models.Seat
import com.example.cinetix.R
import com.example.cinetix.databinding.SeatItemBinding

class SeatListAdapter(
    private val seatList: MutableList<Seat>,
    private val context: Context,
    private val selectedSeats: MutableSet<Int>,
    private val listener: SelectedSeat
) : RecyclerView.Adapter<SeatListAdapter.SeatViewHolder>() {

    inner class SeatViewHolder(private val binding: SeatItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(seat: Seat, position: Int) {
            when (seat.status) {
                Seat.SeatStatus.AVAILABLE -> {
                    binding.seat.setBackgroundResource(
                        if (selectedSeats.contains(position)) R.drawable.ic_seat_selected
                        else R.drawable.ic_seat_available
                    )
                    binding.seat.setOnClickListener {
                        toggleSelection(position)
                    }
                }
                Seat.SeatStatus.UNAVAILABLE -> {
                    binding.seat.setBackgroundResource(R.drawable.ic_seat_unavailable)
                    binding.seat.isClickable = false
                }

                else -> {}
            }
        }

        private fun toggleSelection(position: Int) {
            if (selectedSeats.contains(position)) {
                selectedSeats.remove(position)
                listener.Return(position, false)
            } else {
                selectedSeats.add(position)
                listener.Return(position, true)
            }
            notifyItemChanged(position) // Refresh the specific item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeatViewHolder {
        val binding = SeatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SeatViewHolder(binding)
    }

    override fun getItemCount(): Int = seatList.size

    override fun onBindViewHolder(holder: SeatViewHolder, position: Int) {
        holder.bind(seatList[position], position)
    }

    interface SelectedSeat {
        fun Return(selectedSeatIndex: Int, isSelected: Boolean)
    }
}
