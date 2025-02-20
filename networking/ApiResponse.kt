package com.nereus.craftbeer.networking

import com.nereus.craftbeer.util.parseJson
import retrofit2.Response
import timber.log.Timber
import java.net.HttpURLConnection
import java.util.regex.Pattern

/**
 * Common class used by API responses.
 * @param <T> the type of the response object
 *
</T> */
@Suppress("unused")
sealed class ApiResponse<T> {
    companion object {
        fun <T> create(error: Throwable): ApiErrorResponse<T> {
            return emptyError()
        }

        private fun <T> emptyError(): ApiErrorResponse<T> =
            ApiErrorResponse(
                ErrorBody(
                    HttpURLConnection.HTTP_INTERNAL_ERROR,
                    listOf("Unknown Error"),
                    "Unknown Error"
                )
            )

        fun <T> create(response: Response<T>): ApiResponse<T> {
            return if (response.isSuccessful) {

                val body = response.body()

                if (body == null || response.code() == HttpURLConnection.HTTP_NO_CONTENT) {
                    ApiEmptyResponse()
                } else {
                    ApiSuccessResponse(
                        body = body,
                        linkHeader = response.headers().get("link")
                    )
                }
            } else {
                val msg = response.errorBody()?.string()
                val errorMsg = if (msg.isNullOrEmpty()) {
                    response.message()
                } else {
                    msg
                }

                val errorBody = try {
                    parseJson(errorMsg, SingleMessageErrorBody::class.java).let {
                        ErrorBody(
                            it!!.statusCode,
                            listOf(it.message),
                            it.error
                        )
                    }

                } catch (ex: Exception) {
                    Timber.e(ex)
                    parseJson(errorMsg, ErrorBody::class.java)
                }

                return if (errorBody != null) {
                    ApiErrorResponse(errorBody)
                } else {
                    ApiEmptyResponse()
                }
            }
        }
    }
}

/**
 * separate class for HTTP 204 responses so that we can make ApiSuccessResponse's body non-null.
 *
 */
class ApiEmptyResponse<T> : ApiResponse<T>()

data class ApiSuccessResponse<T>(
    val body: T,
    val links: Map<String, String>
) : ApiResponse<T>() {
    constructor(body: T, linkHeader: String?) : this(
        body = body,
        links = linkHeader?.extractLinks() ?: emptyMap()
    )

    val nextPage: Int? by lazy(LazyThreadSafetyMode.NONE) {
        links[NEXT_LINK]?.let { next ->
            val matcher = PAGE_PATTERN.matcher(next)
            if (!matcher.find() || matcher.groupCount() != 1) {
                null
            } else {
                try {
                    Integer.parseInt(matcher.group(1)!!)
                } catch (ex: NumberFormatException) {
                    Timber.w("cannot parse next page from %s", next)
                    null
                }
            }
        }
    }

    companion object {
        private val LINK_PATTERN = Pattern.compile("<([^>]*)>[\\s]*;[\\s]*rel=\"([a-zA-Z0-9]+)\"")
        private val PAGE_PATTERN = Pattern.compile("\\bpage=(\\d+)")
        private const val NEXT_LINK = "next"

        /**
         * Extract links
         *
         * @return
         */
        private fun String.extractLinks(): Map<String, String> {
            val links = mutableMapOf<String, String>()
            val matcher = LINK_PATTERN.matcher(this)

            while (matcher.find()) {
                val count = matcher.groupCount()
                if (count == 2) {
                    links[matcher.group(2)!!] = matcher.group(1)!!
                }
            }
            return links
        }

    }
}

/**
 * Api error response
 *
 * @param T
 * @property body
 * コンストラクタ  Api error response
 */
data class ApiErrorResponse<T>(val body: ErrorBody) : ApiResponse<T>()

/**
 * Error body
 *
 * @property statusCode
 * @property message
 * @property error
 * コンストラクタ  Error body
 */
data class ErrorBody(val statusCode: Int, val message: List<String>, val error: String?)

/**
 * Single message error body
 *
 * @property statusCode
 * @property message
 * @property error
 * コンストラクタ  Single message error body
 */
data class SingleMessageErrorBody(val statusCode: Int, val message: String, val error: String?)