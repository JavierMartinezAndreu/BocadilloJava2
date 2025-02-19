package com.example.mibocadillo2.ui.alumno

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mibocadillo2.data.adapter.BocadilloAdapter
import com.example.mibocadillo2.data.api.RetrofitConnect
import com.example.mibocadillo2.data.model.Bocadillo
import com.example.mibocadillo2.databinding.FragmentReservarBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import retrofit2.awaitResponse

class ReservarFragment : Fragment() {

    private var _binding: FragmentReservarBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: BocadilloAdapter
    private val listaBocadillos = mutableListOf<Bocadillo>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReservarBinding.inflate(inflater, container, false)

        // Configurar RecyclerView
        adapter = BocadilloAdapter(listaBocadillos)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        obtenerBocadillosDelDia()

        return binding.root
    }

    private fun obtenerBocadillosDelDia() {
        val diaActual = obtenerDiaActual()

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitConnect.apiBocadillo.getBocadillos().awaitResponse()
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
        return dateFormat.format(Date()).replaceFirstChar { it.uppercase() } // "Lunes", "Martes", etc.
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
