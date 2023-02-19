package com.carlosgonzalezruiz2dam.trabajo1trimestre.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class New(  val urlImage: String,
            val title: String,
            val description: String,
            val date : String,
            val urlArticle : String): Parcelable {
}