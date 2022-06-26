package com.nereus.craftbeer.di

import android.content.Context
import com.android.example.github.util.LiveDataCallAdapterFactory
import com.google.gson.*
import com.nereus.craftbeer.BuildConfig
import com.nereus.craftbeer.R
import com.nereus.craftbeer.constant.SHIFT_JIS_CHARSET
import com.nereus.craftbeer.constant.XML_SHIFT_JIS_MEDIA_TYPE
import com.nereus.craftbeer.constant.XML_SHIFT_JIS_PROLOG
import com.nereus.craftbeer.database.CraftBeerDatabase
import com.nereus.craftbeer.database.dao.ErrorLogDao
import com.nereus.craftbeer.networking.BeerCraftApi
import com.nereus.craftbeer.networking.CoreApiForLogger
import com.nereus.craftbeer.networking.CoreApiWithoutLiveData
import com.nereus.craftbeer.networking.PointPlusApiWithoutLiveData
import com.nereus.craftbeer.realm.RealmApplication
import com.nereus.craftbeer.repository.ErrorLogRepository
import com.nereus.craftbeer.socket.SocketIO
import com.nereus.craftbeer.util.CoreApiEventLogger
import com.nereus.craftbeer.util.PointPlusEventLogger
import com.nereus.craftbeer.util.bodyToString
import com.nereus.craftbeer.util.simplexml.SimpleXmlConverterFactory
import com.nereus.craftbeer.util.toHalfWidthString
import com.seikoinstruments.sdk.thermalprinter.PrinterManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import io.socket.client.Socket
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import okio.ByteString
import org.simpleframework.xml.convert.AnnotationStrategy
import org.simpleframework.xml.core.Persister
import org.simpleframework.xml.strategy.Visitor
import org.simpleframework.xml.strategy.VisitorStrategy
import org.simpleframework.xml.stream.Format
import org.simpleframework.xml.stream.InputNode
import org.simpleframework.xml.stream.NodeMap
import org.simpleframework.xml.stream.OutputNode
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.*
import java.lang.reflect.Type
import java.nio.charset.Charset
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton
import javax.net.ssl.*
import javax.security.cert.CertificateException
import kotlin.jvm.Throws


