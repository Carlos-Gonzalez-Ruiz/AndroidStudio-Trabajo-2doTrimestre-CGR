package com.carlosgonzalezruiz2dam.trabajo1trimestre.ui.news

import androidx.lifecycle.*
import com.carlosgonzalezruiz2dam.trabajo1trimestre.model.New
import com.carlosgonzalezruiz2dam.trabajo1trimestre.model.NewsData
import com.carlosgonzalezruiz2dam.trabajo1trimestre.model.server.RemoteConnection
import kotlinx.coroutines.*

class NewsViewModel(countryCode : String,
                    newsData : NewsData,
                    fromDate : String,
                    apiKey: String): ViewModel() {
    private val _state = MutableLiveData(UiState())
    val state: LiveData<UiState> get() = _state
    val newsData = newsData

    init {
        viewModelScope.launch(Dispatchers.Main) {
            _state.value = _state.value?.copy(loading = true)
            val result = RemoteConnection.service.todaysNews(countryCode, newsData.category, fromDate, apiKey)
            val news = result.articles.map {

                New(    if (it.urlToImage == null) "" else it.urlToImage,
                        if (it.title == null) "" else it.title,
                        if (it.description == null) "" else it.description,
                        if (it.publishedAt == null) "" else it.publishedAt.split("T").get(0), // Mostrar solo AÑO/MES/DÍA
                        it.url
                )
            }

            _state.value = _state.value?.copy(loading = false, news = news)
        }
    }

    fun build(countryCode : String, category: String, fromDate: String, apiKey: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _state.value = _state.value?.copy(loading = true)
            val result = RemoteConnection.service.todaysNews(countryCode, category, fromDate, apiKey)
            val news = result.articles.map {
                New(    if (it.urlToImage == null) "" else it.urlToImage,
                    if (it.title == null) "" else it.title,
                    if (it.description == null) "" else it.description,
                    if (it.publishedAt == null) "" else it.publishedAt.split("T").get(0), // Mostrar solo AÑO/MES/DÍA
                    it.url
                )
            }

            _state.value = _state.value?.copy(loading = false, news = news)
        }
    }

    fun navigateTo(newArticle: New) {
        _state.value = _state.value?.copy(navigateTo = newArticle)
    }

    fun onNavigateDone(){
        _state.value = _state.value?.copy(navigateTo = null)
    }

    data class UiState(
        val loading: Boolean = false,
        val news: List<New>? = null,
        val navigateTo: New? = null
    )

}

class NewsViewModelFactory( private val countryCode: String,
                            private val newsData: NewsData,
                            private val fromDate: String,
                            private val apiKey: String): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewsViewModel(countryCode, newsData, fromDate, apiKey) as T
    }
}