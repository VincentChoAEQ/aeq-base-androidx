package ca.aequilibrium.base.networking

import ca.aequilibrium.base.util.Preferences
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by Chris on 2018-09-29.
 */
class AuthInterceptor : Interceptor {

    companion object {
        private const val HEADER_AUTHORIZATION = "Authorization"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val builder = original.newBuilder()

        Preferences.token?.let {
            builder.addHeader(HEADER_AUTHORIZATION, "Bearer $it")
        }

        val request = builder.build()
        return chain.proceed(request)
    }
}