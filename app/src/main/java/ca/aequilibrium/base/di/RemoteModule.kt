package ca.aequilibrium.base.di

import ca.aequilibrium.base.data.TransformerRemoteDataSource
import ca.aequilibrium.base.networking.AuthInterceptor
import ca.aequilibrium.base.networking.TransformerDeserializer
import ca.aequilibrium.base.networking.TransformerSerializer
import ca.aequilibrium.base.networking.TransformerService
import ca.aequilibrium.base.vo.transformer.Transformer
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

fun createRemoteModule(baseUrl: String) = module {

    factory<OkHttpClient> { OkHttpClient.Builder().addInterceptor(AuthInterceptor()).addInterceptor(HttpLoggingInterceptor().apply{ level = HttpLoggingInterceptor.Level.BODY}).build() }

    single {
        val gson = GsonBuilder()
            .registerTypeAdapter(Transformer::class.java, TransformerDeserializer())
            .registerTypeAdapter(Transformer::class.java, TransformerSerializer())
            .create()

        Retrofit.Builder()
            .client(get())
            .baseUrl(baseUrl)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    factory{ get<Retrofit>().create(TransformerService::class.java) }

    factory<CoroutineScope> { CoroutineScope(Dispatchers.IO) }

    factory<TransformerRemoteDataSource> { TransformerRemoteDataSource(get()) }
}