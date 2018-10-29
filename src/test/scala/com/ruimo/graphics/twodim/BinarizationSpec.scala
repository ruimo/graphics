package com.ruimo.graphics.twodim

import java.awt.{Color, Graphics2D}
import java.awt.image.BufferedImage
import org.specs2.mutable.Specification

class BinarizationSpec extends Specification {
  "Binarization" should {
    "Work for image" in {
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
          rgb.red === 0
          rgb.green === 0
          rgb.blue === 0
        }
        else {
          rgb.red === 0xff
          rgb.green === 0xff
          rgb.blue === 0xff
        }
      }
      1 === 1
    }
  }
}
