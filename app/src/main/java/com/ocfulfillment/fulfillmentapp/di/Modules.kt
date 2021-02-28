package com.ocfulfillment.fulfillmentapp.di

import android.app.Application
import com.ocfulfillment.fulfillmentapp.BuildConfig
import com.ocfulfillment.fulfillmentapp.data.remote.AuthInterceptor
import com.ocfulfillment.fulfillmentapp.data.remote.PickingJobsApi
import com.ocfulfillment.fulfillmentapp.repository.PickingJobRepository
import com.ocfulfillment.fulfillmentapp.ui.viewmodel.MainViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


val viewmodelModule = module {
    viewModel { provideMainViewModel(get(), androidApplication()) }
}

val repositoryModule = module {
    single { providePickingJobsRepository(get()) }
}

val remoteModule = module {
    factory { provideAuthInterceptor() }
    factory { provideOkHttpClient(get()) }
    single { provideRetrofit(get()) }
    factory { providePickingJobsApi(get()) }
}

private fun provideMainViewModel(
    pickingJobRepository: PickingJobRepository,
    application: Application
): MainViewModel {
    return MainViewModel(pickingJobRepository, application)
}

private fun providePickingJobsRepository(pickingJobsApi: PickingJobsApi): PickingJobRepository {
    return PickingJobRepository(pickingJobsApi)
}

private fun provideAuthInterceptor(): AuthInterceptor {
    return AuthInterceptor()
}

private fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
    return OkHttpClient()
        .newBuilder()
        .addInterceptor(authInterceptor)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level =
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        })
        .build()
}

private fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl(BuildConfig.API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()
}

private fun providePickingJobsApi(retrofit: Retrofit): PickingJobsApi {
    return retrofit.create(PickingJobsApi::class.java)
}