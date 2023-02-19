package com.carlosgonzalezruiz2dam.trabajo1trimestre.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.carlosgonzalezruiz2dam.trabajo1trimestre.model.New

class DetailViewModel(newArticle: New): ViewModel() {
    private val _new = MutableLiveData(newArticle)
    val newArticle: LiveData<New> get() = _new
}

@Suppress("UNCHECKED_CAST")
class DetailViewModelFactory(private val newArticle: New): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DetailViewModel(newArticle) as T
    }
}