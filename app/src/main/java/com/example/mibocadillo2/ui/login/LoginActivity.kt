package com.example.mibocadillo2.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricManager
import androidx.core.content.ContextCompat
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.mibocadillo2.databinding.ActivityLoginBinding
import com.example.mibocadillo2.data.api.RetrofitConnect
import com.example.mibocadillo2.data.model.Usuario
import com.example.mibocadillo2.ui.admin.AdminActivity
import com.example.mibocadillo2.ui.alumno.AlumnoActivity
import com.example.mibocadillo2.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.firebaseAuthSettings.setAppVerificationDisabledForTesting(true)

        // Configura BiometricPrompt
        val executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(applicationContext, "Error de biometría: $errString", Toast.LENGTH_SHORT).show()
            }
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                // Autenticación biométrica exitosa; recupera las credenciales y haz login.
                signInWithSavedCredentials()
            }
            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(applicationContext, "Autenticación biométrica fallida", Toast.LENGTH_SHORT).show()
            }
        })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticación biométrica")
            .setSubtitle("Utiliza tu huella digital para iniciar sesión")
            .setNegativeButtonText("Cancelar")
            .build()

        binding.btnBiometric.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }

        binding.botonIniciarsesion.setOnClickListener {
            signInWithCredentials()
        }
    }

    private fun signInWithCredentials() {
        val email = binding.correoElectronico.text.toString()
        val password = binding.contrasena.text.toString()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        // Guardar credenciales de forma segura para futuros inicios biométricos.
                        saveCredentials(email, password)
                        CoroutineScope(Dispatchers.Main).launch {
                            val usuario = obtenerUsuario(user.uid)
                            if (usuario != null) {
                                val rol = when (usuario.rol) {
                                    "ALUMNO" -> "ALUMNO"
                                    "ADMINISTRADOR" -> "ADMINISTRADOR"
                                    else -> "invitado"
                                }
                                if (rol == "ALUMNO") {
                                    val intent = Intent(this@LoginActivity, AlumnoActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                } else if (rol == "ADMINISTRADOR") {
                                    val intent = Intent(this@LoginActivity, AdminActivity::class.java)
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

    // Recupera credenciales guardadas y las usa para iniciar sesión.
    private fun signInWithSavedCredentials() {
        val sharedPreferences = EncryptedSharedPreferences.create(
            "user_credentials",
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        val email = sharedPreferences.getString("email", null)
        val password = sharedPreferences.getString("password", null)

        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            Toast.makeText(this, "No hay credenciales guardadas. Inicia sesión manualmente primero.", Toast.LENGTH_LONG).show()
            return
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        CoroutineScope(Dispatchers.Main).launch {
                            val usuario = obtenerUsuario(user.uid)
                            if (usuario != null) {
                                val rol = when (usuario.rol) {
                                    "ALUMNO" -> "ALUMNO"
                                    "ADMINISTRADOR" -> "ADMINISTRADOR"
                                    else -> "invitado"
                                }
                                if (rol == "ALUMNO") {
                                    val intent = Intent(this@LoginActivity, AlumnoActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                } else if (rol == "ADMINISTRADOR") {
                                    val intent = Intent(this@LoginActivity, AdminActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                }
                            } else {
                                Toast.makeText(applicationContext, "Usuario no encontrado", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "Error al iniciar sesión con biometría", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun saveCredentials(email: String, password: String) {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val sharedPreferences = EncryptedSharedPreferences.create(
            "user_credentials",
            masterKeyAlias,
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        with(sharedPreferences.edit()) {
            putString("email", email)
            putString("password", password)
            apply()
        }
    }

    private suspend fun obtenerUsuario(uid: String): Usuario? {
        return withContext(Dispatchers.IO) {
            try {
                RetrofitConnect.apiUsuario.getUsuarioByUid(uid)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
