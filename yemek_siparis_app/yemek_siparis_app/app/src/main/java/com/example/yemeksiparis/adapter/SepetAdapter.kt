package com.example.yemeksiparis.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.yemeksiparis.databinding.SepetCardTasarimBinding
import com.example.yemeksiparis.model.Yemekler

class SepetAdapter : ListAdapter<Yemekler, SepetAdapter.SepetViewHolder>(YemekDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SepetViewHolder {
        val binding = SepetCardTasarimBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SepetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SepetViewHolder, position: Int) {
        val yemek = getItem(position)
        holder.bind(yemek)



    }
    class SepetViewHolder(private val binding: SepetCardTasarimBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(yemek: Yemekler) {
            binding.yemek = yemek

            // Glide ile yemek resmini yükle
            val url = "http://kasimadalan.pe.hu/yemekler/resimler/${yemek.yemek_resim_adi}"
            Glide.with(binding.root.context)
                .load(url)
                .into(binding.ivSepet) // SepetCardTasarimBinding'deki ImageView'ın ID'si 'ivYemekResim' olarak varsayıldı

            // Adet ve toplam fiyat bilgilerini güncelle
            binding.adetSepet.text = yemek.yemek_siparis_adet.toString() // SepetCardTasarimBinding'deki TextView'ın ID'si 'tvAdet' olarak varsayıldı
            binding.toplamFiyatSepet.text = "${yemek.yemek_fiyat * yemek.yemek_siparis_adet} TL" // SepetCardTasarimBinding'deki TextView'ın ID'si 'tvToplamFiyat' olarak varsayıldı

            // Diğer bağlama işlemleri
        }
    }

    class YemekDiffCallback : DiffUtil.ItemCallback<Yemekler>() {
        override fun areItemsTheSame(oldItem: Yemekler, newItem: Yemekler): Boolean {
            return oldItem.yemek_id == newItem.yemek_id
        }

        override fun areContentsTheSame(oldItem: Yemekler, newItem: Yemekler): Boolean {
            return oldItem == newItem
        }
    }
}
