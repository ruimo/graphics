package com.ruimo.graphics.twodim

import java.io.File
import java.nio.file.{Files, Paths}

import com.ruimo.graphics.twodim.Hsv.Hue
import com.ruimo.scoins.Percent
import javax.imageio.ImageIO
import org.specs2.mutable.Specification

class ColorPassFilterSpec extends Specification {
  "Color pass filter" should {
    "Work for data00" in {
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
      diff must beLessThan(1.0)
    }
  }
}
