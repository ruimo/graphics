package com.ruimo.graphics.twodim

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import java.awt.{Color, Graphics2D}
import java.awt.image.BufferedImage

class BinarizationSpec extends AnyFlatSpec with should.Matchers {
  it should "Work for image" in {
    val bi = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB)
    val g: Graphics2D = bi.createGraphics()
    (0 until 256).foreach { i =>
      g.setColor(new Color(Rgb.byRgb(i, i, i).rgb))
      val x = i % 16
      val y = i / 16
      g.fillRect(x, y, 1, 1)
    }

    Binarization.perform(
      bi,
      Binarization.settings(brightnessThreshold = 100)
    )

    (0 until 256).foreach { i =>
      val x = i % 16
      val y = i / 16
      val rgb: Rgb = Rgb(bi.getRGB(x, y))
      val orgRgb = Rgb.byRgb(i, i, i)
      if (orgRgb.brightness < 100) {
        assert(rgb.red === 0)
        assert(rgb.green === 0)
        assert(rgb.blue === 0)
      }
      else {
        assert(rgb.red === 0xff)
        assert(rgb.green === 0xff)
        assert(rgb.blue === 0xff)
      }
    }
  }
}
