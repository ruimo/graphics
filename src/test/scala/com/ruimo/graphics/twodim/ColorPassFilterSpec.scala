package com.ruimo.graphics.twodim

import java.io.File
import java.nio.file.{Files, Paths}
import com.ruimo.graphics.twodim.Hsv.Hue
import com.ruimo.scoins.Percent
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import javax.imageio.ImageIO

class ColorPassFilterSpec extends AnyFlatSpec with should.Matchers {
  it should "Work for data00" in {
    val bi = ImageIO.read(new File("testdata/colorfilter/colorpass01.png"))
    ColorPassFilter.perform(
      bi,
      ColorPassFilter.settings(
        Hue(226), Percent(10)
      ),
      Rgb(0xffffff)
    )

    val outPath = Files.createTempFile(null, ".png")
    ImageIO.write(bi, "png", outPath.toFile)
    val diff = ImageDiff.diff(Paths.get("testdata/colorfilter/colorpass01-expected.png"), outPath)
    assert(diff < 1.0)
  }
}
