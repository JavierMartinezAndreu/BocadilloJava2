package com.example.mibocadillo2.ui.alumno

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.mibocadillo2.data.api.RetrofitConnect
import com.example.mibocadillo2.data.model.Usuario
import com.example.mibocadillo2.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            Toast.makeText(requireContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        } else {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val usuario: Usuario? = RetrofitConnect.apiUsuario.getUsuarioByUid(uid)
                    withContext(Dispatchers.Main) {
                        if (usuario != null) {
                            binding.textViewNombre.text = "Nombre: ${usuario.nombre}"
                            binding.textViewApellidos.text = "Apellidos: ${usuario.apellidos}"
                            binding.textViewCurso.text = "Curso: ${usuario.curso}"
                            binding.textViewCorreo.text = "Correo: ${usuario.correo}"
                        } else {
                            Toast.makeText(requireContext(), "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
