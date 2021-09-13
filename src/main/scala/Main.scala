import io.github.edadma.libyaml._

import scala.annotation.tailrec

object Main extends App {

  val parser = new Parser
  val event  = new Event
  val input =
    """
      |hr:  65    # Home runs
      |avg: 0.278 # Batting average
      |rbi: 147   # Runs Batted In
      |""".stripMargin

  parser.setInputString(input)

  @tailrec
  def eventLoop(): Unit = {
    parser.parse(event)

    if (event.getType == EventType.SCALAR)
      println(s"${event.getType.value}")
    else
      println(s"${event.getType.value}")

    if (event.getType == EventType.STREAM_END)
      event.destroy()
    else {
      event.delete()
      eventLoop()
    }
  }

  eventLoop()
  parser.destroy()

}

//import io.github.edadma.libyaml._
//
//import scala.annotation.tailrec
//
//object Main extends App {
//
//  println(parseFromString("123"))
//
//  def parseFromString(s: String): Any = {
//    val parser = new Parser
//
//    parser.setInputString("a: b")
//    parse(parser)
//  }
//
//  def parse(parser: Parser): Any = {
//    val event = new Event
//
//    @tailrec
//    def eventLoop(): Unit = {
//      parser.parse(event)
//
//      if (event.getType == EventType.STREAM_END)
//        event.destroy()
//      else {
//        event.delete()
//        eventLoop()
//      }
//    }
//
//    eventLoop()
//    parser.destroy()
//
//  }
//
//}
