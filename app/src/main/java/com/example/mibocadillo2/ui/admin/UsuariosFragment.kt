package com.example.mibocadillo2.ui.admin

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mibocadillo2.R
import com.example.mibocadillo2.data.adapter.UsuarioAdapter
import com.example.mibocadillo2.data.api.RetrofitConnect
import com.example.mibocadillo2.data.api.ApiUsuario.FirebasePostResponse
import com.example.mibocadillo2.data.model.Usuario
import com.example.mibocadillo2.databinding.FragmentUsuariosBinding
import com.google.firebase.auth.FirebaseAuth
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
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentUsuariosBinding.inflate(inflater, container, false)
        binding.recyclerViewUsuarios.layoutManager = LinearLayoutManager(requireContext())
        adapter = UsuarioAdapter(listaUsuarios,
            onEditClick = { usuario -> mostrarDialogoEditar(usuario) },
            onDeleteClick = { usuario -> confirmarBorrado(usuario) }
        )
        binding.recyclerViewUsuarios.adapter = adapter

        binding.btnCreateUser.setOnClickListener {
            mostrarDialogoCrearUsuario()
        }

        obtenerUsuarios()
        return binding.root
    }

    private fun obtenerUsuarios() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitConnect.apiUsuario.getUsuarios()
                if (response.isSuccessful) {
                    val usuariosMap = response.body() ?: emptyMap()
                    // Asigna la clave real a cada usuario usando la key del Map
                    val alumnos = usuariosMap.map { (key, user) ->
                        user.copy(id = key)
                    }.filter { it.rol == "ALUMNO" }
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

    private fun mostrarDialogoCrearUsuario() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_usuario, null)
        val etNombre = dialogView.findViewById<EditText>(R.id.etNombre)
        val etApellidos = dialogView.findViewById<EditText>(R.id.etApellidos)
        val etCurso = dialogView.findViewById<EditText>(R.id.etCurso)
        val etCorreo = dialogView.findViewById<EditText>(R.id.etCorreo)
        val etPassword = dialogView.findViewById<EditText>(R.id.etPassword)

        AlertDialog.Builder(requireContext())
            .setTitle("Crear Usuario")
            .setView(dialogView)
            .setPositiveButton("Crear") { dialog, _ ->
                val nuevoUsuario = Usuario(
                    id = "",  // se asignará el UID generado por Firebase Auth
                    nombre = etNombre.text.toString(),
                    apellidos = etApellidos.text.toString(),
                    curso = etCurso.text.toString(),
                    correo = etCorreo.text.toString(),
                    password = etPassword.text.toString(),
                    rol = "ALUMNO"
                )
                crearUsuario(nuevoUsuario)
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun crearUsuario(usuario: Usuario) {
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(usuario.correo, usuario.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Obtenemos el UID generado por Firebase Auth
                    val generatedUID = task.result?.user?.uid ?: ""
                    usuario.id = generatedUID

                    // En vez de llamar a createUsuario (POST) y luego update, hacemos directamente un update (PUT/PATCH)
                    lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            // Actualizamos/creamos el usuario en la ruta "Usuarios/$generatedUID.json"
                            val response = RetrofitConnect.apiUsuario.updateUsuario(generatedUID, usuario)
                            withContext(Dispatchers.Main) {
                                if (response.isSuccessful) {
                                    Toast.makeText(requireContext(), "Usuario creado con id=$generatedUID", Toast.LENGTH_SHORT).show()
                                    obtenerUsuarios()
                                } else {
                                    Toast.makeText(requireContext(), "Error al crear usuario en la base de datos", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Error en Auth: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun mostrarDialogoEditar(usuario: Usuario) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_usuario, null)
        val etNombre = dialogView.findViewById<EditText>(R.id.etNombre)
        val etApellidos = dialogView.findViewById<EditText>(R.id.etApellidos)
        val etCurso = dialogView.findViewById<EditText>(R.id.etCurso)
        val etCorreo = dialogView.findViewById<EditText>(R.id.etCorreo)
        val etPassword = dialogView.findViewById<EditText>(R.id.etPassword)

        etNombre.setText(usuario.nombre)
        etApellidos.setText(usuario.apellidos)
        etCurso.setText(usuario.curso)
        etCorreo.setText(usuario.correo)
        etPassword.setText(usuario.password)

        AlertDialog.Builder(requireContext())
            .setTitle("Editar Usuario")
            .setView(dialogView)
            .setPositiveButton("Guardar") { dialog, _ ->
                val usuarioActualizado = usuario.copy(
                    nombre = etNombre.text.toString(),
                    apellidos = etApellidos.text.toString(),
                    curso = etCurso.text.toString(),
                    correo = etCorreo.text.toString(),
                    password = etPassword.text.toString(),
                    rol = "ALUMNO"
                )
                editarUsuario(usuarioActualizado)
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun editarUsuario(usuario: Usuario) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitConnect.apiUsuario.updateUsuario(usuario.id, usuario)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Usuario actualizado", Toast.LENGTH_SHORT).show()
                        obtenerUsuarios()
                    } else {
                        Toast.makeText(requireContext(), "Error al actualizar usuario", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun confirmarBorrado(usuario: Usuario) {
        AlertDialog.Builder(requireContext())
            .setTitle("Borrar Usuario")
            .setMessage("¿Estás seguro de que quieres borrar a ${usuario.nombre} ${usuario.apellidos}?")
            .setPositiveButton("Sí") { dialog, _ ->
                borrarUsuario(usuario)
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun borrarUsuario(usuario: Usuario) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitConnect.apiUsuario.deleteUsuario(usuario.id)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Usuario borrado", Toast.LENGTH_SHORT).show()
                        obtenerUsuarios()
                    } else {
                        Toast.makeText(requireContext(), "Error al borrar usuario", Toast.LENGTH_SHORT).show()
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
