package com.example.nutritionalapplication.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.nutritionalapplication.databinding.FragmentFoodDetailBinding
import com.example.nutritionalapplication.util.dowlandImage
import com.example.nutritionalapplication.util.placeHolderDo
import com.example.nutritionalapplication.viewmodel.FoodDetailViewModel
import com.example.nutritionalapplication.viewmodel.FoodListViewModel

class FoodDetailFragment : Fragment() {
    private var _binding : FragmentFoodDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FoodDetailViewModel
    var foodId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       _binding = FragmentFoodDetailBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[FoodDetailViewModel::class.java]
        arguments?.let {
            foodId = FoodDetailFragmentArgs.fromBundle(it).foodId
        }
        viewModel.getDataroom(foodId)
        observeLiveData()
    }

    private fun observeLiveData(){
        viewModel.foodLiveData.observe(viewLifecycleOwner){
            binding.foodName.text = it.foodName
            binding.foodCalori.text = it.foodCalori
            binding.foodOil.text = it.foodYag
            binding.foodProtein.text = it.foodProtein
            binding.foodcarbohydrate.text = it.foodCarbohydrate
            binding.foodImage.dowlandImage(it.foodGorsel, placeHolderDo(requireContext()))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}