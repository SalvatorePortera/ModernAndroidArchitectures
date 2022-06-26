package com.nereus.craftbeer.util

/**
 * Resource
 *
 * @param T
 * @property status
 * @property data
 * @property message
 * @constructor Create empty Resource
 */
data class Resource<out T>(val status: Status, val data: T?, val message: String?) {

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    companion object {

        /**
         * Success
         *
         * @param T
         * @param data
         * @return
         */
        fun <T> success(data: T): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        /**
         * Error
         *
         * @param T
         * @param message
         * @param data
         * @return
         */
        fun <T> error(message: String, data: T? = null): Resource<T> {
            return Resource(Status.ERROR, data, message)
        }

        /**
         * Loading
         *
         * @param T
         * @param data
         * @return
         */
        fun <T> loading(data: T? = null): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }
    }
}