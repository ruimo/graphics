package com.ruimo.graphics.twodim

import java.awt.image.BufferedImage

import scala.annotation.tailrec

object Binarization {
  trait Settings {
    val brightnessThreshold: Int
  }

  private case class SettingsImpl(
    brightnessThreshold: Int
  ) extends Settings

  def settings(
    brightnessThreshold: Int
  ): Settings = SettingsImpl(brightnessThreshold)

  def perform(img: BufferedImage, settings: Settings): Unit = {
    val width = img.getWidth()
    val height = img.getHeight()

    def filtered(color: Int): Int = {
      val brightness = Rgb(color).brightness
      if (brightness < settings.brightnessThreshold) Rgb.Black.rgb else Rgb.White.rgb
    }

    @tailrec def performFilter(idx: Int, size: Int, pixels: Array[Int]): Array[Int] = {
      if (idx >= size) pixels
      else {
        pixels(idx) = filtered(pixels(idx));
        performFilter(idx + 1, size, pixels)
      }
    }

    val pixels: Array[Int] = img.getRGB(0, 0, width, height, new Array[Int](width * height), 0, width)
    img.setRGB(0, 0, width, height, performFilter(0, width * height, pixels), 0, width)
  }
}
