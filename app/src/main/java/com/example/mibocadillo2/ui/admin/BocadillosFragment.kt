package com.example.mibocadillo2.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mibocadillo2.R

class BocadillosFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar un layout simple
        return inflater.inflate(R.layout.fragment_bocadillos, container, false)
    }
}
