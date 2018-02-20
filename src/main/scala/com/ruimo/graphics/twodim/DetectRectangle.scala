package com.ruimo.graphics.twodim

import scala.collection.{immutable => imm, mutable => mut}
import java.awt.image.BufferedImage

import com.ruimo.graphics.twodim.Hugh.FoundLineWithDots
import Degree.toRadian

import scala.math.Pi
import scala.math.{abs, sqrt, min, max, pow}
import scala.annotation.tailrec

// Detect rectangles. The rectangles should be constracted with horizontal and vertical lines.
object DetectRectangle {
  // maxAngleToDetect, roResolution, thetaResolution are used for hugh conversion.
  // By Hugh conversion, horizontal and vertical lines are found. Take top lineCount lines.
  // Detect largest rectangle from these taken lines.
  // When looking for lines, errorAllowance is used to allow non consecutive dots in a line.
  def findLargest(
    image: BufferedImage,
    maxAngleToDetect: Double = 1.0, roResolution: Int = 3000, thetaResolution: Int = 200,
    lineCount: Int = 100, errorAllowance: Int = 10
  ): Option[Rectangle] = {
    val rangeVertical = imm.Seq(
      Range(min = toRadian(-maxAngleToDetect), max = toRadian(maxAngleToDetect)),
      Range(min = toRadian(180 - maxAngleToDetect), max = Pi)
    )

    val rangeHorizontal = imm.Seq(
      Range(min = toRadian(90 - maxAngleToDetect / 2), max = toRadian(90 + maxAngleToDetect / 2))
    )

    val foundVertical: imm.IndexedSeq[FoundLineWithDots] = Hugh.performImageWithDots(
      image, roResolution, thetaResolution, thRange = rangeVertical
    )

    val foundHorizontal: imm.IndexedSeq[FoundLineWithDots] = Hugh.performImageWithDots(
      image, roResolution, thetaResolution, thRange = rangeHorizontal
    )
    sealed trait DetectedLine {
      val sortedDots: List[(Int, Int)]
      val dots: imm.Set[(Int, Int)]
    }

    case class HorizontalLine(dots: imm.Set[(Int, Int)]) extends DetectedLine {
      lazy val left: (Int, Int) = sortedDots.head
      lazy val right: (Int, Int) = sortedDots.last
      lazy val y = (left._2 + right._2) / 2

      lazy val sortedDots: List[(Int, Int)] = dots.toList.sortBy(_._1)

      def split: imm.Seq[HorizontalLine] = {
        @tailrec def splitter(
          remaining: List[(Int, Int)], current: imm.Set[(Int, Int)], result: List[HorizontalLine], lastX: Int = -1
        ): List[HorizontalLine] = remaining match {
          case Nil => if (current.isEmpty) result else HorizontalLine(current) :: result
          case head::tail =>
            if (lastX == -1) {
              splitter(tail, current + head, result, head._1)
            }
            else {
              if (head._1 - lastX <= errorAllowance) splitter(tail, current + head, result, head._1)
              else splitter(tail, imm.HashSet(head), HorizontalLine(current) :: result, head._1)
            }
        }

        splitter(sortedDots, imm.HashSet(), List())
      }
    }

    case class VerticalLine(dots: imm.Set[(Int, Int)]) extends DetectedLine {
      lazy val top: (Int, Int) = sortedDots.head
      lazy val bottom: (Int, Int) = sortedDots.last
      lazy val x = (top._1 + bottom._1) / 2

      lazy val sortedDots: List[(Int, Int)] = dots.toList.sortBy(_._2)

      def split: imm.Seq[VerticalLine] = {
        @tailrec def splitter(
          remaining: List[(Int, Int)], current: imm.Set[(Int, Int)], result: List[VerticalLine], lastY: Int = -1
        ): List[VerticalLine] = remaining match {
          case Nil => if (current.isEmpty) result else VerticalLine(current) :: result
          case head::tail =>
            if (lastY == -1) {
              splitter(tail, current + head, result, head._2)
            }
            else {
              if (head._2 - lastY <= errorAllowance) splitter(tail, current + head, result, head._2)
              else splitter(tail, imm.HashSet(head), VerticalLine(current) :: result, head._2)
            }
        }

        splitter(sortedDots.toList, imm.TreeSet(), List())
      }
    }

    def distinct[T <: DetectedLine](lines: imm.Seq[T]): imm.Seq[T] = {
      val set = new mut.HashSet[T]() ++= lines

      def includes(l0: DetectedLine, l1: DetectedLine): Boolean = {
        l0 match {
          case HorizontalLine(seq0) => l1 match {
            case HorizontalLine(seq1) => seq1.find((e1: (Int, Int)) => ! seq0(e1)) == None
            case _ => false
          }
          case VerticalLine(seq0) => l1 match {
            case VerticalLine(seq1) => seq1.find((e1: (Int, Int)) => ! seq0(e1)) == None
            case _ => false
          }
        }
      }

      var ret = imm.Seq[T]()
      set.toSeq.sortBy(e => -e.dots.size).foreach { e =>
        ret.find(rete => includes(rete, e)) match {
          case Some(_) =>
          case None =>
            ret = ret :+ e
        }
      }
      ret
    }

    def splitNonConsecutiveVLine(lines: imm.Seq[VerticalLine]): imm.Seq[VerticalLine] = lines.flatMap(_.split)
    def splitNonConsecutiveHLine(lines: imm.Seq[HorizontalLine]): imm.Seq[HorizontalLine] = lines.flatMap(_.split)

    val resultVertical = distinct(foundVertical.take(lineCount).map(l => VerticalLine(l.dots)))
    val resultHorizontal = distinct(foundHorizontal.take(lineCount).map(l => HorizontalLine(l.dots)))

    val splitVertical: imm.Seq[VerticalLine] = splitNonConsecutiveVLine(resultVertical)
    val splitHorizontal: imm.Seq[HorizontalLine] = splitNonConsecutiveHLine(resultHorizontal)

    def findRectangle(splitVertical: imm.Seq[VerticalLine], splitHorizontal: imm.Seq[HorizontalLine]): imm.Seq[Rectangle] = {
      def distance(p0: (Int, Int), p1: (Int, Int)): Double = sqrt(
        pow(p1._1 - p0._1, 2) + pow(p1._2 - p0._2, 2)
      )

      def rectangle(vl0: VerticalLine, hl0: HorizontalLine, vl1: VerticalLine, hl1: HorizontalLine): Rectangle = {
        val x0 = min(vl0.x, vl1.x)
        val x1 = max(vl0.x, vl1.x)
        val y0 = min(hl0.y, hl1.y)
        val y1 = max(hl0.y, hl1.y)

        Rectangle(x0, y0, x1 - x0, y1 - y0)
      }

      var found: List[Rectangle] = List()

      def init(vls: imm.Seq[VerticalLine], hls: imm.Seq[HorizontalLine]) {
        vls.foreach { vl =>
          findTopLeft(vls, hls, vl)
        }
      }

      def findTopLeft(
        vls: imm.Seq[VerticalLine], hls: imm.Seq[HorizontalLine], line0: VerticalLine
      ) {
        hls.foreach { hl =>
          if (distance(hl.left, line0.top) <= errorAllowance) {
            findTopRight(vls, hls, line0, hl)
          }
        }
      }

      def findTopRight(
        vls: imm.Seq[VerticalLine], hls: imm.Seq[HorizontalLine],
        line0: VerticalLine, line1: HorizontalLine
      ) {
        vls.foreach { vl =>
          if (
            vl.x != line0.x &&
            distance(vl.top, line1.right) <= errorAllowance
          ) {
            findBottomRight(vls, hls, line0, line1, vl)
          }
        }
      }

      def findBottomRight(
        vls: imm.Seq[VerticalLine], hls: imm.Seq[HorizontalLine],
        line0: VerticalLine, line1: HorizontalLine, line2: VerticalLine
      ) {
        hls.foreach { hl =>
          if (
            hl.y != line1.y
              && distance(hl.right, line2.bottom) <= errorAllowance
              && distance(hl.left, line0.bottom) <= errorAllowance
          ) {
            found = rectangle(line0, line1, line2, hl)::found
          }
        }
      }

      init(splitVertical, splitHorizontal)
      found
    }

    def findLargest(rects: imm.Seq[Rectangle]): Option[Rectangle] = {
      if (rects.size == 0) None
      else if (rects.size == 1) Some(rects(0))
      else {
        var ret = rects(0)
        rects.tail.foreach { r =>
          if (ret.area < r.area) {
            ret = r
          }
        }
        Some(ret)
      }
    }

    findLargest(findRectangle(splitVertical.toList, splitHorizontal.toList))
  }
}