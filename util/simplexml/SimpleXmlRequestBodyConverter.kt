package com.nereus.craftbeer.util.simplexml

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.Buffer
import org.simpleframework.xml.Serializer
import retrofit2.Converter
import java.io.IOException
import java.io.OutputStreamWriter

/**
 * Simple xml request body converter
 *
 * @param T
 * @property serializer
 * @property mediaType
 * @property charset
 * @constructor Create empty Simple xml request body converter
 */
internal class SimpleXmlRequestBodyConverter<T>(
    private val serializer: Serializer,
    private val mediaType: MediaType,
    private val charset: String
) :
    Converter<T, RequestBody> {
    @Throws(IOException::class)
    override fun convert(value: T): RequestBody {
        val buffer = Buffer()
        try {
            val osw = OutputStreamWriter(buffer.outputStream(), charset)
            serializer.write(value, osw)
            osw.flush()
        } catch (e: RuntimeException) {
            throw e
        } catch (e: IOException) {
            throw e
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
        return RequestBody.create(mediaType, buffer.readByteString())
    }
}