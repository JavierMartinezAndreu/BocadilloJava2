package com.example.mibocadillo2.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mibocadillo2.databinding.ActivityLoginBinding
import com.example.mibocadillo2.data.api.RetrofitConnect
import com.example.mibocadillo2.data.model.Usuario
import com.example.mibocadillo2.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//BINDING
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //AUTENTICACIÓN
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.firebaseAuthSettings.setAppVerificationDisabledForTesting(true)

        binding.botonIniciarsesion.setOnClickListener {

            //Autenticar usuario y contraseña
            firebaseAuth.signInWithEmailAndPassword(binding.correoElectronico.text.toString(), binding.contrasena.text.toString())
                //Cuando se complete el intento de inicio de sesión
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //obtener usuario
                        val user = firebaseAuth.currentUser
                        if (user != null) {
                            CoroutineScope(Dispatchers.Main).launch {
                                val usuario = obtenerUsuario(user.uid)
                                if (usuario != null) {
                                    //obtener rol de usuario dentro de la corrutina
                                    val rol = when (usuario.rol) {
                                        "ALUMNO" -> "ALUMNO"
                                        "ADMINISTRADOR" -> "ADMINISTRADOR"
                                        else -> "invitado"
                                    }
                                    if (rol == "ALUMNO") {
                                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(intent)
                                    } else if (rol == "ADMINISTRADOR") {
                                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(intent)
                                    }
                                } else {
                                    Toast.makeText(applicationContext, "Usuario no encontrado", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    } else {
                        Toast.makeText(this, "INICIO DE SESIÓN ERRÓNEO", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }


    private suspend fun obtenerUsuario(uid: String): Usuario? {
        return withContext(Dispatchers.IO) {
            try {
                val usuario = RetrofitConnect.api.getUsuarioByUid(uid)
                usuario
            } catch (e: Exception) {
                null
            }
        }
    }
}