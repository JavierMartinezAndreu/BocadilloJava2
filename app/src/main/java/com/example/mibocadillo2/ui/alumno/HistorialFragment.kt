package com.example.mibocadillo2.ui.alumno

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mibocadillo2.databinding.FragmentHistorialBinding
import com.example.mibocadillo2.data.model.Pedido
import com.example.mibocadillo2.data.adapter.PedidoAdapter

class HistorialFragment : Fragment() {

    private var _binding: FragmentHistorialBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var pedidoAdapter: PedidoAdapter
    private val listaPedidos = mutableListOf<Pedido>() // Lista de pedidos (puedes llenar esto desde tu base de datos o API)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el binding
        _binding = FragmentHistorialBinding.inflate(inflater, container, false)

        // Configurar RecyclerView
        recyclerView = binding.recyclerViewHistorial
        recyclerView.layoutManager = LinearLayoutManager(context)
        pedidoAdapter = PedidoAdapter(listaPedidos)
        recyclerView.adapter = pedidoAdapter

        // Aquí puedes obtener los pedidos de la base de datos (simulación)
        cargarPedidos()

        return binding.root
    }

    private fun cargarPedidos() {
        // Aquí debes crear un pedido correctamente pasando los valores requeridos
        listaPedidos.add(Pedido(
            id = 1,
            usuario = "usuario_1",       // El valor de usuario (string)
            precio = 3.50,                // El valor del precio (double)
            bocadillo = "Bocadillo 1",    // El valor de bocadillo (string)
            fecha = "2025-02-18",         // El valor de fecha (string)
            hora = "12:30",               // El valor de hora (string)
            retirado = false              // El valor de retirado (booleano)
        ))

        listaPedidos.add(Pedido(
            id = 2,
            usuario = "usuario_2",       // El valor de usuario (string)
            precio = 2.48,                // El valor del precio (double)
            bocadillo = "Bocadillo 2",    // El valor de bocadillo (string)
            fecha = "2025-02-17",         // El valor de fecha (string)
            hora = "13:00",               // El valor de hora (string)
            retirado = true               // El valor de retirado (booleano)
        ))

        // Notificar al adaptador que los datos han cambiado
        pedidoAdapter.notifyDataSetChanged()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Limpiar el binding cuando la vista se destruya
    }
}
