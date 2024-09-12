package com.example.retrofitkriptouygulamasi.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofitkriptouygulamasi.databinding.RowLayoutBinding
import com.example.retrofitkriptouygulamasi.model.CryptoModel

class RecyclerViewAdapter(
    private val cryptoList: ArrayList<CryptoModel>,
    private val listener: Listener
) : RecyclerView.Adapter<RecyclerViewAdapter.RowHolder>() {

    interface Listener {
        fun onItemClick(cryptoModel: CryptoModel)
    }

    private val colors: Array<String> = arrayOf(
        "#CA3F21", "#31C435", "#3177DA", "#7F91AB", "#A02FA4",
        "#389392", "#E0CFE1", "#7A7B38"
    )

    class RowHolder(val binding: RowLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cryptoModel: CryptoModel, colors: Array<String>, position: Int, listener: Listener) {
            itemView.setOnClickListener {
                listener.onItemClick(cryptoModel)
            }
            itemView.setBackgroundColor(Color.parseColor(colors[position % colors.size]))
            binding.textName.text = cryptoModel.currency
            binding.textPrice.text = cryptoModel.price
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolder {
        val binding = RowLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RowHolder(binding)
    }

    override fun getItemCount(): Int {
        return cryptoList.size
    }

    override fun onBindViewHolder(holder: RowHolder, position: Int) {
        holder.bind(cryptoList[position], colors, position, listener)
    }
}
