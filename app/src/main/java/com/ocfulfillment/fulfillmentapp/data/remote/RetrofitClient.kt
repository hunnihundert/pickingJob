package com.ocfulfillment.fulfillmentapp.data.remote

import com.ocfulfillment.fulfillmentapp.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {

    companion object {
        @Volatile
        private var INSTANCE: PickingJobsApi? = null

        private fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BuildConfig.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
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

        private fun providePickingJobsApi(retrofit: Retrofit): PickingJobsApi =
            retrofit.create(PickingJobsApi::class.java)


        internal fun getPickingJobsApi(): PickingJobsApi {
            return INSTANCE ?: synchronized(this) {
                val authInterceptor = AuthInterceptor()
                val okHttpClient = provideOkHttpClient(authInterceptor)
                val retrofit = provideRetrofit(okHttpClient)
                providePickingJobsApi(retrofit).also { INSTANCE = it}
            }
        }
    }
}