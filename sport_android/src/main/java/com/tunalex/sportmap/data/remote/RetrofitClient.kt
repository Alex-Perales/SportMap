package com.tunalex.sportmap.data.remote

import com.tunalex.sportmap.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // En debug apunta al backend en Docker local (10.0.2.2 = localhost del
    // host, visto desde el emulador). En release sigue yendo a producción.
    // Así no hay que acordarse de revertir esto antes de publicar la app.
    val BASE_URL = if (BuildConfig.DEBUG)
        "http://10.0.2.2:8000/"
    else
        "https://sportmap-production.up.railway.app/"

    private val okHttp: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
