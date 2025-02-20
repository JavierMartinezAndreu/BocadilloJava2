package com.example.mibocadillo2.ui.alumno

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mibocadillo2.data.adapter.BocadilloAdapter
import com.example.mibocadillo2.data.api.RetrofitConnect
import com.example.mibocadillo2.data.model.Bocadillo
import com.example.mibocadillo2.data.model.Pedido
import com.example.mibocadillo2.databinding.FragmentReservarBinding
import com.example.mibocadillo2.viewmodel.PedidoViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse
import java.text.SimpleDateFormat
import java.util.*

class ReservarFragment : Fragment() {

    private var _binding: FragmentReservarBinding? = null
    private val binding get() = _binding!!

    private lateinit var pedidoViewModel: PedidoViewModel
    private val listaBocadillos = mutableListOf<Bocadillo>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReservarBinding.inflate(inflater, container, false)
        pedidoViewModel = ViewModelProvider(this).get(PedidoViewModel::class.java)

        // Configurar RecyclerView con el adapter que incluye el callback de click
        val adapter = BocadilloAdapter(listaBocadillos) { selectedBocadillo: Bocadillo ->
            // Extraer datos para el Pedido
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown"
            val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val nuevoPedido = Pedido(
                id = null,
                usuario = currentUserId,
                precio = selectedBocadillo.precio,
                bocadillo = selectedBocadillo.nombre,
                tipo = selectedBocadillo.tipo,  // Se toma del Bocadillo
                fecha = currentDate,
                hora = currentTime,
                retirado = false
            )
            // Verificar si ya existe un Pedido para hoy del usuario
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val response = RetrofitConnect.apiPedido.getPedidos() // Función suspend
                    if (response.isSuccessful) {
                        val pedidosMap = response.body() ?: emptyMap()
                        val existingEntry = pedidosMap.entries.find { entry ->
                            entry.value.usuario == currentUserId && entry.value.fecha == currentDate
                        }
                        withContext(Dispatchers.Main) {
                            if (existingEntry != null) {
                                // Si ya existe, actualizamos el Pedido usando el key existente
                                pedidoViewModel.updatePedido(existingEntry.key, nuevoPedido) { success ->
                                    if (success)
                                        Toast.makeText(requireContext(), "Pedido actualizado", Toast.LENGTH_SHORT).show()
                                    else
                                        Toast.makeText(requireContext(), "Error al actualizar el pedido", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                // Si no existe, creamos un nuevo Pedido
                                pedidoViewModel.createPedido(nuevoPedido) { success ->
                                    if (success)
                                        Toast.makeText(requireContext(), "Pedido creado", Toast.LENGTH_SHORT).show()
                                    else
                                        Toast.makeText(requireContext(), "Error al crear el pedido", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Error al verificar pedido", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        // Obtener los Bocadillos del día actual desde Firebase
        obtenerBocadillosDelDia(adapter)

        return binding.root
    }

    private fun obtenerBocadillosDelDia(adapter: BocadilloAdapter) {
        val diaActual = obtenerDiaActual()
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitConnect.apiBocadillo.getBocadillos().awaitResponse<Map<String, Bocadillo>>()
                if (response.isSuccessful) {
                    val bocadillosMap = response.body() ?: emptyMap()
                    val bocadillosList = bocadillosMap.values.filter { it.dia == diaActual }
                    withContext(Dispatchers.Main) {
                        listaBocadillos.clear()
                        listaBocadillos.addAll(bocadillosList)
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error al obtener bocadillos", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun obtenerDiaActual(): String {
        val dateFormat = SimpleDateFormat("EEEE", Locale("es", "ES"))
        return dateFormat.format(Date()).replaceFirstChar { it.uppercase() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
