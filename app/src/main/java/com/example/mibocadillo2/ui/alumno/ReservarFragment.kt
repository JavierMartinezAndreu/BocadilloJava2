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
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
            // Extraer datos para crear un Pedido
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown"
            val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val nuevoPedido = Pedido(
                id = null,
                usuario = currentUserId,
                precio = selectedBocadillo.precio,
                bocadillo = selectedBocadillo.nombre,
                tipo = selectedBocadillo.tipo,
                fecha = currentDate,
                hora = currentTime,
                retirado = false
            )
            // Verificar si ya existe un Pedido para hoy del usuario
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val response = RetrofitConnect.apiPedido.getPedidos() // función suspend
                    if (response.isSuccessful) {
                        val pedidosMap = response.body() ?: emptyMap()
                        val existingEntry = pedidosMap.entries.find { entry ->
                            entry.value.usuario == currentUserId && entry.value.fecha == currentDate
                        }
                        withContext(Dispatchers.Main) {
                            if (existingEntry != null) {
                                // Si ya existe, actualizamos el pedido con la nueva información y generamos el QR
                                pedidoViewModel.updatePedido(existingEntry.key, nuevoPedido) { success ->
                                    if (success) {
                                        generarQr(nuevoPedido)
                                    } else {
                                        Toast.makeText(requireContext(), "Error al actualizar el pedido", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                // Si no existe, creamos un nuevo Pedido y luego generamos el QR
                                pedidoViewModel.createPedido(nuevoPedido) { success, createdPedido ->
                                    if (success && createdPedido != null) {
                                        Toast.makeText(requireContext(), "Pedido creado", Toast.LENGTH_SHORT).show()
                                        generarQr(createdPedido)
                                    } else {
                                        Toast.makeText(requireContext(), "Error al crear el pedido", Toast.LENGTH_SHORT).show()
                                    }
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

        // Al cargar el fragmento, se verifica si el usuario ya tiene un pedido para hoy y se muestra el QR si es el caso.
        mostrarQrSiPedidoExistente()

        return binding.root
    }

    private fun obtenerBocadillosDelDia(adapter: BocadilloAdapter) {
        val diaActual = obtenerDiaActual()
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitConnect.apiBocadillo.getBocadillos()
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

    private fun mostrarQrSiPedidoExistente() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown"
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitConnect.apiPedido.getPedidos()
                if (response.isSuccessful) {
                    val pedidosMap = response.body() ?: emptyMap()
                    val existingEntry = pedidosMap.entries.find { entry ->
                        entry.value.usuario == currentUserId && entry.value.fecha == currentDate
                    }
                    withContext(Dispatchers.Main) {
                        if (existingEntry != null) {
                            generarQr(existingEntry.value)
                        }
                    }
                }
            } catch (e: Exception) {
                // Aquí podrías registrar el error o notificar
            }
        }
    }

    private fun generarQr(pedido: Pedido) {
        val json = Gson().toJson(pedido)
        try {
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.encodeBitmap(json, BarcodeFormat.QR_CODE, 400, 400)
            binding.qrImageView.visibility = View.VISIBLE
            binding.qrImageView.setImageBitmap(bitmap)
            Toast.makeText(requireContext(), "Código QR generado", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error generando el código QR", Toast.LENGTH_SHORT).show()
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
