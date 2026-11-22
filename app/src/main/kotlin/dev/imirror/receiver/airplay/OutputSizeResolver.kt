package dev.imirror.receiver.airplay

import java.util.Locale

/** Pixel dimensions for one decoder or SPS picture. */
internal data class PictureSize(val width: Int, val height: Int) {
    val aspect: Double
        get() = if (width <= 0 || height <= 0) 0.0 else width.toDouble() / height
}

/** Chosen visible size and why the decoder crop was accepted or rejected. */
internal data class OutputSize(
    val width: Int,
    val height: Int,
    val cropAccepted: Boolean,
    val reason: String,
)

/**
 * Selects the picture aspect used by StreamingScreen.
 *
 * Some hardware decoders scale a frame to the panel, then report that scaled buffer size
 * as a crop rectangle. Compare the reported crop with the coded size and SPS dimensions;
 * only accept crops consistent with normal encoder padding.
 */
internal object OutputSizeResolver {
    const val MAX_ASPECT_DEVIATION = 0.05
    const val MIN_EDGE = 64
    const val MAX_EDGE = 8192
    const val MAX_CROP_SHRINK = 0.10

    fun resolve(coded: PictureSize, crop: PictureSize?, reference: PictureSize?): OutputSize {
        val codedUsable = isPlausible(coded)
        val usableReference = reference?.takeIf(::isPlausible)

        if (!codedUsable && (crop == null || !isPlausible(crop))) {
            val fallback = usableReference ?: coded
            return OutputSize(fallback.width, fallback.height, false, "no plausible size; using SPS")
        }
        if (crop == null || !isPlausible(crop)) {
            val fallback = if (codedUsable) coded else usableReference ?: coded
            return OutputSize(fallback.width, fallback.height, false, "no usable crop; using coded size")
        }

        val trueSize = pickReference(coded, usableReference)
        val base = if (codedUsable) coded else trueSize
        if (crop.width > base.width || crop.height > base.height) {
            return OutputSize(trueSize.width, trueSize.height, false, "crop exceeds coded picture")
        }

        val shrink = minOf(crop.width.toDouble() / base.width, crop.height.toDouble() / base.height)
        if (shrink < 1.0 - MAX_CROP_SHRINK) {
            val percent = String.format(Locale.US, "%.1f", (1.0 - shrink) * 100.0)
            return OutputSize(trueSize.width, trueSize.height, false, "crop shrank ${percent}%; likely decoder scaling")
        }

        val deviation = aspectDeviation(crop, trueSize)
        val percent = String.format(Locale.US, "%.1f", deviation * 100.0)
        return if (deviation <= MAX_ASPECT_DEVIATION) {
            OutputSize(crop.width, crop.height, true, "crop aspect differs by ${percent}%")
        } else {
            OutputSize(trueSize.width, trueSize.height, false, "crop aspect differs by ${percent}%; likely decoder scaling")
        }
    }

    private fun pickReference(coded: PictureSize, reference: PictureSize?): PictureSize {
        if (reference == null) return coded
        if (aspectDeviation(coded, reference) <= MAX_ASPECT_DEVIATION) return reference
        val transposed = PictureSize(reference.height, reference.width)
        if (aspectDeviation(coded, transposed) <= MAX_ASPECT_DEVIATION) return transposed
        return reference
    }

    private fun aspectDeviation(a: PictureSize, b: PictureSize): Double {
        if (a.aspect <= 0.0 || b.aspect <= 0.0) return Double.MAX_VALUE
        return kotlin.math.abs(a.aspect / b.aspect - 1.0)
    }

    private fun isPlausible(size: PictureSize): Boolean =
        size.width in MIN_EDGE..MAX_EDGE && size.height in MIN_EDGE..MAX_EDGE
}
