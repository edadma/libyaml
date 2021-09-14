import io.github.edadma.libyaml._

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer

object Main extends App {

  val parser = new Parser
  val event  = new Event
  val input  = "[!!int '3', 4]"
//  val input =
//    """
//      |hr:  65    # Home runs
//      |avg: 0.278 # Batting average
//      |rbi: 147   # Runs Batted In
//      |""".stripMargin

  parser.setInputString(input)

  @tailrec
  def eventLoop(): Unit = {
    parser.parse(event)

    if (event.getType == EventType.SCALAR)
      println(
        s"${event.getType.value}, ${event.scalar.value}, ${event.scalar.tag}, ${event.scalar.plainImplicit}, ${event.scalar.quotedImplicit}")
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
//  def parse(parser: Parser): List[Any] = {
//    val event = new Event
//
//    def parseStream: List[Any] = {
//      if (next == EventType.STREAM_START)
//        parseError("expected stream start")
//
//      val buf = new ListBuffer[Any]
//
//      while (next == EventType.DOCUMENT_START) {
//        buf += parseDocument
//      }
//
//      if (next == EventType.STREAM_END)
//        parseError("expected stream end")
//
//      buf.toList
//    }
//
//    def parseDocument: Any = {
//      if (next == EventType.SCALAR)
//        parseScalar
//    }
//
//    def parseScalar: Any = {
//      val value = fromCString()
//    }
//
//    def next: EventType = {
//      if (event.getType != EventType.NO_EVENT)
//        event.delete()
//
//      parser.parse(event)
//      event.getType
//    }
//
//    def parseError(msg: String): Nothing = {
//      Console.err.println(msg)
//      sys.exit(1)
//    }
//
//    val res = parseStream()
//
//    parser.destroy()
//    res
//  }
//
//}
