package com.ruimo.graphics.twodim

import java.awt.image.BufferedImage

import com.ruimo.graphics.twodim.Hsv.Hue
import com.ruimo.scoins.Percent

import scala.annotation.tailrec

object ColorPassFilter {
  trait Settings {
    val hue: Hsv.Hue
    val hueErrorAllowance: Percent

    val lowLimitHue: Hsv.Hue = Hue(hue.value * (100d - hueErrorAllowance.value) / 100d)
    val highLimitHue: Hsv.Hue = Hue(hue.value * (100d + hueErrorAllowance.value) / 100d)

    def shouldPass(hsv: Hsv): Boolean =
      lowLimitHue <= hsv.h && hsv.h <= highLimitHue
  }

  case class SettingsImpl private(
    hue: Hsv.Hue, hueErrorAllowance: Percent
  ) extends Settings

  def settings(
    hue: Hsv.Hue, hueErrorAllowance: Percent
  ): Settings = SettingsImpl(hue, hueErrorAllowance)

  def perform(img: BufferedImage, settings: Settings, filteredColor: Rgb) {
    val width = img.getWidth()
    val height = img.getHeight()
    
    def filtered(color: Int): Int = {
      val hsv = Hsv(Rgb(color))
      if (settings.shouldPass(hsv)) color else filteredColor.rgb
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
