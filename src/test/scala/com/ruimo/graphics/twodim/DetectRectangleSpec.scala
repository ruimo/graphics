package com.ruimo.graphics.twodim

import java.awt.image.BufferedImage
import java.awt.Graphics2D
import java.awt.Color
import javax.imageio.ImageIO
import java.nio.file.Paths
import com.ruimo.scoins.Percent
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class DetectRectangleSpec extends AnyFlatSpec with should.Matchers {
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

    DetectRectangle.findLargest(
      bi, errorAllowance = 2, lengthLimit = Percent(1)
    ) == None
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

    val rs = DetectRectangle.findLargest(
      bi, lineCount = 500, thetaResolution = 20, errorAllowance = 2, lengthLimit = Percent(1)
    )
    assert(rs === Some(Rectangle(2, 0, 5, 2)))
  }

  it should "Can detect rectangle 2" in {
    val bi = ImageIO.read(Paths.get("testdata/detectrectangle/test0001.png").toFile)
    val rs = DetectRectangle.findLargest(
      bi, lineCount = 500, thetaResolution = 20, errorAllowance = 25, lengthLimit = Percent(50)
    )
    assert(rs === Some(Rectangle(94, 46, 498, 288)))
  }
}
