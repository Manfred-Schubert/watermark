package watermark

typealias PackedPixel = Int
typealias PackedPixelCompanion = Int.Companion

val PackedPixel.alpha: Int
    get() = (this shr 24) and 0xFF

fun PackedPixel.withAlpha(alpha: Int): PackedPixel {
    return (this and 0x00FFFFFF) or ((alpha and 0xFF) shl 24)
}

val PackedPixel.red: Int
    get() = (this shr 16) and 0xFF

fun PackedPixel.withRed(red: Int): PackedPixel {
    return (this and 0xFF00FFFF.toInt()) or ((red and 0xFF) shl 16)
}

val PackedPixel.green: Int
    get() = (this shr 8) and 0xFF

fun PackedPixel.withGreen(green: Int): PackedPixel {
    return (this and 0xFFFF00FF.toInt()) or ((green and 0xFF) shl 8)
}

val PackedPixel.blue: Int
    get() = this and 0xFF

fun PackedPixel.withBlue(blue: Int): PackedPixel {
    return (this and 0xFFFFFF00.toInt()) or (blue and 0xFF)
}

fun PackedPixelCompanion.fromRGB(red: Int, green: Int, blue: Int, alpha: Int = 255): PackedPixel {
    return ((alpha and 0xFF) shl 24) or
            ((red and 0xFF) shl 16) or
            ((green and 0xFF) shl 8) or
            (blue and 0xFF)
}
