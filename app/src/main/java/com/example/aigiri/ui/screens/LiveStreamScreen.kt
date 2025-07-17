package com.example.aigiri.ui.screens

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.aigiri.viewmodel.LiveStreamViewModel
import com.zegocloud.uikit.prebuilt.livestreaming.ZegoUIKitPrebuiltLiveStreamingConfig
import com.zegocloud.uikit.prebuilt.livestreaming.ZegoUIKitPrebuiltLiveStreamingFragment

fun Context.findActivity(): AppCompatActivity? {
    var ctx = this
    var depth = 0
    while (ctx is android.content.ContextWrapper) {
        if (ctx is AppCompatActivity) {
            return ctx
        }
        ctx = ctx.baseContext
        depth++
    }
    return null
}

@Composable
fun LiveStreamScreen(
    viewModel: LiveStreamViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val session by viewModel.liveSession.collectAsState()

    // Get activity and fragmentManager safely
    val activity = remember(context) { context.findActivity() }
    val fragmentManager = activity?.supportFragmentManager

    // Generate a stable view ID ONCE
    val fragmentContainerId = remember { View.generateViewId() }

    if (activity == null || fragmentManager == null) {
        // Optionally show an error message
        Text("Unable to load live stream UI")
        return
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { ctx ->
            FrameLayout(ctx).apply {
                id = fragmentContainerId
            }
        },
        update = { container ->
            if (fragmentManager.findFragmentById(fragmentContainerId) == null) {
                val config = ZegoUIKitPrebuiltLiveStreamingConfig.host() // or audience()

                val fragment = ZegoUIKitPrebuiltLiveStreamingFragment.newInstance(
                    session.appID,
                    session.appSign,
                    session.userID,
                    session.userName,
                    session.liveID,
                    config
                )

                fragmentManager.beginTransaction()
                    .replace(fragmentContainerId, fragment)
                    .commitNowAllowingStateLoss()
            }
        }
    )
}
