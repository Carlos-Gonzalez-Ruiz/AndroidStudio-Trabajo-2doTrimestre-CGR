package com.carlosgonzalezruiz2dam.trabajo1trimestre.ui.comment

import androidx.lifecycle.*
import com.carlosgonzalezruiz2dam.trabajo1trimestre.model.Comment
import com.carlosgonzalezruiz2dam.trabajo1trimestre.model.CommentsData
import com.carlosgonzalezruiz2dam.trabajo1trimestre.model.server.DbFirestore
import kotlinx.coroutines.*

class CommentsViewModel(commentsData : CommentsData): ViewModel() {
    private val _state = MutableLiveData(UiState())
    val state: LiveData<UiState> get() = _state
    val commentsData = commentsData

    init {
        viewModelScope.launch(Dispatchers.Main) {
            _state.value = _state.value?.copy(loading = true)
            val result = DbFirestore.getComments(commentsData.urlArticle)
            val comments = result.map {
                Comment(    it.id,
                            it.userId,
                            it.email,
                            it.displayName,
                            it.content,
                            it.photoUrl,
                            it.postedAt,
                            it.urlArticle)
            }

            _state.value = _state.value?.copy(loading = false, comments = comments.toMutableList())
        }
    }

    fun navigateTo(comment: Comment) {
        _state.value = _state.value?.copy(navigateTo = comment)
    }

    fun onNavigateDone(){
        _state.value = _state.value?.copy(navigateTo = null)
    }

    data class UiState(
        val loading: Boolean = false,
        val comments: MutableList<Comment>? = null,
        val navigateTo: Comment? = null
    )

}

class CommentsViewModelFactory(private val commentsData: CommentsData): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CommentsViewModel(commentsData) as T
    }
}