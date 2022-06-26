package com.nereus.craftbeer.util

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.nereus.craftbeer.R
import com.nereus.craftbeer.constant.*
import com.nereus.craftbeer.fragment.NotificationFragmentDialog
import com.nereus.craftbeer.realm.RealmApplication
import java.util.concurrent.Callable


fun displayToast(context: Context?, message: String, length: Int = Toast.LENGTH_LONG) {
    val toast = Toast.makeText(context, message, length)

    toast.setGravity(Gravity.BOTTOM or Gravity.RIGHT, 0, 0)

    (toast.view as LinearLayout).setBackgroundColor(context?.getColor(R.color.colorTransparent)!!)

    toast.show()
}

fun AppCompatActivity.displayError(messages: List<String>, callback: Callable<Unit>? = null) {
    displayError(messages.joinToString(BREAK_LINE), callback = callback)
}

fun AppCompatActivity.displayErrorWithRetryAction(messages: List<String>) {
    displayError(messages.joinToString(BREAK_LINE))
}

fun AppCompatActivity.setFragment(fragment: Fragment) {
    val fragmentTransaction = supportFragmentManager.beginTransaction()
    fragmentTransaction.add(android.R.id.content, fragment)
    fragmentTransaction.commit()
}

fun AppCompatActivity.getNotificationdialog(): DialogFragment {
    val dialog = supportFragmentManager.findFragmentByTag("notification_dialog")
    return (dialog ?: NotificationFragmentDialog()) as DialogFragment
}

fun AppCompatActivity.handleLoading(loadingState: Int) {
    if (loadingState == CommonConst.LOADING_VISIBLE) {
        findNavController(R.id.nav_host_fragment).navigate(
            R.id.notificationFragmentDialog
        )
    } else {
//        supportFragmentManager.findFragmentById(R.id.loading_dialog)?.let {
//            (it as DialogFragment).dismiss()
//        }
    }
}

fun AppCompatActivity.displayError(
    message: String,
    duration: Int = Snackbar.LENGTH_LONG,
    @ColorRes color: Int = R.color.white,
    @ColorRes bgColor: Int = R.color.design_default_color_error,
    callback: Callable<Unit>? = null
) {
    try {
        findNavController(R.id.nav_host_fragment).navigate(
            R.id.notificationFragmentDialog,
            bundleOf(MESSAGE_BUNDLE_KEY to message)
        )
    } catch (ex: IllegalArgumentException) {
        val view = findViewById<View>(android.R.id.content).rootView
        val snackbar = Snackbar.make(view, message, duration)
            .setTextColor(
                ContextCompat.getColor(
                    RealmApplication.instance.applicationContext,
                    R.color.white
                )
            )
            .setBackgroundTint(
                ContextCompat.getColor(
                    RealmApplication.instance.applicationContext,
                    bgColor
                )
            )

        snackbar.view.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
        snackbar.view.requestFocus()
        snackbar.view.setPadding(0, 0, 0, 0)
        snackbar.show()
    }
}

fun View.setOnClickDebounce(listener: () -> Unit) {
    setOnClickListener {
        if (debounce()) {
//            isSoundEffectsEnabled = true
//            playSoundEffect(0)
            listener()
        }
    }
}

fun View.setOnCheckDebounce(listener: () -> Unit) {
    setOnCheckDebounce {
        if (debounce()) {
//            isSoundEffectsEnabled = true
//            playSoundEffect(0)
            listener()
        }
    }
}

fun View.hideKeyboard(context: Context) {
    val imm: InputMethodManager =
        context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun List<Any>.getEmptyViewVisibility(): Int {
    return if (isEmpty()) View.VISIBLE else View.GONE
}

fun List<Any>.getRecyclerViewVisibility(): Int {
    return !getEmptyViewVisibility()
}

fun Short.asViewVisibility(): Int {
    return if (this > 1) View.VISIBLE else View.GONE
}

private fun View.debounce(): Boolean {
    val DELAY_IN_MS: Long = 900
    if (!isClickable) {
        return false
    }
    isClickable = false
    postDelayed({ isClickable = true }, DELAY_IN_MS)
    return true
}


/**
 * Create a Notification that is shown as a heads-up notification if possible.
 *
 * For this codelab, this is used to show a notification so that you know when different steps
 * of the background work chain are starting
 *
 * @param message Message shown on the notification
 * @param context Context needed to create Toast
 */
fun makeStatusNotification(message: String, context: Context) {

    // Make a channel if necessary
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val name = VERBOSE_NOTIFICATION_CHANNEL_NAME
        val description = VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        channel.description = description

        // Add the channel
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        notificationManager?.createNotificationChannel(channel)
    }

    // Create the notification
    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(NOTIFICATION_TITLE)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setVibrate(LongArray(0))

    // Show the notification
    NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
}

fun <T, K, R> LiveData<T>.combineWith(
    liveData: LiveData<K>,
    block: (T?, K?) -> R
): LiveData<R> {
    val result = MediatorLiveData<R>()
    result.addSource(this) {
        result.value = block(this.value, liveData.value)
    }
    result.addSource(liveData) {
        result.value = block(this.value, liveData.value)
    }
    return result
}

