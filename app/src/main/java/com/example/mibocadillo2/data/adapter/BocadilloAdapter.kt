package com.example.mibocadillo2.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mibocadillo2.data.model.Bocadillo
import com.example.mibocadillo2.databinding.ItemBocadilloBinding

// Agregamos un parámetro onItemClick de tipo ((Bocadillo) -> Unit)? para manejar clics.
class BocadilloAdapter(
    private val listaBocadillos: List<Bocadillo>,
    private val onItemClick: ((Bocadillo) -> Unit)? = null
) : RecyclerView.Adapter<BocadilloAdapter.BocadilloViewHolder>() {

    inner class BocadilloViewHolder(private val binding: ItemBocadilloBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(bocadillo: Bocadillo) {
            binding.nombreBocadillo.text = bocadillo.nombre
            binding.alergenos.text = "ALÉRGENOS: ${bocadillo.alergenos.joinToString(", ")}"
            binding.tipo.text = "TIPO: ${bocadillo.tipo}"
            binding.precio.text = "PRECIO: %.2f€".format(bocadillo.precio)
            // Configuramos el clic para invocar el callback
            binding.root.setOnClickListener { onItemClick?.invoke(bocadillo) }
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
