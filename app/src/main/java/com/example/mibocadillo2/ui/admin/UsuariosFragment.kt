package com.example.mibocadillo2.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mibocadillo2.data.adapter.UsuarioAdapter
import com.example.mibocadillo2.data.api.RetrofitConnect
import com.example.mibocadillo2.data.model.Usuario
import com.example.mibocadillo2.databinding.FragmentUsuariosBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UsuariosFragment : Fragment() {

    private var _binding: FragmentUsuariosBinding? = null
    private val binding get() = _binding!!

    private val listaUsuarios = mutableListOf<Usuario>()
    private lateinit var adapter: UsuarioAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsuariosBinding.inflate(inflater, container, false)

        adapter = UsuarioAdapter(listaUsuarios)
        binding.recyclerViewUsuarios.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewUsuarios.adapter = adapter

        obtenerUsuarios()

        return binding.root
    }

    private fun obtenerUsuarios() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitConnect.apiUsuario.getUsuarios()
                if (response.isSuccessful) {
                    val usuariosMap = response.body() ?: emptyMap()
                    // Filtrar solo los usuarios con rol "ALUMNO"
                    val alumnos = usuariosMap.values.filter { it.rol == "ALUMNO" }
                    withContext(Dispatchers.Main) {
                        listaUsuarios.clear()
                        listaUsuarios.addAll(alumnos)
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error al obtener usuarios", Toast.LENGTH_SHORT).show()
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
