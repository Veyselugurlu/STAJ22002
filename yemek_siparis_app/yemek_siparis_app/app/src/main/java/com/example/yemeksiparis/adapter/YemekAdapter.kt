package com.example.yemeksiparis.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.yemeksiparis.databinding.CardTasarimBinding

import com.example.yemeksiparis.model.Yemekler
import com.example.yemeksiparis.view.fragment.AnasayfaFragmentDirections

class YemekAdapter(private val mContext: Context,  var yemeklerListesi: List<Yemekler>)
    : RecyclerView.Adapter<YemekAdapter.CardTasarimTutucu>() {

    inner class CardTasarimTutucu(var tasarim: CardTasarimBinding) : RecyclerView.ViewHolder(tasarim.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardTasarimTutucu {
        val inflater = LayoutInflater.from(mContext)
        val binding = CardTasarimBinding.inflate(inflater, parent, false)
        return CardTasarimTutucu(binding)
    }

    override fun getItemCount(): Int = yemeklerListesi.size

    override fun onBindViewHolder(holder: CardTasarimTutucu, position: Int) {
        val yemek = yemeklerListesi[position]
        val t = holder.tasarim

        val url = "http://kasimadalan.pe.hu/yemekler/resimler/${yemek.yemek_resim_adi}"
        Glide.with(mContext).load(url).override(500,750).into(t.imageViewYemek)
        t.textViewFiyat.text = "${yemek.yemek_fiyat}"
        t.cardViewFilm.setOnClickListener {
            val action = AnasayfaFragmentDirections.actionAnasayfaFragmentToDetayFragment(yemek)
            holder.itemView.findNavController().navigate(action)
        }
    }
    fun updateYemekListesi(newYemekler: List<Yemekler>) {
        yemeklerListesi = newYemekler
        notifyDataSetChanged()
    }
}
