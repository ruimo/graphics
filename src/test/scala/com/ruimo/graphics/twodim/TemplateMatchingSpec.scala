package com.ruimo.graphics.twodim

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import java.awt.image.BufferedImage
import java.nio.file.Paths
import javax.imageio.ImageIO

class TemplateMatchingSpec extends AnyFlatSpec with should.Matchers {
  it should "Detect template itself" in {
    val template: BufferedImage = ImageIO.read(Paths.get("testdata/templatematching/template.png").toFile)
    val found: Option[Offset] = TemplateMatching.find(
      Bits2d(template),
      Bits2d(template),
      maxError = 100
    )

    assert(found === Some(Offset(0, 0)))
  }

  it should "Detect moved pattern" in {
    // □□
    // □□
    val img0 = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB)
    img0.setRGB(0, 0, 0)
    img0.setRGB(1, 0, 0)
    img0.setRGB(0, 1, 0)
    img0.setRGB(1, 1, 0)
    // ■■■
    // ■□□
    // ■□□
    val img1 = new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB)
    img1.setRGB(0, 0, -1)
    img1.setRGB(1, 0, -1)
    img1.setRGB(2, 0, -1)
    img1.setRGB(0, 1, -1)
    img1.setRGB(1, 1, 0)
    img1.setRGB(2, 1, 0)
    img1.setRGB(0, 2, -1)
    img1.setRGB(1, 2, 0)
    img1.setRGB(2, 2, 0)

    val found: Option[Offset] = TemplateMatching.find(
      Bits2d(img1),
      Bits2d(img0),
      maxError = 1
    )
    assert(found === Some(Offset(1, 1)))
  }

  it should "Detect template itself moved" in {
    val template: BufferedImage = ImageIO.read(Paths.get("testdata/templatematching/template.png").toFile)
    val templateMoved: BufferedImage = ImageIO.read(Paths.get("testdata/templatematching/template-moved.png").toFile)
    val found: Option[Offset] = TemplateMatching.find(
      Bits2d(templateMoved),
      Bits2d(template),
      maxError = 1
    )

    assert(found === Some(Offset(1, 1)))
  }

  it should "Detect template" in {
    val bodyImg: BufferedImage = ImageIO.read(Paths.get("testdata/templatematching/schematics.png").toFile)
    val template: BufferedImage = ImageIO.read(Paths.get("testdata/templatematching/template.png").toFile)

    val found: Option[Offset] = TemplateMatching.find(
      Bits2d(bodyImg),
      Bits2d(template),
      maxError = 1
    )

    assert(found === Some(Offset(96, 277)))
  }

  it should "Detect in shrinked image" in {
    val bodyImg: BufferedImage = ImageIO.read(Paths.get("testdata/templatematching/schematics.png").toFile)
    val shrinkedImg: BufferedImage = ImageIO.read(Paths.get("testdata/templatematching/schematics-s.png").toFile)
    val template: BufferedImage = ImageIO.read(Paths.get("testdata/templatematching/template.png").toFile)
    val shrinkedTemplate: BufferedImage = ImageScaler.scale(
      template,
      (
        ((0.0d + shrinkedImg.getWidth) / bodyImg.getWidth) +
          ((0.0d + shrinkedImg.getHeight) / bodyImg.getHeight)
        ) / 2
    )

    val shrinkedBodyBits = Bits2d(shrinkedImg)
    val shrinkedTemplateBits = Bits2d(shrinkedTemplate)

    val found: Option[Offset] = TemplateMatching.find(
      shrinkedBodyBits,
      shrinkedTemplateBits,
      maxError = 50
    )

    assert(found === Some(Offset(71, 205)))
  }
}
