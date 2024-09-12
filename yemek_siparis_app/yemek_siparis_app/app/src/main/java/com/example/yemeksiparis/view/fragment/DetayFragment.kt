package com.example.yemeksiparis.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.yemeksiparis.R
import com.example.yemeksiparis.databinding.FragmentDetayBinding
import com.example.yemeksiparis.model.Yemekler
import com.example.yemeksiparis.retrofit.ApiUtils
import com.example.yemeksiparis.viewmodel.SepetViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class DetayFragment : Fragment() {
    private lateinit var binding: FragmentDetayBinding
    private val sepetViewModel: SepetViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detay, container, false)
        val yemek = DetayFragmentArgs.fromBundle(requireArguments()).yemekler
        binding.yemek = yemek
        binding.toolDetayBaslik = yemek.yemek_adi

        // Resim ve diğer verileri bağla
        bindData(yemek)

        return binding.root
    }

    private fun bindData(yemek: Yemekler) {
        val url = "http://kasimadalan.pe.hu/yemekler/resimler/${yemek.yemek_resim_adi}"
        Glide.with(this)
            .load(url)
            .into(binding.ivYemek)

        binding.tvFiyat.text = "${yemek.yemek_fiyat} TL"
        binding.adi.text = yemek.yemek_adi
        binding.toplamFiyat.text = "${yemek.yemek_fiyat} TL"
        var tiklamaSayisi = 1

        binding.buttonArttir.setOnClickListener {
            tiklamaSayisi += 1
            val toplamFiyat = tiklamaSayisi * yemek.yemek_fiyat // Toplam fiyatı hesapla
            binding.toplamFiyat.text = "$toplamFiyat TL"
            binding.adet.text = tiklamaSayisi.toString()
        }

        binding.buttonAzalt.setOnClickListener {
            if (tiklamaSayisi > 1) {
                tiklamaSayisi -= 1
                val toplamFiyat = yemek.yemek_fiyat * tiklamaSayisi
                binding.toplamFiyat.text = "$toplamFiyat TL"
                binding.adet.text = tiklamaSayisi.toString()
            }
        }
        binding.favoriEkle.setOnClickListener {
            val action = DetayFragmentDirections.actionDetayFragmentToFavoriFragment(favoriler = yemek)
            findNavController().navigate(action)

        }

        binding.buttonSepet.setOnClickListener {
            val yemekAdi = yemek.yemek_adi
            val yemekFiyati = yemek.yemek_fiyat
            val yemekResimAdi = yemek.yemek_resim_adi
            val yemekSiparisAdet = tiklamaSayisi // Sepetteki adet
            val kullaniciAdi = "kullanici_adi"  // Kullanıcı adını burada belirle

            lifecycleScope.launch {
                try {
                    // YemeklerDao kullanarak sepete yemek ekleme isteği gönder
                    val response = ApiUtils.getYemeklerDao().sepeteYemekEkle(
                        yemekAdi,
                        yemekFiyati,
                        yemekResimAdi,
                        yemekSiparisAdet,
                        kullaniciAdi
                    )
                    if (response.isSuccessful && response.body()?.success == 1) {
                        // Sepete ekleme başarılı

                        Toast.makeText(requireContext(), "Yemek sepete eklendi", Toast.LENGTH_SHORT).show()
                        // SepetFragment'e yönlendirme
                        val action = DetayFragmentDirections.actionDetayFragmentToSepetFragment(yemek)
                        findNavController().navigate(action)


                    } else {
                        Toast.makeText(requireContext(), "Sepete eklenirken hata oluştu", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Bir hata oluştu: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
