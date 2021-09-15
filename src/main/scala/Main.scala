import io.github.edadma.libyaml._

import scala.annotation.tailrec
import scala.collection.immutable.{ArraySeq, VectorMap}
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

//object Main extends App {
//
//  val parser = new Parser
//  val event  = new Event
//  val input  = "[3, 4]"
////  val input =
////    """
////      |hr:  65    # Home runs
////      |avg: 0.278 # Batting average
////      |rbi: 147   # Runs Batted In
////      |""".stripMargin
//
//  parser.setInputString(input)
//
//  @tailrec
//  def eventLoop(): Unit = {
//    parser.parse(event)
//
//    if (event.getType == EventType.SCALAR)
//      println(
//        s"${event.getType.value}, ${event.scalar.value}, ${event.scalar.tag}, ${event.scalar.plainImplicit}, ${event.scalar.quotedImplicit}")
//    else
//      println(s"${event.getType.value}")
//
//    if (event.getType == EventType.STREAM_END)
//      event.destroy()
//    else {
//      event.delete()
//      eventLoop()
//    }
//  }
//
//  eventLoop()
//  parser.destroy()
//
//}

object Main extends App {

//  val result: List[Any] =
//    parseFromString("""
//        |3.4
//        |""".stripMargin)
//
//  println(result, result.head.getClass)

  println(TIMESTAMP matches "2002-12-14")
}
