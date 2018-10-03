package com.ruimo.graphics.twodim

import java.awt.Color

case class Hsv(h: Hsv.Hue, s: Hsv.Saturation, v: Hsv.Value)

object Hsv {
  class Hue private(val value: Double) extends AnyVal with Ordered[Hue] {
    override def compare(that: Hue): Int =
      if (value < that.value) -1
      else if (value > that.value) 1
      else 0
  }

  object Hue {
    def apply(value: Double): Hue = {
      if (value < 0)
        throw new IllegalArgumentException("Invalid HSV hue(" + value + "). should 0 <= value < 360")

      val longValue = value.toLong
      if (longValue >= 360) {
        new Hue(value - (longValue / 360) * 360d)
      } else {
        new Hue(value)
      }
    }
  }

  class Saturation private(val value: Double) extends AnyVal with Ordered[Saturation] {
    override def compare(that: Saturation): Int =
      if (value < that.value) -1
      else if (value > that.value) 1
      else 0
  }

  object Saturation {
    def apply(value: Double): Saturation = {
      if (value < 0 || 100 < value)
        throw new IllegalArgumentException("Invalid HSV saturation(" + value + "). should 0 <= saturation <= 100")
      new Saturation(value)
    }
  }

  class Value private(val value: Double) extends AnyVal with Ordered[Value] {
    override def compare(that: Value): Int =
      if (value < that.value) -1
      else if (value > that.value) 1
      else 0
  }

  object Value {
    def apply(value: Double): Value = {
      if (value < 0 || 100 < value)
        throw new IllegalArgumentException("Invalid HSV value(" + value + "). should 0 <= value <= 100")
      new Value(value)
    }
  }

  def apply(rgb: Rgb): Hsv = {
    val hsv: Array[Float] = new Array[Float](3)
    Color.RGBtoHSB(rgb.red, rgb.green, rgb.blue, hsv)
    Hsv(Hue(hsv(0) * 360), Saturation(hsv(1) * 100), Value(hsv(2) * 100))
  }
}
