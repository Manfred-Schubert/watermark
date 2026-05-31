package watermark

import java.awt.Color
import java.awt.Transparency
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

sealed class BlendMode {
    data object Default: BlendMode()
    data object Transparency: BlendMode()
    data class KeyColor(val color: Color): BlendMode()
}

sealed class Position {
    data class Offset(val dx: Int, val dy: Int): Position()
    data object Grid: Position()
}

fun BufferedImage.composeAtop(
    baseImage: BufferedImage,
    blendMode: BlendMode,
    weight: Int,
    position: Position
): BufferedImage {
    val outputImage = BufferedImage(
        baseImage.width,
        baseImage.height,
        BufferedImage.TYPE_INT_RGB,
    )

    for (y in 0 ..< baseImage.height) {
        for (x in 0 ..< baseImage.width) {
            val baseColor = Color(baseImage.getRGB(x, y))

            val (topX, topY) = when (position) {
                is Position.Offset -> {
                    val tx = x - position.dx
                    val ty = y - position.dy
                    if (tx in 0 ..< width && ty in 0 ..< height) Pair(tx, ty) else Pair(-1, -1)
                }
                is Position.Grid -> {
                    Pair(x % width, y % height)
                }
            }

            if (topX == -1 || topY == -1) {
                outputImage.setRGB(x, y, baseColor.rgb)
                continue
            }

            val topColor = Color(this.getRGB(topX, topY), true)

            val outputColor = when (blendMode) {
                BlendMode.Transparency -> {
                    if (topColor.alpha == 0) baseColor else null
                }
                is BlendMode.KeyColor -> {
                    val keyColor = blendMode.color
                    if (topColor.red == keyColor.red &&
                        topColor.green == keyColor.green &&
                        topColor.blue == keyColor.blue
                    ) baseColor else null
                }
                BlendMode.Default -> null
            } ?: Color(
                (weight * topColor.red + (100 - weight) * baseColor.red) / 100,
                (weight * topColor.green + (100 - weight) * baseColor.green) / 100,
                (weight * topColor.blue + (100 - weight) * baseColor.blue) / 100
            )

            outputImage.setRGB(x, y, outputColor.rgb)
        }
    }

    return outputImage
}

fun main() {
    println("Input the image filename:")
    val imageFile = File(readln())

    if (!imageFile.exists()) {
        println("The file $imageFile doesn't exist.")
        return
    }

    val image: BufferedImage = ImageIO.read(imageFile)

    with(image) {
        if (colorModel.numColorComponents != 3) {
            println("The number of image color components isn't 3.")
            return
        }
        val pixelSize = colorModel.pixelSize
        if (pixelSize != 24 && pixelSize != 32) {
            println("The image isn't 24 or 32-bit.")
            return
        }
    }

    println("Input the watermark image filename:")
    val watermarkFile = File(readln())

    if (!watermarkFile.exists()) {
        println("The file $watermarkFile doesn't exist.")
        return
    }

    val watermark: BufferedImage = ImageIO.read(watermarkFile)

    with(watermark) {
        if (colorModel.numColorComponents != 3) {
            println("The number of watermark color components isn't 3.")
            return
        }
        val pixelSize = colorModel.pixelSize
        if (pixelSize != 24 && pixelSize != 32) {
            println("The watermark isn't 24 or 32-bit.")
            return
        }
    }

    if (watermark.width > image.width || watermark.height > image.height) {
        println("The watermark's dimensions are larger.")
        return
    }

    var blendMode: BlendMode = BlendMode.Default

    if (watermark.colorModel.transparency == Transparency.TRANSLUCENT) {
        println("Do you want to use the watermark's Alpha channel?")
        if (readln().lowercase() == "yes") blendMode = BlendMode.Transparency
    } else {
        println("Do you want to set a transparency color?")
        if (readln().lowercase() == "yes") {
            println("Input a transparency color ([Red] [Green] [Blue]):")
            val keyComponents = readln().split("""\s+""".toRegex())
                .mapNotNull { it.toIntOrNull() }
            if (keyComponents.size != 3) {
                println("The transparency color input is invalid.")
                return
            }
            val (r, g, b) = keyComponents
            if (listOf(r, g, b).any { it !in 0..255}) {
                println("The transparency color input is invalid.")
                return
            }
            blendMode = BlendMode.KeyColor(Color(r, g, b))
        }
    }

    println("Input the watermark transparency percentage (Integer 0-100):")
    val weight = readln().toIntOrNull()

    if (weight == null) {
        println("The transparency percentage isn't an integer number.")
        return
    }
    if (weight !in 0..100) {
        println("The transparency percentage is out of range.")
        return
    }

    val position: Position
    println("Choose the position method (single, grid):")
    when (readln()) {
        "single" -> {
            val dxRange = 0..(image.width - watermark.width)
            val dyRange = 0..(image.height - watermark.height)

            println("Input the watermark position ([x 0-${dxRange.last()}] [y 0-${dyRange.last()}]):")
            val positions = readln().split("""\s+""".toRegex())
                .mapNotNull { it.toIntOrNull() }
            if (positions.size != 2) {
                println("The position input is invalid.")
                return
            }
            val (dx, dy) = positions
            if (dx !in dxRange || dy !in dyRange) {
                println("The position input is out of range.")
                return
            }
            position = Position.Offset(dx = dx, dy = dy)
        }
        "grid" -> position = Position.Grid
        else -> {
            println("The position method input is invalid.")
            return
        }
    }

    println("Input the output image filename (jpg or png extension):")
    val outputFilename = readln()
    val outputFile = File(outputFilename)
    val outputFileSuffix = outputFile.extension.lowercase()
    if (outputFileSuffix != "jpg" && outputFileSuffix != "png") {
        println("The output file extension isn't \"jpg\" or \"png\".")
        return
    }

    val outputImage = watermark.composeAtop(
        baseImage = image,
        blendMode = blendMode,
        weight = weight,
        position = position
    )

    ImageIO.write(outputImage, outputFileSuffix, outputFile)

    println("The watermarked image $outputFile has been created.")
}