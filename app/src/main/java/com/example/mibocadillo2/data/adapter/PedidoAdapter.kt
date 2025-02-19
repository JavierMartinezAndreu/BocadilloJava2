package com.example.mibocadillo2.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mibocadillo2.data.model.Pedido
import com.example.mibocadillo2.databinding.ItemPedidoBinding

class PedidoAdapter(private val listaPedidos: List<Pedido>) : RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        // Inflar el layout con ViewBinding
        val binding = ItemPedidoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PedidoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val pedido = listaPedidos[position]
        holder.bind(pedido)
    }

    override fun getItemCount(): Int {
        return listaPedidos.size
    }

    class PedidoViewHolder(private val binding: ItemPedidoBinding) : RecyclerView.ViewHolder(binding.root) {

        // Utilizar el binding para acceder a las vistas
        fun bind(pedido: Pedido) {
            binding.textViewNombreBocadillo.text = pedido.bocadillo
            binding.textViewFecha.text = pedido.fecha
            binding.textViewEstado.text = pedido.retirado.toString()
        }
    }
}
