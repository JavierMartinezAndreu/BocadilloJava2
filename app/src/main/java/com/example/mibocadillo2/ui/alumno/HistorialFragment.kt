package com.example.mibocadillo2.ui.alumno

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mibocadillo2.data.adapter.PedidoAdapter
import com.example.mibocadillo2.data.api.RetrofitConnect
import com.example.mibocadillo2.data.model.Pedido
import com.example.mibocadillo2.databinding.FragmentHistorialBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

class HistorialFragment : Fragment() {

    private var _binding: FragmentHistorialBinding? = null
    private val binding get() = _binding!!

    private lateinit var pedidoAdapter: PedidoAdapter
    private val listaPedidos = mutableListOf<Pedido>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistorialBinding.inflate(inflater, container, false)

        // Configurar RecyclerView
        binding.recyclerViewHistorial.layoutManager = LinearLayoutManager(context)
        pedidoAdapter = PedidoAdapter(listaPedidos)
        binding.recyclerViewHistorial.adapter = pedidoAdapter

        // Obtener los pedidos del usuario actual desde Firebase
        obtenerPedidosDelUsuario()

        return binding.root
    }

    private fun obtenerPedidosDelUsuario() {
        // Obtén el UID del usuario actual
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown"

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Llamada directa, ya que getPedidos() es suspend
                val response = RetrofitConnect.apiPedido.getPedidos()
                if (response.isSuccessful) {
                    val pedidosMap = response.body() ?: emptyMap()
                    // Filtrar los pedidos para el usuario actual
                    val pedidosList = pedidosMap.values.filter { pedido -> pedido.usuario == currentUserId }
                    withContext(Dispatchers.Main) {
                        listaPedidos.clear()
                        listaPedidos.addAll(pedidosList)
                        pedidoAdapter.notifyDataSetChanged()

                        // Calcular el total gastado (suma de los precios de todos los pedidos)
                        val totalGastado = pedidosList.sumOf { it.precio }
                        // Actualizar el TextView del título con el total gastado
                        binding.textViewTituloHistorial.text =
                            "Historial de Pedidos - Total: %.2f€".format(totalGastado)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error al obtener pedidos", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
