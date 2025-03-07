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
import com.example.mibocadillo2.databinding.FragmentCalendarioBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class CalendarioFragment : Fragment() {

    private var _binding: FragmentCalendarioBinding? = null
    private val binding get() = _binding!!

    private val listaBocadillos = mutableListOf<Bocadillo>()
    private lateinit var adapter: BocadilloAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarioBinding.inflate(inflater, container, false)

        // Configurar RecyclerView
        adapter = BocadilloAdapter(listaBocadillos)
        binding.recyclerViewBocadillos.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewBocadillos.adapter = adapter

        // Configurar CalendarView: se activa cada vez que se selecciona una fecha
        binding.calendarView.setOnDateChangeListener { _: android.widget.CalendarView, year: Int, month: Int, dayOfMonth: Int ->
            // Los meses en CalendarView son 0-indexed, se suma 1 si es necesario
            val calendar = Calendar.getInstance().apply { set(year, month, dayOfMonth) }
            val dayName = SimpleDateFormat("EEEE", Locale("es", "ES")).format(calendar.time)
                .replaceFirstChar { it.uppercase() }
            obtenerBocadillosPorDia(dayName)
        }

        // Al inicio, mostrar los bocadillos para el día actual
        val today = Calendar.getInstance().time
        val dayNameToday = SimpleDateFormat("EEEE", Locale("es", "ES")).format(today)
            .replaceFirstChar { it.uppercase() }
        obtenerBocadillosPorDia(dayNameToday)

        return binding.root
    }

    private fun obtenerBocadillosPorDia(dayName: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Llamar directamente a getBocadillos() ya que es suspend y retorna Response<Map<String, Bocadillo>>
                val response = RetrofitConnect.apiBocadillo.getBocadillos()
                if (response.isSuccessful) {
                    val bocadillosMap: Map<String, Bocadillo> = response.body() ?: emptyMap()
                    // Filtrar usando una lambda con parámetro explícito para evitar ambigüedades
                    val bocadillosList = bocadillosMap.values.filter { bocadillo: Bocadillo ->
                        bocadillo.dia == dayName
                    }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
