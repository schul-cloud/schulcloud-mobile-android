package org.schulcloud.mobile.network

import android.os.Build
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import info.guardianproject.netcipher.client.TlsOnlySocketFactory
import io.realm.RealmList
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.R
import org.schulcloud.mobile.SchulCloudApp
import org.schulcloud.mobile.config.Config
import org.schulcloud.mobile.models.base.RealmString
import org.schulcloud.mobile.models.user.UserRepository
import org.schulcloud.mobile.utils.HOST_API
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStream
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager


object ApiService {
    private var service: ApiServiceInterface? = null

    @Synchronized
    fun getInstance(): ApiServiceInterface {
        val client = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            getTlsClient(null as KeyStore?)
        else
            getOkHttpClientBuilder().build()

        service = getRetrofit(client).create(ApiServiceInterface::class.java)
        return service as ApiServiceInterface
    }

    @Synchronized
    fun getFileDownloadInstance(): ApiServiceInterface {
        val client = getTlsClient(getFileDownloadKeyStore())
        service = getRetrofit(client).create(ApiServiceInterface::class.java)
        return service as ApiServiceInterface
    }

    // To use T-TeleSec GlobalRoot Class 2 certificate on pre-21
    // https://developer.android.com/training/articles/security-ssl
    private fun getFileDownloadKeyStore(): KeyStore {
        val cf: CertificateFactory = CertificateFactory.getInstance("X.509")
        val caInput: InputStream = SchulCloudApp.instance.resources
                .openRawResource(R.raw.globalroot_class_2)
        val ca: X509Certificate = caInput.use {
            cf.generateCertificate(it) as X509Certificate
        }
        val keyStoreType = KeyStore.getDefaultType()

        return KeyStore.getInstance(keyStoreType).apply {
            load(null, null)
            setCertificateEntry("globalroot_class_2", ca)
        }
    }

    private fun getOkHttpClientBuilder(): OkHttpClient.Builder {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC

        return OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request()
                    val builder = request.newBuilder()
                    if (UserRepository.isAuthorized && request.url().host().equals(HOST_API, true))
                        builder.header(Config.HEADER_AUTH, Config.HEADER_AUTH_VALUE_PREFIX + UserRepository.token)
                    chain.proceed(builder.build())
                }
                .addInterceptor(loggingInterceptor)
    }

    private fun getGson(): Gson {
        return GsonBuilder()
                .registerTypeAdapter((object : TypeToken<RealmList<RealmString>>() {}).type,
                        object : TypeAdapter<RealmList<RealmString>>() {
                            override fun read(reader: JsonReader?): RealmList<RealmString> {
                                if (reader == null)
                                    return RealmList()

                                val list = RealmList<RealmString>()
                                reader.beginArray()
                                while (reader.hasNext())
                                    list += RealmString(reader.nextString())
                                reader.endArray()
                                return list
                            }

                            override fun write(out: JsonWriter?, value: RealmList<RealmString>?) {
                                if (out == null || value == null)
                                    return

                                out.beginArray()
                                for (string in value)
                                    out.value(string.value)
                                out.endArray()
                            }
                        })
                .create()
    }

    private fun getRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(BuildConfig.API_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .build()
    }

    // To enable and use TLS 1.1 and 1.2 on pre-21
    // https://square.github.io/okhttp/3.x/okhttp/okhttp3/OkHttpClient.Builder
    private fun getTlsClient(keyStore: KeyStore?): OkHttpClient {
        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply {
            init(keyStore)
        }
        val trustManager = getTrustManager(trustManagerFactory)
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf<TrustManager>(trustManager), null)

        return getOkHttpClientBuilder()
                .sslSocketFactory(TlsOnlySocketFactory(sslContext.socketFactory), trustManager)
                .build()
    }

    @Throws(IllegalStateException::class)
    private fun getTrustManager(trustManagerFactory: TrustManagerFactory): X509TrustManager {
        val trustManagers = trustManagerFactory.getTrustManagers()
        if (trustManagers.size != 1 || trustManagers[0] !is X509TrustManager) {
            throw IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers))
        }
        return trustManagers[0] as X509TrustManager
    }
}
