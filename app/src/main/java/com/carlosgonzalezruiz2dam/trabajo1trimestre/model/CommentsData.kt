package com.carlosgonzalezruiz2dam.trabajo1trimestre.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class CommentsData( val urlArticle: String,
                    val title : String): Parcelable {}