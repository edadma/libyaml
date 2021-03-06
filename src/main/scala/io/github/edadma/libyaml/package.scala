package io.github.edadma

import io.github.edadma.datetime.Datetime
import io.github.edadma.libyaml.extern.LibYAML._

import scala.collection.immutable.{ArraySeq, VectorMap}
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.scalanative.libc.stdio
import scala.scalanative.unsafe._
import scala.scalanative.libc.stdlib._
import scala.scalanative.unsigned._
import scala.util.matching.Regex

package object libyaml {

  val defaultConstructor = new Constructor

  private def bool(a: CInt): Boolean = if (a == 0) false else true

  private val FLOAT_REGEX = """[+-]?(?:[0-9_]*\.[0-9_]+|[0-9][0-9_]*\.)(?:[eE][+-]?[0-9]+)?""".r
  private val INT2_REGEX  = """([-+]?)0b([0-1_]+)""".r
  private val INT8_REGEX  = """([-+]?)0o?([0-7_]+)""".r
  private val INT10_REGEX = """([-+]?(?:0|[1-9][0-9_]*))""".r
  private val INT16_REGEX = """([-+]?)0x([0-9a-fA-F_]+)""".r
  private val INT60_REGEX = """([-+]?[1-9][0-9_]*(:[0-5]?[0-9])+)""".r

  val TIMESTAMP_REGEX: Regex =
    """\d\d\d\d-\d\d-\d\d|\d\d\d\d-\d\d?-\d\d?(?:[Tt]|[ \t]+)\d\d?:\d\d:\d\d(?:\.\d*)?(?:[ \t]*)(?:Z|[-+]\d\d?(?::\d\d)?)?""".r

  implicit class ErrorType(val value: yaml_error_type_t) extends AnyVal

  object ErrorType {
    final val NO_ERROR       = new ErrorType(0)
    final val MEMORY_ERROR   = new ErrorType(1)
    final val READER_ERROR   = new ErrorType(2)
    final val SCANNER_ERROR  = new ErrorType(3)
    final val PARSER_ERROR   = new ErrorType(4)
    final val COMPOSER_ERROR = new ErrorType(5)
    final val WRITER_ERROR   = new ErrorType(6)
    final val EMITTER_ERROR  = new ErrorType(7)
  }

  implicit class TokenType(val value: yaml_token_type_t) extends AnyVal

  object TokenType {
    final val NO_TOKEN             = new TokenType(0)
    final val STREAM_START         = new TokenType(1)
    final val STREAM_END           = new TokenType(2)
    final val VERSION_DIRECTIVE    = new TokenType(3)
    final val TAG_DIRECTIVE        = new TokenType(4)
    final val DOCUMENT_START       = new TokenType(5)
    final val DOCUMENT_END         = new TokenType(6)
    final val BLOCK_SEQUENCE_START = new TokenType(7)
    final val BLOCK_MAPPING_START  = new TokenType(8)
    final val BLOCK_END            = new TokenType(9)
    final val FLOW_SEQUENCE_START  = new TokenType(10)
    final val FLOW_SEQUENCE_END    = new TokenType(11)
    final val FLOW_MAPPING_START   = new TokenType(12)
    final val FLOW_MAPPING_END     = new TokenType(13)
    final val BLOCK_ENTRY          = new TokenType(14)
    final val FLOW_ENTRY           = new TokenType(15)
    final val KEY                  = new TokenType(16)
    final val VALUE                = new TokenType(17)
    final val ALIAS                = new TokenType(18)
    final val ANCHOR               = new TokenType(19)
    final val TAG                  = new TokenType(20)
    final val SCALAR               = new TokenType(21)
  }

  implicit class EventType(val value: event_type_t) extends AnyVal

  object EventType {
    final val NO_EVENT       = new EventType(0)
    final val STREAM_START   = new EventType(1)
    final val STREAM_END     = new EventType(2)
    final val DOCUMENT_START = new EventType(3)
    final val DOCUMENT_END   = new EventType(4)
    final val ALIAS          = new EventType(5)
    final val SCALAR         = new EventType(6)
    final val SEQUENCE_START = new EventType(7)
    final val SEQUENCE_END   = new EventType(8)
    final val MAPPING_START  = new EventType(9)
    final val MAPPING_END    = new EventType(10)
  }

  implicit class ScalarStyle(val value: yaml_scalar_style_t) extends AnyVal

  object ScalarStyle {
    final val ANY           = new ScalarStyle(0)
    final val PLAIN         = new ScalarStyle(1)
    final val SINGLE_QUOTED = new ScalarStyle(2)
    final val DOUBLE_QUOTED = new ScalarStyle(3)
    final val LITERAL       = new ScalarStyle(4)
    final val FOLDED        = new ScalarStyle(5)
  }

  implicit class Encoding(val value: CInt) extends AnyVal

  object Encoding {
    final val ANY     = new Encoding(0)
    final val UTF8    = new Encoding(1)
    final val UTF16LE = new Encoding(2)
    final val UTF16BE = new Encoding(3)
  }

  implicit class Break(val value: yaml_break_t) extends AnyVal

  object Break {
    final val ANY  = new Break(0)
    final val CR   = new Break(1)
    final val LN   = new Break(2)
    final val CRLN = new Break(3)
  }

  implicit class SequenceStyle(val value: yaml_sequence_style_t) extends AnyVal

  object SequenceStyle {
    final val ANY_SEQUENCE   = new SequenceStyle(0)
    final val BLOCK_SEQUENCE = new SequenceStyle(1)
    final val FLOW_SEQUENCE  = new SequenceStyle(2)
  }

  implicit class MappingStyle(val value: yaml_mapping_style_t) extends AnyVal

  object MappingStyle {
    final val ANY_MAPPING   = new MappingStyle(0)
    final val BLOCK_MAPPING = new MappingStyle(1)
    final val FLOW_MAPPING  = new MappingStyle(2)
  }

  class Event {
    private[libyaml] val event: yaml_event_tp = malloc(sizeof[yaml_event_t]).asInstanceOf[yaml_event_tp]

    event._1 = EventType.NO_EVENT.value

    def typ: EventType = event._1

    def scalar: Scalar = Scalar(event.asInstanceOf[Ptr[data_scalar]])

    def alias: Alias = Alias(event.asInstanceOf[Ptr[data_alias]])

    def sequenceStart: SequenceStart = SequenceStart(event.asInstanceOf[Ptr[data_sequence_start]])

    def mappingStart: MappingStart = MappingStart(event.asInstanceOf[Ptr[data_mapping_start]])

    def startMark: Mark = Mark(event._3._1.toInt, event._3._2.toInt + 1, event._3._3.toInt + 1)

    def endMark: Mark = Mark(event._4._1.toInt, event._4._2.toInt + 1, event._4._3.toInt + 1)

    def streamStartEventInitialize(encoding: yaml_encoding_t): Int =
      yaml_stream_start_event_initialize(event, encoding)

    def streamEndEventInitialize(event: Ptr[yaml_event_t]): Int = yaml_stream_end_event_initialize(event)

    //    def documentStartEventInitialize(version_directive: Ptr[yaml_version_directive_t],
    //                                     tag_directives_start: Ptr[yaml_tag_directive_t],
    //                                     tag_directives_end: Ptr[yaml_tag_directive_t],
    //                                     _implicit: Int): Int =
    //      yaml_document_start_event_initialize(event,
    //                                           version_directive,
    //                                           tag_directives_start,
    //                                           tag_directives_end,
    //                                           _implicit)
    def documentEndEventInitialize(_implicit: Int): Int =
      yaml_document_end_event_initialize(event, _implicit)

    def aliasEventInitialize(anchor: Ptr[yaml_char_t]): Int =
      yaml_alias_event_initialize(event, anchor)

    def scalarEventInitialize(anchor: Ptr[yaml_char_t],
                              tag: Ptr[yaml_char_t],
                              value: Ptr[yaml_char_t],
                              length: Int,
                              plain_implicit: Int,
                              quoted_implicit: Int,
                              style: yaml_scalar_style_t): Int =
      yaml_scalar_event_initialize(event, anchor, tag, value, length, plain_implicit, quoted_implicit, style)

    def sequenceStartEventInitialize(anchor: Ptr[yaml_char_t], tag: Ptr[yaml_char_t], _implicit: Int, style: SequenceStyle): Int =
      yaml_sequence_start_event_initialize(event, anchor, tag, _implicit, style.value)

    def sequenceEndEventInitialize(event: Ptr[yaml_event_t]): Int = yaml_sequence_end_event_initialize(event)

    def mappingStartEventInitialize(anchor: Ptr[yaml_char_t], tag: Ptr[yaml_char_t], _implicit: Int, style: MappingStyle): Int =
      yaml_mapping_start_event_initialize(event, anchor, tag, _implicit, style.value)

    def mappingEndEventInitialize(event: Ptr[yaml_event_t]): Int = yaml_mapping_end_event_initialize(event)

    def delete(): Unit = yaml_event_delete(event)

    def destroy(): Unit = {
      delete()
      free(event.asInstanceOf[Ptr[Byte]])
    }
  }

  case class Mark(index: Int, line: Int, column: Int)

  implicit class Scalar(val scalar: Ptr[data_scalar]) extends AnyVal {
    def anchor: String = fromCString(scalar._2)

    def tag: String = fromCString(scalar._3)

    def value: String = fromCString(scalar._4)

    def length: Int = scalar._5.toInt

    def plainImplicit: Boolean = bool(scalar._6)

    def quotedImplicit: Boolean = bool(scalar._7)
  }

  implicit class Alias(val alias: Ptr[data_alias]) extends AnyVal {
    def anchor: String = fromCString(alias._2)
  }

  implicit class SequenceStart(val sequenceStart: Ptr[data_sequence_start]) extends AnyVal {
    def anchor: String = fromCString(sequenceStart._2)

    def tag: String = fromCString(sequenceStart._3)

    def _implicit: Boolean = bool(sequenceStart._4)

    def sequenceStyle: SequenceStyle = SequenceStyle(sequenceStart._5)
  }

  implicit class MappingStart(val mappingStart: Ptr[data_mapping_start]) extends AnyVal {
    def anchor: String = fromCString(mappingStart._2)

    def tag: String = fromCString(mappingStart._3)

    def _implicit: Boolean = bool(mappingStart._4)

    def mappingStyle: MappingStyle = MappingStyle(mappingStart._5)
  }

  class Parser {
    private[libyaml] val parser: yaml_parser_tp = malloc(sizeof[yaml_parser_t]).asInstanceOf[yaml_parser_tp]
    private lazy val inputZone                  = Zone.open()

    if (yaml_parser_initialize(parser) == 0) {
      free(parser.asInstanceOf[Ptr[Byte]])
      sys.error("failed to initialize parser")
    }

    def setInputString(input: String): Unit =
      yaml_parser_set_input_string(parser, toCString(input)(inputZone), input.length.toUInt)

    def setInputFile(file: String): Unit = Zone { implicit z =>
      val fd = stdio.fopen(toCString(file), c"r")

      if (fd eq null)
        sys.error(s"error opening file '$file'")

      yaml_parser_set_input_file(parser, fd)
    }

    def parse(event: Event): Boolean = yaml_parser_parse(parser, event.event) == 0 // todo: handle errors better

    def destroy(): Unit = {
      yaml_parser_delete(parser)
      free(parser.asInstanceOf[Ptr[Byte]])
      inputZone.close()
    }
  }

  def parseFromString(s: String): YAMLStream = {
    val parser = new Parser

    parser.setInputString(s)
    parse(parser)
  }

  def parseFromFile(file: String): YAMLStream = {
    val parser = new Parser

    parser.setInputFile(file)
    parse(parser)
  }

  def constructFromString(s: String): List[Any] = {
    val parser = new Parser

    parser.setInputString(s)
    defaultConstructor.construct(parse(parser))
  }

  def constructFromFile(file: String): List[Any] = {
    val parser = new Parser

    parser.setInputFile(file)
    defaultConstructor.construct(parse(parser))
  }

  def parse(parser: Parser): YAMLStream = {
    val event   = new Event
    val aliases = new mutable.HashMap[String, YAMLValue]

    def addAlias(anchor: String, value: YAMLValue): YAMLValue = {
      if (anchor eq null) value
      else if (aliases contains anchor)
        parseError(s"alias '$anchor' already exists")
      else {
        aliases(anchor) = value
        value
      }
    }

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
        parseError(s"expected end of document: ${event.typ.value}")

      YAMLDocument(value)
    }

    def parseValue: YAMLValue =
      if (event.typ == EventType.SCALAR)
        parseScalar
      else if (event.typ == EventType.SEQUENCE_START)
        parseSequence
      else if (event.typ == EventType.MAPPING_START)
        parseMapping
      else if (event.typ == EventType.ALIAS)
        parseAlias
      else
        parseError(s"unknown value event type: ${event.typ.value}")

    def parseAlias: YAMLValue = {
      val anchor = event.alias.anchor

      aliases get anchor match {
        case Some(value) => value
        case None        => parseError(s"unknown alias: '$anchor'")
      }
    }

    def parseSequence: YAMLValue = {
      val sequenceStart = event.sequenceStart
      val tag           = sequenceStart.tag
      val anchor        = sequenceStart.anchor
      val mark          = event.startMark
      val buf           = new ListBuffer[YAMLValue]

      while (next != EventType.SEQUENCE_END) buf += parseValue

      val sequence =
        tag match {
          case "tag:yaml.org,2002:seq" | null => YAMLSequence(buf.toList)
          case "tag:yaml.org,2002:omap" =>
            if (buf exists {
                  case YAMLMapping(pairs) => pairs.length != 1
                  case _                  => true
                })
              problem("invalid omap", mark)

            YAMLOrderedMapping(buf map { case YAMLMapping(pairs) => pairs.head } toList)
          case "tag:yaml.org,2002:pairs" =>
            if (buf exists {
                  case YAMLMapping(pairs) => pairs.length != 1
                  case _                  => true
                })
              problem("invalid pairs", mark)

            YAMLPairs(buf map { case YAMLMapping(pairs) => pairs.head } toList)
          case _ => YAMLTaggedSequence(tag, buf.toList)
        }

      addAlias(anchor, sequence)
    }

    def parseMapping: YAMLValue = {
      val mappingStart = event.mappingStart
      val tag          = mappingStart.tag
      val anchor       = mappingStart.anchor
      val mark         = event.startMark
      val buf          = new ListBuffer[YAMLPair]

      while (next != EventType.MAPPING_END) {
        val key = parseValue

        next
        buf.append(YAMLPair(key, parseValue))
      }

      val mapping =
        tag match {
          case "tag:yaml.org,2002:map" | null => YAMLMapping(buf.toList)
          case "tag:yaml.org,2002:set" =>
            if (buf exists { case YAMLPair(_, value) => value != YAMLNull })
              problem("invalid set", mark)

            YAMLSet(buf map { case YAMLPair(key, _) => key } toList)
          case _ => YAMLTaggedMapping(tag, buf.toList)
        }

      addAlias(anchor, mapping)
    }

    def parseScalar: YAMLValue = {
      val anchor = event.scalar.anchor
      val tag    = event.scalar.tag
      val quoted = event.scalar.quotedImplicit
      val value  = event.scalar.value
      val mark   = event.startMark

      def typed(value: String): YAMLScalar =
        value match {
          case "true"                         => YAMLBoolean(true)
          case "false"                        => YAMLBoolean(false)
          case "null" | ""                    => YAMLNull
          case ".inf" | ".Inf" | ".INF"       => YAMLFloat(Double.PositiveInfinity)
          case "-.inf" | "-.Inf" | "-.INF"    => YAMLFloat(Double.NegativeInfinity)
          case ".nan" | ".NaN" | ".NAN"       => YAMLFloat(Double.NaN)
          case _ if FLOAT_REGEX matches value => YAMLDecimal(BigDecimal(value))
          case INT2_REGEX(s, n2) =>
            BigInt(s"$s${n2.replace("_", "")}", 2) match {
              case n if n.isValidInt => YAMLInteger(n.toInt)
              case n                 => YAMLBigInt(n)
            }
          case INT8_REGEX(s, n8) =>
            BigInt(s"$s${n8.replace("_", "")}", 8) match {
              case n if n.isValidInt => YAMLInteger(n.toInt)
              case n                 => YAMLBigInt(n)
            }
          case INT10_REGEX(_) =>
            BigInt(value.replace("_", "")) match {
              case n if n.isValidInt => YAMLInteger(n.toInt)
              case n                 => YAMLBigInt(n)
            }
          case INT16_REGEX(s, n16) =>
            BigInt(s"$s${n16.replace("_", "")}", 16) match {
              case n if n.isValidInt => YAMLInteger(n.toInt)
              case n                 => YAMLBigInt(n)
            }
          // todo: base 60
          case _ if TIMESTAMP_REGEX matches value => YAMLTimestamp(Datetime.fromString(value).timestamp)
          case _                                  => YAMLString(value)
        }

      val scalar =
        tag match {
          case null => if (quoted) YAMLString(value) else typed(value)
          case "tag:yaml.org,2002:binary" =>
            try {
              YAMLBinary(new Binary(java.util.Base64.getDecoder.decode(value.replaceAll("\\s", "").getBytes))) //todo: charset
            } catch {
              case _: IllegalArgumentException => problem(s"invalid base 64 string: $value", mark)
            }
          case "tag:yaml.org,2002:bool" =>
            typed(value) match {
              case b: YAMLBoolean => b
              case _              => problem(s"not a valid boolean: $value", mark)
            }
          case "tag:yaml.org,2002:float" =>
            typed(value) match {
              case f: YAMLFloat   => f
              case d: YAMLDecimal => YAMLFloat(d.v.toDouble)
              case _: YAMLInteger => YAMLFloat(value.toDouble)
              case _              => problem(s"not a valid float: $value", mark)
            }
          case "tag:yaml.org,2002:int" =>
            typed(value) match {
              case n @ (_: YAMLInteger | _: YAMLBigInt) => n
              case _                                    => problem(s"not a valid integer: $value", mark)
            }
          case "tag:yaml.org,2002:null" =>
            typed(value) match {
              case `YAMLNull` => YAMLNull
              case _          => problem(s"not a valid null: $value", mark)
            }
          case "tag:yaml.org,2002:timestamp" =>
            typed(value) match {
              case t: YAMLTimestamp => t
              case _                => problem(s"not a valid timestamp: $value", mark)
            }
          case _ => YAMLTaggedScalar(tag, quoted, value, event.startMark)
        }

      addAlias(anchor, scalar)
    }

    def next: EventType = {
      if (event.typ != EventType.NO_EVENT)
        event.delete()

      if (parser.parse(event))
        parseError("error getting next event")
      else
        event.typ
    }

    def parseError(msg: String): Nothing = {
      val mark = event.startMark

      event.destroy()
      parser.destroy()
      problem(msg, mark)
    }

    if (next != EventType.STREAM_START)
      parseError("expected start of stream")

    val res = parseStream

    event.destroy()
    parser.destroy()
    res
  }

  def problem(msg: String, mark: Mark): Nothing = {
    if (mark eq null)
      Console.err.println(s"Error: $msg")
    else
      Console.err.println(s"Error ${mark.line}:${mark.column}: $msg")

    sys.exit(1)
  }

  trait YAML

  case class YAMLStream(documents: List[YAMLDocument]) extends YAML

  case class YAMLDocument(document: YAMLValue) extends YAML

  case class YAMLPair(key: YAMLValue, value: YAMLValue) extends YAML

  trait YAMLValue extends YAML {
    val tag: String
  }

  trait YAMLScalar extends YAMLValue {
    val v: Any
  }

  case class YAMLTaggedScalar(tag: String, quoted: Boolean, v: String, mark: Mark) extends YAMLScalar

  case class YAMLBoolean(v: Boolean) extends YAMLScalar {
    val tag: String = "tag:yaml.org,2002:bool"
  }

  case class YAMLBinary(v: Binary) extends YAMLScalar {
    val tag: String = "tag:yaml.org,2002:binary"
  }

  case class YAMLInteger(v: Int) extends YAMLScalar {
    val tag: String = "tag:yaml.org,2002:int"
  }

  case class YAMLBigInt(v: BigInt) extends YAMLScalar {
    val tag: String = "tag:yaml.org,2002:int"
  }

  case class YAMLFloat(v: Double) extends YAMLScalar {
    val tag: String = "tag:yaml.org,2002:float"
  }

  case class YAMLDecimal(v: BigDecimal) extends YAMLScalar {
    val tag: String = "tag:yaml.org,2002:float"
  }

  case class YAMLString(v: String) extends YAMLScalar {
    val tag: String = "tag:yaml.org,2002:str"
  }

  case object YAMLNull extends YAMLScalar {
    val tag: String = "tag:yaml.org,2002:bool"
    val v: Any      = null
  }

  case class YAMLTimestamp(v: Datetime) extends YAMLScalar {
    val tag: String = "tag:yaml.org,2002:timestamp"
  }

  trait YAMLCollection extends YAMLValue

  case class YAMLSequence(elems: List[YAMLValue]) extends YAMLCollection { val tag: String = "tag:yaml.org,2002:seq" }

  case class YAMLSet(elems: List[YAMLValue]) extends YAMLCollection { val tag: String = "tag:yaml.org,2002:set" }

  case class YAMLTaggedSequence(tag: String, elems: List[YAMLValue]) extends YAMLCollection

  case class YAMLMapping(pairs: List[YAMLPair]) extends YAMLCollection { val tag: String = "tag:yaml.org,2002:map" }

  case class YAMLOrderedMapping(pairs: List[YAMLPair]) extends YAMLCollection { val tag: String = "tag:yaml.org,2002:omap" }

  case class YAMLPairs(pairs: List[YAMLPair]) extends YAMLCollection { val tag: String = "tag:yaml.org,2002:pairs" }

  case class YAMLTaggedMapping(tag: String, pairs: List[YAMLPair]) extends YAMLCollection

}
