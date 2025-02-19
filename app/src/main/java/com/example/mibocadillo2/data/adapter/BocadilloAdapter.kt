package com.example.mibocadillo2.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mibocadillo2.data.model.Bocadillo
import com.example.mibocadillo2.databinding.ItemBocadilloBinding

class BocadilloAdapter(private val listaBocadillos: List<Bocadillo>) :
    RecyclerView.Adapter<BocadilloAdapter.BocadilloViewHolder>() {

    class BocadilloViewHolder(private val binding: ItemBocadilloBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(bocadillo: Bocadillo) {
            binding.nombreBocadillo.text = bocadillo.nombre
            binding.alergenos.text = "ALÉRGENOS: ${bocadillo.alergenos.joinToString(", ")}"
            binding.tipo.text = "TIPO: ${bocadillo.tipo}"
            binding.precio.text = "PRECIO: %.2f€".format(bocadillo.precio)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BocadilloViewHolder {
        val binding = ItemBocadilloBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BocadilloViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BocadilloViewHolder, position: Int) {
        holder.bind(listaBocadillos[position])
    }

    override fun getItemCount(): Int = listaBocadillos.size
}
