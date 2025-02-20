package com.example.mibocadillo2.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mibocadillo2.data.model.Pedido
import com.example.mibocadillo2.databinding.ItemPedidoBinding

class PedidoAdapter(private val listaPedidos: List<Pedido>) : RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder>() {

    inner class PedidoViewHolder(private val binding: ItemPedidoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pedido: Pedido) {
            binding.textViewNombreBocadillo.text = pedido.bocadillo
            binding.textViewTipo.text = "TIPO: ${pedido.tipo}"
            binding.textViewPrecio.text = "PRECIO: %.2f€".format(pedido.precio)
            binding.textViewFecha.text = pedido.fecha
            binding.textViewEstado.text = if (pedido.retirado) "Retirado" else "Pendiente"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val binding = ItemPedidoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PedidoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        holder.bind(listaPedidos[position])
    }

    override fun getItemCount(): Int = listaPedidos.size
}
