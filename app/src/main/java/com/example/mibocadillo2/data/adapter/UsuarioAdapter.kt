package com.example.mibocadillo2.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mibocadillo2.data.model.Usuario
import com.example.mibocadillo2.databinding.ItemUsuarioBinding

class UsuarioAdapter(
    private val usuarios: List<Usuario>,
    private val onEditClick: (Usuario) -> Unit,
    private val onDeleteClick: (Usuario) -> Unit
) : RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder>() {

    inner class UsuarioViewHolder(private val binding: ItemUsuarioBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(usuario: Usuario) {
            binding.textViewNombre.text = usuario.nombre
            binding.textViewApellidos.text = usuario.apellidos
            binding.textViewCurso.text = usuario.curso
            binding.textViewCorreo.text = usuario.correo

            binding.btnEdit.setOnClickListener { onEditClick(usuario) }
            binding.btnDelete.setOnClickListener { onDeleteClick(usuario) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val binding = ItemUsuarioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UsuarioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        holder.bind(usuarios[position])
    }

    override fun getItemCount(): Int = usuarios.size
}
