package com.carlosgonzalezruiz2dam.trabajo1trimestre.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainViewModel(): ViewModel() {}

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel() as T
    }
}