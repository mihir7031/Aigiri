package com.example.aigiri.ui.screens

import android.graphics.Color
import android.util.Log
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
import io.livekit.android.room.track.*
import kotlinx.coroutines.launch
import livekit.org.webrtc.EglBase
import io.livekit.android.room.track.video.CameraCapturerUtils



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveCallScreen(token: String, wsUrl: String, onLeave: () -> Unit) {

    val context = LocalContext.current
    val egl = remember { EglBase.create() }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val localRender = remember {
        SurfaceViewRenderer(context).apply {
            setBackgroundColor(Color.RED) // DEBUG
            init(egl.eglBaseContext, null)
            setZOrderMediaOverlay(true)
            setZOrderOnTop(true) // DEBUG
            setMirror(true)
        }
    }
    var isLocalRenderReady by remember { mutableStateOf(false) }

    val remoteRenders = remember { mutableStateMapOf<Participant.Sid, SurfaceViewRenderer>() }
    var selectedParticipantSid by remember { mutableStateOf<Participant.Sid?>(null) }

    var room by remember { mutableStateOf<Room?>(null) }
    var micEnabled by remember { mutableStateOf(true) }
    var camEnabled by remember { mutableStateOf(true) }
    var camPos by remember { mutableStateOf(CameraPosition.BACK) }
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
                        val videoTrack = ev.track as? VideoTrack
                        if (videoTrack != null) {
                            val renderer = SurfaceViewRenderer(context).apply {
                                init(egl.eglBaseContext, null)
                                setEnableHardwareScaler(true)
                                setMirror(true)
                            }
                            videoTrack.addRenderer(renderer)
                            remoteRenders[ev.participant.sid] = renderer
                            if (selectedParticipantSid == null) {
                                selectedParticipantSid = ev.participant.sid
                            }
                            snackbarHostState.showSnackbar("🎥 Track subscribed")
                            Log.d("LiveCallScreen", "Subscribed to video track from participant: ${ev.participant.identity}")
                        }
                    }

                    is RoomEvent.TrackUnsubscribed -> {
                        remoteRenders.remove(ev.participant.sid)?.release()
                        snackbarHostState.showSnackbar("❌ Track unsubscribed")
                    }
                    is RoomEvent.Disconnected -> {
                        snackbarHostState.showSnackbar("📴 Disconnected")
                        onLeave()
                    }
                    else -> {}
                }
            }
        }

        try {
            r.connect(wsUrl, token)
            val defaults = room!!.videoTrackCaptureDefaults

            val capturerResult = CameraCapturerUtils.createCameraCapturer(context, defaults)

            if (capturerResult == null) {
                snackbarHostState.showSnackbar("❌ No camera capturer found!")
                Log.e("LiveCallScreen", "No camera capturer found!")
                return@LaunchedEffect
            }

            val (capturer, usedOpts) = capturerResult
            val vidTrack = room!!.localParticipant.createVideoTrack("camera", capturer, usedOpts)

            vidTrack.addRenderer(localRender)
            room!!.localParticipant.setCameraEnabled(true)
            room!!.localParticipant.publishVideoTrack(vidTrack)

            localVidTrack = vidTrack
            isLocalRenderReady = true

            val micTrack = room!!.localParticipant.createAudioTrack("mic")
            room!!.localParticipant.publishAudioTrack(micTrack)

            snackbarHostState.showSnackbar("✅ Media tracks published")
        } catch (e: Exception) {
            snackbarHostState.showSnackbar("❌ Failed: ${e.message ?: "Unknown error"}")
            Log.e("LiveCallScreen", "Error during LiveKit connection", e)
        }
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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { pad ->
        Box(Modifier.fillMaxSize().padding(pad)) {
            val mainRenderer = if (selectedParticipantSid == null) {
                if (isLocalRenderReady) localRender else null
            } else {
                remoteRenders[selectedParticipantSid] ?: if (isLocalRenderReady) localRender else null
            }

            mainRenderer?.let { renderer ->
                AndroidView(
                    factory = {
                        FrameLayout(context).apply {
                            setBackgroundColor(Color.GREEN)
                            addView(renderer, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
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
                        .clickable {
                            selectedParticipantSid = if (selectedParticipantSid == sid) null else sid
                        })
                }
            }

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
                    localVidTrack?.switchCamera()
                }) {
                    Icon(Icons.Default.Cameraswitch, contentDescription = "Switch Camera")
                }
            }
        }
    }
}
