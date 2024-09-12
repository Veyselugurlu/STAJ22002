package com.example.nutritionalapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.nutritionalapplication.databinding.FoodRecyclerRowBinding
import com.example.nutritionalapplication.model.Food
import com.example.nutritionalapplication.util.dowlandImage
import com.example.nutritionalapplication.util.placeHolderDo
import com.example.nutritionalapplication.view.FoodListFragmentDirections

class FoodRecyclerAdapter(val foodList: ArrayList<Food>) : RecyclerView.Adapter<FoodRecyclerAdapter.FoodViewHolder>(){

    class FoodViewHolder(val binding: FoodRecyclerRowBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = FoodRecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return FoodViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return foodList.size
    }
    fun updateFoodList(newFoodList : List<Food>){
        foodList.clear()
        foodList.addAll(newFoodList)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.binding.name.text = foodList[position].foodName
        holder.binding.calori.text = foodList[position].foodCalori

        holder.itemView.setOnClickListener {
            val action = FoodListFragmentDirections.actionFoodListFragmentToFoodDetailFragment(foodList[position].uuid)
            Navigation.findNavController(it).navigate(action)
        }
        holder.binding.imageView.dowlandImage(foodList[position].foodGorsel, placeHolderDo(holder.itemView.context))
    }
}