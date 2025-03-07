package com.example.mibocadillo2.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mibocadillo2.data.model.Bocadillo
import com.example.mibocadillo2.databinding.ItemBocadilloAdminBinding

class BocadilloAdminAdapter(
    private val bocadillos: List<Bocadillo>,
    private val onEditClick: (Bocadillo) -> Unit,
    private val onDeleteClick: (Bocadillo) -> Unit
) : RecyclerView.Adapter<BocadilloAdminAdapter.BocadilloAdminViewHolder>() {

    inner class BocadilloAdminViewHolder(private val binding: ItemBocadilloAdminBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(bocadillo: Bocadillo) {
            binding.textViewNombreAdmin.text = bocadillo.nombre
            binding.textViewTipoAdmin.text = "Tipo: ${bocadillo.tipo}"
            binding.textViewPrecioAdmin.text = "Precio: %.2f€".format(bocadillo.precio)
            binding.textViewDiaAdmin.text = "Día: ${bocadillo.dia}"
            binding.btnEditBocadilloAdmin.setOnClickListener { onEditClick(bocadillo) }
            binding.btnDeleteBocadilloAdmin.setOnClickListener { onDeleteClick(bocadillo) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BocadilloAdminViewHolder {
        val binding = ItemBocadilloAdminBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BocadilloAdminViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BocadilloAdminViewHolder, position: Int) {
        holder.bind(bocadillos[position])
    }

    override fun getItemCount(): Int = bocadillos.size
}
