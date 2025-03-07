package com.example.mibocadillo2.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mibocadillo2.R
import com.example.mibocadillo2.data.model.Bocadillo
import com.example.mibocadillo2.databinding.ItemBocadilloBinding

class BocadilloAdapter(
    private val listaBocadillos: List<Bocadillo>,
    private val onItemClick: ((Bocadillo) -> Unit)? = null
) : RecyclerView.Adapter<BocadilloAdapter.BocadilloViewHolder>() {

    inner class BocadilloViewHolder(private val binding: ItemBocadilloBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(bocadillo: Bocadillo) {
            // Asignar textos
            binding.nombreBocadillo.text = bocadillo.nombre
            binding.alergenos.text = "ALÉRGENOS: ${bocadillo.alergenos.joinToString(", ")}"
            binding.tipo.text = "TIPO: ${bocadillo.tipo}"
            binding.precio.text = "PRECIO: %.2f€".format(bocadillo.precio)

            // Asignar icono dependiendo si es "frío" o "caliente"
            val tipoLower = bocadillo.tipo.lowercase() // Ajusta según cómo se guarde el texto
            val iconRes = if (tipoLower == "frío") {
                R.drawable.ic_frio // Reemplaza con tu icono de frío
            } else {
                R.drawable.ic_caliente // Reemplaza con tu icono de caliente
            }
            binding.iconTipo.setImageResource(iconRes)

            // Callback al hacer click en el Card
            binding.root.setOnClickListener {
                onItemClick?.invoke(bocadillo)
            }
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
