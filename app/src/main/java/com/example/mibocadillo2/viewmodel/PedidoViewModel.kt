package com.example.mibocadillo2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mibocadillo2.data.api.RetrofitConnect
import com.example.mibocadillo2.data.model.Pedido
import kotlinx.coroutines.launch

class PedidoViewModel : ViewModel() {
    fun createPedido(pedido: Pedido, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitConnect.apiPedido.createPedido(pedido)
                onResult(response.isSuccessful)
            } catch (e: Exception) {
                onResult(false)
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