@Module
@InstallIn(ApplicationComponent::class)
object CoreModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class RetrofitWithLiveDataAdapter

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class RetrofitWithoutLiveDataAdapter

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class RetrofitForErrorLogAdapter

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class RetrofitXml

    @Singleton
    @RetrofitWithoutLiveDataAdapter
    @Provides
    fun provideCoreServiceWithoutLiveData(coreApiEventLogger: CoreApiEventLogger): Retrofit {
        val logging =
            if (BuildConfig.LOG_CORE_API_EVENT) HttpLoggingInterceptor(coreApiEventLogger) else HttpLoggingInterceptor()


        logging.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder()
        httpClient.connectTimeout(60, TimeUnit.SECONDS)
        httpClient.addInterceptor(logging)
        return Retrofit.Builder()
            .baseUrl(BuildConfig.CORE_URL)
            .addConverterFactory(getGsonConverterFactory())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .client(httpClient.build())
            .build()
    }

    @Singleton
    @RetrofitForErrorLogAdapter
    @Provides
    fun provideCoreServiceForErrorLog(): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder()
        httpClient.connectTimeout(60, TimeUnit.SECONDS)
        httpClient.addInterceptor(logging)
        return Retrofit.Builder()
            .baseUrl(BuildConfig.CORE_URL)
            .addConverterFactory(getGsonConverterFactory())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .client(httpClient.build())
            .build()
    }

    @Singleton
    @RetrofitWithLiveDataAdapter
    @Provides
    fun provideCoreServiceLiveData(coreApiEventLogger: CoreApiEventLogger): Retrofit {
        val logging =
            if (BuildConfig.LOG_CORE_API_EVENT) HttpLoggingInterceptor(coreApiEventLogger) else HttpLoggingInterceptor()

        logging.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder()
        httpClient.connectTimeout(60, TimeUnit.SECONDS)
        httpClient.addInterceptor(logging)
        return Retrofit.Builder()
            .baseUrl(BuildConfig.CORE_URL)
            .addConverterFactory(getGsonConverterFactory())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .client(httpClient.build())
            .build()
    }

    @Singleton
    @RetrofitXml
    @Provides
    fun providePointPlusService(pointPlusEventLogger: PointPlusEventLogger): Retrofit {
        val logging =
            if (BuildConfig.LOG_POINT_PLUS_EVENT) HttpLoggingInterceptor(pointPlusEventLogger) else HttpLoggingInterceptor()

        logging.level = HttpLoggingInterceptor.Level.BODY

        // Disable SSL verification
        val httpClient = OkHttpClient.Builder().ignoreAllSSLErrors()
        httpClient.addInterceptor(HalfwidthRequestInterceptor())
//        httpClient.addInterceptor(FakeResponseInterceptor())
        httpClient.connectTimeout(60, TimeUnit.SECONDS)
        httpClient.addInterceptor(logging)
        val trustManagerFactory = TrustManagerFactory
            .getInstance(TrustManagerFactory.getDefaultAlgorithm())

        trustManagerFactory.init(readKeyStore(RealmApplication.instance.applicationContext))

        val trustManager = trustManagerFactory.trustManagers[0] as X509TrustManager
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf<TrustManager>(trustManager), null)
//        httpClient.hostnameVerifier { hostname, session ->
//
//            val hv: HostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier()
//            /* Never return true without verifying the hostname, otherwise you will be vulnerable
//        to man in the middle attacks. */
//            return@hostnameVerifier hv.verify("bitsit.pos.ppsys.jp", session);
//
//        }
//        httpClient.sslSocketFactory(sslContext.socketFactory, trustManager)
        return Retrofit.Builder()
            .baseUrl(BuildConfig.POINT_PLUS_URL)
            .addConverterFactory(getXmlConverterFactory())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .client(httpClient.build())
            .build()
    }

    private fun OkHttpClient.Builder.ignoreAllSSLErrors(): OkHttpClient.Builder {
        val naiveTrustManager = object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) = Unit
            override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) = Unit
        }

        val insecureSocketFactory = SSLContext.getInstance("TLSv1.2").apply {
            val trustAllCerts = arrayOf<TrustManager>(naiveTrustManager)
            init(null, trustAllCerts, SecureRandom())
        }.socketFactory

        sslSocketFactory(insecureSocketFactory, naiveTrustManager)
        hostnameVerifier(HostnameVerifier { _, _ -> true })
        return this
    }
    //To get certificates from a keystore
