package com.example.mibocadillo2.ui.alumno

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mibocadillo2.databinding.FragmentCalendarioBinding

class CalendarioFragment : Fragment() {

    private var _binding: FragmentCalendarioBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el binding
        _binding = FragmentCalendarioBinding.inflate(inflater, container, false)

        // Mostrar un mensaje "Hola"
        binding.textViewCalendario.text = "Hola"

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Limpiar el binding cuando la vista se destruya
    }
}
