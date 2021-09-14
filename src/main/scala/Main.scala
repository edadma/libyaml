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

  println(parseFromString("""
      |---
      |- 5
      |- {3: [asdf, 6], 4.5: 2021-09-14}
      |---
      |123
      |""".stripMargin))

  def parseFromString(s: String): Any = {
    val parser = new Parser

    parser.setInputString(s)
    parse(parser)
  }

  def parse(parser: Parser): YAMLStream = {
    val event = new Event

    def parseStream: YAMLStream = {
      val buf = new ListBuffer[YAMLDocument]

      while (next == EventType.DOCUMENT_START) buf += parseDocument

      if (next == EventType.STREAM_END)
        parseError("expected end of stream")

      YAMLStream(buf.toList)
    }

    def parseDocument: YAMLDocument = {
      next

      val value = parseValue

      if (next != EventType.DOCUMENT_END)
        parseError(s"expected end of document: ${event.getType.value}")

      YAMLDocument(value)
    }

    def parseValue: YAMLValue =
      if (event.getType == EventType.SCALAR)
        parseScalar
      else if (event.getType == EventType.SEQUENCE_START)
        parseSequence
      else if (event.getType == EventType.MAPPING_START)
        parseMapping
      else
        parseError(s"unknown value event type: ${event.getType.value}")

    def parseSequence: YAMLSequence = {
      val buf = new ListBuffer[YAMLValue]

      while (next != EventType.SEQUENCE_END) buf += parseValue

      YAMLSequence(buf.toList)
    }

    def parseMapping: YAMLMappping = {
      val buf = new ListBuffer[YAMLPair]

      while (next != EventType.MAPPING_END) {
        val key = parseValue

        next
        buf.append(YAMLPair(key, parseValue))
      }

      YAMLMappping(buf.toList)
    }

    def parseScalar: YAMLScalar = {
      val tag   = event.scalar.tag
      val value = event.scalar.value

      YAMLString(value)
    }

    def next: EventType = {
      if (event.getType != EventType.NO_EVENT)
        event.delete()

      if (parser.parse(event))
        parseError("error getting next event")
      else
        event.getType
    }

    def parseError(msg: String): Nothing = {
      event.destroy()
      parser.destroy()
      Console.err.println(s"Error ${event.startMark.line}:${event.startMark.column}: $msg")
      sys.exit(1)
    }

    if (next != EventType.STREAM_START)
      parseError("expected start of stream")

    val res = parseStream

    event.destroy()
    parser.destroy()
    res
  }

  trait YAML
  case class YAMLStream(docs: List[YAMLDocument])       extends YAML
  case class YAMLDocument(doc: YAMLValue)               extends YAML
  case class YAMLPair(key: YAMLValue, value: YAMLValue) extends YAML
  trait YAMLValue                                       extends YAML { val v: Any }
  trait YAMLScalar                                      extends YAMLValue
  case class YAMLBoolean(v: Boolean)                    extends YAMLScalar
  case class YAMLBinary(v: ArraySeq[Byte])              extends YAMLScalar
  case class YAMLInteger(v: Int)                        extends YAMLScalar
  case class YAMLFloat(v: Double)                       extends YAMLScalar
  case class YAMLString(v: String)                      extends YAMLScalar
  case object YAMLNull                                  extends YAMLScalar { val v: Any = null }
  case class YAMLTimestamp(v: String)                   extends YAMLScalar
  trait YAMLCollection                                  extends YAMLValue
  case class YAMLSequence(v: List[YAMLValue])           extends YAMLCollection
  case class YAMLSet(v: List[YAMLValue])                extends YAMLCollection
  case class YAMLMappping(v: List[YAMLPair])            extends YAMLCollection
  case class YAMLOrderedMapping(v: List[YAMLPair])      extends YAMLCollection
  case class YAMLPairs(v: List[YAMLPair])               extends YAMLCollection
  case class YAMLOther(tag: String, v: String)          extends YAMLValue

}
