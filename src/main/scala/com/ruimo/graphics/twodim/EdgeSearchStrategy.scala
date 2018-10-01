package com.ruimo.graphics.twodim

import java.awt.Color

import com.ruimo.scoins.Percent

import scala.annotation.tailrec

trait EdgeSearchStrategy {
  def find(line: Seq[Rgb]): (Boolean, EdgeSearchStrategy)
}

case class BlackEdgeSearchStrategy(blackBrightnessThreshold: Double) extends EdgeSearchStrategy {
  def find(line: Seq[Rgb]): (Boolean, EdgeSearchStrategy) = {
    val black = (line.foldLeft(0L)(_ + _.brightness))
    (black / line.size < blackBrightnessThreshold, this)
  }
}

case class ColorEdgeSearchStrategy(
  h: Double, s: Double, v: Double,
  hsvError: Percent, threshold: Percent
) extends EdgeSearchStrategy {
  def find(line: Seq[Rgb]): (Boolean, EdgeSearchStrategy) = {
    var hsv = new Array[Float](3)

    val hitCount = (line.foldLeft(0L) { (cnt, c) =>
      def error(d0: Double, d1: Double, depth: Double): Percent = Percent(100 * Math.abs(d0 - d1) / depth)

      @tailrec def normalizeH(hvalue: Double): Double =
        if (hvalue < 0) normalizeH(hvalue + 360)
        else if (hvalue > 360) normalizeH(hvalue - 360)
        else hvalue

      Color.RGBtoHSB(c.red, c.green, c.blue, hsv)
      val h0 = normalizeH(hsv(0) * 360)
      val s0 = hsv(1).toDouble * 100
      val v0 = hsv(2).toDouble * 100

      if (
        error(h0, h, 360) < hsvError && error(s0, s, 100) < hsvError && error(v0, v, 100) < hsvError
      ) cnt + 1 else cnt
    })
    (Percent(100 * hitCount / line.size) > threshold, this)
  }
}
