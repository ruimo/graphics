package com.ruimo.graphics.twodim

import java.awt.image.BufferedImage
import java.awt.Graphics2D
import java.awt.Color
import javax.imageio.ImageIO
import java.nio.file.Files
import java.nio.file.Path
import Hugh.{FoundLine, FoundLineWithDots}
import org.scalactic.TolerantNumerics
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import scala.collection.{immutable => imm}
import Math.{PI => Pi}

class HughSpec extends AnyFlatSpec with should.Matchers {
  implicit val doubleEquality = TolerantNumerics.tolerantDoubleEquality(0.01)

  it should "Detect horizontal line" in {
    // ■■■■■■■■■□
    // □□□□□□□□□□
    // □□□□□□□□□□
    // □□□□□□□□□□
    // □□□□□□□□□□
    // □□□□□□□□□□
    // □□□□□□□□□□
    // □□□□□□□□□□
    // □□□□□□□□□□
    // □□□□□□□□□□
    // θ = 0
    val file: Path = Files.createTempFile(null, ".jpg")
    file.toFile.deleteOnExit
    val bi = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB)
    val g: Graphics2D = bi.createGraphics()
    g.setColor(Color.WHITE)
    g.fillRect(0, 0, 10, 10)
    g.setColor(Color.BLACK)
    g.drawLine(0, 0, 9, 0)
    ImageIO.write(bi, "JPG", file.toFile)

    val found: imm.IndexedSeq[FoundLine] = Hugh.perform(file, roResolution = 100, thResolution = 360)
    val most = found.filter(_.count == 10)
    val ave = most.foldLeft(0.0) { (sum, e) => sum + e.th } / most.size

    assert(ave === 0.0 +- 0.1)
  }

  it should "Detect vertical line" in {
    // □■□□□□□□□□
    // □■□□□□□□□□
    // □■□□□□□□□□
    // □■□□□□□□□□
    // □■□□□□□□□□
    // □■□□□□□□□□
    // □■□□□□□□□□
    // □■□□□□□□□□
    // □■□□□□□□□□
    // □□□□□□□□□□
    // θ=π/2
    val file: Path = Files.createTempFile(null, ".jpg")
    file.toFile.deleteOnExit
    val bi = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB)
    val g: Graphics2D = bi.createGraphics()
    g.setColor(Color.WHITE)
    g.fillRect(0, 0, 10, 10)
    g.setColor(Color.BLACK)
    g.drawLine(1, 0, 1, 9)
    ImageIO.write(bi, "JPG", file.toFile)

    val found: imm.IndexedSeq[FoundLine] = Hugh.perform(file, roResolution = 100, thResolution = 360)
    val most = found.filter(_.count == 10).map(_.th).map(th => if (th < 0) th + Pi else th)
    val ave = most.foldLeft(0.0) { (sum, th) => sum + th } / most.size

    assert(ave  === Pi / 2 +- 0.1)
  }

  it should "Detect diagnoal line" in {
    // ■□□□□□□□□□
    // □■□□□□□□□□
    // □□■□□□□□□□
    // □□□■□□□□□□
    // □□□□■□□□□□
    // □□□□□■□□□□
    // □□□□□□■□□□
    // □□□□□□□■□□
    // □□□□□□□□■□
    // □□□□□□□□□□
    // θ=π/4
    val file: Path = Files.createTempFile(null, ".jpg")
    file.toFile.deleteOnExit
    val bi = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB)
    val g: Graphics2D = bi.createGraphics()
    g.setColor(Color.WHITE)
    g.fillRect(0, 0, 10, 10)
    g.setColor(Color.BLACK)
    g.drawLine(0, 0, 9, 9)
    ImageIO.write(bi, "JPG", file.toFile)

    val found: imm.IndexedSeq[FoundLine] = Hugh.perform(file, roResolution = 100, thResolution = 360)
    val most = found.filter(_.count == 10).map(_.th)
    val ave = most.foldLeft(0.0) { (sum, th) => sum + th } / most.size

    assert(ave === Pi / 4 +- 0.1)
  }
}

