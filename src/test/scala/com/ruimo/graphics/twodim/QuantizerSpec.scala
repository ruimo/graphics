package com.ruimo.graphics.twodim

import org.scalactic.TolerantNumerics
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import scala.collection.immutable

class QuantizerSpec extends AnyFlatSpec with should.Matchers {
  implicit val doubleEquality = TolerantNumerics.tolerantDoubleEquality(0.01)

  it should "Can find index" in {
    val q = new Quantizer(100, Range(0.1, 0.4), Range(0.4, 0.9), Range(2.0, 3.0))
    assert(q.totalWidth === (0.4 - 0.1) + (0.9 - 0.4) + (3.0 - 2.0))
    assert(q.min === 0.1)
    assert(q.max === 3.0)
    assert(q.minTable === immutable.Vector(0.1, 0.4, 2.0))
    assert(q.widthSumTable.size === 3)
    assert(q.widthSumTable(0) === 0.0 +- 0.01)
    assert(q.widthSumTable(1) === 0.3 +- 0.01)
    assert(q.widthSumTable(2) === 0.8 +- 0.01)

    assert(q.toIndex(0) === 0)
    assert(q.toIndex(0.2) === 6)
    assert(q.toIndex(0.7) === 33)
    assert(q.toIndex(0.9) === 44)
    assert(q.toIndex(1.5) === 44)
    assert(q.toIndex(2.9) === 94)
    assert(q.toIndex(3.0) === 99)
  }

  it should "Can convert index to value" in {
    val q = new Quantizer(100, Range(0.1, 0.4), Range(0.4, 0.9), Range(2.0, 3.0))

    assert(q.fromIndex(0) === 0.1)
    assert(q.fromIndex(6) === 0.208 +- 0.01)
    assert(q.fromIndex(33) === 0.694 +- 0.01)
    assert(q.fromIndex(44) === 0.892 +- 0.01)
    assert(q.fromIndex(94) === 2.892 +- 0.01)
    assert(q.fromIndex(100) === 3.0 +- 0.01)
  }
}

