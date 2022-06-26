package com.nereus.craftbeer.util

import android.graphics.Bitmap
import android.os.Environment
import android.widget.ImageView
import com.nereus.craftbeer.R
import com.nereus.craftbeer.exception.MessageException
import com.nereus.craftbeer.model.MessagesModel
import com.nereus.craftbeer.realm.RealmApplication
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Load image
 *
 * @param url
 */
fun ImageView.loadImage(url: String?) {
    if (url != null && url.isNotEmpty()) {
        try {
            Picasso.get().load(url).into(this)
        } catch (ex: Exception) {
            // TODO load place holder
            Timber.e(ex)
        }
    }
}

/**
 * Write tofile
 *
 * @param filename
 */
fun Bitmap.writeTofile(filename: String) {
    try {
        FileOutputStream(filename).use { out ->
            this.compress(Bitmap.CompressFormat.JPEG, 70, out)
        }
    } catch (e: IOException) {
        Timber.e(e)
    }
}

/**
 * Write file on internal storage
 *
 * @param sFileName
 * @return
 */
fun Bitmap.writeFileOnInternalStorage(sFileName: String): File {
    val dir =
        RealmApplication.instance.applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
    if (dir == null) {
        throw MessageException(MessagesModel(R.string.msg_print_failed))
    }
    if (!dir.exists()) {
        dir.mkdir()
    }
    try {
        val gpxfile = File(dir, sFileName)
        FileOutputStream(gpxfile).use {
            this.compress(Bitmap.CompressFormat.JPEG, 70, it)
        }
        return gpxfile
    } catch (e: Exception) {
        throw MessageException(MessagesModel(R.string.msg_internal_exception))
    }

}

/**
 * Write file on internal storage
 *
 * @param sFileName
 * @return
 */
fun File.writeFileOnInternalStorage(sFileName: String): File {
    val dir =
        RealmApplication.instance.applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
    if (dir == null) {
        throw MessageException(MessagesModel(R.string.msg_print_failed))
    }
    if (!dir.exists()) {
        dir.mkdir()
    }
    try {
        val gpxfile = File(dir, sFileName)
        FileOutputStream(gpxfile).use {
            it.write(this.readBytes())
        }
        return gpxfile
    } catch (e: Exception) {
        throw MessageException(MessagesModel(R.string.msg_internal_exception))
    }

}

/**
 * Get bitmap
 *
 * @param path
 * @param width
 * @return
 */
suspend fun getBitmap(path: String, width: Int): Bitmap {
    return withContext(Dispatchers.IO) {
        val bitmap = Picasso.get()
            .load(path).get()
        Picasso.get()
            .load(path)
            .resize(width, width * bitmap.height / bitmap.width)
            .onlyScaleDown()
            .get()
    }
}
//
//suspend fun Bitmap.compressForReceipt(): Bitmap {
//
//    val file = this.writeFileOnInternalStorage("${genRandomString(6)}.jpeg")
//
//    val compressedImageFile = Compressor.compress(RealmApplication.instance, file) {
//        size(200) // 2 MB
//    }
//
//    return Picasso.get()
//        .load(compressedImageFile)
//        .get()
//}



