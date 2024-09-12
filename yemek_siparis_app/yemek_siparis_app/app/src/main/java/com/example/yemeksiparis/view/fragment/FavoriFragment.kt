package com.example.yemeksiparis.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yemeksiparis.R
import com.example.yemeksiparis.adapter.SepetAdapter
import com.example.yemeksiparis.databinding.FragmentDetayBinding
import com.example.yemeksiparis.databinding.FragmentFavoriBinding
import com.example.yemeksiparis.databinding.FragmentSepetBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriFragment : Fragment() {
    private lateinit var binding: FragmentFavoriBinding
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
        binding = FragmentFavoriBinding.inflate(inflater, container, false)

        return binding.root
    }

}
