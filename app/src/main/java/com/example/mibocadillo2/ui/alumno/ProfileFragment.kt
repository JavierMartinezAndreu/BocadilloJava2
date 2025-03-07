package com.example.mibocadillo2.ui.alumno

import android.content.Intent
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
import com.example.mibocadillo2.ui.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    // Obtén el FirebaseAuth para cerrar sesión
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        val uid = firebaseAuth.currentUser?.uid
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

        // Configurar botón para cerrar sesión
        binding.btnLogout.setOnClickListener {
            firebaseAuth.signOut()
            // Por ejemplo, regresar al LoginActivity (ajusta según tu flujo)
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
