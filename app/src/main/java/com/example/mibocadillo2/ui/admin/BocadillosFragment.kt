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
import com.example.mibocadillo2.data.adapter.BocadilloAdminAdapter
import com.example.mibocadillo2.data.api.RetrofitConnect
import com.example.mibocadillo2.data.api.ApiBocadillo.FirebasePostResponse
import com.example.mibocadillo2.data.model.Bocadillo
import com.example.mibocadillo2.databinding.FragmentBocadillosBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class BocadillosFragment : Fragment() {

    private var _binding: FragmentBocadillosBinding? = null
    private val binding get() = _binding!!

    private val listaBocadillos = mutableListOf<Bocadillo>()
    private lateinit var adapter: BocadilloAdminAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentBocadillosBinding.inflate(inflater, container, false)
        binding.recyclerViewBocadillos.layoutManager = LinearLayoutManager(requireContext())
        adapter = BocadilloAdminAdapter(
            listaBocadillos,
            onEditClick = { bocadillo -> mostrarDialogoEditar(bocadillo) },
            onDeleteClick = { bocadillo -> confirmarBorrado(bocadillo) }
        )
        binding.recyclerViewBocadillos.adapter = adapter

        binding.btnCreateBocadillo.setOnClickListener {
            mostrarDialogoCrearBocadillo()
        }

        obtenerBocadillos()
        return binding.root
    }

    private fun obtenerBocadillos() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Se asume que getBocadillos() es una función suspendida en Retrofit
                val response: Response<Map<String, Bocadillo>> = RetrofitConnect.apiBocadillo.getBocadillos()
                if (response.isSuccessful) {
                    val bocadillosMap: Map<String, Bocadillo> = response.body() ?: emptyMap()
                    val bocadillosList = bocadillosMap.map { entry ->
                        entry.value.copy(id = entry.key)
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

    private fun mostrarDialogoCrearBocadillo() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_bocadillo, null)
        val etNombre = dialogView.findViewById<EditText>(R.id.etNombre)
        val etTipo = dialogView.findViewById<EditText>(R.id.etTipo)
        val etPrecio = dialogView.findViewById<EditText>(R.id.etPrecio)
        val etDia = dialogView.findViewById<EditText>(R.id.etDia)
        val etIngredientes = dialogView.findViewById<EditText>(R.id.etIngredientes)
        val etAlergenos = dialogView.findViewById<EditText>(R.id.etAlergenos)

        // Configurar campo "tipo" para seleccionar entre "frío" o "caliente"
        etTipo.isFocusable = false
        etTipo.setText("frío")
        etTipo.setOnClickListener {
            val opciones = arrayOf("frío", "caliente")
            AlertDialog.Builder(requireContext())
                .setTitle("Selecciona el tipo")
                .setSingleChoiceItems(opciones, opciones.indexOf(etTipo.text.toString())) { dialog, which ->
                    etTipo.setText(opciones[which])
                    dialog.dismiss()
                }
                .show()
        }

        // Configurar campo "día" para seleccionar el día de la semana; por defecto "Lunes"
        etDia.isFocusable = false
        etDia.setText("Lunes")
        etDia.setOnClickListener {
            val opciones = arrayOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
            AlertDialog.Builder(requireContext())
                .setTitle("Selecciona el día")
                .setSingleChoiceItems(opciones, opciones.indexOf(etDia.text.toString())) { dialog, which ->
                    etDia.setText(opciones[which])
                    dialog.dismiss()
                }
                .show()
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Crear Bocadillo")
            .setView(dialogView)
            .setPositiveButton("Crear") { dialog, _ ->
                val nuevoBocadillo = Bocadillo(
                    id = "",
                    nombre = etNombre.text.toString(),
                    tipo = etTipo.text.toString(),
                    precio = etPrecio.text.toString().toDoubleOrNull() ?: 0.0,
                    dia = etDia.text.toString(),
                    ingredientes = etIngredientes.text.toString().split(",").map { it.trim() },
                    alergenos = etAlergenos.text.toString().split(",").map { it.trim() }
                )
                crearBocadillo(nuevoBocadillo)
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun crearBocadillo(bocadillo: Bocadillo) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response: Response<FirebasePostResponse> = RetrofitConnect.apiBocadillo.createBocadillo(bocadillo)
                if (response.isSuccessful) {
                    val postResult = response.body()
                    if (postResult != null) {
                        val generatedKey = postResult.name
                        bocadillo.id = generatedKey
                        val updateResponse = RetrofitConnect.apiBocadillo.updateBocadillo(generatedKey, bocadillo)
                        withContext(Dispatchers.Main) {
                            if (updateResponse.isSuccessful) {
                                Toast.makeText(requireContext(), "Bocadillo creado", Toast.LENGTH_SHORT).show()
                                obtenerBocadillos()
                            } else {
                                Toast.makeText(requireContext(), "Error al actualizar bocadillo", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Error: respuesta vacía", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error al crear bocadillo", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun mostrarDialogoEditar(bocadillo: Bocadillo) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_bocadillo, null)
        val etNombre = dialogView.findViewById<EditText>(R.id.etNombre)
        val etTipo = dialogView.findViewById<EditText>(R.id.etTipo)
        val etPrecio = dialogView.findViewById<EditText>(R.id.etPrecio)
        val etDia = dialogView.findViewById<EditText>(R.id.etDia)
        val etIngredientes = dialogView.findViewById<EditText>(R.id.etIngredientes)
        val etAlergenos = dialogView.findViewById<EditText>(R.id.etAlergenos)

        etNombre.setText(bocadillo.nombre)
        etTipo.setText(bocadillo.tipo)
        etPrecio.setText(bocadillo.precio.toString())
        etDia.setText(bocadillo.dia)
        etIngredientes.setText(bocadillo.ingredientes.joinToString(", "))
        etAlergenos.setText(bocadillo.alergenos.joinToString(", "))

        // Configurar el campo "tipo" para seleccionar "frío" o "caliente"
        etTipo.isFocusable = false
        etTipo.setOnClickListener {
            val opciones = arrayOf("frío", "caliente")
            AlertDialog.Builder(requireContext())
                .setTitle("Selecciona el tipo")
                .setSingleChoiceItems(opciones, opciones.indexOf(etTipo.text.toString())) { dialog, which ->
                    etTipo.setText(opciones[which])
                    dialog.dismiss()
                }
                .show()
        }

        // Configurar el campo "día" para seleccionar el día de la semana
        etDia.isFocusable = false
        etDia.setOnClickListener {
            val opciones = arrayOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
            AlertDialog.Builder(requireContext())
                .setTitle("Selecciona el día")
                .setSingleChoiceItems(opciones, opciones.indexOf(etDia.text.toString())) { dialog, which ->
                    etDia.setText(opciones[which])
                    dialog.dismiss()
                }
                .show()
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Editar Bocadillo")
            .setView(dialogView)
            .setPositiveButton("Guardar") { dialog, _ ->
                val bocadilloActualizado = bocadillo.copy(
                    nombre = etNombre.text.toString(),
                    tipo = etTipo.text.toString(),
                    precio = etPrecio.text.toString().toDoubleOrNull() ?: 0.0,
                    dia = etDia.text.toString(),
                    ingredientes = etIngredientes.text.toString().split(",").map { it.trim() },
                    alergenos = etAlergenos.text.toString().split(",").map { it.trim() }
                )
                editarBocadillo(bocadilloActualizado)
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun editarBocadillo(bocadillo: Bocadillo) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitConnect.apiBocadillo.updateBocadillo(bocadillo.id, bocadillo)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Bocadillo actualizado", Toast.LENGTH_SHORT).show()
                        obtenerBocadillos()
                    } else {
                        Toast.makeText(requireContext(), "Error al actualizar bocadillo", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun confirmarBorrado(bocadillo: Bocadillo) {
        AlertDialog.Builder(requireContext())
            .setTitle("Borrar Bocadillo")
            .setMessage("¿Estás seguro de que quieres borrar ${bocadillo.nombre}?")
            .setPositiveButton("Sí") { dialog, _ ->
                borrarBocadillo(bocadillo)
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun borrarBocadillo(bocadillo: Bocadillo) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitConnect.apiBocadillo.deleteBocadillo(bocadillo.id)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Bocadillo borrado", Toast.LENGTH_SHORT).show()
                        obtenerBocadillos()
                    } else {
                        Toast.makeText(requireContext(), "Error al borrar bocadillo", Toast.LENGTH_SHORT).show()
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
