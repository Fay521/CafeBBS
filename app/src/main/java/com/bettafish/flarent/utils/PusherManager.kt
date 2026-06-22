package com.bettafish.flarent.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bettafish.flarent.App
import com.bettafish.flarent.MainActivity
import com.bettafish.flarent.R
import com.bettafish.flarent.config.ForumConfig
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange

object PusherManager {
    private const val TAG = "PusherManager"
    private const val CHANNEL_ID = "flarent_notifications"
    private var pusher: Pusher? = null
    private var notificationIdCounter = 0

    fun init(appKey: String, cluster: String) {
        if (pusher != null) return

        val options = PusherOptions().setCluster(cluster)
        pusher = Pusher(appKey, options)

        connect()

        subscribeToPublicChannel()
        createNotificationChannel()
    }

    fun connect() {
        pusher?.connect(object : ConnectionEventListener {
            override fun onConnectionStateChange(change: ConnectionStateChange) {
                Log.d(TAG, "Connection state changed from ${change.previousState} to ${change.currentState}")
            }

            override fun onError(message: String?, code: String?, e: Exception?) {
                Log.e(TAG, "Connection error: $message", e)
            }
        }, ConnectionState.ALL)
    }

    fun disconnect() {
        pusher?.disconnect()
    }

    private fun subscribeToPublicChannel() {
        val channel = pusher?.subscribe("flarum.forum")

        // Unified listener for all events — parse actual content from event data
        channel?.bind("newPost") { event ->
            Log.d(TAG, "Received newPost: ${event.data}")
            handleForumEvent("newPost", event.data)
        }

        channel?.bind("discussionStarted") { event ->
            Log.d(TAG, "Received discussionStarted: ${event.data}")
            handleForumEvent("discussionStarted", event.data)
        }

        channel?.bind("Flarum\\Post\\Event\\Posted") { event ->
            Log.d(TAG, "Received Post\\Event\\Posted: ${event.data}")
            handleForumEvent("Flarum\\Post\\Event\\Posted", event.data)
        }

        channel?.bind("Flarum\\Notification\\Event\\Sending") { event ->
            Log.d(TAG, "Received Notification\\Event\\Sending: ${event.data}")
            handleForumEvent("Flarum\\Notification\\Event\\Sending", event.data)
        }
    }

    private fun handleForumEvent(eventName: String, data: String) {
        val (title, content) = parseEventData(eventName, data)
        showNotification(title, content)
    }

    private fun parseEventData(eventName: String, data: String): Pair<String, String> {
        try {
            val appContext = App.INSTANCE
            return when (eventName) {
                "newPost", "Flarum\\Post\\Event\\Posted" -> {
                    // Try to extract meaningful info from the event data JSON
                    val title = appContext.getString(R.string.notification_new_post)
                    val content = appContext.getString(R.string.notification_new_post_by_user)
                    Pair(title, content)
                }
                "discussionStarted" -> {
                    val title = appContext.getString(R.string.tab_discussions)
                    val content = "A new discussion was started on the forum."
                    Pair(title, content)
                }
                "Flarum\\Notification\\Event\\Sending" -> {
                    val title = appContext.getString(R.string.notifications)
                    val content = "You have a new notification."
                    Pair(title, content)
                }
                else -> {
                    Pair("Forum Activity", "New activity on the forum: $eventName")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing event data", e)
            return Pair("Forum Activity", "New activity on the forum")
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Forum Notifications"
            val descriptionText = "Notifications for new posts, replies, and activities"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
                enableLights(true)
                setShowBadge(true)
            }
            val notificationManager: NotificationManager =
                App.INSTANCE.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(title: String, content: String) {
        val intent = Intent(App.INSTANCE, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            App.INSTANCE,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(App.INSTANCE, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setCategory(NotificationCompat.CATEGORY_SOCIAL)
            .setGroup(CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager: NotificationManager =
            App.INSTANCE.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Use incrementing ID so notifications stack rather than overwrite
        notificationIdCounter++
        notificationManager.notify(notificationIdCounter, builder.build())
    }
}
