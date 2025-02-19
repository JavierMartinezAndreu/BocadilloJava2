package com.example.mibocadillo2.ui.alumno

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mibocadillo2.data.adapter.BocadilloAdapter
import com.example.mibocadillo2.data.model.Bocadillo
import com.example.mibocadillo2.databinding.FragmentReservarBinding
import java.text.SimpleDateFormat
import java.util.*

class ReservarFragment : Fragment() {

    private var _binding: FragmentReservarBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReservarBinding.inflate(inflater, container, false)

        // Obtener el día actual
        val diaActual = obtenerDiaActual()

        // Lista simulada de bocadillos
        val listaBocadillos = listOf(
            Bocadillo(1, "frío", "Bocadillo Caprese", listOf("pesto", "tomate", "mozzarella"), listOf("Lácteos"), 3.50, "Martes"),
            Bocadillo(2, "caliente", "Bocadillo de Salchichas y Queso", listOf("salchicha", "queso"), listOf("Lácteos"), 3.75, "Martes"),
            Bocadillo(3, "frío", "Bocadillo Vegetal", listOf("lechuga", "tomate", "huevo", "atún"), listOf("Gluten", "Huevo", "Pescado"), 3.20, "Miércoles"),
            Bocadillo(4, "caliente", "Bocadillo de Pollo", listOf("pollo", "queso", "pimiento"), listOf("Gluten", "Lácteos"), 4.00, "Miércoles")
        )

        // Filtrar los bocadillos del día actual
        val bocadillosDelDia = listaBocadillos.filter { it.dia == diaActual }

        // Configurar RecyclerView
        val adapter = BocadilloAdapter(bocadillosDelDia)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        return binding.root
    }

    private fun obtenerDiaActual(): String {
        val dateFormat = SimpleDateFormat("EEEE", Locale("es", "ES"))
        return dateFormat.format(Date()).replaceFirstChar { it.uppercase() } // "Martes", "Miércoles", etc.
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
