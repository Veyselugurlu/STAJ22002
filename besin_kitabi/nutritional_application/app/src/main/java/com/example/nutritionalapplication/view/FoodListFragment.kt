package com.example.nutritionalapplication.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nutritionalapplication.adapter.FoodRecyclerAdapter
import com.example.nutritionalapplication.databinding.FragmentFoodListBinding
import com.example.nutritionalapplication.viewmodel.FoodListViewModel

class FoodListFragment : Fragment() {
  //  https://raw.githubusercontent.com/atilsamancioglu/BTK20-JSONVeriSeti/master/besinler.json
    private var _binding :  FragmentFoodListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FoodListViewModel
    private val foodRecyclerAdapter = FoodRecyclerAdapter(arrayListOf())


    override
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFoodListBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[FoodListViewModel::class.java]
        viewModel.refreshData()

        binding.foodRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.foodRecyclerView.adapter = foodRecyclerAdapter

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.foodRecyclerView.visibility = View.GONE
            binding.foodeErrorMessage.visibility= View.GONE
            binding.foodLoading.visibility = View.VISIBLE
            viewModel.refreshDataFromIntnet()
            binding.swipeRefreshLayout.isRefreshing = false
        }
        observeLiveData()
    }
    private fun observeLiveData(){
        viewModel.foods.observe(viewLifecycleOwner){
            foodRecyclerAdapter.updateFoodList(it)
            //adapter
            binding.foodRecyclerView.visibility = View.VISIBLE
        }
        viewModel.foodMessageError.observe(viewLifecycleOwner){
            if (it){
                binding.foodeErrorMessage.visibility = View.VISIBLE
                binding.foodRecyclerView.visibility = View.GONE

            }
            else
            {
                binding.foodeErrorMessage.visibility = View.GONE
            }
        }

        viewModel.loadingFood.observe(viewLifecycleOwner){
            if (it){
                binding.foodeErrorMessage.visibility = View.GONE
                binding.foodRecyclerView.visibility = View.GONE
                binding.foodLoading.visibility = View.VISIBLE
            }
            else{
                binding.foodLoading.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
       _binding = null
    }

}