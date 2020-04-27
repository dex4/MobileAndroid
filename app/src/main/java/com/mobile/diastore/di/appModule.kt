package com.mobile.diastore.di

import androidx.room.Room
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.mobile.diastore.database.DiaStoreDataBase
import com.mobile.diastore.feature.details.EntryDetailsViewModel
import com.mobile.diastore.feature.home.EntriesSharedViewModel
import com.mobile.diastore.feature.home.HomeViewModel
import com.mobile.diastore.service.EntriesService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

val viewModels = module {
    single { EntriesSharedViewModel() }
    single { EntryDetailsViewModel() }
    single { HomeViewModel(get(), get()) }
    single {
        Room.databaseBuilder(
            androidApplication(),
            DiaStoreDataBase::class.java,
            "diastore_database"
        ).fallbackToDestructiveMigration().build()
    }
    single { get<DiaStoreDataBase>().entriesDao() }
}

val networkModule = module {
    single { provideDefaultOkHttpClient() }
    single { provideRetrofit(get()) }
    single { provideApiService(get()) }
}

fun provideDefaultOkHttpClient(): OkHttpClient {
    val interceptor = HttpLoggingInterceptor()
    interceptor.level = HttpLoggingInterceptor.Level.BODY

    val headerAuthorizationInterceptor = Interceptor { chain ->

        val request = chain.request().url().newBuilder().build()
        val newRequest = chain.request().newBuilder()
            .header("Content-Type", "application/json")
            .url(request)
            .build()
        chain.proceed(newRequest)
    }

    val logging = HttpLoggingInterceptor()
    // sets desired log level
    logging.level = HttpLoggingInterceptor.Level.BODY

    return OkHttpClient.Builder()
        .addNetworkInterceptor(interceptor)
        .addInterceptor(logging)
        .addInterceptor(headerAuthorizationInterceptor)
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()
}

fun provideRetrofit(client: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl("https://mobileserver.conveyor.cloud")
        //.baseUrl("https://jsonplaceholder.typicode.com")
        .addConverterFactory(MoshiConverterFactory.create())
        .client(client)
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()
}

fun provideApiService(retrofit: Retrofit): EntriesService =
    retrofit.create(EntriesService::class.java)