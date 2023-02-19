package com.carlosgonzalezruiz2dam.trabajo1trimestre.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Comment(var id : String? = "",
              val userId : String? = "",
              val email : String? = "",
              val displayName : String? = "",
              val content : String? = "",
              val photoUrl : String? = "",
              val postedAt : String? = "",
              val urlArticle : String? = ""): Parcelable {
}