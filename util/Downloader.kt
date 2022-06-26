@file:Suppress("DEPRECATION")

package com.nereus.craftbeer.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import com.nereus.craftbeer.worker.UpdateBeerWorker.Companion.countDownloadFile
import timber.log.Timber
import java.io.*
import java.net.HttpURLConnection
import java.net.URL


/**
 * Context
 */
@SuppressLint("StaticFieldLeak")
private lateinit var context: Context

class Downloader(contexts: Context) : AsyncTask<String, Int, String>() {

    private var fileN: String? = null

    init {

        context = contexts
    }

    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg sUrl: String?): String? {


        var input: InputStream? = null
        var output: OutputStream? = null
        var connection: HttpURLConnection? = null
        try {
            val url = URL(sUrl[0])
            connection = url.openConnection() as HttpURLConnection
            connection.connect()
            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                    .toString() + " " + connection.getResponseMessage()
            }
            val fileLength: Int = connection.getContentLength()
            input = connection.inputStream
            //常にパス階層の先頭ファイル名を指すように修正
            val cnt = url.toString().split("/").count() - 1
            fileN = url.toString().split("/")[cnt]
            val filename = File(
                context.getExternalFilesDir(null)?.absolutePath.toString() + "/DownloadedVideo/",
                fileN
            )
            Timber.d("Beer shop video url: %s", filename)
            output = FileOutputStream(filename)
            val data = ByteArray(4096)
            var total: Long = 0
            var count: Int = 0
            while (input.read(data).also({ count = it }) != -1) {
                if (isCancelled) {
                    input.close()
                    return null
                }
                total += count.toLong()
                if (fileLength > 0)
                    output.write(data, 0, count)
            }
        } catch (e: Exception) {
            return e.toString()
        } finally {
            try {
                if (output != null) output.close()
                if (input != null) input.close()
            } catch (ignored: IOException) {
            }
            connection?.disconnect()
        }
        return null
    }


    /**
     * On post execute
     *
     * @param result
     */
    override fun onPostExecute(result: String?) {
        if (result != null) {
            Timber.d("Download error: $result")
        } else {
            countDownloadFile += 1
        }
    }
}




