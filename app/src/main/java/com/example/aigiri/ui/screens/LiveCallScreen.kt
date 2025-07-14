package com.example.aigiri.ui.screens

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import io.livekit.android.LiveKit
import io.livekit.android.events.RoomEvent
import io.livekit.android.events.collect
import io.livekit.android.renderer.SurfaceViewRenderer
import io.livekit.android.room.Room
import io.livekit.android.room.participant.Participant
import io.livekit.android.room.track.CameraPosition
import io.livekit.android.room.track.LocalVideoTrack
import io.livekit.android.room.track.LocalVideoTrackOptions
import io.livekit.android.room.track.VideoTrack
import io.livekit.android.room.track.video.CameraCapturerUtils
import kotlinx.coroutines.launch
import livekit.org.webrtc.EglBase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveCallScreen(token: String, wsUrl: String, onLeave: () -> Unit) {
    val context = LocalContext.current
    val egl = remember { EglBase.create() }
    val scope = rememberCoroutineScope()

    val localRender = remember {
        SurfaceViewRenderer(context).apply {
            init(egl.eglBaseContext, null)
            setZOrderMediaOverlay(true)
        }
    }

    val remoteRenders = remember { mutableStateMapOf<Participant.Sid, SurfaceViewRenderer>() }
    var selectedParticipantSid by remember { mutableStateOf<Participant.Sid?>(null) }

    var room by remember { mutableStateOf<Room?>(null) }
    var micEnabled by remember { mutableStateOf(true) }
    var camEnabled by remember { mutableStateOf(true) }
    var camPos by remember { mutableStateOf(CameraPosition.FRONT) }
    var localVidTrack by remember { mutableStateOf<LocalVideoTrack?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            room?.disconnect()
            localRender.release()
            remoteRenders.values.forEach { it.release() }
            egl.release()
        }
    }

    LaunchedEffect(Unit) {
        val r = LiveKit.create(context)
        room = r

        scope.launch {
            r.events.collect { ev ->
                when (ev) {
                    is RoomEvent.TrackSubscribed -> {
                        if (ev.track is VideoTrack) {
                            val videoTrack = ev.track as VideoTrack
                            val renderer = SurfaceViewRenderer(context).apply {
                                init(egl.eglBaseContext, null)
                            }
                            videoTrack.addRenderer(renderer)
                            remoteRenders[ev.participant.sid] = renderer
                            if (selectedParticipantSid == null) {
                                selectedParticipantSid = ev.participant.sid
                            }
                        }
                    }

                    is RoomEvent.TrackUnsubscribed -> {
                        remoteRenders.remove(ev.participant.sid)?.release()
                    }

                    is RoomEvent.Disconnected -> {
                        onLeave()
                    }

                    else -> {}
                }
            }
        }

        val opts = LocalVideoTrackOptions(position = camPos)
        val (capturer, updatedOpts) = CameraCapturerUtils.createCameraCapturer(context, opts)
            ?: error("No available camera")

        val vidTrack = r.localParticipant.createVideoTrack("camera", capturer, updatedOpts)
        vidTrack.addRenderer(localRender)
        r.localParticipant.publishVideoTrack(vidTrack)
        localVidTrack = vidTrack

        val micTrack = r.localParticipant.createAudioTrack("mic")
        r.localParticipant.publishAudioTrack(micTrack)

        r.connect(wsUrl, token)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Live Video Call") },
                actions = {
                    TextButton(onClick = {
                        room?.disconnect()
                        onLeave()
                    }) {
                        Text("Leave")
                    }
                }
            )
        }
    ) { pad ->
        Box(Modifier.fillMaxSize().padding(pad)) {
            val mainRenderer = selectedParticipantSid?.let { remoteRenders[it] }

            mainRenderer?.let { renderer ->
                AndroidView(factory = {
                    FrameLayout(context).apply {
                        addView(renderer, FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT))
                    }
                }, modifier = Modifier.fillMaxSize())
            }

            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                remoteRenders.forEach { (sid, renderer) ->
                    AndroidView(factory = {
                        FrameLayout(context).apply {
                            addView(renderer, FrameLayout.LayoutParams(200, 200))
                        }
                    }, modifier = Modifier
                        .size(100.dp)
                        .clickable { selectedParticipantSid = sid })
                }
            }

            AndroidView(factory = {
                FrameLayout(context).apply {
                    addView(localRender, FrameLayout.LayoutParams(300, 400))
                }
            }, modifier = Modifier
                .size(120.dp)
                .padding(16.dp)
                .align(Alignment.BottomEnd))

            Row(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = {
                    micEnabled = !micEnabled
                    scope.launch {
                        room?.localParticipant?.setMicrophoneEnabled(micEnabled)
                    }
                }) {
                    Icon(
                        imageVector = if (micEnabled) Icons.Default.Mic else Icons.Default.MicOff,
                        contentDescription = "Toggle Mic"
                    )
                }

                IconButton(onClick = {
                    camEnabled = !camEnabled
                    scope.launch {
                        room?.localParticipant?.setCameraEnabled(camEnabled)
                    }
                }) {
                    Icon(
                        imageVector = if (camEnabled) Icons.Default.Videocam else Icons.Default.VideocamOff,
                        contentDescription = "Toggle Camera"
                    )
                }

                IconButton(onClick = {
                    camPos = if (camPos == CameraPosition.FRONT) CameraPosition.BACK else CameraPosition.FRONT
                    localVidTrack?.switchCamera(position = camPos)
                }) {
                    Icon(Icons.Default.Cameraswitch, contentDescription = "Switch Camera")
                }
            }
        }
    }
}
