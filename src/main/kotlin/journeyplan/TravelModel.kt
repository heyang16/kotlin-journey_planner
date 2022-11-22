package journeyplan

// Add your code for modelling public transport networks in this file.
class Station(val name: String, var closed: Boolean = false) {
  override fun toString() = name
  override fun equals(other: Any?) =
    name == other.toString()

  fun close() {
    closed = true
  }
  fun open() {
    closed = false
  }
}

class Line(val name: String, var suspended: Boolean = false) {
  override fun toString() = "$name Line"
  override fun equals(other: Any?) =
    "$name Line" == other.toString()

  fun suspend() {
    suspended = true
  }
  fun resume() {
    suspended = false
  }
}

class Segment(
  val from: Station,
  val to: Station,
  val line: Line,
  val avg: Int
) {
  override fun toString() =
    "$from to $to by $line"

  override fun equals(other: Any?) =
    "$from to $to by $line" == other.toString()
}
