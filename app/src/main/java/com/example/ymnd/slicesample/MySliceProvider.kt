package com.example.ymnd.slicesample

import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.core.graphics.drawable.IconCompat
import androidx.slice.Slice
import androidx.slice.SliceProvider
import androidx.slice.builders.ListBuilder
import androidx.slice.builders.SliceAction
import androidx.slice.builders.list
import androidx.slice.builders.row

class MySliceProvider : SliceProvider() {
    /**
     * Instantiate any required objects. Return true if the provider was successfully created,
     * false otherwise.
     */
    override fun onCreateSliceProvider(): Boolean {
        return true
    }

    /**
     * Converts URL to content URI (i.e. content://com.example.ymnd.slicesample...)
     */
    override fun onMapIntentToUri(intent: Intent?): Uri {
        // Note: implementing this is only required if you plan on catching URL requests.
        // This is an example solution.
        var uriBuilder: Uri.Builder = Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT)
        if (intent == null) return uriBuilder.build()
        val data = intent.data
        if (data != null && data.path != null) {
            val path = data.path.replace("/", "")
            uriBuilder = uriBuilder.path(path)
        }
        val context = context
        if (context != null) {
            uriBuilder = uriBuilder.authority(context.getPackageName())
        }
        return uriBuilder.build()
    }

    fun createActivityAction(): SliceAction {
        return SliceAction.create(
                PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), 0),
                IconCompat.createWithResource(context, R.drawable.ic_android_black),
                ListBuilder.ICON_IMAGE,
                "Enter app"
        )
    }

    /**
     * Construct the Slice and bind data if available.
     */
    override fun onBindSlice(sliceUri: Uri): Slice? {
        val context = context ?: return null
        val activityAction = createActivityAction()
        return if (sliceUri.path == "/found") {
            // Path recognized. Customize the Slice using the androidx.slice.builders API.
            // Note: ANR and StrictMode are enforced here so don't do any heavy operations.
            // Only bind data that is currently available in memory.
            list(context, sliceUri, ListBuilder.INFINITY) {
                row {
                    title = "Hello Slices:)"
                    primaryAction = activityAction
                }
            }
        } else {
            ListBuilder(context, sliceUri, ListBuilder.INFINITY)
                    .addRow {
                        it.setTitle("URI not found.")
                        it.setPrimaryAction(activityAction)
                    }
                    .build()
        }
    }

    /**
     * Slice has been pinned to external process. Subscribe to data source if necessary.
     */
    override fun onSlicePinned(sliceUri: Uri?) {
        // When data is received, call context.contentResolver.notifyChange(sliceUri, null) to
        // trigger MySliceProvider#onBindSlice(Uri) again.
    }

    /**
     * Unsubscribe from data source if necessary.
     */
    override fun onSliceUnpinned(sliceUri: Uri?) {
        // Remove any observers if necessary to avoid memory leaks.
    }
}
