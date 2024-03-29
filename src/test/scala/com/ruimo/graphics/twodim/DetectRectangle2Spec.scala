package com.ruimo.graphics.twodim

import java.awt.image.BufferedImage
import java.awt.Graphics2D
import java.awt.Color
import javax.imageio.ImageIO
import java.nio.file.{Files, Path, Paths}
import Hugh.FoundLine

import scala.collection.immutable
import Math.{PI => Pi}
import java.io.File
import com.ruimo.scoins.Percent
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class DetectRectangleSpec2 extends AnyFlatSpec with should.Matchers {
  it should "Can detect rectangle 0" in {
    // 　０１２３４５６７８９
    // ０□□□□□□□□□□
    // １□■□□□□□□□□
    // ２□■□□□□□□□□
    // ３□■□□□□□□□□
    // ４□□□□□□□□□□
    // ５□□□□□□□□□□
    // ６□□□□□□□□□□
    // ７□□□□□□□□□□
    // ８□□□□□□□□□□
    // ９□□□□□□□□□□

    val bi = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB)
    val g: Graphics2D = bi.createGraphics()
    g.setColor(Color.WHITE)
    g.fillRect(0, 0, 10, 10)
    g.setColor(Color.BLACK)
    g.drawLine(1, 1, 1, 3)

    DetectRectangle2.findLargest(
      bi, errorAllowance = 2, lengthLimit = Percent(20), slantAllowance = 0
    ) should === (None)
  }

  it should "Can detect rectangle 1" in {
    // 　０１２３４５６７８９
    // ０□■■■■■■□□□
    // １□□■□■□□■□□
    // ２□□■■■■■■□□
    // ３□□□□□□□□□□
    // ４□□□□□□□□□□
    // ５□■■■■■■□□□
    // ６□□□□□□□□□□
    // ７□□□□□□□□■□
    // ８□□□□□□□□■□
    // ９□□□□□□□□□□

    val bi = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB)
    val g: Graphics2D = bi.createGraphics()
    g.setColor(Color.WHITE)
    g.fillRect(0, 0, 10, 10)
    g.setColor(Color.BLACK)
    g.drawLine(2, 2, 7, 2)
    g.drawLine(1, 0, 6, 0)
    g.drawLine(4, 0, 4, 2)
    g.drawLine(2, 0, 2, 2)
    g.drawLine(7, 1, 7, 2)
    g.drawLine(1, 5, 6, 5)
    g.drawLine(8, 7, 8, 8)

    val rs = DetectRectangle2.findLargest(
      bi, errorAllowance = 2, lengthLimit = Percent(20), slantAllowance = 0
    )
    assert(rs === Some(Rectangle(2, 0, 5, 2)))
  }

  it should "Can detect rectangle 2" in {
    val bi = ImageIO.read(Paths.get("testdata/detectrectangle/test0001.png").toFile)
    val rs = DetectRectangle2.findLargest(
      bi, errorAllowance = 25, lengthLimit = Percent(40)
    )
    assert(rs === Some(Rectangle(94, 46, 498, 288)))
  }

  it should "Can detect rectangle 3" in {
    val bi = ImageIO.read(Paths.get("testdata/detectrectangle/test0101.png").toFile)
    val rs = DetectRectangle.findLargest(
      bi, errorAllowance = 25, lengthLimit = Percent(40)
    )
    assert(rs === Some(Rectangle(50, 58, 519, 299)))
  }

  it should "Can detect rectangle 4" in {
    val bi = ImageIO.read(Paths.get("testdata/detectrectangle/test0102.png").toFile)
    val rs = DetectRectangle2.findLargest(
      bi, errorAllowance = 40, lengthLimit = Percent(30),  slantAllowance = 4
    )
    assert(rs === Some(Rectangle(150, 213, 852, 611)))
  }

  it should "Can detect rectangle 5" in {
    val bi = ImageIO.read(Paths.get("testdata/detectrectangle/test0103.png").toFile)
    val rs = DetectRectangle2.findLargest(
      bi, errorAllowance = 40, lengthLimit = Percent(40), slantAllowance = 8
    )
    assert(rs === Some(Rectangle(118, 170, 844, 608)))
  }
}
