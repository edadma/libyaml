package io.github.edadma

import io.github.edadma.libyaml.extern.LibYAML._

import scala.collection.immutable.ArraySeq
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.scalanative.unsafe._
import scala.scalanative.libc.stdlib._
import scala.scalanative.unsigned._
import scala.util.matching.Regex

package object libyaml {

  private def bool(a: CInt): Boolean = if (a == 0) false else true

  private val FLOAT_REGEX = """[+-]?[0-9]*\.[0-9]+([eE][+-]?[0-9]+)?""".r
  private val INT_REGEX   = """0|[+-]?[1-9][0-9]+""".r
  val TIMESTAMP_REGEX: Regex =
    """[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]|[0-9][0-9][0-9][0-9]-[0-9][0-9]?-[0-9][0-9]?([Tt]|[ \t]+)[0-9][0-9]?:[0-9][0-9]:[0-9][0-9](\.[0-9]*)?(([ \t]*)Z|[-+][0-9][0-9]?(:[0-9][0-9])?)?""".r

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

  implicit class StreamStart(val enc: Ptr[yaml_encoding_t]) extends AnyVal {
    def encoding: Encoding = !enc
  }

  class Event {
    private[libyaml] val event: yaml_event_tp = malloc(sizeof[yaml_event_t]).asInstanceOf[yaml_event_tp]

    event._1 = EventType.NO_EVENT.value

    def getType: EventType = event._1

    def scalar: Scalar = Scalar(event.asInstanceOf[Ptr[data_scalar]])

    def alias: Alias = Alias(event.asInstanceOf[Ptr[data_alias]])

    def startMark: Mark = Mark(event._3._1.toInt, event._3._2.toInt, event._3._3.toInt)

    def endMark: Mark = Mark(event._4._1.toInt, event._4._2.toInt, event._4._3.toInt)

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
    def sequenceStartEventInitialize(anchor: Ptr[yaml_char_t],
                                     tag: Ptr[yaml_char_t],
                                     _implicit: Int,
                                     style: SequenceStyle): Int =
      yaml_sequence_start_event_initialize(event, anchor, tag, _implicit, style.value)
    def sequenceEndEventInitialize(event: Ptr[yaml_event_t]): Int = yaml_sequence_end_event_initialize(event)
    def mappingStartEventInitialize(anchor: Ptr[yaml_char_t],
                                    tag: Ptr[yaml_char_t],
                                    _implicit: Int,
                                    style: MappingStyle): Int =
      yaml_mapping_start_event_initialize(event, anchor, tag, _implicit, style.value)
    def mappingEndEventInitialize(event: Ptr[yaml_event_t]): Int = yaml_mapping_end_event_initialize(event)

    def delete(): Unit = yaml_event_delete(event)

    def destroy(): Unit = {
      delete()
      free(event.asInstanceOf[Ptr[Byte]])
    }
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

  def parse(parser: Parser): YAMLStream = {
    val event   = new Event
    val aliases = new mutable.HashMap[String, YAMLValue]

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
      else if (event.getType == EventType.ALIAS)
        parseAlias
      else
        parseError(s"unknown value event type: ${event.getType.value}")

    def parseAlias: YAMLValue = {
      val anchor = event.alias.anchor

      aliases get anchor match {
        case Some(value) => value
        case None        => parseError(s"unknown alias: '$anchor'")
      }
    }

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
      val anchor = event.scalar.anchor
      val tag    = event.scalar.tag
      val value  = event.scalar.value
      val plain  = event.scalar.plainImplicit
      val quoted = event.scalar.quotedImplicit

      def typed: YAMLScalar =
        value match {
          case "true"                         => YAMLBoolean(true)
          case "false"                        => YAMLBoolean(false)
          case "null" | ""                    => YAMLNull
          case ".inf"                         => YAMLFloat(Double.PositiveInfinity)
          case "-.inf"                        => YAMLFloat(Double.NegativeInfinity)
          case ".nan" | ".NaN"                => YAMLFloat(Double.NaN)
          case _ if FLOAT_REGEX matches value => YAMLFloat(value.toDouble)
          case _ if INT_REGEX matches value =>
            BigInt(value) match {
              case n if n.isValidInt => YAMLInteger(n.toInt)
              case n                 => YAMLBigInt(n)
            }
          case _ if TIMESTAMP_REGEX matches value => YAMLTimestamp(value)
          case _                                  => YAMLString(value)
        }

      val scalar =
        if (tag ne null) {
          val typ =
            tag.lastIndexOf(':') match {
              case -1  => parseError(s"unknown tag: $tag")
              case idx => tag.substring(idx + 1)
            }

          typ match {
            case "str" => YAMLString(value)
            case "int" =>
              typed match {
                case n @ (_: YAMLInteger | _: YAMLBigInt) => n
                case _                                    => parseError(s"not a valid integer: $value")
              }
            case "float" =>
              typed match {
                case f: YAMLFloat   => f
                case _: YAMLInteger => YAMLFloat(value.toDouble)
                case _              => parseError(s"not a valid float: $value")
              }
            case "bool" =>
              typed match {
                case b: YAMLBoolean => b
                case _              => parseError(s"not a valid boolean: $value")
              }
            case "null" =>
              typed match {
                case `YAMLNull` => YAMLNull
                case _          => parseError(s"not a valid null: $value")
              }
            case "timestamp" =>
              typed match {
                case t: YAMLTimestamp => t
                case _                => parseError(s"not a valid timestamp: $value")
              }
            case "binary" =>
            case _        => parseError(s"unknown type: $typ")
          }
        } else if (quoted)
          YAMLString(value)
        else
          typed

      if (anchor eq null) scalar
      else if (aliases contains anchor)
        parseError(s"alias '$anchor' already exists")
      else {
        aliases(anchor) = scalar
        scalar
      }
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

  implicit def yaml2scala(v: YAMLValue): Any =
    v match {
      case YAMLSequence(s)     => s map yaml2scala
      case YAMLSet(s)          => s map yaml2scala toSet
      case YAMLMappping(elems) => elems map { case YAMLPair(k, v) => (yaml2scala(k), yaml2scala(v)) } toMap
      case YAMLOther(tag, v)   => v
      case YAMLString(s)       => s
      case YAMLInteger(n)      => n
      case YAMLBigInt(n)       => n
      case YAMLFloat(n)        => n
      case YAMLNull            => null
    }

  implicit def yaml2scala(d: YAMLDocument): Any = yaml2scala(d.document)

  implicit def yaml2scala(s: YAMLStream): List[Any] = s.documents map yaml2scala

  trait YAML
  case class YAMLStream(documents: List[YAMLDocument])  extends YAML
  case class YAMLDocument(document: YAMLValue)          extends YAML
  case class YAMLPair(key: YAMLValue, value: YAMLValue) extends YAML
  trait YAMLValue                                       extends YAML { val v: Any }
  trait YAMLScalar                                      extends YAMLValue
  case class YAMLBoolean(v: Boolean)                    extends YAMLScalar
  case class YAMLBinary(v: ArraySeq[Byte])              extends YAMLScalar
  case class YAMLInteger(v: Int)                        extends YAMLScalar
  case class YAMLBigInt(v: BigInt)                      extends YAMLScalar
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
