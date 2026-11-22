package dev.imirror.receiver.ui

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.Gravity
import android.view.Surface
import android.graphics.SurfaceTexture
import android.view.TextureView
import android.widget.FrameLayout
import android.widget.TextView
import dev.imirror.receiver.airplay.StreamStats
import dev.imirror.receiver.util.Logger

/**
 * StreamingScreen — Full-screen view that displays the AirPlay video stream.
 *
 * WHY: The decoded video from MediaCodec must be rendered to a [Surface].
 * A [TextureView] composites MediaCodec output as part of the Activity window,
 * avoiding a separate SurfaceView window during task transitions.
 *
 * HOW: Add this view to the streaming_container in activity_main.xml.
 * Call [getSurface] to get the Surface to pass to [VideoDecoder.initialize].
 * The Surface is valid as long as this view is attached to the window.
 *
 * IMPORTANT: The Surface becomes available asynchronously after the view is
 * laid out. [getSurface] returns null if called before the Surface is ready.
 * [VideoDecoder.initialize] must not be called until [getSurface] returns non-null.
 *
 * Example:
 *   val streamingScreen = StreamingScreen(context)
 *   container.addView(streamingScreen)
 *   // Later, when surface is ready:
 *   val surface = streamingScreen.getSurface()
 *   videoDecoder.initialize(spsBytes, ppsBytes, width, height)
 */
class StreamingScreen @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    // TextureView content participates in the Activity window composition.
    private val textureView: TextureView = TextureView(context)

    // MediaCodec writes into a Surface backed by the TextureView's SurfaceTexture.
    private var surface: Surface? = null
    private var activeSurfaceTexture: SurfaceTexture? = null

    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(texture: SurfaceTexture, width: Int, height: Int) {
            activeSurfaceTexture = texture
            surface = Surface(texture)
            Logger.d("StreamingScreen: TextureView surface available ${width}x${height}")
        }

        override fun onSurfaceTextureSizeChanged(texture: SurfaceTexture, width: Int, height: Int) {
            Logger.d("StreamingScreen: TextureView surface changed ${width}x${height}")
        }

        override fun onSurfaceTextureDestroyed(texture: SurfaceTexture): Boolean {
            if (activeSurfaceTexture === texture) {
                val oldSurface = surface
                surface = null
                activeSurfaceTexture = null
                oldSurface?.release()
            }
            Logger.d("StreamingScreen: TextureView surface destroyed")
            return true
        }

        override fun onSurfaceTextureUpdated(texture: SurfaceTexture) = Unit
    }

    // Optional debug HUD (Settings → "Debug overlay"), drawn on top of the video.
    private val debugView = TextView(context).apply {
        setTextColor(Color.parseColor("#FF00FF66"))
        setBackgroundColor(Color.parseColor("#A6000000"))
        textSize = 13f
        typeface = Typeface.MONOSPACE
        setPadding(24, 16, 24, 16)
        visibility = GONE
    }
    // Last applied surface size, so we only re-layout on an actual change (rotation/resolution switch).
    private var lastSurfaceW = Int.MIN_VALUE
    private var lastSurfaceH = Int.MIN_VALUE

    private val handler = Handler(Looper.getMainLooper())
    private val tick = object : Runnable {
        override fun run() {
            applyAspectFit()
            val containerLocation = IntArray(2)
            getLocationOnScreen(containerLocation)
            StreamStats.surfaceSize =
                "container=${width}x${height}@${containerLocation[0]},${containerLocation[1]} " +
                    "view=${textureView.width}x${textureView.height}@${textureView.left},${textureView.top}"
            val metrics = resources.displayMetrics
            StreamStats.surfaceBuffer =
                "texture=${textureView.width}x${textureView.height} " +
                    "display=${metrics.widthPixels}x${metrics.heightPixels} rot=${display?.rotation ?: -1}"
            if (StreamStats.overlayEnabled) {
                debugView.visibility = VISIBLE
                debugView.text = StreamStats.summary()
            } else if (debugView.visibility != GONE) {
                debugView.visibility = GONE
            }
            handler.postDelayed(this, REFRESH_MS)
        }
    }

    init {
        // Black backing keeps letterbox/pillarbox bars black.
        setBackgroundColor(Color.BLACK)

        // TextureView is centred and aspect-fit by resizing its view bounds.
        textureView.surfaceTextureListener = surfaceTextureListener
        addView(textureView, LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        ).apply { gravity = Gravity.CENTER })

        // Debug HUD overlay, top-right, above the video surface.
        addView(debugView, LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        ).apply { gravity = Gravity.TOP or Gravity.RIGHT; topMargin = 48; rightMargin = 48 })

    }

    /**
     * Returns the [Surface] where decoded video frames will be rendered.
     *
     * Returns null if the Surface has not been created yet (the view hasn't
     * been laid out) or if it has been destroyed. The caller must check for
     * null before passing this to [VideoDecoder.initialize].
     *
     * @return The rendering Surface, or null if not yet available.
     */
    fun getSurface(): Surface? = surface

    /**
     * Sizes the TextureView to the decoded video's aspect ratio (letterbox/pillarbox) instead of
     * stretching it to fill 16:9. Without this, a portrait phone stream is squashed horizontally.
     * Falls back to filling the container when the size isn't known yet.
     */
    private fun applyAspectFit() {
        val vw = StreamStats.videoWidth
        val vh = StreamStats.videoHeight
        val cw = width
        val ch = height
        val (targetW, targetH) = if (vw <= 0 || vh <= 0 || cw <= 0 || ch <= 0) {
            LayoutParams.MATCH_PARENT to LayoutParams.MATCH_PARENT
        } else {
            val videoRatio = vw.toFloat() / vh
            val containerRatio = cw.toFloat() / ch
            if (videoRatio > containerRatio) cw to (cw / videoRatio).toInt()   // fit width, bars top/bottom
            else (ch * videoRatio).toInt() to ch                              // fit height, bars left/right
        }
        if (targetW == lastSurfaceW && targetH == lastSurfaceH) return
        lastSurfaceW = targetW
        lastSurfaceH = targetH
        textureView.layoutParams = (textureView.layoutParams as LayoutParams).apply {
            width = targetW; height = targetH; gravity = Gravity.CENTER
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        handler.post(tick)                 // drive aspect-fit + debug HUD
    }

    override fun onDetachedFromWindow() {
        handler.removeCallbacks(tick)
        super.onDetachedFromWindow()
    }

    companion object {
        private const val REFRESH_MS = 200L
    }
}
