package com.example.yemeksiparis.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yemeksiparis.adapter.SepetAdapter
import com.example.yemeksiparis.databinding.FragmentSepetBinding
import com.example.yemeksiparis.viewmodel.SepetViewModel
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class SepetFragment : Fragment() {
    private lateinit var binding: FragmentSepetBinding
    private val sepetViewModel: SepetViewModel by viewModels()
    private lateinit var sepetAdapter: SepetAdapter
    private lateinit var adapter: SepetAdapter
    private var _binding: FragmentSepetBinding? = null
    val args: SepetFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSepetBinding.inflate(inflater, container, false)

        // Adapter'i başlat ve RecyclerView'a bağlama
        sepetAdapter = SepetAdapter()
        binding.sepetRv.layoutManager = LinearLayoutManager(context)
        binding.toolbarSepet.title = "Sepet"

        binding.sepetRv.adapter = sepetAdapter
        adapter = SepetAdapter()
        binding.sepetRv.adapter = adapter

        // ViewModel'den verileri gözlemle
        sepetViewModel.sepetYemekListesi.observe(viewLifecycleOwner) { yemekListesi ->
            yemekListesi?.let {
                adapter.submitList(it)
            }
        }

        // Sepet verilerini yükleme
        sepetViewModel.loadSepetYemekler()

        // DetayFragment'ten gelen yemek verisini al
        val args: SepetFragmentArgs by navArgs()
        val yemek = args.yemekler

        // Sepet'e ekleme işlemini gerçekleştirme
        yemek?.let { yemekItem ->
            sepetViewModel.addYemekToSepet(yemekItem)
        }

        return binding.root
    }

}
