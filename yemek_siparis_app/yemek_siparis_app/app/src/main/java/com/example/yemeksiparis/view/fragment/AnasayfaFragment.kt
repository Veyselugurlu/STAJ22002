package com.example.yemeksiparis.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.yemeksiparis.R
import com.example.yemeksiparis.adapter.YemekAdapter
import com.example.yemeksiparis.databinding.FragmentAnasayfaBinding
import com.example.yemeksiparis.viewmodel.AnasayfaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AnasayfaFragment : Fragment() {
    private lateinit var binding: FragmentAnasayfaBinding
    private val viewModel: AnasayfaViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_anasayfa, container, false)
        binding.toolbarAnasayfa.title = "Yemekler"
        binding.lifecycleOwner = this

        val yemekAdapter = YemekAdapter(requireContext(), emptyList())
        binding.yemekRv.adapter = yemekAdapter

        viewModel.yemeklerListesi.observe(viewLifecycleOwner, Observer { yemekler ->
            yemekAdapter.updateYemekListesi(yemekler)
        })

        return binding.root
    }

}
