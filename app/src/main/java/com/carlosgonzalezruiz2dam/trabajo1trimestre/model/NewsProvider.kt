package com.carlosgonzalezruiz2dam.trabajo1trimestre.model

object NewsProvider {
    fun getNews(tipo: String = "sevilla"): List<New> {
        Thread.sleep(2000)
        return (1..100).map {
            New("https://loremflickr.com/240/320/$tipo?lock=$it",
                "Noticia $it",
                "descripcion de la noticia",
                "2003-10-28",
                "")
        }
    }
}