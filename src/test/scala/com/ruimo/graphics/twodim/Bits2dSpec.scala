package com.ruimo.graphics.twodim

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import java.awt.image.BufferedImage

class Bits2dSpec extends AnyFlatSpec with should.Matchers {
  it should "Convert bits" in {
    // □■
    // □□
    val img0 = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB)
    img0.setRGB(0, 0, 0)
    img0.setRGB(1, 0, -1)
    img0.setRGB(0, 1, 0)
    img0.setRGB(1, 1, 0)

    val bits = Bits2d(img0)
    assert(bits(0, 0) === false)
    assert(bits(1, 0) === true)
    assert(bits(0, 1) === false)
    assert(bits(1, 1) === false)
  }

  it should "Convert bits with offset" in {
    // □■□
    // □■□
    // □□□
    val img0 = new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB)
    img0.setRGB(0, 0, 0)
    img0.setRGB(1, 0, -1)
    img0.setRGB(2, 0, 0)
    img0.setRGB(0, 1, 0)
    img0.setRGB(1, 1, -1)
    img0.setRGB(2, 1, 0)
    img0.setRGB(0, 2, 0)
    img0.setRGB(1, 2, 0)
    img0.setRGB(2, 2, 0)

    val bits = Bits2d(img0)
    assert(bits(0, 0) === false)
    assert(bits(1, 0) === true)
    assert(bits(2, 0) === false)
    assert(bits(0, 1) === false)
    assert(bits(1, 1) === true)
    assert(bits(2, 1) === false)
    assert(bits(0, 2) === false)
    assert(bits(1, 2) === false)
    assert(bits(2, 2) === false)

    val bitsWithOffset0 = Bits2d(img0, area = Some(Rectangle(1, 1, 2, 2)))
    assert(bitsWithOffset0(1, 1) === true)
    assert(bitsWithOffset0(2, 1) === false)
    assert(bitsWithOffset0(1, 2) === false)
    assert(bitsWithOffset0(2, 2) === false)

    val bitsWithOffset1 = Bits2d(img0, area = Some(Rectangle(0, 1, 2, 2)))
    assert(bitsWithOffset1(0, 1) === false)
    assert(bitsWithOffset1(1, 1) === true)
    assert(bitsWithOffset1(0, 2) === false)
    assert(bitsWithOffset1(1, 2) === false)

    val bitsWithOffset2 = Bits2d(img0, area = Some(Rectangle(1, 0, 2, 2)))
    assert(bitsWithOffset2(1, 0) === true)
    assert(bitsWithOffset2(2, 0) === false)
    assert(bitsWithOffset2(1, 1) === true)
    assert(bitsWithOffset2(2, 1) === false)
  }

  it should "Can find image" in {
    // img0
    // □□□□□
    // □□■■□
    // □□■□□
    // □□□□□
    val img0 = new BufferedImage(5, 4, BufferedImage.TYPE_INT_ARGB)
    img0.setRGB(0, 0, 0)
    img0.setRGB(1, 0, 0)
    img0.setRGB(2, 0, 0)
    img0.setRGB(3, 0, 0)
    img0.setRGB(4, 0, 0)
    img0.setRGB(0, 1, 0)
    img0.setRGB(1, 1, 0)
    img0.setRGB(2, 1, -1)
    img0.setRGB(3, 1, -1)
    img0.setRGB(4, 1, 0)
    img0.setRGB(0, 2, 0)
    img0.setRGB(1, 2, 0)
    img0.setRGB(2, 2, -1)
    img0.setRGB(3, 2, 0)
    img0.setRGB(4, 2, 0)
    img0.setRGB(0, 3, 0)
    img0.setRGB(1, 3, 0)
    img0.setRGB(2, 3, 0)
    img0.setRGB(3, 3, 0)
    img0.setRGB(4, 3, 0)
    val bits0 = Bits2d(img0)

    // img1
    // ■■
    // ■□
    val img1 = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB)
    img1.setRGB(0, 0, -1)
    img1.setRGB(1, 0, -1)
    img1.setRGB(0, 1, -1)
    img1.setRGB(1, 1, 0)
    val bits1 = Bits2d(img1)

    bits0.find(
      bits1, maxError = 0,
      xstart = 0, ystart = 0, xend = 3, yend = 2
    ) should === (Some(Offset(2, 1)))
  }

  it should "Can create sub image" in {
    // img0
    // □□□□□
    // □□■■□
    // □□■□□
    // □□□□□
    val img0 = new BufferedImage(5, 4, BufferedImage.TYPE_INT_ARGB)
    img0.setRGB(0, 0, 0)
    img0.setRGB(1, 0, 0)
    img0.setRGB(2, 0, 0)
    img0.setRGB(3, 0, 0)
    img0.setRGB(4, 0, 0)
    img0.setRGB(0, 1, 0)
    img0.setRGB(1, 1, 0)
    img0.setRGB(2, 1, -1)
    img0.setRGB(3, 1, -1)
    img0.setRGB(4, 1, 0)
    img0.setRGB(0, 2, 0)
    img0.setRGB(1, 2, 0)
    img0.setRGB(2, 2, -1)
    img0.setRGB(3, 2, 0)
    img0.setRGB(4, 2, 0)
    img0.setRGB(0, 3, 0)
    img0.setRGB(1, 3, 0)
    img0.setRGB(2, 3, 0)
    img0.setRGB(3, 3, 0)
    img0.setRGB(4, 3, 0)
    val bits0 = Bits2d(img0)

    val sub = Bits2d.subImage(bits0, 2, 1, 2, 2)
    assert(sub.width === 2)
    assert(sub.height === 2)
    assert(sub(0, 0) === true)
    assert(sub(1, 0) === true)
    assert(sub(0, 1) === true)
    assert(sub(1, 1) === false)
  }
}
