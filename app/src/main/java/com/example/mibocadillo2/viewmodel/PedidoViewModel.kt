package com.example.mibocadillo2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mibocadillo2.data.api.RetrofitConnect
import com.example.mibocadillo2.data.api.ApiPedido.FirebasePostResponse
import com.example.mibocadillo2.data.model.Pedido
import kotlinx.coroutines.launch

class PedidoViewModel : ViewModel() {
    fun createPedido(pedido: Pedido, onResult: (Boolean, Pedido?) -> Unit) {
        viewModelScope.launch {
            try {
                // POST: crear el pedido y obtener la clave generada
                val response = RetrofitConnect.apiPedido.createPedido(pedido)
                if (response.isSuccessful) {
                    val postResponse: FirebasePostResponse? = response.body()
                    if (postResponse != null) {
                        val generatedKey = postResponse.name  // clave generada por Firebase
                        // Asigna la clave al pedido (ya que id es mutable var)
                        pedido.id = generatedKey
                        // PUT: actualizar el pedido para guardar el id en la DB
                        val updateResponse = RetrofitConnect.apiPedido.updatePedido(generatedKey, pedido)
                        if (updateResponse.isSuccessful) {
                            onResult(true, pedido)
                        } else {
                            onResult(false, null)
                        }
                    } else {
                        onResult(false, null)
                    }
                } else {
                    onResult(false, null)
                }
            } catch (e: Exception) {
                onResult(false, null)
            }
        }
    }

    fun updatePedido(pedidoKey: String, pedido: Pedido, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitConnect.apiPedido.updatePedido(pedidoKey, pedido)
                onResult(response.isSuccessful)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }
}
