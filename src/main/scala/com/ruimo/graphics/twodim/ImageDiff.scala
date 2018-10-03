package com.ruimo.graphics.twodim

import java.nio.file.Path

import com.ruimo.graphics.twodim.Rgb
import javax.imageio.ImageIO
import scala.math.{sqrt, pow}

object ImageDiff {
  def colorDiff(rgb0: Rgb, rgb1: Rgb): Double =
    sqrt(
      pow(rgb0.red - rgb1.red, 2) +
      pow(rgb0.blue - rgb1.blue, 2) +
      pow(rgb0.green - rgb1.green, 2)
    )

  def diff(file0: Path, file1: Path): Double = {
    val img0 = ImageIO.read(file0.toFile)
    val img1 = ImageIO.read(file1.toFile)

    val width0 = img0.getWidth
    val width1 = img1.getWidth
    val height0 = img0.getHeight
    val height1 = img1.getHeight

    if (width0 != width1 || height0 != height1)
      throw new AssertionError("Image files " + file0 + " and " + file1 + " have different size.")

    var sum = 0.0
    (0 until width0).foreach { x =>
      (0 until height0).foreach { y =>
        val c0 = img0.getRGB(x, y)
        val c1 = img1.getRGB(x, y)
        sum += colorDiff(Rgb(c0), Rgb(c1))
      }
    }
    sum / (width0 * height0)
  }
}
