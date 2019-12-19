package ca.aequilibrium.base.networking

import ca.aequilibrium.base.vo.transformer.Transformer
import ca.aequilibrium.base.vo.transformer.Transformers
import com.google.gson.GsonBuilder
import io.reactivex.Observable
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*

/**
 * Created by Chris on 2018-09-29.
 */
interface TransformerService {

    @GET("version")
    suspend fun getVesion(): String

    @GET("allspark")
    suspend fun getAllSpark(): String

    @GET("transformers")
    suspend fun getAll(): Response<Transformers>

    @POST("transformers")
    suspend fun add(@Body transformer: Transformer): Response<Transformer>

    @PUT("transformers")
    suspend fun update(@Body transformer: Transformer): Response<Transformer>

    @DELETE("transformers/{transformerId}")
    suspend fun delete(@Path("transformerId") id: String): Response<Unit>


//    companion object {
//
//
//        //private const val BASE_URL = "http://10.0.2.2:5001/transformers-api/us-central1/app/"
//        private const val BASE_URL = "https://transformers-api.firebaseapp.com"
//
//        private val retrofit by lazy { getClient() }
//
//        private fun getClient(): Retrofit {
//
//            val builder = OkHttpClient.Builder()
//                .addInterceptor(AuthInterceptor())
//            /**
//             *  Certificate Pinning
//             *  "certificate pinning makes your app safer, but while it's relatively easy to put the
//             *   code into the app, managing the server side component is not as trivial, and I'd
//             *   recommend people making sure they have a plan before accidentally locking people
//             *   out of their app"
//             */
//            //        builder.certificatePinner(
//            //            CertificatePinner.Builder()
//            //            .add("publichost.com", "sha256/afwiKY3RxoMmLkuRW1l7QsPZTJPwDS2pdDROQjXw8ig=")
//            //            .build())
//
//            val client = builder.build()
//
//            val gson = GsonBuilder()
//                .registerTypeAdapter(Transformer::class.java, TransformerDeserializer())
//                .registerTypeAdapter(Transformer::class.java, TransformerSerializer())
//                .create()
//
//            return Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .client(client)
//                .addConverterFactory(ScalarsConverterFactory.create())
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .build()
//        }
//        fun create() = retrofit.create(TransformerService::class.java)
//    }

}