//    @Throws(java.lang.Exception::class)
//    private fun getCertificatesFromTrustStore(): List<X509Certificate?>? {
//        val truststore = KeyStore.getInstance("JKS")
//        truststore.load(readKeyStore(RealmApplication.instance.applicationContext))
//        val params = PKIXParameters(truststore)
//        val trustAnchors: Set<TrustAnchor> = params.getTrustAnchors()
//        LOG.debug(
//            "{} certificates found in {} which will be used",
//            trustAnchors.size,
//            trustStorePath
//        )
//        return trustAnchors.stream()
//            .map<Any>(TrustAnchor::getTrustedCert)
//            .collect(Collectors.toList())
//    }
    /**
     * Get keys store. Key file should be encrypted with pkcs12 standard. It    can be done with standalone encrypting java applications like "keytool". File password is also required.
     *
     * @param context Activity or some other context.
     * @return Keys store.
     * @throws KeyStoreException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    @Throws(
        KeyStoreException::class,
        CertificateException::class,
        NoSuchAlgorithmException::class,
        IOException::class
    )
    /**
     * @param context The Android context to be used for retrieving the keystore from raw resource
     * @return the KeyStore read or null on error
     */
    private fun readKeyStore(context: Context): KeyStore? {
        val password = "K2uMGsi6".toCharArray()

        // for non-android usage:
        // try(FileInputStream is = new FileInputStream(keystoreName)) {
        try {
            context.resources.openRawResource(R.raw.keystore).use { `is` ->
                val ks =
                    KeyStore.getInstance(KeyStore.getDefaultType())
                ks.load(`is`, password)
                return ks
            }
        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        }
        return null
    }

    @Provides
    fun provideBeerCraftApi(@RetrofitWithLiveDataAdapter retrofit: Retrofit): BeerCraftApi =
        retrofit.create(
            BeerCraftApi::class.java
        )

    @Provides
    fun provideCoreApiForLogger(@RetrofitForErrorLogAdapter retrofit: Retrofit): CoreApiForLogger =
        retrofit.create(
            CoreApiForLogger::class.java
        )

    @Provides
    fun provideCoreApi(@RetrofitWithoutLiveDataAdapter retrofit: Retrofit): CoreApiWithoutLiveData =
        retrofit.create(
            CoreApiWithoutLiveData::class.java
        )

    @Provides
    fun provideErrorLogRepository(
        coreApi: CoreApiForLogger,
        errorLogDao: ErrorLogDao
    ): ErrorLogRepository =
        ErrorLogRepository(coreApi, errorLogDao)

    @Provides
    fun providePointPlusEventLogger(errorLogRepository: ErrorLogRepository): PointPlusEventLogger =
        PointPlusEventLogger(errorLogRepository)

    @Provides
    fun provideCoreApiEventLogger(errorLogRepository: ErrorLogRepository): CoreApiEventLogger =
        CoreApiEventLogger(errorLogRepository)

    @Provides
    fun providePointPlusApi(@RetrofitXml retrofit: Retrofit): PointPlusApiWithoutLiveData =
        retrofit.create(
            PointPlusApiWithoutLiveData::class.java
        )

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context) = CraftBeerDatabase.getInstance(
        appContext
    )

    @Singleton
    @Provides
    fun providePrinterManager(@ApplicationContext appContext: Context): PrinterManager {
        val printerManager = PrinterManager(
            appContext
        )
        printerManager.codePage = PrinterManager.CODE_PAGE_KATAKANA
        return printerManager
    }

    @Singleton
    @Provides
    fun provideObinizWebsocket(): Socket? {
        return SocketIO.getSocket()
    }

    @Singleton
    @Provides
    fun provideSaleLogDao(db: CraftBeerDatabase) = db.saleLogDao

    @Singleton
    @Provides
    fun provideErrorLogDao(db: CraftBeerDatabase) = db.errorLogDao

    @Singleton
    @Provides
    fun provideTopUpgDao(db: CraftBeerDatabase) = db.topUpDao

    @Singleton
    @Provides
    fun provideGoodsDao(db: CraftBeerDatabase) = db.goodsDao

    @Singleton
    @Provides
    fun provideSaleLogListDao(db: CraftBeerDatabase) = db.saleLogListDao

    @Singleton
    @Provides
    fun provideSaleLogDetailDao(db: CraftBeerDatabase) = db.saleLogDetailDao
}

fun getGsonConverterFactory(): GsonConverterFactory {
    val gson = GsonBuilder().registerTypeAdapter(
        LocalDateTime::class.java,
        LocalDateTimeDeserializer()
    ).create()
    return GsonConverterFactory.create(gson)
}

fun getXmlConverterFactory(): SimpleXmlConverterFactory {
    val format = Format(XML_SHIFT_JIS_PROLOG)
    return SimpleXmlConverterFactory.createNonStrict(
        Persister(
            AnnotationStrategy(VisitorStrategy(SanitizerVisitor())),
            format // important part!
        ),
        MediaType.get(XML_SHIFT_JIS_MEDIA_TYPE),
        SHIFT_JIS_CHARSET
    )
}

internal class HalfwidthRequestInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        request.body()
        val body = request.body()
        println("--------------body")

        if (body != null) {
            val builder: Request.Builder = request.newBuilder()
            val toHalfWidthString = body.bodyToString().toHalfWidthString()
            val newRequest = builder.headers(request.headers())
                .method(
                    request.method(), RequestBody.create(
                        body.contentType(),
                        toHalfWidthString
                    )
                )
                .build()
            return chain.proceed(newRequest)
        }

        return chain.proceed(request)
    }
}

