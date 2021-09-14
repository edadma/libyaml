package io.github.edadma

import io.github.edadma.libyaml.extern.LibYAML._

import scala.scalanative.unsafe._
import scala.scalanative.libc.stdlib._
import scala.scalanative.unsigned._

package object libyaml {

  private def bool(a: CInt): Boolean = if (a == 0) false else true

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

  case class Mark(index: Int, line: Int, column: Int)

  implicit class Scalar(val scalar: Ptr[data_scalar]) extends AnyVal {
    def anchor: String = fromCString(scalar._2)

    def tag: String = fromCString(scalar._3)

    def value: String = fromCString(scalar._4)

    def length: Int = scalar._5.toInt

    def plainImplicit: Boolean = bool(scalar._6)

    def quotedImplicit: Boolean = bool(scalar._7)
  }

  class Event {
    private[libyaml] val event: yaml_event_tp = malloc(sizeof[yaml_event_t]).asInstanceOf[yaml_event_tp]

    event._1 = EventType.NO_EVENT.value

    def getType: EventType = event._1

    def scalar: Scalar = Scalar(event.asInstanceOf[Ptr[data_scalar]])

    def startMark: Mark = Mark(event._3._1.toInt, event._3._2.toInt, event._3._3.toInt)

    def endMark: Mark = Mark(event._4._1.toInt, event._4._2.toInt, event._4._3.toInt)

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

}
