package com.carlosgonzalezruiz2dam.trabajo1trimestre.model.server

import retrofit2.http.GET
import retrofit2.http.Query

interface RemoteService {
    @GET ("top-headlines?sortBy=publishedAt")
    suspend fun todaysNews( @Query("country") countryCode: String,
                            @Query("category") category : String,
                            @Query("from") fromDate : String,
                            @Query("apiKey") apiKey: String): RemoteResult
}