internal class FakeResponseInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var response: Response? = null
        try {
            response = chain.proceed(chain.request())
        } catch (ex: Exception) {
            val builder: Response.Builder = Response.Builder()
            val requestBody = chain.request().body()
            println("--------------Fake Response body")

            if (requestBody != null) {
            }

            return builder.headers(chain.request().headers())
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("")
                .body(
                    buildFakeResponse(requestBody!!.bodyToString())
                )
                .build()

        }

        return response
    }
}

private fun buildFakeResponse(bodyToString: String): ResponseBody {
    try {
        RealmApplication.instance.resources.openRawResource(R.raw.results).use {
            return ResponseBody.create(
                getXmlConverterFactory().mediaType,
                readTextFile(it, SHIFT_JIS_CHARSET)
            )
        }
    } catch (ex: Exception) {
        Timber.e(ex)
    }
    return ResponseBody.create(getXmlConverterFactory().mediaType, ByteString.EMPTY)
}

// TODO for tesing encode/decode ShiftJIS. Remove when done
fun main(args: Array<String>) {
    /* val transactions = BasedCardRequestTransactions(RequestType.QUERY_BALANCE)
     val transactions1 = BasedCardRequestTransactions(RequestType.QUERY_BALANCE)
 //    transactions.fillBalanceInquiryRequest("[memberCode]124523256586")

     val format = Format(XML_SHIFT_JIS_PROLOG)
     val persister =
         Persister(
             AnnotationStrategy(VisitorStrategy(SanitizerVisitor())), format // important part!
         )
     val charset = StandardCharsets.UTF_8
     val charset2 = Charset.forName("SHIFT-JIS")
     val result = File("results.xml");
     val request = File("request.xml");
     val response = BasedCardResponseTransactions()
     val response1 = BasedCardResponseTransactions()
     response.fillBalanceInquiryResponse()

     val resultOs = FileOutputStream(result, true)


     persister.write(response, resultOs, "SHIFT-JIS")
 //    persister.read(response1, result, false)
     //TODO check encoding
     try {

 //        println("transactions1.transaction.requestId")
 //        println(transactions1.transaction.requestId)
 //        println(transactions1.transaction.clientSignature)
 //        println(transactions1.transaction.retryCount)
 //
 //        readFile(result, charset)
         readFile(result, charset2)
     } catch (e: java.lang.Exception) {
         e.printStackTrace()
     }*/
}

fun readFile(file: File, charset: Charset) {
    FileInputStream(file).use { fis ->
        InputStreamReader(fis, charset).use { isr ->
            BufferedReader(isr).use { reader ->
                var str: String?
                while (reader.readLine().also { str = it } != null) {
                    println(str)
                }
            }
        }
    }
}

fun readTextFile(inputStream: InputStream, charset: String): String? {
    val outputStream = ByteArrayOutputStream()
    val buf = ByteArray(1024)
    var len: Int
    try {
        while (inputStream.read(buf).also { len = it } != -1) {
            outputStream.write(buf, 0, len)
        }
        outputStream.close()
        inputStream.close()
    } catch (e: IOException) {
    }
    return outputStream.toString(charset)
}

class SanitizerVisitor : Visitor {
    override fun read(type: org.simpleframework.xml.strategy.Type, node: NodeMap<InputNode>) {
//        TODO("Not yet implemented")
    }

    override fun write(type: org.simpleframework.xml.strategy.Type, node: NodeMap<OutputNode>) {
        val element = node.node
        val type: Class<*> = type.type
        val comment = type.name
        if (!element.isRoot) {
            node.remove("class")
        }
    }
}

class LocalDateTimeDeserializer : JsonDeserializer<LocalDateTime> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): LocalDateTime {
        return LocalDateTime.parse(
            json.asString,
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .withLocale(Locale.ENGLISH)
        )
    }
}
