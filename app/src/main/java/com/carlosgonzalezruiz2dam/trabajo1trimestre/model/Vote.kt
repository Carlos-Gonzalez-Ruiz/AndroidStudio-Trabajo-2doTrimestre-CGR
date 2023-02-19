package com.carlosgonzalezruiz2dam.trabajo1trimestre.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Vote(     var id : String? = "",
                val urlArticle : String? = "",
                val userId : String? = ""): Parcelable {
}