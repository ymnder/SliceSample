package com.example.ymnd.slicesample

import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.IconCompat
import androidx.slice.Slice
import androidx.slice.SliceProvider
import androidx.slice.builders.*
import androidx.slice.builders.ListBuilder.SMALL_IMAGE
import androidx.slice.core.SliceHints

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

    private fun createActivityAction(): SliceAction {
        return SliceAction.create(
                PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), 0),
                IconCompat.createWithResource(context, R.drawable.ic_android),
                ListBuilder.SMALL_IMAGE,
                "Enter app"
        )
    }

    private fun createAction(intent: PendingIntent, @DrawableRes resource: Int): SliceAction {
        return SliceAction.create(
                intent,
                IconCompat.createWithResource(context, resource),
                ListBuilder.SMALL_IMAGE,
                "Increment"
        )
    }

    private fun createIntentToast(): PendingIntent {
        return PendingIntent.getBroadcast(
                context, 0,
                Intent(context, MyBroadcastReceiver::class.java)
                        .putExtra(MyBroadcastReceiver.EXTRA_INCREMENT, false), 0
        )
    }

    private fun createIntentToastForDebug(requestCode: Int, message: String): PendingIntent {
        return PendingIntent.getBroadcast(
                context, requestCode,
                Intent(context, MyBroadcastReceiver::class.java)
                        .putExtra(MyBroadcastReceiver.EXTRA_INCREMENT, false)
                        .putExtra(MyBroadcastReceiver.EXTRA_DEBUG_TOAST, message), 0
        )
    }

    private fun createIntentIncrement(): PendingIntent {
        return PendingIntent.getBroadcast(
                context, 0,
                Intent(context, MyBroadcastReceiver::class.java)
                        .putExtra(MyBroadcastReceiver.EXTRA_INCREMENT, true), 0
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
                    addEndItem(IconCompat.createWithResource(
                            context,
                            R.drawable.ic_share
                    ), SliceHints.SMALL_IMAGE)
                }
            }
        } else if (sliceUri.path == "/increment") {
            val action = createAction(createIntentIncrement(), R.drawable.ic_plus)
            list(context, sliceUri, ListBuilder.INFINITY) {
                row {
                    title = "Count: ${MyBroadcastReceiver.currentCount}"
                    primaryAction = action
//                    addEndItem(
//                            IconCompat.createWithResource(
//                                    context, R.drawable.ic_plus
//                            ), SliceHints.ICON_IMAGE
//                    )
                }
            }
        } else if (sliceUri.path == "/template") {
            val item = Integer.parseInt(sliceUri.fragment)
            val anotherAction = createAction(createIntentToast(), R.drawable.ic_plus)
            when (item) {
                0 -> list(context, sliceUri, ListBuilder.INFINITY) {
                    row {
                        title = "タイトル"
                        subtitle = "サブタイトル"
                        setTitleItem(anotherAction)
                        addEndItem(activityAction)
                    }
                }
                1 -> list(context, sliceUri, ListBuilder.INFINITY) {
                    for (i in 1 until 10) {
                        row {
                            title = "タイトル"
                            subtitle = "サブタイトル"
                            setTitleItem(anotherAction)
                            addEndItem(activityAction)
                        }
                    }
                }
                2 -> list(context, sliceUri, ListBuilder.INFINITY) {
                    header {
                        title = "電車が来ます"
                        subtitle = "２分後に１番ホーム"
                        summary = "２分後に１番ホームに電車が来ます"
                        primaryAction = anotherAction
                    }
                    row {
                        title = "東京駅着"
                        subtitle = "４分、￥140"
                        primaryAction = activityAction
                        addEndItem(
                                IconCompat.createWithResource(context, R.drawable.ic_android),
                                ListBuilder.ICON_IMAGE
                        )
                    }
                }
                3 -> {
                    list(context, sliceUri, ListBuilder.INFINITY) {

                    }
                }
                4 -> list(context, sliceUri, ListBuilder.INFINITY) {
                    header {
                        title = "グリッドアイテム"
                    }
                    gridRow {
                        cell {
                            addTitleText("アイテム１")
                            addText("テキスト１")
                            addImage(IconCompat.createWithResource(context, R.drawable.ic_android), SMALL_IMAGE)
                            contentIntent = createIntentToastForDebug(2, "cell")
                        }
                        cell {
                            addTitleText("アイテム２")
                            addImage(IconCompat.createWithResource(context, R.drawable.ic_android), SMALL_IMAGE)
                            addText("テキスト２")
                            contentIntent = createIntentToastForDebug(3, "cell2")
                        }
                        cell {
                            addImage(IconCompat.createWithResource(context, R.drawable.ic_android), SMALL_IMAGE)
                            addTitleText("アイテム３")
                            addText("テキスト３")
                            contentIntent = createIntentToastForDebug(4, "cell3")
                        }
                        cell {
                            addImage(IconCompat.createWithResource(context, R.drawable.ic_android), SMALL_IMAGE)
                            addText("テキスト４")
                            addTitleText("アイテム４")
                            contentIntent = createIntentToastForDebug(5, "cell4")
                        }
                        cell {
                            addTitleText("アイテム５")
                            addImage(IconCompat.createWithResource(context, R.drawable.ic_android), SMALL_IMAGE)
                            addText("テキスト５")
                            contentIntent = createIntentToastForDebug(6, "cell5")
                        }
                        cell {
                            addTitleText("アイテム６")
                            addImage(IconCompat.createWithResource(context, R.drawable.ic_android), SMALL_IMAGE)
                            addText("テキスト６")
                            contentIntent = createIntentToastForDebug(7, "cell6")
                        }
                        seeMoreCell {
                            addTitleText("more")
                            addImage(IconCompat.createWithResource(context, R.drawable.ic_android), SMALL_IMAGE)
                            addText("more text")
                            contentIntent = createIntentToastForDebug(8, "cell8")
                        }

                        primaryAction = createAction(createIntentToastForDebug(300, "gridRow"), R.drawable.ic_android)
                    }
                }
                5 -> list(context, sliceUri, ListBuilder.INFINITY) {
                    range {
                        title = "作業進捗"
                        subtitle = "あと一息です！！"
                        primaryAction = anotherAction
                        value = 60
                    }
                }
                6 -> list(context, sliceUri, ListBuilder.INFINITY) {
                    inputRange {
                        title = "スライダー"
                        thumb = IconCompat.createWithResource(context, R.drawable.ic_android)
                        primaryAction = anotherAction
                        inputAction = createIntentToastForDebug(20, "input")
                    }
                }
                7 -> list(context, sliceUri, ListBuilder.INFINITY) {
                    row {
                        title = "タイトル"
                        subtitle = "サブタイトル"
                        primaryAction = anotherAction
                        addEndItem(
                                SliceAction.createDeeplink(PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), 0),
                                        IconCompat.createWithResource(context, R.drawable.ic_android),
                                        ListBuilder.SMALL_IMAGE,
                                        "Enter app"
                                )
//                                SliceAction.createToggle(
//                                        createIntentToastForDebug(40, "toggle"),
//                                        "Toggle",
//                                        true
//                                )
                        )
                    }
                }
                else -> list(context, sliceUri, ListBuilder.INFINITY) {
                    header { }
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
