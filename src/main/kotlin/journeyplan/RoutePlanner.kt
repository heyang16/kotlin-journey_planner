package journeyplan

// Add your code for the route planner in this file.
class SubwayMap(private val segments: List<Segment>) {
  fun routesFrom(
    origin: Station,
    destination: Station,
    optimisingFor: (Route) -> Int = Route::duration
  ): List<Route> {
    // Finds all routes linking 2 stations
    fun helper(
      origin: Station,
      visited: List<Station>
    ): List<Route> {
      return if (origin == destination) {
        // Base case
        listOf(Route(emptyList()))
      } else {
        // All segments extending out from the origin that do not go back to
        // a visited station, using lines that are not suspended
        val fromOrigin = segments.filter { s -> !s.line.suspended }
          .filter { s -> s.from == origin }
          .filter { s -> !visited.contains(s.to) }
        // Converts each segment extending out from the origin into a sub-route
        // Adds the origin into the list of visited stations
        fromOrigin.map { s ->
          helper(s.to, visited + s.from).map { r ->
            // Prepends the origin to every sub-route
            Route(listOf(s) + r.segments)
          }
        }.flatten()
      }
    }

    fun checkRoute(route: Route): Boolean {
      // Checks if a route uses a closed station to interchange
      // Returns false if it does
      val l = route.segments.size
      for ((i, segment) in route.segments.withIndex()) {
        if (i < l - 1 &&
          segment.to.closed &&
          segment.line != route.segments[i + 1].line // Interchanges here
        ) {
          return false
        }
      }
      return true
    }

    return helper(origin, emptyList()).filter(::checkRoute)
      .sortedBy(optimisingFor)
  }
}

class Route(val segments: List<Segment>) {
  override fun toString(): String {
    val sb = StringBuilder()
    sb.append(
      "${segments[0].from} to ${segments.last().to}"
    )
    sb.append(
      " - ${duration()} minutes, ${numChanges()} changes"
    )

    // Starts with the first half of the first segment.
    sb.append("\n - " + segments[0].from.toString() + " to ")
    val l = segments.size
    for ((i, s) in segments.withIndex()) {
      if (i == l - 1) {
        // Prints the second of half of the last segment.
        sb.append("${s.to} by ${s.line}")
      } else if (s.line != segments[i + 1].line) {
        // Once a new Line is reached, print the second half of the segment
        // and the first half of the new segment
        sb.append("${s.to} by ${s.line}\n - ${segments[i + 1].from} to ")
      }
      // If the second segment is still on the same Line, do nothing
    }
    return sb.toString()
  }

  // Calculates the amount of time a route takes
  fun duration(): Int {
    var t = 0
    for (s in segments) {
      t += s.avg
    }
    return t
  }

  // Calculates the amount of changes a route requires
  fun numChanges(): Int {
    val lines = segments.map { s -> s.line.toString() }
    return maxOf(0, (lines.distinct().size - 1))
  }
}

val piccadillyLine = Line("Piccadilly")
val victoriaLine = Line("Victoria")
val districtLine = Line("District")
val southKensington = Station("South Kensington")
val knightsbridge = Station("Knightsbridge")
val hydeParkCorner = Station("Hyde Park Corner")
val greenPark = Station("Green Park")
val oxfordCircus = Station("Oxford Circus")
val victoria = Station("Victoria")
val sloaneSquare = Station("Sloane Square")

fun londonUnderground(): SubwayMap = SubwayMap(
  listOf(
    Segment(southKensington, knightsbridge, piccadillyLine, 3),
    Segment(knightsbridge, hydeParkCorner, piccadillyLine, 4),
    Segment(hydeParkCorner, greenPark, piccadillyLine, 2),
    Segment(greenPark, oxfordCircus, victoriaLine, 1),
    Segment(greenPark, victoria, victoriaLine, 1),
    Segment(victoria, greenPark, victoriaLine, 1),
    Segment(victoria, sloaneSquare, districtLine, 6),
    Segment(sloaneSquare, southKensington, districtLine, 3),
    Segment(southKensington, sloaneSquare, districtLine, 6),
    Segment(sloaneSquare, victoria, districtLine, 6)
  )
)

fun main() {
  val map = londonUnderground()

  println(map.routesFrom(southKensington, oxfordCircus))
}
